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
package nl.jdj.jsimulation.r5.swing.example;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import nl.jdj.jsimulation.r5.DefaultSimEventList;
import nl.jdj.jsimulation.r5.SimEventList;
import nl.jdj.jsimulation.r5.SimEventListListener;
import nl.jdj.jsimulation.r5.swing.JSimEventList;

/** Example program showing the use of {@link JSimEventList} and basic interactions with a {@link SimEventList}.
 *
 * <p>
 * <b>Last javadoc Review:</b> Jan de Jongh, TNO, 20180404, r5.1.0.
 * 
 * @see SimEventList
 * @see JSimEventList
 *
 */
public final class Main
{
  
  /** Inhibits instantiation.
   * 
   */
  private Main ()
  {
  }
  
  /** Starts the main program.
   * 
   * @param args The command line arguments (ignored).
   * 
   */
  public static void main (final String[] args)
  {
    SwingUtilities.invokeLater (new Runnable ()
      {
        
        private JFrame frame;
        
        private SimEventList eventList;

        @Override
        public void run ()
        {
          frame = new JFrame ("JSimQueue demonstration.");
          frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
          final JPanel topPanel = new JPanel ();
          topPanel.setLayout (new BoxLayout (topPanel, BoxLayout.PAGE_AXIS));
          frame.getContentPane ().add (topPanel);
          this.eventList = new DefaultSimEventList ();
          this.eventList.schedule (0.0, null, "First Event");
          this.eventList.schedule (5.0, null, "Second Event");
          this.eventList.schedule (1000.0, null, "Third Event");
          this.eventList.schedule (Double.POSITIVE_INFINITY, null, "Fourth Event");
          final JLabel timeLabel = new JLabel ("" + this.eventList.getTime ());
          this.eventList.addListener (new SimEventListListener ()
          {
            @Override
            public final void notifyEventListReset (final SimEventList eventList)
            {
              timeLabel.setText ("" + eventList.getTime ());
            }
            @Override
            public final void notifyEventListUpdate (final SimEventList eventList, final double time)
            {
              timeLabel.setText ("" + time);
            }
            @Override
            public final void notifyEventListEmpty (final SimEventList eventList, final double time)
            {
              /* EMPTY */
            }
          });
          topPanel.add (Box.createRigidArea (new Dimension (0, 10)));
          final JPanel timePanel = new JPanel ();
          timePanel.add (timeLabel);
          timePanel.setBorder
            (BorderFactory.createTitledBorder
            (BorderFactory.createLineBorder (Color.orange, 3, true), "Current Time"));
          timePanel.setMaximumSize (new Dimension (Integer.MAX_VALUE, 150));
          topPanel.add (timePanel);
          final JSimEventList jSimEventList = new JSimEventList (this.eventList);
          jSimEventList.setBorder
            (BorderFactory.createTitledBorder
            (BorderFactory.createLineBorder (Color.orange, 3, true), "Event List"));
          topPanel.add (Box.createRigidArea (new Dimension (0, 10)));
          topPanel.add (jSimEventList);
          final JPanel buttonPanel = new JPanel ();
          buttonPanel.setLayout (new BoxLayout (buttonPanel, BoxLayout.LINE_AXIS));
          buttonPanel.add (new JButton (new AbstractAction ("Reset")
          {
            @Override
            public final void actionPerformed (ActionEvent ae)
            {
              eventList.reset ();
            }
          }));
          buttonPanel.add (Box.createRigidArea (new Dimension (10, 0)));
          buttonPanel.add (new JButton (new AbstractAction ("New@t+10")
          {
            @Override
            public final void actionPerformed (ActionEvent ae)
            {
              eventList.schedule (eventList.getTime () + 10, null, "New Event");
              jSimEventList.eventListChangedNotification ();
            }
          }));
          buttonPanel.add (Box.createRigidArea (new Dimension (10, 0)));
          buttonPanel.add (new JButton (new AbstractAction ("New@t+100")
          {
            @Override
            public final void actionPerformed (ActionEvent ae)
            {
              eventList.schedule (eventList.getTime () + 100, null, "New Event");
              jSimEventList.eventListChangedNotification ();
            }
          }));
          buttonPanel.add (Box.createRigidArea (new Dimension (10, 0)));
          buttonPanel.add (new JButton (new AbstractAction ("New@Infinity")
          {
            @Override
            public final void actionPerformed (ActionEvent ae)
            {
              eventList.schedule (Double.POSITIVE_INFINITY, null, "New Event");
              jSimEventList.eventListChangedNotification ();
            }
          }));
          buttonPanel.add (Box.createRigidArea (new Dimension (10, 0)));
          buttonPanel.add (new JButton (new AbstractAction ("Step")
          {
            @Override
            public void actionPerformed (ActionEvent ae)
            {
              eventList.runSingleStep ();
              jSimEventList.eventListChangedNotification ();
            }
          }));
          buttonPanel.add (Box.createRigidArea (new Dimension (10, 0)));
          buttonPanel.add (new JButton (new AbstractAction ("Run Until / Set Time")
          {
            @Override
            public final void actionPerformed (ActionEvent ae)
            {
              final String s = (String) JOptionPane.showInputDialog (
                    frame,
                    "Enter Time",
                    "Enter Time",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    Double.toString (eventList.getTime ()));
              if (s != null)
                try 
                {
                  final double newTime = Double.parseDouble (s);
                  if (newTime >= eventList.getTime ())
                    eventList.runUntil (newTime, true, true);
                  else
                    JOptionPane.showMessageDialog (buttonPanel, "Time is in the past!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                catch (NumberFormatException nfe)
                {
                  JOptionPane.showMessageDialog (buttonPanel, "Illegal time value!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
          }));
          buttonPanel.add (Box.createRigidArea (new Dimension (10, 0)));
          buttonPanel.add (new JButton (new AbstractAction ("Run")
          {
            @Override
            public void actionPerformed (ActionEvent ae)
            {
              eventList.run ();
            }
          }));
          buttonPanel.setAlignmentY (0.5f);
          buttonPanel.setBorder
            (BorderFactory.createTitledBorder
            (BorderFactory.createLineBorder (Color.orange, 3, true), "Control"));
          topPanel.add (Box.createRigidArea (new Dimension (0, 10)));
          topPanel.add (buttonPanel);
          topPanel.add (Box.createRigidArea (new Dimension (0, 10)));
          frame.pack ();
          frame.setLocationRelativeTo (null);
          frame.setVisible (true);
        }
      });
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
