package nl.jdj.jsimulation.r4.swing;

import java.awt.Dimension;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import nl.jdj.jsimulation.r4.SimEvent;
import nl.jdj.jsimulation.r4.SimEventList;
import nl.jdj.jsimulation.r4.SimEventListListener;

/**
 *
 */
public class JSimEventList
extends JComponent
implements SimEventListListener.Fine
{
  
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
  
  private TableModel tableModel = new AbstractTableModel ()
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
    setOpaque (true);
    setLayout (new BoxLayout (this, BoxLayout.LINE_AXIS));
    add (new JScrollPane (this.table));
    this.eventList.addListener (this);
  }

}
