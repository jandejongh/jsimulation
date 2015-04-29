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
  public void checkUpdate (E e)
  {
    if (this.firstUpdate || e.getTime () > this.lastUpdateTime)
    {
      this.lastUpdateTime = e.getTime ();
      this.firstUpdate = false;
      for (SimEventAction a : this.updateListeners) a.action (e);
    }
  }

  private boolean running = false;
    
  /** Run the event list until it is empty (or until interrupted).
   * 
   * @throws IllegalStateException If the method is invoked more than once.
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
    while (! isEmpty ())
    {
      final E e = pollFirst ();
      checkUpdate (e);
      final SimEventAction a = e.getEventAction ();
      if (a != null)
        a.action (e);
    }
  }
  
}
