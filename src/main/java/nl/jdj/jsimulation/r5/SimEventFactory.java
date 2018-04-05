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
package nl.jdj.jsimulation.r5;

/** A factory for {@link SimEvent}s.
 *
 * <p>
 * <b>Last javadoc Review:</b> Jan de Jongh, TNO, 20180404, r5.1.0.
 * 
 * @param <E> The (base) type of {@link SimEvent}s generated.
 * 
 */
@FunctionalInterface
public interface SimEventFactory<E extends SimEvent>
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NEW INSTANCE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Creates a new {@link SimEvent} instance.
   * 
   * @param name        The name of the event.
   * @param time        The time at which the event is to be scheduled on a {@link SimEventList}.
   * @param eventAction The associated event action (may be <code>null</code>).
   *
   * @return The new {@link SimEvent} instance.
   * 
   */
  E newInstance (String name, double time, SimEventAction eventAction);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
