package nl.jdj.jsimulation.r5;

/** A default {@link SimEventFactory} for {@link SimEvent}s
 *
 * <p>
 * The factory generates {@link DefaultSimEvent}s.
 * 
 */
public class DefaultSimEventFactory
implements SimEventFactory<SimEvent>
{

  /** Returns a new {@link DefaultSimEvent}.
   * 
   * @return A new {@link DefaultSimEvent}.
   * 
   */
  @Override
  public SimEvent newInstance (final String name, final double time, final SimEventAction eventAction)
  {
    return new DefaultSimEvent (name, time, null, eventAction);
  }
  
}
