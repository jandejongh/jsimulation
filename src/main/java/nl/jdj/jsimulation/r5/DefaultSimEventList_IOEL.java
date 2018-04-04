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

import java.util.Set;

/** The IOEL (Insertion-Order Event List) variant of {@link AbstractSimEventList}.
 * 
 * <p>
 * In IOEL, events that have the same time, are processed in insertion order!
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
public final class DefaultSimEventList_IOEL<E extends SimEvent>
extends AbstractSimEventList<E>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERIALIZATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final long serialVersionUID = 7744885373343754672L;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Creates a new {@link DefaultSimEventList_IOEL} (main constructor).
   * 
   * @param defaultResetTime The default reset time.
   * @param eventClass       The base class {@link SimEvent}s supported.
   * 
   */
  public DefaultSimEventList_IOEL (final double defaultResetTime, final Class<E> eventClass)
  {
    super (new DefaultSimEventComparator (), defaultResetTime, eventClass);
    this.deconflict_IOEL = Long.MIN_VALUE;
  }
  
  /** Creates a new {@link DefaultSimEventList_IOEL} for {@link DefaultSimEvent}s.
   * 
   * @param defaultResetTime The default reset time.
   * 
   */
  public DefaultSimEventList_IOEL (final double defaultResetTime)
  {
    this (defaultResetTime, (Class<E>) DefaultSimEvent.class);
  }
  
  /** Creates a new {@link DefaultSimEventList_IOEL}.
   * 
   * @param eventClass The base class {@link SimEvent}s supported.
   * 
   * <p>
   * The default reset time is set to {@link Double#NEGATIVE_INFINITY}.
   * 
   */
  public DefaultSimEventList_IOEL (final Class<E> eventClass)
  {
    this (Double.NEGATIVE_INFINITY, eventClass);
  }

  /** Creates a new {@link DefaultSimEventList_IOEL} for {@link DefaultSimEvent}s.
   * 
   * <p>
   * The default reset time is set to {@link Double#NEGATIVE_INFINITY}.
   * 
   */
  public DefaultSimEventList_IOEL ()
  {
    this (Double.NEGATIVE_INFINITY, (Class<E>) DefaultSimEvent.class);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DECONFLICT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  private long deconflict_IOEL = Long.MIN_VALUE;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Set / TreeSet
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Overridden in order to set the <i>deconflict</i> value on the event added.
   * 
   * <p>
   * This implementation uses an internal increasing counter to set the
   * so-called <i>deconflict</i> value on a {@link SimEvent} when it is added
   * to the event list (IOEL).
   * Hence, as long as the (long) counter does not roll over,
   * simultaneous events are processed in insertion-order.
   * Note that the counter is reset in this method
   * when the list is empty upon entry.
   * 
   * @param e See {@link Set#add}.
   * 
   * @return See {@link Set#add}.
   * 
   * @see SimEvent#setSimEventListDeconflictValue
   * 
   */
  @Override
  public boolean add (final E e)
  {
    if (e == null)
      throw new NullPointerException ("Attempt to add null event to event list!");
    if (! contains (e))
    {
      if (super.isEmpty ())
        this.deconflict_IOEL = Long.MIN_VALUE;
      this.deconflict_IOEL++;
      e.setSimEventListDeconflictValue (this.deconflict_IOEL);
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
