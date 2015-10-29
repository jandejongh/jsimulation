/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.jdj.jsimulation.r4;

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
    SimEventList instance = new SimEventList (SimEvent.class);
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
    SimEventList instance = new SimEventList (SimEvent.class);
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

  /**
   * Test of checkUpdate method, of class SimEventList.
   */
  @Test
  public void testCheckUpdate ()
  {
    System.out.println ("checkUpdate");
    SimEventList instance = new SimEventList (SimEvent.class);
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
    final SimEventList instance = new SimEventList (SimEvent.class);
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
    instance.runUntil (10, false);
    assert ! instance.isEmpty ();
    assert ! this.action1Done;
    assert ! this.action2Done;
    instance.runUntil (10, true);
    assert ! instance.isEmpty ();
    assert ! this.action1Done;
    assert this.action2Done;
    instance.runUntil (15, true);
    assert ! instance.isEmpty ();
    assert ! this.action1Done;
    assert this.action2Done;
    instance.runUntil (20, false);
    assert instance.isEmpty ();
    assert this.action1Done;
    assert this.action2Done;
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
  
  private class TestSimEventAction
  implements SimEventAction
  {
    private boolean executed = false;
    private double executionTime = Double.NEGATIVE_INFINITY;
    @Override
    public void action (SimEvent event)
    {
      assert ! this.executed;
      this.executed = true;
      this.executionTime = event.getTime ();
    }
  }
  
  private class ReschedulingSimEvent
  extends SimEvent
  {
    private final SimEventList eventList;
    private int count = 0;
    private final int maxCount;
    private final double interval;
    private double lastActionTime = Double.NEGATIVE_INFINITY;
    private ReschedulingSimEvent (final SimEventList eventList, final int maxCount, final double interval)
    {
      this.eventList = eventList;
      this.maxCount = maxCount;
      this.interval = interval;
      setEventAction (new SimEventAction ()
      {
        @Override
        public void action (SimEvent event)
        {
          count++;
          lastActionTime = event.getTime ();
          if (count < maxCount)
            eventList.reschedule (event.getTime () + interval, event);
        }
      });
    }
    
  }
  
  /**
   * Test of various schedule/reschedule methods, of class SimEventList.
   */
  @Test
  public void testSchedule ()
  {
    System.out.println ("schedule");
    final SimEventList el = new SimEventList (SimEvent.class);
    final TestSimEventAction a1 = new TestSimEventAction ();
    final TestSimEventAction a2 = new TestSimEventAction ();
    el.schedule (15.0, a1);
    el.schedule (-40.0, a2);
    el.run ();
    assert a1.executed;
    assertEquals (15.0, a1.executionTime, 0.0);
    assert a2.executed;
    assertEquals (-40.0, a2.executionTime, 0.0);
    el.reset ();
    a1.executed = false;
    a2.executed = false;
    final SimEvent e1 = el.schedule (15.0, a1);
    try
    {
      el.schedule (e1);
      fail ("Attempt to schedule already scheduled event should fail!");
    }
    catch (IllegalArgumentException iae)
    {
    }
    try
    {
      el.schedule (39.4, e1);
      fail ("Attempt to schedule already scheduled event should fail!");
    }
    catch (IllegalArgumentException iae)
    {
    }
    el.reschedule (-123455.0, e1);
    el.reschedule (-77.0, e1);
    el.run ();
    assert a1.executed;
    assertEquals (-77.0, a1.executionTime, 0.0);
    el.reset ();
    a1.executed = false;
    a2.executed = false;
    el.reschedule (-459.0, e1);
    el.run ();
    assert a1.executed;
    assertEquals (-459.0, a1.executionTime, 0.0);
    el.reset ();
    a1.executed = false;
    a2.executed = false;
    final ReschedulingSimEvent rse1 = new ReschedulingSimEvent (el, 10, 10.0);
    el.schedule (0.0, rse1);
    el.run ();
    assertEquals (rse1.maxCount, rse1.count);
    assertEquals ((rse1.maxCount - 1) * rse1.interval, rse1.lastActionTime, 0.0);
    el.reset ();
    a1.executed = false;
    a2.executed = false;
    final ReschedulingSimEvent rse2 = new ReschedulingSimEvent (el, 100, 0);
    el.schedule (0.0, rse2);
    el.run ();
    assertEquals (rse2.maxCount, rse2.count);
    assertEquals (0, rse2.lastActionTime, 0.0);
  }
  
}
