/*
 * Copyright 2010-2018 Jan de Jongh, TNO.
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
package nl.jdj.jsimulation.r5;

import java.util.Random;
import java.util.Set;

/** The ROEL (Random-Order Event List) variant of {@link AbstractSimEventList}.
 * 
 * <p>
 * In ROEL, events that have the same time, are processed in random order!
 * This event list does not maintain insertion order!
 * 
 * <p>
 * The implementation is not thread-safe.
 * An event list is really meant to be processed and operated upon by a single thread only.
 * 
 * <p>
 * <b>Last javadoc Review:</b> Jan de Jongh, TNO, 20180404, r5.1.0.
 * 
 * @param <E> The type of {@link SimEvent}s supported.
 * 
 */
public class DefaultSimEventList_ROEL<E extends SimEvent>
extends AbstractSimEventList<E>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERIALIZATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final long serialVersionUID = 1135083343524220732L;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Creates a new {@link DefaultSimEventList_ROEL} (main constructor).
   * 
   * @param defaultResetTime The default reset time.
   * @param eventClass       The base class {@link SimEvent}s supported.
   * 
   */
  public DefaultSimEventList_ROEL (final double defaultResetTime, final Class<E> eventClass)
  {
    super (new DefaultSimEventComparator (), defaultResetTime, eventClass);
  }

  /** Creates a new {@link DefaultSimEventList_ROEL} for {@link DefaultSimEvent}s.
   * 
   * @param defaultResetTime The default reset time.
   * 
   */
  public DefaultSimEventList_ROEL (final double defaultResetTime)
  {
    this (defaultResetTime, (Class<E>) DefaultSimEvent.class);
  }
  
  /** Creates a new {@link DefaultSimEventList_ROEL}.
   * 
   * @param eventClass The base class {@link SimEvent}s supported.
   * 
   * <p>
   * The default reset time is set to {@link Double#NEGATIVE_INFINITY}.
   * 
   */
  public DefaultSimEventList_ROEL (final Class<E> eventClass)
  {
    this (Double.NEGATIVE_INFINITY, eventClass);
  }

  /** Creates a new {@link DefaultSimEventList_ROEL} for {@link DefaultSimEvent}s.
   * 
   * <p>
   * The default reset time is set to {@link Double#NEGATIVE_INFINITY}.
   * 
   */
  public DefaultSimEventList_ROEL ()
  {
    this (Double.NEGATIVE_INFINITY, (Class<E>) DefaultSimEvent.class);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DECONFLICT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  private final Random rngDeconflict_ROEL = new Random ();

  /** Sets the seed for the pseudo-random sequence for de-conflicting event-list collisions.
   * 
   * <p>
   * An event-list collision occurs when two {@link SimEvent}s
   * are scheduled at the same time in a {@link SimEventList}.
   * 
   * <p>
   * This implementation uses an internal {@link Random} object to set the
   * so-called <i>deconflict</i> value on a {@link SimEvent} when it is added
   * to the event list (default ROEL).
   * 
   * @param seed The new seed.
   * 
   */
  public final void setDeconflicterSeed_ROEL (final long seed)
  {
    this.rngDeconflict_ROEL.setSeed (seed);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Set / TreeSet
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Overridden in order to set the <i>deconflict</i> value on the event added.
   * 
   * <p>
   * This implementation uses an internal {@link Random} object to set the
   * so-called <i>deconflict</i> value on a {@link SimEvent} when it is added
   * to the event list (default ROEL).
   * 
   * @param e See {@link Set#add}.
   * 
   * @return See {@link Set#add}.
   * 
   * @see SimEvent#setSimEventListDeconflictValue
   * 
   */
  @Override
  public final boolean add (final E e)
  {
    if (e == null)
      throw new NullPointerException ("Attempt to add null event to event list!");
    if (! contains (e))
    {
      e.setSimEventListDeconflictValue (this.rngDeconflict_ROEL.nextLong ());
      return super.add (e);
    }
    return false;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
