package nl.jdj.jsimulation.r4;

import java.util.Random;

/** A (timed) event to be used in a {@link SimEventList}.
 * 
 * A {@link SimEvent} maintains its own scheduled time on a {@link SimEventList},
 * has a name an an associated {@link SimEventAction}, and an optional user object.
 * The name and the user object are not changed by the {@link nl.jdj.jsimulation} packages, and may be <code>null</code>.
 * If two {@link SimEvent}s are scheduled on a {@link SimEventList} with identical times, their order of execution is undetermined
 * (in this implementation, their order of execution is random with equal probabilities).
 * In particular, implementations should not assume insertion order of execution for events with identical times.
 * 
 * <p> A {@link SimEvent} is not thread-safe.
 * 
 * @param <T> The type of the user object.
 * 
 */
public class SimEvent<T>
{

  private String name;

  /** Gets the name of this {@link SimEvent}.
   * 
   * The name may be <code>null</code>.
   * It is not used by the {@link nl.jdj.jsimulation} package, except for logging purposes.
   * 
   * @return The name of the event (may be <code>null</code>).
   * 
   */
  public final String getName ()
  {
    return this.name;
  }

  /** Sets the name of this {@link SimEvent}.
   * 
   * The name may be <code>null</code>.
   * It is not used by the {@link nl.jdj.jsimulation} package, except for logging purposes.
   * 
   * @param name The new name of the event (may be <code>null</code>).
   * 
   */
  public final void setName (final String name)
  {
    this.name = name;
  }

  private double time;

  /** Returns the time at which this {@link SimEvent} is (to be) scheduled on a {@link SimEventList}.
   * 
   * The time property should not be changed when the event is currently scheduled on a {@link SimEventList}.
   * 
   * @return The time at which this {@link SimEvent} is (to be) scheduled on a {@link SimEventList}.
   * 
   */
  public final double getTime ()
  {
    return this.time;
  }

  /** Sets the time at which this {@link SimEvent} is to be scheduled on a {@link SimEventList}.
   * 
   * The time property should not be changed when the event is currently scheduled on a {@link SimEventList}.
   * 
   * @param time The new time at which this {@link SimEvent} is to be scheduled on a {@link SimEventList}.
   */
  public final void setTime (final double time)
  {
    this.time = time;
    // XXX Why is this necessary??
    this.deconflict = SimEvent.deconflicter.nextLong ();
  }

  /** The value used for solving event-list collisions.
   * 
   * The event with the smallest de-conflict property value is scheduled first.
   * The de-conflict property should be unique among {@link SimEvent}s in a single {@link SimEventList}.
   * 
   * @see DefaultSimEventComparator
   * 
   */
  protected Long deconflict;

  private static Random deconflicter = new Random ();

  /** Sets the seed for the pseudo-random sequence for de-conflicting event-list collisions.
   * 
   * An event-list collision occurs when two {@link SimEvent}s are scheduled at the same time in a {@link SimEventList}.
   * 
   * @param seed The new seed.
   * 
   */
  public static void setDeconflicterSeed (final long seed)
  {
    SimEvent.deconflicter.setSeed (seed);
  }

  private T object;

  /** Returns the user object associated with this event.
   * 
   * The user object need not be present (may be <code>null</code> and is never used or changed by the {@link nl.jdj.jsimulation}
   * package.
   * Also, multiple {@link SimEvent}s may share a single user object.
   * Note, however, that the user object (property) should not be changed when the {@link SimEvent} is scheduled on
   * a {@link SimEventList}.
   * 
   * @return The user object associated with this event.
   * 
   */
  public final T getObject ()
  {
    return this.object;
  }

  /** Sets the user object associated with this event.
   * 
   * The user object need not be present (may be <code>null</code> and is never used or changed by the {@link nl.jdj.jsimulation}
   * package.
   * Also, multiple {@link SimEvent}s may share a single user object.
   * Note, however, that the user object (property) should not be changed when the {@link SimEvent} is scheduled on
   * a {@link SimEventList}.
   * 
   * @param object The new user object (not checked).
   * 
   */
  public final void setObject (final T object)
  {
    this.object = object;
  }

  private SimEventAction eventAction;

  /** Returns the {@link SimEventAction} associated with this event.
   * 
   * The action is invoked when the event is executed on the {@link SimEventList}.
   * The event action (property) should not be changed when the {@link SimEvent} is scheduled on
   * a {@link SimEventList}.
   * 
   * @return The {@link SimEventAction} (may be <code>null</code>) associated with this event.
   * 
   */
  public final SimEventAction getEventAction ()
  {
    return this.eventAction;
  }

  /** Sets the {@link SimEventAction} associated with this event.
   * 
   * The action is invoked when the event is executed on the {@link SimEventList}.
   * The event action (property) should not be changed when the {@link SimEvent} is scheduled on
   * a {@link SimEventList}.
   * 
   * @param eventAction The new {@link SimEventAction} (may be <code>null</code>) associated with this event.
   * 
   */
  public final void setEventAction (final SimEventAction eventAction)
  {
    this.eventAction = eventAction;
  }

  /** Creates a new {@link SimEvent} with given parameters.
   * 
   * @param name The name of the event.
   * @param time The time at which the event is to be scheduled on a {@link SimEventList}.
   * @param object The associated user object (may be <code>null</code>).
   * @param eventAction The associated event action (may be <code>null</code>).
   * 
   */
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

  /** Creates a new {@link SimEvent} with given parameters and name \"No Name\".
   * 
   * @param time The time at which the event is to be scheduled on a {@link SimEventList}.
   * @param object The associated user object (may be <code>null</code>).
   * @param eventAction The associated event action (may be <code>null</code>).
   * 
   */
  public SimEvent
    (final double time,
    final T object,
    final SimEventAction eventAction)
  {
    this ("No Name", time, object, eventAction);
  }

  /** Creates a new {@link SimEvent} name \"No Name\", time at negative infinity and <code>null</code> object and action.
   * 
   */
  public SimEvent ()
  {
    this (Double.NEGATIVE_INFINITY, null, null);
  }

}