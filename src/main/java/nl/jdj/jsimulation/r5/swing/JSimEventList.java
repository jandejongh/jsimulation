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
package nl.jdj.jsimulation.r5.swing;

import java.awt.Dimension;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import nl.jdj.jsimulation.r5.SimEvent;
import nl.jdj.jsimulation.r5.SimEventList;
import nl.jdj.jsimulation.r5.SimEventListListener;

/** A Swing <code>JComponent</code> showing the contents of a {@link SimEventList}.
 *
 * <p>
 * The component updates itself by registering as a {@link SimEventListListener}(<code>.Fine</code>) to the event list.
 * 
 * <p>
 * <b>Last javadoc Review:</b> Jan de Jongh, TNO, 20180404, r5.1.0.
 * 
 * @see SimEventList
 * 
 */
public class JSimEventList
extends JComponent
implements SimEventListListener.Fine
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S)
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JSimEventList (final SimEventList eventList)
  {
    super ();
    if (eventList == null)
      throw new IllegalArgumentException ();
    setPreferredSize (new Dimension (200, 200));
    // setMaximumSize (new Dimension (600, 600));
    this.eventList = eventList;
    this.table = new JTable (this.tableModel);
    this.table.setRowSelectionAllowed (false);
    this.table.setColumnSelectionAllowed (false);
    setOpaque (true);
    setLayout (new BoxLayout (this, BoxLayout.LINE_AXIS));
    add (new JScrollPane (this.table));
    this.eventList.addListener (this);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EVENT LIST
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final SimEventList eventList;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // EVENT LIST CHANGED NOTIFICATION
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /** Notification that the internally held {@link SimEventList} has changed, and needs to be redrawn in this {@link JComponent}.
   * 
   */
  public void eventListChangedNotification ()
  {
    ((AbstractTableModel) this.tableModel).fireTableDataChanged ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // SimEventListListener.Fine
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public void notifyNextEvent (final SimEventList eventList, final double time)
  {
    eventListChangedNotification ();
  }

  @Override
  public void notifyEventListReset (final SimEventList eventList)
  {
    eventListChangedNotification ();
  }

  @Override
  public void notifyEventListUpdate (final SimEventList eventList, final double time)
  {
    eventListChangedNotification ();
  }

  @Override
  public void notifyEventListEmpty (final SimEventList eventList, final double time)
  {
    eventListChangedNotification ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TABLE AND TABLE MODEL
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JTable table;
  
  private final TableModel tableModel = new AbstractTableModel ()
  {

    @Override
    public final int getRowCount ()
    {
      return JSimEventList.this.eventList.size ();
    }

    @Override
    public final int getColumnCount ()
    {
      return 4;
    }

    @Override
    public final String getColumnName (int column)
    {
      if (column < 0)
        return null;
      else if (column == 0)
        return "Time";
      else if (column== 1)
        return "Name";
      else if (column == 2)
        return "Object";
      else if (column == 3)
        return "Action";
      else
        return null;
    }

    @Override
    public final Object getValueAt (int r, int c)
    {
      if (r < 0 || r >= JSimEventList.this.eventList.size ())
        return null;
      if (c < 0 || c >= 4)
        return null;
      final Iterator<SimEvent> iterator = JSimEventList.this.eventList.iterator ();
      SimEvent e = iterator.next ();
      int i = 0;
      while (i < r)
      {
        e = iterator.next ();
        i++;
      }
      if (c == 0)
        return e.getTime ();
      else if (c ==1)
        return e.getName ();
      else if (c == 2)
        return e.getObject ();
      else if (c == 3)
        return e.getEventAction ();
      throw new RuntimeException ();
    }
    
  };

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
