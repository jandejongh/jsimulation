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

/** A default implementation of {@link SimEventList}.
 * 
 * <p>
 * Note that in this implementation, events that have the same time, are processed in random order (ROEL)!
 * This event list does not maintain insertion order!
 * 
 * <p>
 * This class is merely an alias for {@link DefaultSimEventList_ROEL}.
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
public final class DefaultSimEventList<E extends SimEvent>
extends DefaultSimEventList_ROEL<E>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERIALIZATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final long serialVersionUID = 1102486475440954600L;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Creates a new {@link DefaultSimEventList} (main constructor).
   * 
   * <p>
   * The base class for {@link SimEvent}s supported (<code>E</code>) must feature a constructor with no arguments.
   * 
   * @param defaultResetTime The default reset time.
   * @param eventClass       The base class {@link SimEvent}s supported.
   * 
   */
  public DefaultSimEventList (final double defaultResetTime, final Class<E> eventClass)
  {
    super (defaultResetTime, eventClass);
  }

  /** Creates a new {@link DefaultSimEventList} for {@link DefaultSimEvent}s.
   * 
   * @param defaultResetTime The default reset time.
   * 
   */
  public DefaultSimEventList (final double defaultResetTime)
  {
    this (defaultResetTime, (Class<E>) DefaultSimEvent.class);
  }
  
  /** Creates a new {@link DefaultSimEventList}.
   * 
   * @param eventClass The base class {@link SimEvent}s supported.
   * 
   * <p>
   * The default reset time is set to {@link Double#NEGATIVE_INFINITY}.
   * 
   */
  public DefaultSimEventList (final Class<E> eventClass)
  {
    this (Double.NEGATIVE_INFINITY, eventClass);
  }

  /** Creates a new {@link DefaultSimEventList} for {@link DefaultSimEvent}s.
   * 
   * <p>
   * The default reset time is set to {@link Double#NEGATIVE_INFINITY}.
   * 
   */
  public DefaultSimEventList ()
  {
    this (Double.NEGATIVE_INFINITY, (Class<E>) DefaultSimEvent.class);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
