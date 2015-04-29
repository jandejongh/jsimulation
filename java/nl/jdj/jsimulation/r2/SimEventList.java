package nl.jdj.jsimulation.r2;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/** An event list for {@link SimEvent}s, implemented as a {@link TreeSet}.
 * 
 * The current implementation is not thread-safe.
 * 
 * @param <E> The type of {@link SimEvent}s supported.
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
   * @return The current time, zero or positive.
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
  
  public final void addUpdateSimEventAction (SimEventAction a)
  {
    if (a == null)
      throw new IllegalArgumentException ();
    this.updateListeners.add (a);
  }
  
  public final void removeUpdateSimEventAction (SimEventAction a)
  {
    if (a == null)
      throw new IllegalArgumentException ();
    this.updateListeners.remove (a);
  }
  
  public SimEventList ()
  {
    this (new DefaultSimEventComparator ());
  }

  public SimEventList (Comparator comparator)
  {
    super (comparator);
  }

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
      final SimEventAction a = e.getEventAction ();
      if (a != null)
      {
        checkUpdate (e);
        a.action (e);
      }
    }
  }
  
}
