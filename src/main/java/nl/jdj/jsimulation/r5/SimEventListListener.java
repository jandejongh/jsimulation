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

/** An object capable of listening to important changes in a {@link SimEventList}.
 * 
 * <p>
 * The listener is notified
 * <ul>
 *   <li>when the list is reset (see {@link SimEventListResetListener}),
 *   <li>when an update occurs during list processing (a strictly positive jump in time), and,
 *   <li>when the list is empty.
 * </ul>
 * Note that the first notification can only be issued when the event is not being processed.
 * 
 * <p>
 * If, in addition, you want to be notified of processing individual events in sequence,
 * implement {@link Fine}.
 *
 * <p>
 * <b>Last javadoc Review:</b> Jan de Jongh, TNO, 20180404, r5.1.0.
 * 
 */
public interface SimEventListListener
extends SimEventListResetListener
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NOTIFY UPDATE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Notification of an update in the event list while processing it.
   * 
   * @param eventList The event list.
   * @param time The new time.
   * 
   */
  public void notifyEventListUpdate (SimEventList eventList, double time);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // NOTIFY EMPTY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Notification of the event list becoming empty (end of simulation).
   * 
   * @param eventList The event list.
   * @param time The time of the last event.
   * 
   */
  public void notifyEventListEmpty (SimEventList eventList, double time);
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SimEventListListener.Fine
  // NOTIFY NEXT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** A listener that also wants to be notified of individual events processing by the event list.
   * 
   * This is mostly useful for debugging.
   * 
   */
  public interface Fine extends SimEventListListener
  {

    /** Notification that the event list's {@link SimEventList#run} method (or another processor) is about to remove the next
     * event from the list.
     * 
     * @param eventList The event list.
     * @param time The time of the previous event (i.e., the event just processed).
     * 
     * @see SimEventList#addListener
     * 
     */
    public void notifyNextEvent (SimEventList eventList, double time);

  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
