package nl.jdj.jsimulation.v1.jobqueue;

import nl.jdj.jsimulation.v1.SimEventAction;

/** A queue has one or more waiting lines for {@link SimJob}s
 *  and zero or more servers to serve them.
 *
 */
public interface SimQueue
{

  public void arrive (SimJob job, double time);

  public boolean revoke (SimJob job, double time, boolean interruptService);

  public void addDepartureAction (SimEventAction action);

  public void removeDepartureAction (SimEventAction action);

}
