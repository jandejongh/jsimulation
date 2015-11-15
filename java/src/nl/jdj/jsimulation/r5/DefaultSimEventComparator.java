package nl.jdj.jsimulation.r5;

import java.util.Comparator;

/**  A default {@link Comparator} on {@link SimEvent}s.
 * 
 * This comparator extends the partial ordering of {@link SimEvent}s using their time property
 * to a total ordering using, in case of a tie in the event times, the de-conflict field of the event.
 * 
 * @param <E> The type of {@link SimEvent}s supported.
 * 
 * @see SimEvent#getSimEventListDeconflictValue
 * 
 */
public class DefaultSimEventComparator<E extends SimEvent> implements Comparator<E>
{

  @Override
  public int compare (E e1, E e2)
  {
    int c = Double.compare (e1.getTime (), e2.getTime ());
    if (c == 0)
    {
      c = e1.getSimEventListDeconflictValue ().compareTo (e2.getSimEventListDeconflictValue ());
    }
    if ((e1 == e2 && c != 0) || (e1 != e2 && c == 0))
      throw new RuntimeException ("Error attempting to order events.");
    return c;
  }

}
