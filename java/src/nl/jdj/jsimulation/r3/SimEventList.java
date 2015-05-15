package nl.jdj.jsimulation.r3;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/** An event list for {@link SimEvent}s, implemented as a {@link TreeSet}.
 * 
 * Since a {@link TreeSet} is being used for bookkeeping of the events, the
 * events themselves must be totally ordered.
 * 
 * <p>
 * During its lifetime, an event list always has a notion of "current time", upon creation the time is set to
 * {@link Double#NEGATIVE_INFINITY}. The time is only updated as a result of processing the event list (e.g, in {@link #run},
 * during which time is non-decreasing.
 * 
 * <p>
 * The process of running an event list is simply to repeatedly remove the first element of the underlying {@link TreeSet},
 * until the set is empty. This means you can for instance 
 * 
 * <p>
 * An event-list instance can be reused by resetting the time, which is done through {@link #reset}, after which time is
 * {@link Double#NEGATIVE_INFINITY} again. Obviously, this should not be done while processing the event list (e.g., this should
 * probably not be done from within an event action), as this will result in the list throwing an exception (noting time is no
 * longer non-decreasing).
 * 
 * <p>
 * In between event-list runs ({@link #run}), the event list can be safely added to and removed from. While running the list,
 * events can be added and removed at will, as long as no events are added that are in the past. The list will throw an exception
 * when noting this. Adding events with time equal to the current time is always safe though.
 * 
 * <p>
 * Note that events that have the same time, are processed in random order! This event list does not maintain insertion order!
 * 
 * <p>
 * While running, the event-list maintains the notion of updates, being "jumps in time".
 * So, as long as the list processes events with equal times, it does not fire an update.
 * The concept of updates is very useful for statistics.
 * Note that irrespective of its time, the first event processed during a run always fires an update.
 * 
 * <p>
 * A {@link SimEventList} supports various notification mechanisms. First, a user can, at all times, register a
 * {@link SimEventAction} that is invoked when an update occurs. Seconds, a user can register as listener that will be
 * notified of reset and update events and also of the end of a run (i.e., an empty event list).
 * Specific types of listeners will even be notified of each individual event being removed from the list while running,
 * see {@link SimEventListListener.Fine}.
 * 
 * <p>
 * The current implementation is not thread-safe.
 * An event list is really meant to be processed and operated upon by a single thread only.
 * 
 * @param <E> The type of {@link SimEvent}s supported.
 * 
 * @see DefaultSimEventComparator
 * @see SimEvent
 * 
 */
