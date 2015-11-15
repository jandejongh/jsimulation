package nl.jdj.jsimulation.r5;

import java.util.Random;

/** The ROEL (Random-Order Event List) variant of {@link AbstractSimEventList}.
 * 
 * <p>
 * In ROEL, events that have the same time, are processed in random order!
 * This event list does not maintain insertion order!
 * 
 * <p>
 * The implementation is not thread-safe.
 * An event list is really meant to be processed and operated upon by a single thread only.
 * 
 * @param <E> The type of {@link SimEvent}s supported.
 * 
 */
public class DefaultSimEventList_ROEL<E extends SimEvent>
extends AbstractSimEventList<E>
{

  /** Creates a new {@link DefaultSimEventList_ROEL} for plain {@link SimEvent}s.
   * 
   */
  public DefaultSimEventList_ROEL ()
  {
    this ((Class<E>) SimEvent.class);
  }
  
  /** Creates a new {@link DefaultSimEventList_ROEL}.
   * 
   * @param eventClass The base class {@link SimEvent}s supported.
   * 
   */
  public DefaultSimEventList_ROEL (final Class<E> eventClass)
  {
    super (new DefaultSimEventComparator (), eventClass);
  }

  private final Random rngDeconflict_ROEL = new Random ();

  /** Sets the seed for the pseudo-random sequence for de-conflicting event-list collisions.
   * 
   * <p>
   * An event-list collision occurs when two {@link SimEvent}s
   * are scheduled at the same time in a {@link SimEventList}.
   * 
   * <p>
   * This implementation uses an internal {@link Random} object to set the
   * so-called <i>deconflict</i> value on a {@link SimEvent} when it is added
   * to the event list (default ROEL).
   * 
   * @param seed The new seed.
   * 
   */
  public final void setDeconflicterSeed_ROEL (final long seed)
  {
    this.rngDeconflict_ROEL.setSeed (seed);
  }

  /** Overridden in order to set the <i>deconflict</i> value on the event added.
   * 
   * <p>
   * This implementation uses an internal {@link Random} object to set the
   * so-called <i>deconflict</i> value on a {@link SimEvent} when it is added
   * to the event list (default ROEL).
   * 
   * @see SimEvent#setSimEventListDeconflictValue
   * 
   */
  @Override
  public final boolean add (final E e)
  {
    if (e == null)
      throw new NullPointerException ("Attempt to add null event to event list!");
    if (! contains (e))
    {
      e.setSimEventListDeconflictValue (this.rngDeconflict_ROEL.nextLong ());
      return super.add (e);
    }
    return false;
  }

}
