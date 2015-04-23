package nl.jdj.jsimulation.r2;

import java.util.Comparator;

/** A default {@link Comparator} on {SimEvent}s.
 * 
 * @param <E> The type of {@link SimEvent}s supported.
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
      c = e1.deconflict.compareTo (e2.deconflict);
    }
    assert c != 0;
    return c;
  }

}
