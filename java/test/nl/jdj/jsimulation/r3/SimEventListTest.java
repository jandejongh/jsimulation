/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test.nl.jdj.jsimulation.r3;

import nl.jdj.jsimulation.r3.SimEvent;
import nl.jdj.jsimulation.r3.SimEventAction;
import nl.jdj.jsimulation.r3.SimEventList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jan
 */
public class SimEventListTest
{
  
  public SimEventListTest ()
  {
  }
  
  @BeforeClass
  public static void setUpClass ()
  {
  }
  
  @AfterClass
  public static void tearDownClass ()
  {
  }
  
  @Before
  public void setUp ()
  {
  }
  
  @After
  public void tearDown ()
  {
  }

  /**
   * Test of getTime method, of class SimEventList.
   */
  @Test
  public void testGetTime ()
  {
    System.out.println ("getTime");
    SimEventList instance = new SimEventList ();
    double expResult = Double.NEGATIVE_INFINITY;
    double result = instance.getTime ();
    assertEquals (expResult, result, 0.0);
    SimEvent e1 = new SimEvent (15.8, null, null);
    instance.add (e1);
    SimEvent e2 = new SimEvent (10.0, null, null);
    instance.add (e2);
    instance.run ();
    expResult = 15.8;
    result = instance.getTime ();
    assertEquals (expResult, result, 15.8);
  }

  /**
   * Test of reset method, of class SimEventList.
   */
  @Test
  public void testReset ()
  {
    System.out.println ("reset");
    SimEventList instance = new SimEventList ();
    SimEvent e1 = new SimEvent (15.8, null, null);
    instance.add (e1);
    SimEvent e2 = new SimEvent (10.0, null, null);
    instance.add (e2);
    instance.run ();
    double expResult = 15.8;
    double result = instance.getTime ();
    assertEquals (expResult, result, 0.0);
    assert instance.isEmpty ();
    instance.reset ();
    instance.add (e2);
    instance.run ();
    expResult = 10.0;
    result = instance.getTime ();
    assertEquals (expResult, result, 0.0);
    assert instance.isEmpty ();
  }

  /** For private use in testAddUpdateSimEventAction and testRemoveUpdateSimEventAction.
   * 
   */
  
  private boolean updateListenerCalled = false;
  
  private boolean updateListenerMayBeCalled = true;
  
  private final SimEventAction updateListener = new SimEventAction ()
  {
    @Override
    public void action (SimEvent e)
    {
      SimEventListTest.this.updateListenerCalled = true;
      if (! SimEventListTest.this.updateListenerMayBeCalled)
        throw new RuntimeException ("Update listener should not be called!");
    }
  };

  /**
   * Test of addUpdateSimEventAction method, of class SimEventList.
   */
  @Test
  public void testAddUpdateSimEventAction ()
  {
    System.out.println ("addUpdateSimEventAction");
    SimEventList instance = new SimEventList ();
    SimEvent e1 = new SimEvent (15.8, null, null);
    instance.add (e1);
    SimEvent e2 = new SimEvent (10.0, null, null);
    instance.add (e2);
    this.updateListenerCalled = false;
    this.updateListenerMayBeCalled = false;
    instance.run ();
    if (this.updateListenerCalled)
      fail ("Update listener should not be called.");
    instance = new SimEventList ();
    instance.add (e1);
    instance.add (e2);
    instance.addUpdateSimEventAction (this.updateListener);
    this.updateListenerCalled = false;
    this.updateListenerMayBeCalled = true;
    instance.run ();
    if (! this.updateListenerCalled)
      fail ("Update listener should have been called.");
  }

  /**
   * Test of removeUpdateSimEventAction method, of class SimEventList.
   */
  @Test
  public void testRemoveUpdateSimEventAction ()
  {
    System.out.println ("removeUpdateSimEventAction");
    SimEventList instance = new SimEventList ();
    SimEvent e1 = new SimEvent (15.8, null, null);
    instance.add (e1);
    SimEvent e2 = new SimEvent (10.0, null, null);
    instance.add (e2);
    instance.addUpdateSimEventAction (this.updateListener);
    this.updateListenerCalled = false;
    this.updateListenerMayBeCalled = true;
    instance.run ();
    if (! this.updateListenerCalled)
      fail ("Update listener should have been called.");
    instance = new SimEventList ();
    instance.add (e1);
    instance.add (e2);
    instance.addUpdateSimEventAction (this.updateListener);
    this.updateListenerCalled = false;
    this.updateListenerMayBeCalled = false;
    instance.removeUpdateSimEventAction (this.updateListener);
    instance.run ();
    if (this.updateListenerCalled)
      fail ("Update listener should not be called.");
  }

  /**
   * Test of checkUpdate method, of class SimEventList.
   */
  @Test
  public void testCheckUpdate ()
  {
    System.out.println ("checkUpdate");
    SimEventList instance = new SimEventList ();
    SimEvent e = new SimEvent (15.8, null, null);
    instance.checkUpdate (e);
    double expResult = 15.8;
    double result = instance.getTime ();
    assertEquals (expResult, result, 0.0); 
    e = new SimEvent (13.2, null, null); // Before current time; should be ignored.
    instance.checkUpdate (e);
    expResult = 15.8;
    result = instance.getTime ();
    assertEquals (expResult, result, 0.0); 
    e = new SimEvent (42.0, null, null); // Before current time; should be ignored.
    instance.checkUpdate (e);
    expResult = 42.0;
    result = instance.getTime ();
    assertEquals (expResult, result, 0.0);
  }

  /** For private use in testRun
   * 
   */
  private boolean action1Done = false;
  
  /** For private use in testRun
   * 
   */
  private boolean action2Done = false;
  
  /**
   * Test of run method, of class SimEventList.
   */
  @Test
  public void testRun ()
  {
    System.out.println ("run");
    final SimEventList instance = new SimEventList ();
    this.action1Done = false;
    this.action2Done = false;
    SimEventAction action1 = new SimEventAction ()
    {
      @Override
      public void action (SimEvent event)
      {
        if (SimEventListTest.this.action1Done)
          fail ("Event (1) triggered more than once!");
        Object expResult = 15.8;
        Object result = instance.getTime ();
        assertEquals (expResult, result); 
        SimEventListTest.this.action1Done = true;
      }
    };
    SimEvent e1 = new SimEvent (15.8, null, action1);
    instance.add (e1);
    SimEventAction action2 = new SimEventAction ()
    {
      @Override
      public void action (SimEvent event)
      {
        if (SimEventListTest.this.action2Done)
          fail ("Event (2) triggered more than once!");
        if (SimEventListTest.this.action2Done)
          fail ("Event 2 scheduled before 1!");
        Object expResult = 10.0;
        Object result = instance.getTime ();
        assertEquals (expResult, result); 
        SimEventListTest.this.action2Done = true;
      }
    };
    SimEvent e2 = new SimEvent (10.0, null, action2);
    instance.add (e2);
    instance.run ();
    assert instance.isEmpty ();
    // Should be able to execute the event list again...
    instance.reset ();
    this.action1Done = false;
    this.action2Done = false;
    instance.add (e1);    
    instance.run ();
    assert instance.isEmpty ();
    assert this.action1Done;
  }
  
}
