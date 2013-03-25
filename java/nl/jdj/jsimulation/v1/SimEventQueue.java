package nl.jdj.jsimulation.v1;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 */
public class SimEventQueue
  extends TreeSet<SimEvent>
{

  double lastUpdateTime = 0.0;
  boolean firstUpdate = true;
  
  private final Set<SimEventAction> updateListeners = new HashSet<SimEventAction> ();
  
  public final void addUpdateSimEventAction (SimEventAction a)
  {
    if (a == null) throw new IllegalArgumentException ();
    this.updateListeners.add (a);
  }
  
  public final void removeUpdateSimEventAction (SimEventAction a)
  {
    if (a == null) throw new IllegalArgumentException ();
    this.updateListeners.remove (a);
  }
  
  public SimEventQueue ()
  {
    this (new DefaultSimEventComparator ());
  }

  public SimEventQueue (Comparator comparator)
  {
    super (comparator);
  }

  public void checkUpdate (SimEvent e)
  {
    if (this.firstUpdate || e.getTime () > this.lastUpdateTime)
    {
      this.lastUpdateTime = e.getTime ();
      this.firstUpdate = false;
      for (SimEventAction a : this.updateListeners) a.action (e);
    }
  }
  
}
