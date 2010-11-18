package nl.jdj.jsimulation.v1;

import java.util.Random;

/**
 *
 */
public class SimEvent<T>
{

  private String name;

  public final String getName ()
  {
    return this.name;
  }

  public final void setName (final String name)
  {
    this.name = name;
  }

  private double time;

  public final double getTime ()
  {
    return this.time;
  }

  public final void setTime (final double time)
  {
    this.time = time;
    deconflict = SimEvent.deconflicter.nextLong ();
  }

  protected Long deconflict;

  private static Random deconflicter = new Random ();

  public static void setDeconflicterSeed (final long seed)
  {
    SimEvent.deconflicter.setSeed (seed);
  }

  private T object;

  public final T getObject ()
  {
    return this.object;
  }

  public final void setObject (final T object)
  {
    this.object = object;
  }

  private SimEventAction eventAction;

  public final SimEventAction getEventAction ()
  {
    return this.eventAction;
  }

  public final void setEventAction (final SimEventAction eventAction)
  {
    this.eventAction = eventAction;
  }

  public SimEvent
    (final String name,
    final double time,
    final T object,
    final SimEventAction eventAction)
  {
    this.name = name;
    this.time = time;
    this.deconflict = SimEvent.deconflicter.nextLong ();
    this.object = object;
    this.eventAction = eventAction;
  }

  public SimEvent
    (final double time,
    final T object,
    final SimEventAction eventAction)
  {
    this ("No Name", time, object, eventAction);
  }

}