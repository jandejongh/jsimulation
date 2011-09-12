package nl.jdj.jsimulation.v1;

import java.util.Comparator;
import java.util.TreeSet;

/**
 *
 */
public class SimEventQueue
  extends TreeSet<SimEvent>
{

  public SimEventQueue ()
  {
    this (new DefaultSimEventComparator ());
  }

  public SimEventQueue (Comparator comparator)
  {
    super (comparator);
  }

}
