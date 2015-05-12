package nl.jdj.jsimulation.r2;

/** A general-purpose timer.
 * 
 * This abstract timer class hides the details of scheduling a {@link SimEvent} with
 * an appropriate {@link SimEventAction} on a {@link SimEventList} and
 * instead uses a callback method {@link #expireAction} to
 * notify concrete subclasses of timer expiration.
 * Concrete subclasses only have to override {@link #expireAction}.
 * The timer is scheduled on a {@link SimEventList} through
 * {@link #schedule}, passing the (positive or zero) delay.
 * Upon scheduling, the override-able {@link #scheduleAction} is invoked
 * (the default implementation does nothing).
 * Canceling a pending timer event is supported through {@link #cancel}.
 * Canceling a timer that is not currently scheduled has no effect.
 * Upon cancellation, the override-able {@link #cancelAction} is invoked
 * (the default implementation does nothing).
 * A timer can only be scheduled on a single {@link SimEventList} at a time,
 * but once expired or canceled, the timer can be rescheduled, even on a different
 * {@link SimEventList}.
 * An attempt to schedule an already scheduled timer will result in a {@link RuntimeException}.
 * 
 */
public abstract class SimTimer
{

  public final String NAME;
  
  public SimTimer (String name)
  {
    this.NAME = (name == null) ? "" : name;
  }
  
  private SimEventList eventList = null;
  
  private final SimEventAction EXPIRE_EVENT_ACTION = new SimEventAction ()
  {

    @Override
    public void action (SimEvent event)
    {
      SimTimer.this.eventList = null;
      SimTimer.this.expireAction (event.getTime ());
    }

  };
  
  private final SimEvent EXPIRE_EVENT
    = new SimEvent (this.NAME + "_expire", 0.0, null, this.EXPIRE_EVENT_ACTION);
  
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
   * @param delay The delay until expiration.
   * @param eventList The event list.
   * 
   * @throws IllegalArgumentException If delay is negative or eventList is null.
   * @throws RuntimeException If the timer is already scheduled.
   */
  public void schedule (double delay, SimEventList eventList)
  {
    if (delay < 0.0 || eventList == null) throw new IllegalArgumentException ();
    if (this.eventList != null) throw new RuntimeException ("Timer already scheduled!");
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
