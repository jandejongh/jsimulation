package nl.jdj.jsimulation.r5;

/** The IOEL (Insertion-Order Event List) variant of {@link AbstractSimEventList}.
 * 
 * <p>
 * In IOEL, events that have the same time, are processed in insertion order!
 * 
 * <p>
 * The implementation is not thread-safe.
 * An event list is really meant to be processed and operated upon by a single thread only.
 * 
 * @param <E> The type of {@link SimEvent}s supported.
 * 
 */
public final class DefaultSimEventList_IOEL<E extends SimEvent>
extends AbstractSimEventList<E>
{

  /** Creates a new {@link DefaultSimEventList_IOEL} for plain {@link SimEvent}s.
   * 
   */
  public DefaultSimEventList_IOEL ()
  {
    this ((Class<E>) SimEvent.class);
  }
  
  /** Creates a new {@link DefaultSimEventList_IOEL}.
   * 
   * @param eventClass The base class {@link SimEvent}s supported.
   * 
   */
  public DefaultSimEventList_IOEL (final Class<E> eventClass)
  {
    super (new DefaultSimEventComparator (), eventClass);
    this.deconflict_IOEL = Long.MIN_VALUE;
  }
  
  private long deconflict_IOEL = Long.MIN_VALUE;

  /** Overridden in order to set the <i>deconflict</i> value on the event added.
   * 
   * <p>
   * This implementation uses an internal increasing counter to set the
   * so-called <i>deconflict</i> value on a {@link SimEvent} when it is added
   * to the event list (IOEL).
   * Hence, as long as the (long) counter does not roll over,
   * simultaneous events are processed in insertion-order.
   * Note that the counter is reset in this method
   * when the list is empty upon entry.
   * 
   * @see SimEvent#setSimEventListDeconflictValue
   * 
   */
  @Override
  public boolean add (final E e)
  {
    if (e == null)
      throw new NullPointerException ("Attempt to add null event to event list!");
    if (! contains (e))
    {
      if (super.isEmpty ())
        this.deconflict_IOEL = Long.MIN_VALUE;
      this.deconflict_IOEL++;
      e.setSimEventListDeconflictValue (this.deconflict_IOEL);
      return super.add (e);
    }
    return false;
  }

}
