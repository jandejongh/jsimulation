/* 
 * Copyright 2010-2018 Jan de Jongh <jfcmdejongh@gmail.com>, TNO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package nl.jdj.jsimulation.r5;

import java.io.PrintStream;
import java.util.SortedSet;

/** An event list for {@link SimEvent}s.
 * 
 * <p>
 * Since a {@link SortedSet} is being used for bookkeeping of the events, the
 * events themselves must be totally ordered.
 * 
 * <p>
 * During its life, an event list always has a notion of "current time", see {@link #getTime}.
 * Upon creation of the event list,
 * the time is set to the so-called <i>default reset time</i>,
 * see {@link #getDefaultResetTime}.
 * The latter should default to {@link Double#NEGATIVE_INFINITY},
 * but can be set through {@link #setDefaultResetTime}.
 * In addition, implementations are strongly encouraged to provide constructors
 * capable of setting the default reset time at creation time of the event list.
 *
 * <p>
 * The time is only updated as a result of running the event list (e.g, in {@link #run}),
 * during which time is non-decreasing.
 * 
 * <p>
 * The process of running an event list is simply to repeatedly remove the first element of the underlying {@link SortedSet},
 * until the set is empty (or until another termination criterion is met before that).
 * This means you can for instance schedule new events from within the context of execution of
 * an event. However, <i>all</i> events scheduled this way must <i>not</i> be in the past.
 * This is rigorously checked for! If the event list discovers the insertion of new {@link SimEvent}s in the past,
 * it will throw an exception!
 * 
 * <p>
 * An event-list instance can be reused by resetting it, which is done through {@link #reset},
 * after which the list is empty and time is {@link #getDefaultResetTime}.
 * It is also possible to reset to a specific time (still clearing the event list, though)
 * through {@link #reset(double)}.
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
 * Note that events that have the same time, may not be processed in insertion order!
 * It is up to the implementation whether to specify whether it guarantees insertion order
 * or some other criterion.
 * However, between events scheduled at the same time,
 * the <i>order</i> of execution itself is fixed as long as these events are present (and are not <i>rescheduled</i>).
 * This implies that upon insertion of an event having the same time as an event already present,
 * their relative order is determined immediately (yet, this <i>may not be</i> insertion order).
 * 
 * <p>
 * While running, the event-list maintains the notion of updates, being "jumps in time".
 * So, as long as the list processes events with equal times, it does not fire such an update.
 * The concept of updates is very useful for statistics, in particular, for integration/averaging, since these
 * operations require non-trivial time steps for updates.
 * Note that irrespective of its time, the first event processed after creation of the event list or
 * after a reset always fires an update.
 * 
 * <p>
 * A {@link SimEventList} supports various notification mechanisms to registered <i>listeners</i>.
 * When and with what detail listeners are notified of changes to the event list depends on the actual listener <i>type</i>,
 * see {@link SimEventListResetListener}, {@link SimEventListListener} and {@link SimEventListListener.Fine}.
 * 
 * <p>
 * An event list is really meant to be processed and operated upon by a single thread only.
 * 
 * <p>
 * A {@link SimEventList} must be capable of generating suitable {@link SimEvent}s itself,
 * for instance for scheduling user-provided {@link SimEventAction}s at a specific time.
 * The preferred way of achieving this is through setting a <i>factory</i> method,
 * see {@link #getSimEventFactory} and {@link #setSimEventFactory}.
 * Note that the factory may be absent,
 * because we do not want to burden our users with its mandatory registration.
 * When the event factory is absent,
 * the {@link SimEvent} {@code class} (as provided through the {@code <E>} type parameter
 * and its runtime companion {@link #getSimEventClass})
 * <i>must</i> support a parameterless constructor.
 * 
 * <p>
 * <b>Last javadoc Review:</b> Jan de Jongh, TNO, 20180404, r5.1.0.
 * 
 * @param <E> The type of {@link SimEvent}s supported.
 * 
 * @see DefaultSimEventComparator
 * @see SimEvent
 * 
 */
