package nl.jdj.jsimulation.r3;

/** An object capable of listening to important changes in a {@link SimEventList}.
 * 
 * The listener is notified
 * <ul>
 *   <li>when the list is reset,
 *   <li>when an update occurs during list processing (a strictly positive jump in time), and,
 *   <li>when the list is empty.
 * </ul>
 * Note that the first event can only be issued when the event is not being processed.
 *
 */
public interface SimEventListListener
{

  /** Notification of the event list being reset.
   * 
   * @param eventList The event list.
   * 
   */
  public void notifyEventListReset (SimEventList eventList);
  
  /** Notification of an update in the event list while processing it.
   * 
   * @param eventList The event list.
   * @param time The new time.
   * 
   */
  public void notifyEventListUpdate (SimEventList eventList, double time);
  
  /** Notification of the event list becoming empty (end of simulation).
   * 
   * @param eventList The event list.
   * @param time The time of the last event.
   * 
   */
  public void notifyEventListEmpty (SimEventList eventList, double time);
  
  /** A listener that also wants to be notified of individual events processing by the event list.
   * 
   * This is mostly useful for debugging.
   * 
   */
  public interface Fine extends SimEventListListener
  {

    /** Notification that the event list's {@link SimEventList#run} method (or another processor) is about to remove the next
     * event from the list.
     * 
     * @param eventList The event list.
     * @param time The time of the previous event (i.e., the event just processed).
     * 
     * @see SimEventList#addListener
     * 
     */
    public void notifyNextEvent (SimEventList eventList, double time);

  }
  
}