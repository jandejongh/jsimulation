package nl.jdj.jsimulation.r5.example;

import nl.jdj.jsimulation.r5.SimEvent;
import nl.jdj.jsimulation.r5.SimEventAction;
import nl.jdj.jsimulation.r5.DefaultSimEventList;
import nl.jdj.jsimulation.r5.AbstractSimTimer;
import nl.jdj.jsimulation.r5.SimEventList;

/** Example code for jsimulation.
 * 
 */
public final class Main
{
  
  /** Prevents instantiation.
   * 
   */
  private Main ()
  {
  }
  
  /** Main method.
   * 
   * Creates a (reusable) event list, some events and actions and shows the main feature of the package.
   * Results are sent to {@link System#out}.
   * 
   * @param args The command-line arguments (ignored).
   * 
   */
  public static void main (String[] args)
  {
    System.out.println ("=== EXAMPLE PROGRAM FOR jsimulation PACKAGE ===");
    System.out.println ("-> Creating actions...");
    final SimEventAction<Object> a1 = new SimEventAction<Object> ()
    {
      @Override
      public void action (SimEvent<Object> event)
      {
        System.out.println ("  -> Action 1 on " + event.getName () + ", user object: " + (int) event.getObject ()
          + " @" + event.getTime () + ".");
      }
    };
    final SimEventAction<Object> a2 = new SimEventAction<Object> ()
    {
      @Override
      public void action (SimEvent<Object> event)
      {
        System.out.println ("  -> Action 2 on " + event.getName () + ", user object: " + (int) event.getObject ()
          + " @" + event.getTime () + ".");
      }
    };
    System.out.println ("-> Creating and populating event list with events at one-second interval...");
    final SimEventList el = new DefaultSimEventList ();
    for (int n = 0; n < 15; n++)
      el.add (new SimEvent<> ((double) n, n, (n % 2 == 0) ? a1 : a2));
    System.out.println ("-> Executing event list...");
    el.run ();
    System.out.println ("-> Resetting event list...");
    el.reset ();
    System.out.println ("-> Populating event list with events all scheduled at t = 1.0; these will be executed in random order...");
    for (int n = 0; n < 15; n++)
      el.add (new SimEvent<> (1.0, n, (n % 2 == 0) ? a1 : a2));
    System.out.println ("-> Executing event list...");
    el.run ();
    final SimEventAction<Object> a3 = new SimEventAction<Object> ()
    {
      @Override
      public void action (SimEvent<Object> event)
      {
        if (event.getTime () < 16)
        {
          System.out.println ("  -> Rescheduling event at @" + (event.getTime () + 1.0 ) + ".");
          event.setTime (event.getTime () + 1.0);
          el.add (event);
        }
      }
    };
    System.out.println ("-> Resetting event list...");
    el.reset ();
    System.out.println ("-> Populating event list with auto-rescheduling event...");
    el.add (new SimEvent<> (1.0, null, a3));
    System.out.println ("-> Executing event list...");
    el.run ();
    System.out.println ("-> Resetting event list to zero time...");
    el.reset (0.0);
    System.out.println ("-> Populating event list with 16-seconds timer...");
    new AbstractSimTimer ("Timer")
    {
      @Override
      public void expireAction (double time)
      {
        System.out.println ("  -> Timer expired @" + time + ".");
      }
    }.schedule (16.0, el);
    System.out.println ("-> Executing event list...");
    el.run ();
    System.out.println ("=== FINISHED ===");
  }
  
}