package nl.jdj.jsimulation.r4;

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
 * until the set is empty. This means you can for instance schedule new events from within the context of execution of
 * an event. However, <i>all</i> scheduled events this way must <i>not</i> be in the past.
 * This is rigorously checked for! If the event list discovers the insertion of new {@link SimEvent}s in the past,
 * it will throw an exception!
 * 
 * <p>
 * An event-list instance can be reused by resetting the time, which is done through {@link #reset}, after which
 * the list is empty and time is {@link Double#NEGATIVE_INFINITY} again.
 * It is also possible to reset to a specific time (still clearing the event list, though).
 * Obviously, resetting should not be done while processing the event list (e.g., this should
 * probably not be done from within an event action), as this will result in the list throwing an exception (noting time is no
 * longer non-decreasing).
 * 
 * <p>
 * In between event-list runs ({@link #run}), the event list can be safely added to and removed from. While running the list,
 * events can be added and removed at will, as long as no events are added that are in the past. The list will throw an exception
 * when noting this. Adding events with time equal to the current time is always safe though; such events will always be
 * executed after the current event (though other events may precede in between!).
 * 
 * <p>
 * Note that events that have the same time, are processed in random order! This event list does not maintain insertion order!
 * 
 * <p>
 * While running, the event-list maintains the notion of updates, being "jumps in time".
 * So, as long as the list processes events with equal times, it does not fire such an update.
 * The concept of updates is very useful for statistics, in particular, for integration/averaging, since these
 * operations require non-trivial time steps for updates.
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
 * <p>
 * As of r4, a {@link SimEventList} is capable of generating suitable {@link SimEvent}s itself,
 * for instance to schedule user-provided {@link SimEventAction}s at a specific time.
 * This allowed the inclusion of several convenience methods for scheduling in r4.
 * However, this required the availability of some user-provided factory for {@link SimEvent}s in r4,
 * which was implemented as a {@link Class} parameter in the constructor;
 * the <code>Class</code> giving access to a suitable (parameter-less) constructor for internally-generated
 * {@link SimEvent}s.
 * However, this came at the major expense of incompatibility between r4 and r3 uses.
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
   * Removes all events, and sets time to negative infinity.
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
   * Removes all events, and sets time to the given value.
   * An exception is thrown if the event list is currently running.
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
      clear ();
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
  
  private final Class<E> eventClass;
  
  /** Creates a new {@link SimEventList} with default {@link Comparator}.
   * 
   * The base class for {@link SimEvent}s supported, viz., {@link E}, must feature a constructor with no arguments.
   * 
   * @param eventClass The base class {@link SimEvent}s supported.
   * 
   * @see DefaultSimEventComparator
   * 
   */
  public SimEventList (final Class<E> eventClass)
  {
    this (new DefaultSimEventComparator (), eventClass);
  }

  /** Creates a new {@link SimEventList} with given {@link Comparator}.
   * 
   * The base class for {@link SimEvent}s supported, viz., {@link E}, must feature a constructor with no arguments.
   * 
   * @param comparator The comparator for {@link SimEvent}s.
   * @param eventClass The base class {@link SimEvent}s supported.
   * 
   * @throws IllegalArgumentException If <code>eventClass</code> is <code>null</code>.
   * 
   */
  public SimEventList (final Comparator comparator, final Class<E> eventClass)
  {
    super (comparator);
    if (eventClass == null)
      throw new IllegalArgumentException ();
    this.eventClass = eventClass;
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
      for (SimEventListListener l : this.listeners)
        l.notifyEventListUpdate (this, this.lastUpdateTime);
      for (SimEventAction a : this.updateListeners)
        a.action (e);
    }
  }

  private volatile boolean running = false;
  
  /** Run the event list until it is empty, interrupted, or until a specific point in time has been reached.
   * 
   * After returning from this method, it can be invoked later, but the end time passed must not decrease in this sequence.
   * See also {@link #reset}.
   * 
   * <p>
   * Note that upon return, the time on the event list is set to that of the last event processed.
   * This may be smaller than the end time provided!
   * (In other words, this method does not set the time on the event list to the end time provided,
   * but only uses the end time as a criterion for stopping event processing and returning from this method.)
   * 
   * @param endTime The time until which to run the event list.
   * @param inclusive Whether to include events at the end time parameter.
   * 
   * @throws IllegalStateException If the method is invoked recursively (or from another thread before finishing).
   * @throws IllegalArgumentException If <code>endTime</code> is in the past.
   * 
   * @see #getTime
   * @see SimEvent#getTime
   * @see #run
   * 
   */
  public final void runUntil (final double endTime, final boolean inclusive)
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
      for (SimEventListListener.Fine l : this.fineListeners)
        l.notifyNextEvent (this, this.lastUpdateTime);
      final E e = pollFirst ();      
      // Updates this.lastUpdateTime.
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
  
  /** Run the event list until it is empty (or until interrupted).
   * 
   * @throws IllegalStateException If the method is invoked recursively (or from another thread before finishing).
   * 
   * @see #runUntil
   * 
   */
  @Override
  public void run ()
  {
    runUntil (Double.POSITIVE_INFINITY, true);
  }
  
  /** Runs a single (the first) event from the event list ("single-stepping").
   * 
   */
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
    for (SimEventListListener.Fine l : this.fineListeners)
      l.notifyNextEvent (this, this.lastUpdateTime);
    final E e = pollFirst ();      
    // Updates this.lastUpdateTime.
    checkUpdate (e);
    final SimEventAction a = e.getEventAction ();
    if (a != null)
      a.action (e);
    if (isEmpty ())
      for (SimEventListListener l : this.listeners)
        l.notifyEventListEmpty (this, this.lastUpdateTime);
    this.running = false;
  }
  
  /** Schedules an event on this list, at given time (overriding the time set on the event itself).
   * 
   * The time on the {@link SimEvent} argument is overwritten.
   * 
   * @param time The schedule time for the event.
   * @param event The event to schedule.
   * 
   * @throws IllegalArgumentException If the event is <code>null</code> or already scheduled,
   *                                  or the schedule time is in the past.
   * 
   * @see SimEvent#setTime
   * @see #getTime
   * 
   */
  public final void schedule (final double time, final E event)
  {
    if (event == null)
      throw new IllegalArgumentException ();
    event.setTime (time);
    schedule (event);
  }
  
  /** Reschedules an event on this list, at given time (overriding the time set on the event itself).
   * 
   * The time on the {@link SimEvent} argument is overwritten.
   * 
   * <p>
   * This method reinserts the event (after setting the new time on it) into the event list.
   * If the event is not present in the list, this effects of this method are identical to those of
   * {@link #schedule(double, E)}.
   * 
   * @param time The (new) schedule time for the event.
   * @param event The event to (re)schedule.
   * 
   * @throws IllegalArgumentException If the event is <code>null</code>,
   *                                  or the new schedule time is in the past.
   * 
   * @see SimEvent#setTime
   * @see #getTime
   * 
   */
  public final void reschedule (final double time, final E event)
  {
    if (event == null)
      throw new IllegalArgumentException ();
    remove (event);
    schedule (time, event);
  }
  
  /** Schedules an event on this list, taking the schedule time from the event itself.
   * 
   * @param event The event to schedule.
   * 
   * @throws IllegalArgumentException If the event is <code>null</code>, has a schedule time in the past, or
   *                                  is already scheduled.
   * 
   * @see #getTime
   * @see SimEvent#getTime
   * 
   */
  public final void schedule (final E event)
  {
    if (event == null || event.getTime () < getTime () || contains (event))
      throw new IllegalArgumentException ();
    add (event);
  }
  
  /** Schedules an action at given time.
   * 
   * Note that if the <code>action</code> argument is <code>null</code>,
   * a {@link SimEvent} is still created and scheduled
   * with <code>null</code> {@link SimEventAction}.
   * 
   * @param time The schedule time for the action.
   * @param action The action to schedule, may be <code>null</code>.
   * 
   * @return The new (scheduled) event.
   * 
   * @throws IllegalArgumentException If the schedule time is the past.
   * @throws IllegalStateException If a new {@link SimEvent} could not be instantiated.
   * 
   */
  public final E schedule (final double time, final SimEventAction action)
  {
    if (time < getTime ())
      throw new IllegalArgumentException ();
    try
    {
      final E event = this.eventClass.newInstance ();
      event.setTime (time);
      event.setEventAction (action);
      event.setObject (null);
      schedule (event);
      return event;
    }
    catch (InstantiationException | IllegalAccessException e)
    {
      throw new IllegalStateException (e);
    }
  }
  
  /** Schedules an event on this list at current time (overriding the time set on the event itself).
   * 
   * The time on the {@link SimEvent} argument is overwritten.
   * 
   * <p>
   * There is no guarantee that the new scheduled event will be the next event to be executed.
   * 
   * <p>
   * Note that it is legal to reschedule the event currently being executed from within its {@link SimEvent#getEventAction},
   * even at the same time (in which case the action is invoked again, with the same time argument).
   * 
   * @param event The event to schedule.
   * 
   * @throws IllegalArgumentException If the event is <code>null</code> or already scheduled.
   * 
   * @see SimEvent#setTime
   * @see #getTime
   * 
   */
  public final void scheduleNow (final E event)
  {
    if (event == null)
      throw new IllegalArgumentException ();
    event.setTime (getTime ());
    schedule (event);
  }
  
  /** Schedules an action now.
   * 
   * Note that if the <code>action</code> argument is <code>null</code>,
   * a {@link SimEvent} is still created and scheduled
   * with <code>null</code> {@link SimEventAction}.
   * 
   * <p>
   * There is no guarantee that the new scheduled event will be the next event to be executed.
   * 
   * @param action The action to schedule, may be <code>null</code>.
   * 
   * @return The new (scheduled) event.
   * 
   * @throws IllegalStateException If a new {@link SimEvent} could not be instantiated.
   * 
   */
  public final E scheduleNow (final SimEventAction action)
  {
    return schedule (getTime (), action);
  }
  
}
