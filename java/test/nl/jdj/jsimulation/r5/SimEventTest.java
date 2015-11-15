/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.jdj.jsimulation.r5;

import nl.jdj.jsimulation.r5.SimEventAction;
import nl.jdj.jsimulation.r5.SimEvent;
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
public class SimEventTest
{
  
  public SimEventTest ()
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
   * Test of getName method, of class SimEvent.
   */
  @Test
  public void testGetName ()
  {
    System.out.println ("getName");
    SimEvent instance = new SimEvent ("My First Event", 12.0, null, null);
    String expResult = "My First Event";
    String result = instance.getName ();
    assertEquals (expResult, result);
  }

  /**
   * Test of setName method, of class SimEvent.
   */
  @Test
  public void testSetName ()
  {
    System.out.println ("setName");
    String name = "";
    SimEvent instance = new SimEvent ("My First Event", 12.0, null, null);
    String expResult = "My First Event";
    String result = instance.getName ();
    assertEquals (expResult, result);
    instance.setName ("My First Renamed Event");
    expResult = "My First Renamed Event";
    result = instance.getName ();
    assertEquals (expResult, result);
  }

  /**
   * Test of getTime method, of class SimEvent.
   */
  @Test
  public void testGetTime ()
  {
    System.out.println ("getTime");
    SimEvent instance = new SimEvent ("My First Event", 13.7, null, null);
    double expResult = 13.7;
    double result = instance.getTime ();
    assertEquals (expResult, result, 0.0);
    instance = new SimEvent ("My Second Event", -14.78, null, null);
    expResult = -14.78;
    result = instance.getTime ();
    assertEquals (expResult, result, 0.0);    
  }

  /**
   * Test of setTime method, of class SimEvent.
   */
  @Test
  public void testSetTime ()
  {
    System.out.println ("setTime");
    SimEvent instance = new SimEvent ("My First Event", 13.7, null, null);
    double expResult = 13.7;
    double result = instance.getTime ();
    assertEquals (expResult, result, 0.0);
    instance.setTime (-17.77);
    expResult = -17.77;
    result = instance.getTime ();
    assertEquals (expResult, result, 0.0);    
  }

  /**
   * Test of setDeconflicterSeed method, of class SimEvent.
   */
  @Test
  public void testSetDeconflicterSeed ()
  {
    System.out.println ("setDeconflicterSeed");
    long seed = 0L;
    SimEvent.setDeconflicterSeed (seed);
    // XXX Cannot really test this...
  }

  /**
   * Test of getObject method, of class SimEvent.
   */
  @Test
  public void testGetObject ()
  {
    System.out.println ("getObject");
    Object object = (double) 23;
    SimEvent instance = new SimEvent ("My First Event", 13.7, object, null);
    Object expResult = object;
    Object result = instance.getObject ();
    assertEquals (expResult, result);
  }

  /**
   * Test of setObject method, of class instance.add (e1);SimEvent.
   */
  @Test
  public void testSetObject ()
  {
    System.out.println ("setObject");
    Object object = (double) 23;
    SimEvent instance = new SimEvent ("My First Event", 13.7, object, null);
    Object expResult = object;
    Object result = instance.getObject ();
    assertEquals (expResult, result);
    object = (int) -34;
    instance.setObject (object);
    expResult = object;
    result = instance.getObject ();
    assertEquals (expResult, result);
  }
  
  /**
   * Test of getEventAction method, of class SimEvent.
   */
  @Test
  public void testGetEventAction ()
  {
    System.out.println ("getEventAction");
    SimEventAction action = new SimEventAction ()
    {
      @Override
      public void action (SimEvent event)
      {
        fail ("The action should not be executed.");
      }   
    };
    SimEvent instance = new SimEvent ("My First Event", 13.7, null, action);
    SimEventAction expResult = action;
    SimEventAction result = instance.getEventAction ();
    assertEquals (expResult, result);
  }

  /**
   * Test of setEventAction method, of class SimEvent.
   */
  @Test
  public void testSetEventAction ()
  {
    System.out.println ("setEventAction");
    SimEventAction action1 = new SimEventAction ()
    {
      @Override
      public void action (SimEvent event)
      {
        fail ("The action (1) should not be executed.");
      }   
    };
    SimEvent instance = new SimEvent ("My First Event", 13.7, null, action1);
    SimEventAction expResult = action1;
    SimEventAction result = instance.getEventAction ();
    assertEquals (expResult, result);
    SimEventAction action2 = new SimEventAction ()
    {
      @Override
      public void action (SimEvent event)
      {
        fail ("The action (2) should not be executed.");
      }   
    };
    instance.setEventAction (action2);
    expResult = action2;
    result = instance.getEventAction ();
    assertEquals (expResult, result);
  }
  
}
