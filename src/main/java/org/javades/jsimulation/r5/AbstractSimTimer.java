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
package org.javades.jsimulation.r5;

/** A general-purpose timer.
 *
 * <p>
 * This abstract timer class hides the details of scheduling a {@link SimEvent} with
 * an appropriate {@link SimEventAction} on a {@link SimEventList} and
 * instead uses a callback method {@link #expireAction} to
 * notify concrete subclasses of timer expiration.
 * Concrete subclasses only have to override {@link #expireAction}.
 * 
 * <p>
 * The timer is scheduled on a {@link SimEventList} through
 * {@link #schedule}, passing the (positive or zero) delay.
 * Upon scheduling, the override-able {@link #scheduleAction} is invoked
 * (the default implementation does nothing).
 * 
 * <p>
 * Canceling a pending timer event is supported through {@link #cancel}.
 * Canceling a timer that is not currently scheduled has no effect.
 * Upon cancellation, the override-able {@link #cancelAction} is invoked
 * (the default implementation does nothing).
 * 
 * <p>
 * A timer can only be scheduled on a single {@link SimEventList} at a time,
 * but once expired or canceled, the timer can be rescheduled, even on a different
 * {@link SimEventList}.
 * An attempt to schedule an already scheduled timer will result in a {@link RuntimeException}.
 *
 * <p>
 * Although (believed to be) still functional,
 * this class is maintained for mainly historic reasons,
 * since its functionality can be much easier obtained by scheduling a {@link SimEventAction}
 * on a {@link SimEventList}.
 * 
 * <p>
 * <b>Last javadoc Review:</b> Jan de Jongh, TNO, 20180404, r5.1.0.
 * 
 */
public abstract class AbstractSimTimer
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Creates a timer with given name.
   * 
   * @param name The name of the timer; replaced with the empty string when {@code null}.
   * 
   */
  public AbstractSimTimer (final String name)
  {
    this.name = (name == null) ? "" : name;
    this.EXPIRE_EVENT = new DefaultSimEvent (this.name + "_expire", 0.0, null, this.EXPIRE_EVENT_ACTION);
  }
  
  /** Creates a timer without name ({@code null} name).
   * 
   * <p>
   * The name is replaced with the empty string.
   * 
   */
  public AbstractSimTimer ()
  {
    this (null);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final String name;

  /** Returns the name of this timer.
   * 
   * @return The name of this timer; non-{@code null} but may be empty.
   * 
   */
  public final String getName ()
  {
    return this.name;
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EVENT LIST [WHEN SCHEDULED]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private SimEventList eventList = null;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EXPIRE EVENT [WHEN SCHEDULED]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final SimEvent EXPIRE_EVENT;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EXPIRE ACTION [PRIVATE ACTION WHEN THE EXPIRE EVENT IS EXECUTED]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final SimEventAction EXPIRE_EVENT_ACTION = (SimEventAction) (SimEvent event) ->
  {
    AbstractSimTimer.this.eventList = null;
    AbstractSimTimer.this.expireAction (event.getTime ());
  };

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EXPIRE ACTION [SUB-CLASS DEFINED ACTION WHEN THE EXPIRE EVENT IS EXECUTED]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Abstract method that is invoked upon expiration of the timer.
   *
   * @param time The current time at expiration.
   *
   */
  public abstract void expireAction (double time);

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SCHEDULE ACTION [OPTIONAL SUB-CLASS DEFINED ACTION WHEN THE TIMER IS SCHEDULED]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Method that is invoked upon scheduling the timer.
   *
   * <p>
   * The default implementation does nothing.
   *
   * @param time The current time when scheduling.
   *
   * @see #schedule
   * 
   */
  public void scheduleAction (double time)
  {
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CANCEL ACTION [OPTIONAL SUB-CLASS DEFINED ACTION WHEN THE TIMER IS CANCELED]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Method that is invoked upon canceling the timer.
   *
   * <p>
   * The default implementation does nothing.
   * 
   * <p>
   * Note that this method is only invoked if the timer was actually
   * scheduled on a {@link SimEventList} when {@link #cancel} was invoked.
   *
   * @param time The current time when canceling.
   *
   */
  public void cancelAction (double time)
  {
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SCHEDULE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Schedules this timer on an event list.
   *
   * Invokes {@link #scheduleAction} once the appropriate event is scheduled on the event lList.
   *
   * <p>Note that this method take the current time from the event list to which it adds a delay
   * in order to schedule the expiration event.
   * Especially in between event-list runs, the current time may be {@link Double#NEGATIVE_INFINITY},
   * leading to indefinite rescheduling of the times at negative infinite.
   *
   * @param delay     The delay until expiration.
   * @param eventList The event list.
   *
   * @throws IllegalArgumentException If delay is negative or infinite (positive or negative),
   *                                    the eventList is null,
   *                                    or the current time on the event list is negative or positive infinity.
   * @throws RuntimeException If the timer is already scheduled.
   * 
   */
  public void schedule (final double delay, final SimEventList eventList)
  {
    if (delay < 0.0 || Double.isInfinite (delay)
      || eventList == null || Double.isInfinite (eventList.getTime ()))
      throw new IllegalArgumentException ();
    if (this.eventList != null)
      throw new RuntimeException ("Timer already scheduled!");
    this.eventList = eventList;
    this.EXPIRE_EVENT.setTime (this.eventList.getTime () + delay);
    this.eventList.add (this.EXPIRE_EVENT);
    scheduleAction (this.eventList.getTime ());
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CANCEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Cancels a pending timer.
   *
   * <p>
   * Ignored if this timer is not scheduled.
   * Invokes {@link #cancelAction} after removal of the appropriate event on the event list
   * (and <i>not</i> if the timer was previously unscheduled).
   *
   * @see #cancelAction
   * 
   */
  public void cancel ()
  {
    if (this.eventList != null)
    {
      final double time = this.eventList.getTime ();
      this.eventList.remove (this.EXPIRE_EVENT);
      this.eventList = null;
      cancelAction (time);
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
