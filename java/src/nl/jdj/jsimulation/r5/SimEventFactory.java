package nl.jdj.jsimulation.r5;

/** A factory for {@link SimEvent}s.
 *
 * @param <E> The (base) type of {@link SimEvent}s generated.
 * 
 */
@FunctionalInterface
public interface SimEventFactory<E extends SimEvent>
{
  
  /** Creates a new {@link SimEvent} instance.
   * 
   * @param name        The name of the event.
   * @param time        The time at which the event is to be scheduled on a {@link SimEventList}.
   * @param eventAction The associated event action (may be <code>null</code>).
   *
   */
  E newInstance (String name, double time, SimEventAction eventAction);
  
}
