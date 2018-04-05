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

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** Test(s) for {@link DefaultSimEvent}.
 * 
 * <p>
 * <b>Last javadoc Review:</b> Jan de Jongh, TNO, 20180404, r5.1.0.
 * 
 *
 */
public class DefaultSimEventTest
{
  
  public DefaultSimEventTest ()
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
   * Test of getName method, of class DefaultSimEvent.
   */
  @Test
  public void testGetName ()
  {
    System.out.println ("getName");
    SimEvent instance = new DefaultSimEvent ("My First Event", 12.0, null, null);
    String expResult = "My First Event";
    String result = instance.getName ();
    assertEquals (expResult, result);
  }

  /**
   * Test of setName method, of class DefaultSimEvent.
   */
  @Test
  public void testSetName ()
  {
    System.out.println ("setName");
    String name = "";
    SimEvent instance = new DefaultSimEvent ("My First Event", 12.0, null, null);
    String expResult = "My First Event";
    String result = instance.getName ();
    assertEquals (expResult, result);
    instance.setName ("My First Renamed Event");
    expResult = "My First Renamed Event";
    result = instance.getName ();
    assertEquals (expResult, result);
  }

  /**
   * Test of getTime method, of class DefaultSimEvent.
   */
  @Test
  public void testGetTime ()
  {
    System.out.println ("getTime");
    SimEvent instance = new DefaultSimEvent ("My First Event", 13.7, null, null);
    double expResult = 13.7;
    double result = instance.getTime ();
    assertEquals (expResult, result, 0.0);
    instance = new DefaultSimEvent ("My Second Event", -14.78, null, null);
    expResult = -14.78;
    result = instance.getTime ();
    assertEquals (expResult, result, 0.0);    
  }

  /**
   * Test of setTime method, of class DefaultSimEvent.
   */
  @Test
  public void testSetTime ()
  {
    System.out.println ("setTime");
    SimEvent instance = new DefaultSimEvent ("My First Event", 13.7, null, null);
    double expResult = 13.7;
    double result = instance.getTime ();
    assertEquals (expResult, result, 0.0);
    instance.setTime (-17.77);
    expResult = -17.77;
    result = instance.getTime ();
    assertEquals (expResult, result, 0.0);    
  }

  /**
   * Test of getObject method, of class DefaultSimEvent.
   */
  @Test
  public void testGetObject ()
  {
    System.out.println ("getObject");
    Object object = (double) 23;
    SimEvent instance = new DefaultSimEvent ("My First Event", 13.7, object, null);
    Object expResult = object;
    Object result = instance.getObject ();
    assertEquals (expResult, result);
  }

  /**
   * Test of setObject method, of class instance.add (e1);DefaultSimEvent.
   */
  @Test
  public void testSetObject ()
  {
    System.out.println ("setObject");
    Object object = (double) 23;
    SimEvent instance = new DefaultSimEvent ("My First Event", 13.7, object, null);
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
   * Test of getEventAction method, of class DefaultSimEvent.
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
    SimEvent instance = new DefaultSimEvent ("My First Event", 13.7, null, action);
    SimEventAction expResult = action;
    SimEventAction result = instance.getEventAction ();
    assertEquals (expResult, result);
  }

  /**
   * Test of setEventAction method, of class DefaultSimEvent.
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
    SimEvent instance = new DefaultSimEvent ("My First Event", 13.7, null, action1);
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
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
