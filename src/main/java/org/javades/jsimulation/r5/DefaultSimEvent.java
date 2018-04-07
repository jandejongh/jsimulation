/* 
 * Copyright 2010-2018 Jan de Jongh <jfcmdejongh@gmail.com>, TNO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.javades.jsimulation.r5;

/** An implementation of a {@link SimEvent}.
 * 
 * <p>
 * A {@link DefaultSimEvent} is not thread-safe.
 * 
 * <p>
 * <b>Last javadoc Review:</b> Jan de Jongh, TNO, 20180404, r5.1.0.
 * 
 * @param <T> The type of the user object.
 * 
 * @see SimEvent
 * @see DefaultSimEventComparator
 * 
 */
public class DefaultSimEvent<T> implements SimEvent<T>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Creates a new {@link DefaultSimEvent} with given parameters.
   * 
   * @param name        The name of the event.
   * @param time        The time at which the event is to be scheduled on a {@link SimEventList}.
   * @param object      The associated user object (may be <code>null</code>).
   * @param eventAction The associated event action (may be <code>null</code>).
   * 
   */
  public DefaultSimEvent
    (final String name,
    final double time,
    final T object,
    final SimEventAction eventAction)
  {
    this.name = name;
    this.time = time;
    this.simEventListDeconflictValue = Long.MIN_VALUE;
    this.object = object;
    this.eventAction = eventAction;
  }

  /** Creates a new {@link DefaultSimEvent} with given parameters and name \"No Name\" (main constructor).
   * 
   * @param time        The time at which the event is to be scheduled on a {@link SimEventList}.
   * @param object      The associated user object (may be <code>null</code>).
   * @param eventAction The associated event action (may be <code>null</code>).
   * 
   */
  public DefaultSimEvent
    (final double time,
    final T object,
    final SimEventAction eventAction)
  {
    this ("No Name", time, object, eventAction);
  }

  /** Creates a new {@link DefaultSimEvent} named \"No Name\", given time and action and <code>null</code> object.
   * 
   * @param time        The time at which the event is to be scheduled on a {@link SimEventList}.
   * @param eventAction The associated event action (may be <code>null</code>).
   * 
   */
  public DefaultSimEvent
    (final double time,
    final SimEventAction eventAction)
  {
    this ("No Name", time, null, eventAction);
  }

  /** Creates a new {@link DefaultSimEvent} named \"No Name\", given time and <code>null</code> object and action.
   * 
   * @param time The time at which the event is to be scheduled on a {@link SimEventList}.
   * 
   */
  public DefaultSimEvent (final double time)
  {
    this ("No Name", time, null, null);
  }

  /** Creates a new {@link DefaultSimEvent} named \"No Name\", time at negative infinity and <code>null</code> object and action.
   * 
   */
  public DefaultSimEvent ()
  {
    this (Double.NEGATIVE_INFINITY, null, null);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private String name;

  @Override
  public final String getName ()
  {
    return this.name;
  }

  @Override
  public final void setName (final String name)
  {
    this.name = name;
  }

  /** Returns the registered name if non-<code>null</code>, else the super method return value.
   * 
   * @return The registered name if non-<code>null</code>, else the super method return value.
   *
   * @see #getName
   * 
   */
  @Override
  public String toString ()
  {
    if (this.name != null)
      return this.name;
    else
      return super.toString ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TIME
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private double time;

  @Override
  public final double getTime ()
  {
    return this.time;
  }

  @Override
  public final void setTime (final double time)
  {
    this.time = time;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EVENT-LIST DECONFLICT VALUE [XXX DO WE REALLY NEED THIS IN THE INTERFACE?]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** The value used for solving event-list collisions.
   * 
   * The event with the smallest de-conflict property value is scheduled first.
   * The de-conflict property should be unique among {@link SimEvent}s in a single {@link SimEventList}.
   * 
   * @see DefaultSimEventComparator
   * 
   */
  private long simEventListDeconflictValue = Long.MIN_VALUE;

  @Override
  public final long getSimEventListDeconflictValue ()
  {
    return this.simEventListDeconflictValue;
  }
  
  @Override
  public final void setSimEventListDeconflictValue (final long simEventListDeconflictValue)
  {
    this.simEventListDeconflictValue = simEventListDeconflictValue;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // [USER] OBJECT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private T object;

  @Override
  public final T getObject ()
  {
    return this.object;
  }

  @Override
  public final void setObject (final T object)
  {
    this.object = object;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EVENT ACTION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private SimEventAction eventAction;

  @Override
  public final SimEventAction getEventAction ()
  {
    return this.eventAction;
  }

  @Override
  public final void setEventAction (final SimEventAction eventAction)
  {
    this.eventAction = eventAction;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}