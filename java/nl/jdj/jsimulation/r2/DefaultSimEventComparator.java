package nl.jdj.jsimulation.r2;

import java.util.Comparator;

public class DefaultSimEventComparator implements Comparator<SimEvent>
{

  @Override
  public int compare (SimEvent e1, SimEvent e2)
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
