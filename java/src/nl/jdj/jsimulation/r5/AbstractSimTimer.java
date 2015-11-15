package nl.jdj.jsimulation.r5;

/** A general-purpose timer.
 *
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
 */
public abstract class AbstractSimTimer
{

  public final String NAME;

  private final SimEvent EXPIRE_EVENT;

  public AbstractSimTimer (String name)
  {
    this.NAME = (name == null) ? "" : name;
    this.EXPIRE_EVENT = new DefaultSimEvent (this.NAME + "_expire", 0.0, null, this.EXPIRE_EVENT_ACTION);
  }

  private SimEventList eventList = null;

  private final SimEventAction EXPIRE_EVENT_ACTION = new SimEventAction ()
  {

    @Override
    public void action (SimEvent event)
    {
      AbstractSimTimer.this.eventList = null;
      AbstractSimTimer.this.expireAction (event.getTime ());
    }

  };

  /** Abstract method that is invoked upon expiration of the timer.
   *
   * @param time The current time at expiration.
   *
   */
  public abstract void expireAction (double time);

  /** Method that is invoked upon scheduling the timer.
   *
   * The default implementation does nothing.
   *
   * @param time The current time when scheduling.
   *
   */
  public void scheduleAction (double time)
  {
  }

  /** Method that is invoked upon canceling the timer.
   *
   * The default implementation does nothing.
   * Note that this method is only invoked if the timer was actually
   * scheduled on a {@link SimEventList} when {@link #cancel} was invoked.
   *
   * @param time The current time when canceling.
   *
   */
  public void cancelAction (double time)
  {
  }

  /** Schedule this timer on an event list.
   *
   * Invokes {@link #scheduleAction} once the appropriate event is scheduled on the event lList.
   *
   * <p>Note that this method take the current time from the event list to which it adds a delay
   * in order to schedule the expiration event.
   * Especially in between event-list runs, the current time may be {@link Double#NEGATIVE_INFINITY},
   * leading to indefinite rescheduling of the times at negative infinite.
   *
   * @param delay The delay until expiration.
   * @param eventList The event list.
   *
   * @throws IllegalArgumentException If delay is negative or infinite (positive or negative),
   *                                    the eventList is null,
   *                                    or the current time on the event list is negative or positive infinity.
   * @throws RuntimeException If the timer is already scheduled.
   */
  public void schedule (double delay, SimEventList eventList)
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

  /** Cancel a pending timer.
   *
   * Ignored if this timer is not scheduled.
   * Invokes {@link #cancelAction} after removal of the appropriate event on the event list
   * (and not if the timer was previously unscheduled).
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

}
