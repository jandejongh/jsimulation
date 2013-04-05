package nl.jdj.jsimulation.v1;

/** A general-purpose timer.
 * 
 * This abstract timer class hides the details of scheduling a {@link SimEvent} with
 * an appropriate {@link SimAction} on a {@link SimEventQueue} and
 * instead uses a callback method {@link #expireAction} to
 * notify concrete subclasses of timer expiration.
 * Concrete subclasses only have to override {@link #expireAction}.
 * The timer is scheduled on a {@link SimEventQueue} through
 * {@link #schedule}, passing the (positive or zero) delay.
 * Upon scheduling, the override-able {@link scheduleAction} is invoked
 * (the default implementation does nothing).
 * Canceling a pending timer event is supported through {@link #cancel}.
 * Canceling a timer that is not currently scheduled has no effect.
 * Upon cancellation, the override-able {@link #cancelAction} is invoked
 * (the default implementation does nothing).
 * A timer can only be scheduled on a single {@link SimEventQueue} at a time,
 * but once expired or canceled, the timer can be rescheduled, even on a different
 * {@link SimEventQueue}.
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
  
  private SimEventQueue queue = null;
  
  private final SimEvent EXPIRE_EVENT
    = new SimEvent (this.NAME + "_expire", 0.0, null, this.EXPIRE_EVENT_ACTION);
  
  private final SimEventAction EXPIRE_EVENT_ACTION = new SimEventAction ()
  {

    @Override
    public void action (SimEvent event)
    {
      SimTimer.this.queue = null;
      SimTimer.this.expireAction (event.getTime ());
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
   * scheduled on a {@link SimEventQueue>} when {@link #cancel} was invoked.
   * 
   * @param time The current time when canceling.
   * 
   */
  public void cancelAction (double time)
  {
  }
  
  /** Schedule this timer on an event queue.
   * 
   * Invokes {@link scheduleAction} once the appropriate event is scheduled on the event queue.
   * 
   * @param delay The delay until expiration.
   * @param queue The event queue.\
   * 
   * @throws IllegalArgumementException If delay is negative or queue is null.
   * @throws RuntimeException If the timer is already scheduled.
   */
  public void schedule (double delay, SimEventQueue queue)
  {
    if (delay < 0.0 || queue == null) throw new IllegalArgumentException ();
    if (this.queue != null) throw new RuntimeException ("Timer already scheduled!");
    this.queue = queue;
    this.EXPIRE_EVENT.setTime (this.queue.getTime () + delay);
    this.queue.add (this.EXPIRE_EVENT);
    scheduleAction (this.queue.getTime ());
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
    if (this.queue != null)
    {
      final double time = this.queue.getTime ();
      this.queue.remove (this.EXPIRE_EVENT);
      this.queue = null;
      cancelAction (time);
    }
  }
  
}
