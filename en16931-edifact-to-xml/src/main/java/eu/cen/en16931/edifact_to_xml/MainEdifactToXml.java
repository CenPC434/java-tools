/**
 * Copyright (C) 2016-2018 CEN TC/434 for EN 16931
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
