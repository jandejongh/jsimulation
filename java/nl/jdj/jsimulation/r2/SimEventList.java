package nl.jdj.jsimulation.r2;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/** An event list for {@link SimEvent}s, implemented as a {@link TreeSet}.
 * 
 * Since a {@link TreeSet} is being used for bookkeeping of the events, the
 * events themselves must be totally ordered.
 * 
 * The current implementation is not thread-safe.
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

  private double lastUpdateTime = 0.0;
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
    synchronized (this)
    {
      if (this.running)
        throw new IllegalStateException ();
      this.lastUpdateTime = 0.0;
      this.firstUpdate = true;
    }
    for (SimEventListListener l : this.listeners) l.notifyEventListReset (this);
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
  
  /** Adds a listener to this event list.
   * 
   * @param l The listener to be added, ignored if <code>null</code>.
   * 
   */
  public final void addListener (SimEventListListener l)
  {
    if (l != null)
      this.listeners.add (l);
  }
  
  /** Removes a listener to this event list.
   * 
   * @param l The listener to be removed, ignored if <code>null</code> or not present.
   * 
   */
  public final void removeListener (SimEventListListener l)
  {
    this.listeners.remove (l);
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
      final E e = pollFirst ();
      checkUpdate (e);
      final SimEventAction a = e.getEventAction ();
      if (a != null)
        a.action (e);
    }
    if (isEmpty ())
      for (SimEventListListener l : this.listeners) l.notifyEventListEmpty (this, this.lastUpdateTime);
    this.running = false;
  }
  
}
