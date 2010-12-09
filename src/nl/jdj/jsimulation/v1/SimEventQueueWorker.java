package nl.jdj.jsimulation.v1;

import nl.jdj.jworkers.core.AbstractTransformer;

/**
 *
 */
public class SimEventQueueWorker
  extends AbstractTransformer<SimEventQueue, SimEventQueue>
{

  public SimEventQueue callUncheckedInterrupts
    (SimEventQueue output)
    throws Exception
  {
    final SimEventQueue input = getInput ();
    if (output == null)
    {
      output = input;
    }
    else if (output != input)
    {
      output.clear ();
      output.addAll (input);
    }
    while (! output.isEmpty ())
    {
      final SimEvent event = output.pollFirst ();
      final SimEventAction eventAction = event.getEventAction ();
      if (eventAction != null)
      {
        setWorkDone (event.getTime ());
        eventAction.action (event);
      }
    }
    return output;
  }

  public SimEventQueueWorker (final SimEventQueue i, final SimEventQueue o)
  {
    super (i, o);
    setWorkDone (0);
  }

}