public interface SimEventList<E extends SimEvent>
  extends SortedSet<E>, Runnable
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME / toString / PRINT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Prints a representation of this event list on {@link System#out}.
   * 
   */
  default void print ()
  {
    print (System.out);
  }
  
  /** Prints a representation of this event list on given stream.
   * 
   * @param stream The stream to which to print; if {@code null}, {@link System#out} is used.
   * 
   */
  default void print (final PrintStream stream)
  {
    final PrintStream ps = ((stream == null) ? System.out : stream);
    ps.println ("SimEventList " + this + ", class=" + getClass ().getSimpleName () + ", time=" + getTime () + ":");
    if (isEmpty ())
      ps.println ("  EMPTY!");
    else
      for (final SimEvent event : this)
      {
        ps.println ("  t=" + event.getTime ()
          + ", name=" + event.getName ()
          + ", object=" + event.getObject ()
          + ", action=" + event.getEventAction () + ".");
      }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEFAULT RESET TIME
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Gets the default reset time, the time on the event list after it is reset (without explicit time argument).
   * 
   * <p>
   * The default value (i.e., the default reset time if not set explicitly through {@link #setDefaultResetTime})
   * is negative infinity.
   * 
   * @return The default reset time (default minus infinity).
   * 
   * @see #reset()
   * @see #reset(double)
   * 
   */
  double getDefaultResetTime ();
  
  /** Sets the default reset time, the time on the event list after it is reset (without explicit time argument).
   * 
   * <p>
   * The value supplied (obviously) survives resets;
   * the last value set through this method is only
   * used by {@link #reset()}.
   * 
   * <p>
   * This method can be called at any time; the default reset time is only used
   * while actually performing a reset.
   * 
   * @param defaultResetTime The new default reset time (minus and positive infinity are allowed).
   * 
   * @see #reset()
   * @see #reset(double)
   * 
   */
  void setDefaultResetTime (double defaultResetTime);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // [LAST-UPDATE] TIME
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Returns the current time during processing of the event list.
   * 
   * @return The current time.
   * 
   */
  double getTime ();

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EVENT CLASS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Returns the {@link Class} of allowable {@link SimEvent}s in this event list.
   * 
   * @return The {@link Class} of allowable {@link SimEvent}s in this event list.
   * 
   */
  Class<E> getSimEventClass ();
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EVENT FACTORY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Gets the factory for {@link SimEvent}s created by this {@link SimEventList}.
   * 
   * @return The event factory; may be {@code null}.
   * 
   */
  SimEventFactory<? extends E> getSimEventFactory ();
  
  /** Sets the factory for {@link SimEvent}s created by this {@link SimEventList}.
   * 
   * @param eventFactory The event factory; may be {@code null} to stop using it.
   * 
   */
  void setSimEventFactory (SimEventFactory<? extends E> eventFactory);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RESET
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  /** Resets the event list to a specific time.
   * 
   * <p>
   * Removes all events, and sets time to the given value.
   * An exception is thrown if the event list is currently running.
   * 
   * <p>
   * This method ignores the default reset time, as given by {@link #getDefaultResetTime},
   * but does <i>not</i> change that value.
   * 
   * @param time The new time of the event list.
   * 
   * @throws IllegalStateException If the event list is currently running.
   * 
   * @see #run
   * 
   */
  void reset (double time);
  
  /** Resets the event list.
   * 
   * <p>
   * Removes all events, and sets time to {@link #getDefaultResetTime}.
   * An exception is thrown if the event list is currently running.
   * 
   * @throws IllegalStateException If the event list is currently running.
   * 
   * @see #run
   * @see #getDefaultResetTime
   * @see #setDefaultResetTime
   * 
   */
  default void reset ()
  {
    reset (getDefaultResetTime ());
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RUN [UNTIL]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Runs the event list until it is empty, interrupted, or until a specific point in time has been reached.
   * 
   * <p>
   * After returning from this method, it can be invoked later, but the end time passed must not decrease in this sequence.
   * See also {@link #reset}.
   * 
   * <p>
   * Optionally, but only if <code>inclusive == true</code>, the time on the event list is increased to the <code>endTime</code>
   * argument.
   * Otherwise, not that the current time on the event list may be smaller than the end time provided!
   * 
   * @param endTime          The time until which to run the event list.
   * @param inclusive        Whether to include events at the end time parameter.
   * @param setTimeToEndTime Whether to increase the current time to the end time given after processing the applicable events
   *                         (ignored if <code>inclusive == false</code>).
   * 
   * @throws IllegalStateException If the method is invoked recursively (or from another thread before finishing).
   * @throws IllegalArgumentException If <code>endTime</code> is in the past.
   * 
   * @see #getTime
   * @see SimEvent#getTime
   * @see #run
   * 
   */
  void runUntil (final double endTime, final boolean inclusive, final boolean setTimeToEndTime);
  
  /** Runs the event list until it is empty (or until interrupted).
   * 
   * <p>
   * This method leaves the time to that of the last event processed.
   * 
   * @throws IllegalStateException If the method is invoked recursively (or from another thread before finishing).
   * 
   * @see #runUntil
   * 
   */
  @Override
  default void run ()
  {
    runUntil (Double.POSITIVE_INFINITY, true, false);
  }
  
  /** Runs a single (the first) event from the event list ("single-stepping").
   * 
   */
  void runSingleStep ();
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** Adds a listener to this event list.
   * 
   * @param l The listener to be added, ignored if <code>null</code>.
   * 
   */
  void addListener (SimEventListResetListener l);
  
  /** Removes a listener from this event list.
   * 
   * @param l The listener to be removed, ignored if <code>null</code> or not present.
   * 
   */
  void removeListener (SimEventListResetListener l);

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // UTILITY METHODS FOR SCHEDULING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
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
  default void schedule (final E event)
  {
    if (event == null || event.getTime () < getTime () || contains (event))
      throw new IllegalArgumentException ();
    add (event);
  }
  
  /** Schedules an event on this list, at given time (overriding the time set on the event itself).
   * 
   * <p>
   * The time on the {@link SimEvent} argument is overwritten.
   * 
   * @param time  The schedule time for the event.
   * @param event The event to schedule.
   * 
   * @throws IllegalArgumentException If the event is <code>null</code> or already scheduled,
   *                                  or the schedule time is in the past.
   * 
   * @see SimEvent#setTime
   * @see #getTime
   * 
   */
  default void schedule (final double time, final E event)
  {
    if (event == null)
      throw new IllegalArgumentException ();
    event.setTime (time);
    schedule (event);
  }
  
  /** Reschedules an event on this list, at given time (overriding the time set on the event itself).
   * 
   * <p>
   * The time on the {@link SimEvent} argument is overwritten.
   * 
   * <p>
   * This method reinserts the event (after setting the new time on it) into the event list.
   * If the event is not present in the list, the effects of this method are identical to those of
   * {@link #schedule(double, SimEvent)}.
   * 
   * @param time  The (new) schedule time for the event.
   * @param event The event to (re)schedule.
   * 
   * @throws IllegalArgumentException If the event is <code>null</code>,
   *                                  or the new schedule time is in the past.
   * 
   * @see SimEvent#setTime
   * @see #getTime
   * 
   */
  default void reschedule (final double time, final E event)
  {
    if (event == null)
      throw new IllegalArgumentException ();
    remove (event);
    schedule (time, event);
  }
  
  /** Schedules an action at given time.
   * 
   * <p>
   * Note that if the <code>action</code> argument is <code>null</code>,
   * a {@link SimEvent} is still created and scheduled
   * with <code>null</code> {@link SimEventAction}.
   * 
   * <p>
   * This method (and all others instantiating {@link SimEvent}s)
   * uses a non-{@code null} {@link SimEventFactory}
   * if available through {@link #getSimEventFactory}.
   * Otherwise,
   * it attempts to find and invoke a null constructor
   * on {@link #getSimEventClass} (which fails if that is an interface).
   * 
   * @param time   The schedule time for the action.
   * @param action The action to schedule, may be <code>null</code>.
   * @param name   The name for the event, may be <code>null</code>.
   * 
   * @return The new (scheduled) event.
   * 
   * @throws IllegalArgumentException If the schedule time is the past.
   * @throws IllegalStateException If a new {@link SimEvent} could not be instantiated.
   * 
   */
  default E schedule (final double time, final SimEventAction action, final String name)
  {
    if (time < getTime ())
      throw new IllegalArgumentException ("Schedule time is in the past: " + time + " < " + getTime () + "!");
    try
    {
      final E event;
      if (getSimEventFactory () != null)
        event = getSimEventFactory ().newInstance (name, time, action);
      else
        event = getSimEventClass ().newInstance ();
      event.setTime (time);
      event.setEventAction (action);
      event.setObject (null);
      if (name != null)
        event.setName (name);
      schedule (event);
      return event;
    }
    catch (InstantiationException | IllegalAccessException e)
    {
      throw new IllegalStateException ("Cannot instantiate " + getSimEventClass ().getSimpleName () + "!", e);
    }
  }
  
  /** Schedules an action at given time.
   * 
   * <p>
   * Note that if the <code>action</code> argument is <code>null</code>,
   * a {@link SimEvent} is still created and scheduled
   * with <code>null</code> {@link SimEventAction}.
   * 
   * @param time   The schedule time for the action.
   * @param action The action to schedule, may be <code>null</code>.
   * 
   * @return The new (scheduled) event.
   * 
   * @throws IllegalArgumentException If the schedule time is the past.
   * @throws IllegalStateException If a new {@link SimEvent} could not be instantiated.
   * 
   */
  default E schedule (final double time, final SimEventAction action)
  {
    return schedule (time, action, null);
  }
  
  /** Schedules an event on this list at current time (overriding the time set on the event itself).
   * 
   * <p>
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
  default void scheduleNow (final E event)
  {
    if (event == null)
      throw new IllegalArgumentException ();
    event.setTime (getTime ());
    schedule (event);
  }
  
  /** Schedules an action now.
   * 
   * <p>
   * Note that if the <code>action</code> argument is <code>null</code>,
   * a {@link SimEvent} is still created and scheduled
   * with <code>null</code> {@link SimEventAction}.
   * 
   * <p>
   * There is no guarantee that the new scheduled event will be the next event to be executed.
   * 
   * @param action The action to schedule, may be <code>null</code>.
   * @param name   The name for the event, may be <code>null</code>.
   * 
   * @return The new (scheduled) event.
   * 
   * @throws IllegalStateException If a new {@link SimEvent} could not be instantiated.
   * 
   */
  default E scheduleNow (final SimEventAction action, final String name)
  {
    return schedule (getTime (), action, name);
  }

  /** Schedules an action now.
   * 
   * <p>
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
  default E scheduleNow (final SimEventAction action)
  {
    return scheduleNow (action, null);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
