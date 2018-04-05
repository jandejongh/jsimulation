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

/** An object capable of listening to a reset of a {@link SimEventList}.
 * 
 * <p>
 * This interface is meant for objects that want to capture the reset events of an event list, but do not care about any other
 * events like updates.
 * 
 * <p>
 * <b>Last javadoc Review:</b> Jan de Jongh, TNO, 20180404, r5.1.0.
 * 
 * @see SimEventListListener
 *
 */
public interface SimEventListResetListener
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NOTIFY RESET
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Notification of the event list being reset.
   * 
   * @param eventList The event list.
   * 
   */
  public void notifyEventListReset (SimEventList eventList);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
