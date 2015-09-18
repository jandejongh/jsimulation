package nl.jdj.jsimulation.r4;

/** An action provider for {@link SimEvent}s.
 * 
 * In the interface main method, {@link #action}, a {@link SimEvent} is passed.
 * As a result, a single {@link SimEventAction} interface can be used for multiple {@link SimEvent}s.
 * 
 * @param <T> The type of the user object of the {@link SimEvent}.
 * 
 */
public interface SimEventAction<T>
{

  /** Invoke the action for supplied {@link SimEvent}.
   *
   * @param event The event.
   *
   * @throws IllegalArgumentException If <code>event</code> is <code>null</code>.
   */
  public void action (SimEvent<T> event);

}
