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

import java.util.Comparator;

/** A default {@link Comparator} on {@link SimEvent}s.
 * 
 * <p>
 * This comparator extends the partial ordering of {@link SimEvent}s using their time property
 * to a total ordering using, in case of a tie in the event times, the de-conflict field of the event.
 * 
 * <p>
 * <b>Last javadoc Review:</b> Jan de Jongh, TNO, 20180404, r5.1.0.
 * 
 * @param <E> The type of {@link SimEvent}s supported.
 * 
 * @see SimEvent#getSimEventListDeconflictValue
 * 
 */
public class DefaultSimEventComparator<E extends SimEvent> implements Comparator<E>
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // Comparator
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  @Override
  public int compare (final E e1, final E e2)
  {
    // XXX What if e1 == null || e2 == null?
    int c = Double.compare (e1.getTime (), e2.getTime ());
    if (c == 0)
      c = Long.compare (e1.getSimEventListDeconflictValue (), e2.getSimEventListDeconflictValue ());
      // c = e1.getSimEventListDeconflictValue ().compareTo (e2.getSimEventListDeconflictValue ());
    if ((e1 == e2 && c != 0) || (e1 != e2 && c == 0))
      throw new RuntimeException ("Error attempting to order events.");
    return c;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
