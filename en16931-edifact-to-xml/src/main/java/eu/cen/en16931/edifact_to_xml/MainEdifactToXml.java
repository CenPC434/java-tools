package eu.cen.en16931.edifact_to_xml;

import com.mapforce.MappingConsole;

/**
 * Just a wrapper, so that the main class is in out package name.
 * 
 * @author Philip Helger
 */
public final class MainEdifactToXml
{
  public static void main (final String [] args)
  {
    boolean bCanRun = false;
    for (int i = 0; i < args.length; i++)
    {
      String sName = args[i];
      if (sName.equals ("/input") && (i + 1) < args.length)
      {
        bCanRun = true;
        break;
      }
    }

    if (bCanRun)
      MappingConsole.main (args);
    else
    {
      System.err.println ();
      System.err.println ("WARNING: No parameters given!");
      System.err.println ("SYNTAX: java " + MainEdifactToXml.class.getName () + " [/input ...] ");
      System.err.println ("Note: If you want to use spaces as values write them in-between double quotes.");
      System.err.println ();
    }
  }
}
