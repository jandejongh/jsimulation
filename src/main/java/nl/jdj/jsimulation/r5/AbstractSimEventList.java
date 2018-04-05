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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.logging.Logger;

/** A {@link TreeSet}-based implementation of {@link SimEventList}.
 * 
 * <p>
 * Note that in the default implementation, events that have the same time, are processed in random order!
 * 
 * <p>
 * The implementation is not thread-safe.
 * An event list is really meant to be processed and operated upon by a single thread only.
 * 
 * <p>
 * <b>Last javadoc Review:</b> Jan de Jongh, TNO, 20180402, r5.1.0.
 * 
 * @param <E> The type of {@link SimEvent}s supported.
 * 
 * @see SimEventList
 * @see DefaultSimEventList
 * @see DefaultSimEventList_ROEL
 * @see DefaultSimEventList_IOEL
 * 
 */
public abstract class AbstractSimEventList<E extends SimEvent>
extends TreeSet<E>
implements SimEventList<E>
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 
  private static final Logger LOG = Logger.getLogger (AbstractSimEventList.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SERIALIZATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final long serialVersionUID = 3944696628174847328L;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // USE ARRAY OPTIMIZATION [COMPILE-TIME SWITCH]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** When {@code true}, this class and possibly some sub-classes maintain various array copies of collections
   *  often iterated over, like listeners, and uses array iteration instead of Collection iteration.
   * 
   */
  protected final static boolean USE_ARRAY_OPTIMIZATION = true;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS / CLONING / FACTORIES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Creates a new (abstract) event list (main constructor).
   * 
   * <p>
   * Unless an event-factory is registered after construction,
   * the base class for {@link SimEvent}s supported (<code>E</code>)
   * must feature a constructor with no arguments,
   * see {@link #setSimEventFactory}.
   * 
   * @param comparator       The comparator for {@link SimEvent}s.
   * @param defaultResetTime The default reset time.
   * @param eventClass       The base class {@link SimEvent}s supported.
   * 
   * @throws IllegalArgumentException If <code>eventClass</code> is <code>null</code>.
   * 
   * @see TreeSet#TreeSet(java.util.Comparator)
   * 
   */
  protected AbstractSimEventList (final Comparator comparator, final double defaultResetTime, final Class<E> eventClass)
  {
    super (comparator);
    this.defaultResetTime = defaultResetTime;
    if (eventClass == null)
      throw new IllegalArgumentException ();
    this.eventClass = eventClass;
    resetFromContructor ();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private Function<SimEventList, String> toStringFunction = null;
  
  /** Gets the function that generates the string for {@link #toString}.
   * 
   * <p>
   * Note that this class turns {@link #toString} final.
   * 
   * <p>
   * If this method returns {@code null}, which is the default after construction,
   * the {@link #toString} method returns a compile-time default string.
   * 
   * @return The function that generates the string for {@link #toString}.
   * 
   * @see #toString
   * @see #setToStringFunction
   * 
   */
  public final Function<SimEventList, String> getToStringFunction ()
  {
    return this.toStringFunction;
  }
  
  /** Sets the function that generates the string for {@link #toString}.
   * 
   * <p>
   * Note that this class turns {@link #toString} final.
   *
   * <p>
   * If the function is set to {@code null}, the {@link #toString} method returns a compile-time default string.
   * 
   * @param toStringFunction The function returning the appropriate print string when {@code this} is supplied as argument.
   * 
   * @see #toString
   * @see #getToStringFunction
   * 
   */
  public final void setToStringFunction (final Function<SimEventList, String> toStringFunction)
  {
    this.toStringFunction = toStringFunction;
  }
  
  /** Returns a string representation of this event list (final, but customizable).
   * 
   * <p>
   * Since we intend to create one of more {@code final} sub-classes,
   * but still want end-users to be able to customize the printed output,
   * this method applies the {@link #getToStringFunction} if present.
   * Otherwise, it returns a compile-time default string.
   * 
   * @return A string representation of this event list.
   * 
   * @see #getToStringFunction
   * @see #setToStringFunction
   * 
   */
  @Override
  public final String toString ()
  {
    if (this.toStringFunction != null)
      return "" + this.toStringFunction.apply (this);
    else
      // return getClass ().getName () + '@' + Integer.toHexString (hashCode ());
      return "EventList[t=" + this.lastUpdateTime + "]";
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEFAULT RESET TIME
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private double defaultResetTime = Double.NEGATIVE_INFINITY;
  
  @Override
  public final double getDefaultResetTime ()
  {
    return this.defaultResetTime;
  }

  @Override
  public final void setDefaultResetTime (final double defaultResetTime)
  {
    this.defaultResetTime = defaultResetTime;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // [LAST-UPDATE] TIME
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private double lastUpdateTime = Double.NEGATIVE_INFINITY;
  
  private boolean firstUpdate = true;
  
  @Override
  public final double getTime ()
  {
    return this.lastUpdateTime;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EVENT CLASS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Class<E> eventClass;

  @Override
  public final Class<E> getSimEventClass ()
  {
    return this.eventClass;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EVENT FACTORY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private SimEventFactory<? extends E> eventFactory = null;

  @Override
  public final SimEventFactory<? extends E> getSimEventFactory ()
  {
    return this.eventFactory;
  }

  @Override
  public final void setSimEventFactory (SimEventFactory<? extends E> eventFactory)
  {
    this.eventFactory = eventFactory;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // ADD [ALL] (TreeSet)
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  /** Overridden in order (ensure) the setting the <i>de-conflict</i> value on the event added.
   * 
   * <p>
   * Overridden and made final to ensure this method uses the abstract {@link #add} method.
   * 
   * <p>
   * You do not have to override this method in case you want to change
   * setting the de-conflict value; override {@link #add} instead.
   * 
   * @param collection See {@link TreeSet#addAll}.
   * 
   * @return See {@link TreeSet#addAll}.
   * 
   * @see SimEvent#setSimEventListDeconflictValue
   * 
   */
  @Override
  public final boolean addAll (final Collection<? extends E> collection)
  {
    if (collection == null)
      throw new NullPointerException ("Attempt to add null collection to event list!");
    boolean changed = false;
    for (final E e : collection)
      changed = add (e) || changed;
    return changed;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RESET
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  @Override
  public void reset (double time)
  {
    synchronized (this)
    {
      if (this.running)
        throw new IllegalStateException ();
      clear ();
      this.lastUpdateTime = time;
      this.firstUpdate = true;
    }
    fireReset ();
  }
  
  /** Reset method called from the constructor.
   * 
   * <p>
   * Should match {@link #reset(double)} with the exceptions that
   * we do not check whether we are running or not (we assume not),
   * we do not have to clear the (super) set,
   * and we do not notify listeners.
   * 
   */
  private void resetFromContructor ()
  {
    this.lastUpdateTime = this.defaultResetTime;
    this.firstUpdate = true;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // UPDATE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  /** Checks the progress of time and notifies listeners when an update has taken place.
   * 
   * An update is defined as the processing of the first event or an increase in the current time.
   * If needed, this method updates the current time.
   * 
   * @param newTime The new time.
   * 
   * @throws IllegalArgumentException If this is not the first update (after construction or after a reset)
   *                                  and the new time is strictly smaller than the current time.
   * @see #getTime
   * @see #fireUpdate
   * 
   */ 
  protected final void checkUpdate (final double newTime)
  {
    if ((! this.firstUpdate) && newTime < this.lastUpdateTime)
      throw new IllegalArgumentException ();
    if (this.firstUpdate || newTime > this.lastUpdateTime)
    {
      this.lastUpdateTime = newTime;
      this.firstUpdate = false;
      fireUpdate ();
    }
  }

  /** Checks the progress of time when processing a given event
   * and notifies listeners when an update has taken place.
   * 
   * An update is defined as the processing of the first event or an increase in the current time.
   * If needed, this method updates the current time.
   * 
   * @param e The event being processed.
   * 
   * @throws IllegalArgumentException If the event is <code>null</code> or if this is not the first update
   *                                  (after construction or after a reset)
   *                                  and the new time on the event is strictly smaller than the current time.
   * 
   * @see #getTime
   * @see #fireUpdate
   * 
   */ 
  protected final void checkUpdate (final E e)
  {
    if (e == null)
      throw new IllegalArgumentException ();
    checkUpdate (e.getTime ());
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // RUN [UNTIL]
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private volatile boolean running = false;
  
  /** Overridden to make the default method final.
   * 
   * @see SimEventList#run
   * 
   */
  @Override
  public final void run ()
  {
    SimEventList.super.run ();
  }
  
  @Override
  public final void runUntil (final double endTime, final boolean inclusive, final boolean setTimeToEndTime)
  {
    synchronized (this)
    {
      if (this.running)
        throw new IllegalStateException ();
      this.running = true;
    }
    if (endTime < getTime ())
    {
      this.running = false;
      throw new IllegalArgumentException ();
    }
    while ((! isEmpty ())
      && (first ().getTime () < endTime || (inclusive && first ().getTime () == endTime))
      && ! Thread.interrupted ())
    {
      fireNextEvent ();
      final E e = pollFirst ();      
      // Updates this.lastUpdateTime.
      checkUpdate (e);
      final SimEventAction a = e.getEventAction ();
      if (a != null)
        a.action (e);
    }
    if (inclusive && setTimeToEndTime && getTime () < endTime)
      checkUpdate (endTime);
    if (isEmpty ())
      fireEmpty ();
    this.running = false;
  }
  
  /** Runs a single (the first) event from the event list ("single-stepping").
   * 
   */
  @Override
  public final void runSingleStep ()
  {
    synchronized (this)
    {
      if (isEmpty ())
        return;
      if (this.running)
        throw new IllegalStateException ();
      this.running = true;
    }
    fireNextEvent ();
    final E e = pollFirst ();      
    // Updates this.lastUpdateTime.
    checkUpdate (e);
    final SimEventAction a = e.getEventAction ();
    if (a != null)
      a.action (e);
    if (isEmpty ())
      fireEmpty ();
    this.running = false;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /** The reset listeners to this event list.
   * 
   */
  private final Set<SimEventListResetListener> resetListeners = new HashSet<> ();
  
  /** The (full) listeners to this event list.
   * 
   */
  private final Set<SimEventListListener> listeners = new HashSet<> ();
  
  /** The listeners in array form, if {@code USE_ARRAY_OPTIMIZATION == true}.
   * 
   */
  private SimEventListListener[] listenersAsArray = new SimEventListListener[0];

  /** The listeners to this event list that need per-event notifications.
   * 
   */
  private final Set<SimEventListListener.Fine> fineListeners = new HashSet<> ();
  
  /** The fine listeners in array form, if {@code USE_ARRAY_OPTIMIZATION == true}.
   * 
   */
  private SimEventListListener.Fine[] fineListenersAsArray = new SimEventListListener.Fine[0];

  @Override
  public final void addListener (SimEventListResetListener l)
  {
    if (l != null)
    {
      if (l instanceof SimEventListListener.Fine)
      {
        if (! this.fineListeners.contains ((SimEventListListener.Fine) l))
        {
          this.fineListeners.add ((SimEventListListener.Fine) l);
          if (AbstractSimEventList.USE_ARRAY_OPTIMIZATION)
            this.fineListenersAsArray = this.fineListeners.toArray (new SimEventListListener.Fine[this.fineListeners.size ()]);
        }
      }
      else if (l instanceof SimEventListListener)
      {
        if (! this.listeners.contains ((SimEventListListener) l))
        {
          this.listeners.add ((SimEventListListener) l);
          if (AbstractSimEventList.USE_ARRAY_OPTIMIZATION)
            this.listenersAsArray = this.listeners.toArray (new SimEventListListener[this.listeners.size ()]);
        }
      }
      else
      {
        this.resetListeners.add (l);
      }
    }
  }

  @Override
  public final void removeListener (SimEventListResetListener l)
  {
    if (l != null)
    {
      this.resetListeners.remove (l);
      if (l instanceof SimEventListListener)
      {
        if (this.listeners.contains ((SimEventListListener) l))
        {
          this.listeners.remove ((SimEventListListener) l);
          if (AbstractSimEventList.USE_ARRAY_OPTIMIZATION)
            this.listenersAsArray = this.listeners.toArray (new SimEventListListener[this.listeners.size ()]);
        }
      }
      if (l instanceof SimEventListListener.Fine)
      {
        if (this.fineListeners.contains ((SimEventListListener.Fine) l))
        {
          this.fineListeners.remove ((SimEventListListener.Fine) l);
          if (AbstractSimEventList.USE_ARRAY_OPTIMIZATION)
            this.fineListenersAsArray = this.fineListeners.toArray (new SimEventListListener.Fine[this.fineListeners.size ()]);
        }
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LISTENER NOTIFICATIONS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Fires a reset notification to registered listeners.
   * 
   * @see SimEventListResetListener#notifyEventListReset
   * 
   */
  protected final void fireReset ()
  {
    for (SimEventListListener.Fine l : this.fineListeners)
      l.notifyEventListReset (this);
    for (SimEventListListener l : this.listeners)
      l.notifyEventListReset (this);
    for (SimEventListResetListener l : this.resetListeners)
      l.notifyEventListReset (this);
  }
  
  /** Fires an update notification to registered listeners.
   * 
   * @see SimEventListListener#notifyEventListUpdate
   * 
   */
  protected final void fireUpdate ()
  {
    if (AbstractSimEventList.USE_ARRAY_OPTIMIZATION)
    {
      if (! this.fineListeners.isEmpty ())
        for (SimEventListListener.Fine l : this.fineListenersAsArray)
          l.notifyEventListUpdate (this, this.lastUpdateTime);
      if (! this.listeners.isEmpty ())
        for (SimEventListListener l : this.listenersAsArray)
          l.notifyEventListUpdate (this, this.lastUpdateTime);      
    }
    else
    {
      if (! this.fineListeners.isEmpty ())
        for (SimEventListListener.Fine l : this.fineListeners)
          l.notifyEventListUpdate (this, this.lastUpdateTime);
      if (! this.listeners.isEmpty ())
        for (SimEventListListener l : this.listeners)
          l.notifyEventListUpdate (this, this.lastUpdateTime);
    }
  }
  
  /** Fires an empty-event-list notification to registered listeners.
   * 
   * @see SimEventListListener#notifyEventListEmpty
   * 
   */
  protected final void fireEmpty ()
  {
    if (AbstractSimEventList.USE_ARRAY_OPTIMIZATION)
    {
      if (! this.fineListeners.isEmpty ())
        for (SimEventListListener.Fine l : this.fineListenersAsArray)
          l.notifyEventListEmpty (this, this.lastUpdateTime);
      if (! this.listeners.isEmpty ())
        for (SimEventListListener l : this.listenersAsArray)
          l.notifyEventListEmpty (this, this.lastUpdateTime);      
    }
    else
    {
      if (! this.fineListeners.isEmpty ())
        for (SimEventListListener.Fine l : this.fineListeners)
          l.notifyEventListEmpty (this, this.lastUpdateTime);
      if (! this.listeners.isEmpty ())
        for (SimEventListListener l : this.listeners)
          l.notifyEventListEmpty (this, this.lastUpdateTime);
    }
  }
  
  /** Fires a next-event notification to registered listeners.
   * 
   * @see SimEventListListener.Fine#notifyNextEvent
   * 
   */
  protected final void fireNextEvent ()
  {
    if (AbstractSimEventList.USE_ARRAY_OPTIMIZATION)
    {
      if (! this.fineListeners.isEmpty ())
        for (SimEventListListener.Fine l : this.fineListenersAsArray)
          l.notifyNextEvent (this, this.lastUpdateTime);
    }
    else
    {
      if (! this.fineListeners.isEmpty ())
        for (SimEventListListener.Fine l : this.fineListeners)
          l.notifyNextEvent (this, this.lastUpdateTime);
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
