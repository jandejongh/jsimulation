package nl.jdj.jsimulation.r5;

/** A default implementation of {@link SimEventList}.
 * 
 * <p>
 * Note that in this implementation, events that have the same time, are processed in random order (ROEL)!
 * This event list does not maintain insertion order!
 * 
 * <p>
 * This class is merely an alias for {@link DefaultSimEventList_ROEL}.
 * 
 * <p>
 * The implementation is not thread-safe.
 * An event list is really meant to be processed and operated upon by a single thread only.
 * 
 * @param <E> The type of {@link SimEvent}s supported.
 * 
 */
public class DefaultSimEventList<E extends SimEvent>
extends DefaultSimEventList_ROEL<E>
{

  /** Creates a new {@link DefaultSimEventList} for plain {@link DefaultSimEvent}s.
   * 
   */
  public DefaultSimEventList ()
  {
    this ((Class<E>) DefaultSimEvent.class);
  }
  
  /** Creates a new {@link DefaultSimEventList}.
   * 
   * The base class for {@link SimEvent}s supported (<code>E</code>) must feature a constructor with no arguments.
   * 
   * @param eventClass The base class {@link SimEvent}s supported.
   * 
   */
  public DefaultSimEventList (final Class<E> eventClass)
  {
    super (eventClass);
  }

}
