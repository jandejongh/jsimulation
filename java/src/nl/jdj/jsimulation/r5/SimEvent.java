package nl.jdj.jsimulation.r5;

/** A (timed) event to be used in a {@link SimEventList}.
 * 
 * <p>
 * A {@link SimEvent} maintains its own scheduled time on a {@link SimEventList},
 * has a name an an associated {@link SimEventAction}, and an optional user object.
 * The name and the user object are not changed by the <code>jsimulation</code> packages, and may be <code>null</code>.
 * 
 * <p>
 * If two {@link SimEvent}s are scheduled on a {@link SimEventList} with identical times, their order of execution is
 * determined by the {@link SimEventList} implementation.
 * In particular, implementations should not assume insertion order of execution for events with identical times
 * <i>without</i> checking this feature for the particular event list.
 * 
 * @param <T> The type of the user object.
 * 
 */
public interface SimEvent<T>
{

  /** Returns the time at which this {@link SimEvent} is (to be) scheduled on a {@link SimEventList}.
   *
   * <p>
   * The time property should not be changed when the event is currently scheduled on a {@link SimEventList}.
   *
   * @return The time at which this {@link SimEvent} is (to be) scheduled on a {@link SimEventList}.
   *
   */
  double getTime ();

  /** Sets the time at which this {@link SimEvent} is to be scheduled on a {@link SimEventList}.
   *
   * <p>
   * The time property should not be changed when the event is currently scheduled on a {@link SimEventList}.
   *
   * @param time The new time at which this {@link SimEvent} is to be scheduled on a {@link SimEventList}.
   * 
   */
  void setTime (double time);
  
  /** Returns the value used for "de-conflicting" simultaneous events on a {@link SimEventList}.
   * 
   * <p>
   * This method is for internal use by a {@link SimEventList}.
   * Implementations must keep a private long (or Long) member to keep the property.
   * 
   * @return The value used for "de-conflicting" simultaneous events on a {@link SimEventList}.
   * 
   */
  Long getSimEventListDeconflictValue ();

  /** Sets the value used for "de-conflicting" simultaneous events on a {@link SimEventList}.
   * 
   * <p>
   * This method is for internal use by a {@link SimEventList}.
   * Implementations must keep a private long (or Long) member to keep the property.
   * 
   * @param simEventListDeconflictValue  The new value used for "de-conflicting" simultaneous events on a {@link SimEventList}.
   * 
   */
  void setSimEventListDeconflictValue (long simEventListDeconflictValue);

  /** Returns the {@link SimEventAction} associated with this event.
   *
   * <p>
   * The action is invoked when the event is executed on the {@link SimEventList}.
   * The event action (property) should not be changed when the {@link SimEvent} is scheduled on
   * a {@link SimEventList}.
   *
   * @return The {@link SimEventAction} (may be <code>null</code>) associated with this event.
   *
   */
  SimEventAction getEventAction ();

  /** Sets the {@link SimEventAction} associated with this event.
   *
   * <p>
   * The action is invoked when the event is executed on the {@link SimEventList}.
   * The event action (property) should not be changed when the {@link SimEvent} is scheduled on
   * a {@link SimEventList}.
   *
   * @param eventAction The new {@link SimEventAction} (may be <code>null</code>) associated with this event.
   *
   */
  void setEventAction (SimEventAction eventAction);

  /** Returns the user object associated with this event.
   *
   * <p>
   * The user object need not be present (may be <code>null</code> and is never used or changed by the
   * <code>nl.jdj.jsimulation}</code> package.
   * Also, multiple {@link SimEvent}s may share a single user object.
   * Note, however, that the user object (property) should not be changed when the {@link SimEvent} is scheduled on
   * a {@link SimEventList}.
   *
   * @return The user object associated with this event.
   *
   */
  T getObject ();

  /** Sets the user object associated with this event.
   *
   * <p>
   * The user object need not be present (may be <code>null</code> and is never used or changed
   * by the <code>jsimulation</code> package.
   * Also, multiple {@link SimEvent}s may share a single user object.
   * Note, however, that the user object (property) should not be changed when the {@link SimEvent} is scheduled on
   * a {@link SimEventList}.
   *
   * @param object The new user object (not checked).
   *
   */
  void setObject (T object);

  /** Gets the name of this {@link SimEvent}.
   *
   * <p>
   * The name may be <code>null</code>.
   * It is not used by the <code>jsimulation</code> package, except for logging purposes.
   *
   * @return The name of the event (may be <code>null</code>).
   *
   */
  String getName ();

  /** Sets the name of this {@link SimEvent}.
   *
   * <p>
   * The name may be <code>null</code>.
   * It is not used by the <code>jsimulation</code> package, except for logging purposes.
   *
   * @param name The new name of the event (may be <code>null</code>).
   *
   */
  void setName (String name);

}