public class SimEventList<E extends SimEvent>
  extends TreeSet<E>
  implements Runnable
{

  private double lastUpdateTime = Double.NEGATIVE_INFINITY;
  private boolean firstUpdate = true;
  
  /** Returns the current time during processing of the event list.
   * 
   * @return The current time.
   * 
   */
  public final double getTime ()
  {
    return this.lastUpdateTime;
  }

  /** Resets the event list.
   * 
   * An exception is thrown if the event list is currently running.
   * 
   * @throws IllegalStateException If the event list is currently running.
   * 
   * @see #run
   * 
   */
  public void reset ()
  {
    reset (Double.NEGATIVE_INFINITY);
  }
  
  /** Resets the event list to a specific time.
   * 
   * An exception is thrown if the event list is currently running,
   * or the time argument is beyond the event time of the first element in this list, if any.
   * 
   * @param time The new time of the event list.
   * 
   * @throws IllegalStateException If the event list is currently running.
   * 
   * @see #run
   * 
   */
  public void reset (double time)
  {
    synchronized (this)
    {
      if (this.running)
        throw new IllegalStateException ();
      if ((! isEmpty ()) && first ().getTime () < time)
        throw new IllegalArgumentException ();
      this.lastUpdateTime = time;
      this.firstUpdate = true;
    }
    for (SimEventListListener l : this.listeners)
      l.notifyEventListReset (this);
  }
  
  /** The listeners to update to the current time in this event list.
   * 
   * Beware that listeners are only invoked upon changes in the time; which may the result of
   * processing multiple events in sequence.
   * 
   * @see #getTime
   * 
   */
  private final Set<SimEventAction> updateListeners = new HashSet<> ();
  
  /** Adds a listener for updates to the current time in this event list.
   * 
   * Beware that listeners are only invoked upon changes in the time; which may the result of
   * processing multiple events in sequence.
   * 
   * @param a The action to be added.
   * 
   * @throws IllegalArgumentException If <code>a == null</code>.
   * 
   */
  public final void addUpdateSimEventAction (SimEventAction a)
  {
    if (a == null)
      throw new IllegalArgumentException ();
    this.updateListeners.add (a);
  }
  
  /** Removes a listener for updates to the current time in this event list.
   * 
   * @param a The action to be removed (ignored if not present).
   * 
   * @throws IllegalArgumentException If <code>a == null</code>.
   * 
   */
  public final void removeUpdateSimEventAction (SimEventAction a)
  {
    if (a == null)
      throw new IllegalArgumentException ();
    this.updateListeners.remove (a);
  }
  
  /** The listeners to this event list.
   * 
   */
  private final Set<SimEventListListener> listeners = new HashSet<> ();
  
  /** The listeners to this event list that need per-event notifications.
   * 
   */
  private final Set<SimEventListListener.Fine> fineListeners = new HashSet<> ();
  
  /** Adds a listener to this event list.
   * 
   * @param l The listener to be added, ignored if <code>null</code>.
   * 
   */
  public final void addListener (SimEventListListener l)
  {
    if (l != null)
    {
      this.listeners.add (l);
      if (l instanceof SimEventListListener.Fine)
        this.fineListeners.add ((SimEventListListener.Fine) l);
    }
  }
  
  /** Removes a listener to this event list.
   * 
   * @param l The listener to be removed, ignored if <code>null</code> or not present.
   * 
   */
  public final void removeListener (SimEventListListener l)
  {
    this.listeners.remove (l);
    if (l instanceof SimEventListListener.Fine)
      this.fineListeners.remove ((SimEventListListener.Fine) l);
  }
  
  /** Creates a new {@link SimEventList} with default {@link Comparator}.
   * 
   * @see DefaultSimEventComparator
   * 
   */
  public SimEventList ()
  {
    this (new DefaultSimEventComparator ());
  }

  /** Creates a new {@link SimEventList} with given {@link Comparator}.
   * 
   * @param comparator The comparator for {@link SimEvent}s.
   * 
   */
  public SimEventList (Comparator comparator)
  {
    super (comparator);
  }

  /** Checks the progress of time when processing a given event
   * and triggers actions on update listeners when an update has taken place.
   * 
   * An update is defined as the processing of the first event or an increase in the current time.
   * If needed, this method updates the current time.
   * 
   * @param e The event being processed.
   * 
   * @see #getTime
   * @see #addUpdateSimEventAction
   * @see SimEventAction
   * 
   */ 
  protected void checkUpdate (E e)
  {
    if (this.firstUpdate || e.getTime () > this.lastUpdateTime)
    {
      this.lastUpdateTime = e.getTime ();
      this.firstUpdate = false;
      for (SimEventListListener l : this.listeners) l.notifyEventListUpdate (this, this.lastUpdateTime);
      for (SimEventAction a : this.updateListeners) a.action (e);
    }
  }

  private boolean running = false;
    
  /** Run the event list until it is empty (or until interrupted).
   * 
   * @throws IllegalStateException If the method is invoked recursively (or from another thread before finishing).
   */
  @Override
  public void run ()
  {
    synchronized (this)
    {
      if (this.running)
        throw new IllegalStateException ();
      this.running = true;
    }
    while ((! isEmpty ()) && ! Thread.interrupted ())
    {
      for (SimEventListListener.Fine l : this.fineListeners)
        l.notifyNextEvent (this, this.lastUpdateTime);
      final E e = pollFirst ();
      checkUpdate (e);
      final SimEventAction a = e.getEventAction ();
      if (a != null)
        a.action (e);
    }
    if (isEmpty ())
      for (SimEventListListener l : this.listeners)
        l.notifyEventListEmpty (this, this.lastUpdateTime);
    this.running = false;
  }
  
}
