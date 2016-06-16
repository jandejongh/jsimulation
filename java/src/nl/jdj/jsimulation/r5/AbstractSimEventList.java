package nl.jdj.jsimulation.r5;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/** A {@link TreeSet}-based implementation of {@link SimEventList}.
 * 
 * <p>
 * Note that in the default implementation, events that have the same time, are processed in random order!
 * This event list does not maintain insertion order!
 * 
 * <p>
 * The implementation is not thread-safe.
 * An event list is really meant to be processed and operated upon by a single thread only.
 * 
 * @param <E> The type of {@link SimEvent}s supported.
 * 
 */
public abstract class AbstractSimEventList<E extends SimEvent>
extends TreeSet<E>
implements SimEventList<E>
{

  /** Creates a new {@link SimEventList} for plain {@link SimEvent}s with default {@link Comparator}.
   * 
   * <p>
   * This method instantiates a {@link DefaultSimEventFactory}
   * and registers it through {@link #setSimEventFactory}.
   * 
   * @see DefaultSimEventComparator
   * @see DefaultSimEventFactory
   * 
   */
  public AbstractSimEventList ()
  {
    this ((Class<E>) SimEvent.class);
    setSimEventFactory ((SimEventFactory <E>) new DefaultSimEventFactory ());
  }
  
  /** Creates a new {@link SimEventList} with default {@link Comparator}.
   * 
   * Unless an event-factory is registered,
   * the base class for {@link SimEvent}s supported (<code>E</code>) must feature a constructor with no arguments,
   * see {@link #setSimEventFactory}.
   * 
   * @param eventClass The base class {@link SimEvent}s supported.
   * 
   * @see DefaultSimEventComparator
   * 
   */
  public AbstractSimEventList (final Class<E> eventClass)
  {
    this (new DefaultSimEventComparator (), eventClass);
  }

  /** Creates a new {@link SimEventList} with given {@link Comparator}.
   * 
   * Unless an event-factory is registered,
   * the base class for {@link SimEvent}s supported (<code>E</code>) must feature a constructor with no arguments,
   * see {@link #setSimEventFactory}.
   * 
   * @param comparator The comparator for {@link SimEvent}s.
   * @param eventClass The base class {@link SimEvent}s supported.
   * 
   * @throws IllegalArgumentException If <code>eventClass</code> is <code>null</code>.
   * 
   */
  public AbstractSimEventList (final Comparator comparator, final Class<E> eventClass)
  {
    super (comparator);
    if (eventClass == null)
      throw new IllegalArgumentException ();
    this.eventClass = eventClass;
  }

  /** Overridden in order (ensure) the setting the <i>de-conflict</i> value on the event added.
   * 
   * <p>
   * Overridden and made final to ensure this method uses the abstract {@link #add} method.
   * 
   * <p>
   * You do not have to override this method in case you want to change
   * setting the de-conflict value; override {@link #add} instead.
   * 
   * @see SimEvent#setSimEventListDeconflictValue
   * 
   */
  @Override
  public final boolean addAll (final Collection<? extends E> collection)
  {
    if (collection == null)
      throw new NullPointerException ("Attempt to add null collection to event list!");
    boolean changed = false;
    for (final E e : collection)
      changed = add (e) || changed;
    return changed;
  }

  private final Class<E> eventClass;

  @Override
  public final Class<E> getSimEventClass ()
  {
    return this.eventClass;
  }
  
  private SimEventFactory<? extends E> eventFactory = null;

  @Override
  public final SimEventFactory<? extends E> getSimEventFactory ()
  {
    return this.eventFactory;
  }

  @Override
  public final void setSimEventFactory (SimEventFactory<? extends E> eventFactory)
  {
    this.eventFactory = eventFactory;
  }
  
  private double lastUpdateTime = Double.NEGATIVE_INFINITY;
  
  private boolean firstUpdate = true;
  
  @Override
  public final double getTime ()
  {
    return this.lastUpdateTime;
  }

  private double defaultResetTime = Double.NEGATIVE_INFINITY;
  
  @Override
  public final double getDefaultResetTime ()
  {
    return this.defaultResetTime;
  }

  @Override
  public final void setDefaultResetTime (final double defaultResetTime)
  {
    this.defaultResetTime = defaultResetTime;
  }

  @Override
  public void reset (double time)
  {
    synchronized (this)
    {
      if (this.running)
        throw new IllegalStateException ();
      clear ();
      this.lastUpdateTime = time;
      this.firstUpdate = true;
    }
    fireReset ();
  }
  
  /** The reset listeners to this event list.
   * 
   */
  private final Set<SimEventListResetListener> resetListeners = new HashSet<> ();
  
  /** The (full) listeners to this event list.
   * 
   */
  private final Set<SimEventListListener> listeners = new HashSet<> ();
  
  /** The listeners to this event list that need per-event notifications.
   * 
   */
  private final Set<SimEventListListener.Fine> fineListeners = new HashSet<> ();
  
  @Override
  public final void addListener (SimEventListResetListener l)
  {
    if (l != null)
    {
      if (l instanceof SimEventListListener.Fine)
        this.fineListeners.add ((SimEventListListener.Fine) l);
      else if (l instanceof SimEventListListener)
        this.listeners.add ((SimEventListListener) l);
      else
        this.resetListeners.add (l);
    }
  }

  @Override
  public final void removeListener (SimEventListResetListener l)
  {
    if (l != null)
    {
      this.resetListeners.remove (l);
      if (l instanceof SimEventListListener)
        this.listeners.remove ((SimEventListListener) l);
      if (l instanceof SimEventListListener.Fine)
        this.fineListeners.remove ((SimEventListListener.Fine) l);
    }
  }

  /** Fires a reset notification to registered listeners.
   * 
   * @see SimEventListResetListener#notifyEventListReset
   * 
   */
  protected final void fireReset ()
  {
    for (SimEventListListener.Fine l : this.fineListeners)
      l.notifyEventListReset (this);
    for (SimEventListListener l : this.listeners)
      l.notifyEventListReset (this);
    for (SimEventListResetListener l : this.resetListeners)
      l.notifyEventListReset (this);
  }
  
  /** Fires an update notification to registered listeners.
   * 
   * @see SimEventListListener#notifyEventListUpdate
   * 
   */
  protected final void fireUpdate ()
  {
    for (SimEventListListener.Fine l : this.fineListeners)
      l.notifyEventListUpdate (this, this.lastUpdateTime);    
    for (SimEventListListener l : this.listeners)
      l.notifyEventListUpdate (this, this.lastUpdateTime);    
  }
  
  /** Fires an empty-event-list notification to registered listeners.
   * 
   * @see SimEventListListener#notifyEventListEmpty
   * 
   */
  protected final void fireEmpty ()
  {
    for (SimEventListListener.Fine l : this.fineListeners)
      l.notifyEventListEmpty (this, this.lastUpdateTime);    
    for (SimEventListListener l : this.listeners)
      l.notifyEventListEmpty (this, this.lastUpdateTime);    
  }
  
  /** Fires a next-event notification to registered listeners.
   * 
   * @see SimEventListListener.Fine#notifyNextEvent
   * 
   */
  protected final void fireNextEvent ()
  {
    for (SimEventListListener.Fine l : this.fineListeners)
      l.notifyNextEvent (this, this.lastUpdateTime);
  }
  
  /** Checks the progress of time and notifies listeners when an update has taken place.
   * 
   * An update is defined as the processing of the first event or an increase in the current time.
   * If needed, this method updates the current time.
   * 
   * @param newTime The new time.
   * 
   * @throws IllegalArgumentException If this is not the first update (after construction or after a reset)
   *                                  and the new time is strictly smaller than the current time.
   * @see #getTime
   * @see #fireUpdate
   * 
   */ 
  protected final void checkUpdate (final double newTime)
  {
    if ((! this.firstUpdate) && newTime < this.lastUpdateTime)
      throw new IllegalArgumentException ();
    if (this.firstUpdate || newTime > this.lastUpdateTime)
    {
      this.lastUpdateTime = newTime;
      this.firstUpdate = false;
      fireUpdate ();
    }
  }

  /** Checks the progress of time when processing a given event
   * and notifies listeners when an update has taken place.
   * 
   * An update is defined as the processing of the first event or an increase in the current time.
   * If needed, this method updates the current time.
   * 
   * @param e The event being processed.
   * 
   * @throws IllegalArgumentException If the event is <code>null</code> or if this is not the first update
   *                                  (after construction or after a reset)
   *                                  and the new time on the event is strictly smaller than the current time.
   * 
   * @see #getTime
   * @see #fireUpdate
   * 
   */ 
  protected final void checkUpdate (final E e)
  {
    if (e == null)
      throw new IllegalArgumentException ();
    checkUpdate (e.getTime ());
  }

  private volatile boolean running = false;
  
  @Override
  public final void runUntil (final double endTime, final boolean inclusive, final boolean setTimeToEndTime)
  {
    synchronized (this)
    {
      if (this.running)
        throw new IllegalStateException ();
      this.running = true;
    }
    if (endTime < getTime ())
    {
      this.running = false;
      throw new IllegalArgumentException ();
    }
    while ((! isEmpty ())
      && (first ().getTime () < endTime || (inclusive && first ().getTime () == endTime))
      && ! Thread.interrupted ())
    {
      fireNextEvent ();
      final E e = pollFirst ();      
      // Updates this.lastUpdateTime.
      checkUpdate (e);
      final SimEventAction a = e.getEventAction ();
      if (a != null)
        a.action (e);
    }
    if (inclusive && setTimeToEndTime && getTime () < endTime)
      checkUpdate (endTime);
    if (isEmpty ())
      fireEmpty ();
    this.running = false;
  }
  
  /** Runs a single (the first) event from the event list ("single-stepping").
   * 
   */
  @Override
  public final void runSingleStep ()
  {
    synchronized (this)
    {
      if (isEmpty ())
        return;
      if (this.running)
        throw new IllegalStateException ();
      this.running = true;
    }
    fireNextEvent ();
    final E e = pollFirst ();      
    // Updates this.lastUpdateTime.
    checkUpdate (e);
    final SimEventAction a = e.getEventAction ();
    if (a != null)
      a.action (e);
    if (isEmpty ())
      fireEmpty ();
    this.running = false;
  }
  
  @Override
  public String toString ()
  {
    return getClass ().getName () + '@' + Integer.toHexString (hashCode ());
  }
  
}
