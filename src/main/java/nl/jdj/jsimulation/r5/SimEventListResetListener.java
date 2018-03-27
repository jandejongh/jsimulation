package nl.jdj.jsimulation.r5;

/** An object capable of listening to a reset of a {@link SimEventList}.
 * 
 * <p>
 * This interface is meant for objects that want to capture the reset events of an event list, but do not care about any other
 * events like updates.
 * 
 * @see SimEventListListener
 *
 */
public interface SimEventListResetListener
{

  /** Notification of the event list being reset.
   * 
   * @param eventList The event list.
   * 
   */
  public void notifyEventListReset (SimEventList eventList);
  
}
