/**
 * Copyright (C) 2016-2020 CEN TC/434 for EN 16931
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
/*
 * Apache 2.0 license is applied. Using Philip Helgers template for validating.
 */
package eu.cen.en16931.xmlvalidator;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.schematron.ISchematronResource;
import com.helger.schematron.SchematronHelper;
import com.helger.schematron.pure.SchematronResourcePure;
import com.helger.schematron.svrl.SVRLFailedAssert;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.SVRLMarshaller;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import com.helger.schematron.xslt.SchematronResourceSCH;
import com.helger.schematron.xslt.SchematronResourceXSLT;
import com.helger.xml.schema.XMLSchemaCache;
import com.helger.xml.transform.TransformSourceFactory;

/**
 * @author Andreas Pelekies (andreas.pelekies(at)validool.org)
 * @author Philip Helger
 */
public final class XMLValidator
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (XMLValidator.class);

  private static enum EMode
  {
    XSD ("XML schema"),
    PURE ("Pure Schematron"),
    SCH ("Schematron"),
    XSLT ("XSLT");

    private final String m_sName;

    private EMode (@Nonnull @Nonempty final String sName)
    {
      m_sName = sName;
    }

    @Nonnull
    @Nonempty
    public String getDisplayName ()
    {
      return m_sName;
    }

    public boolean isSchematronBased ()
    {
      return this == SCH || this == XSLT || this == PURE;
    }
  }

  public static void main (final String [] args)
  {
    File xmlFile = null;
    EMode eMode = null;
    File ruleFile = null;
    File svrlFile = null;

    if (args.length >= 2)
    {
      final String sType = args[0];
      final String sValue = args[1];
      if (sType.equals ("-xml"))
      {
        final File f = new File (sValue);
        if (f.exists () && f.isFile ())
          xmlFile = f;
        else
          s_aLogger.error ("The xml instance file '" + sValue + "' does not exist.");
      }
      else
        s_aLogger.error ("Invalidate file type '" + sType + "' provided (try '-xml').");
    }

    if (args.length >= 4)
    {
      final String sType = args[2];
      final String sValue = args[3];

      if (sType.equals ("-xsd"))
        eMode = EMode.XSD;
      else
        if (sType.equals ("-sch"))
          eMode = EMode.SCH;
        else
          if (sType.equals ("-xslt"))
            eMode = EMode.XSLT;
          else
            if (sType.equals ("-pure"))
              eMode = EMode.PURE;
      if (eMode != null)
      {
        final File f = new File (sValue);
        if (f.exists () && f.isFile ())
          ruleFile = f;
        else
          s_aLogger.error ("The " + eMode.getDisplayName () + " file '" + sValue + "' does not exist.");
      }
      else
        s_aLogger.error ("Invalid validation mode '" + sType + "' provided.");
    }

    if (args.length >= 6)
    {
      final String sType = args[4];
      final String sValue = args[5];

      if (sType.equals ("-svrl") && eMode != null && eMode.isSchematronBased ())
      {
        final File f = new File (sValue);
        if (!f.exists () || f.isFile ())
          svrlFile = f;
        else
          s_aLogger.error ("The SVRL file '" + sValue + "' does not exist.");
      }
      else
        s_aLogger.error ("Invalid optional type '" + sType + "' provided.");
    }

    if (xmlFile == null || eMode == null || ruleFile == null)
    {
      final String sJarName = "en16931-xml-validator-x.y.z-jar-with-dependencies.jar";
      s_aLogger.info ("Required commandline parameter is missing!");
      s_aLogger.info ("Usage:");
      s_aLogger.info ("  java -jar " + sJarName + " -xml file [-xsd|-sch|-xslt|-pure] file [-svrl file]");
      s_aLogger.info ("With the following allowed options:");
      s_aLogger.info ("   -xml filename");
      s_aLogger.info ("       The XML file to be validated. This parameter is mandatory");
      s_aLogger.info ("   -xsd filename");
      s_aLogger.info ("       The XML schema (XSD) file used for validation");
      s_aLogger.info ("   -sch filename");
      s_aLogger.info ("       The Schematron file used for validation (using XSLT based validation)");
      s_aLogger.info ("   -xslt filename");
      s_aLogger.info ("       The precompiled Schematron XSLT file used for validation");
      s_aLogger.info ("   -pure filename");
      s_aLogger.info ("       The Schematron file used for validation (using Pure XPath-only validation)");
      s_aLogger.info ("   -svrl filename");
      s_aLogger.info ("       The SVRL file which should be written (only for Schematron validation)");
      return;
    }

    s_aLogger.info ("=========================================");
    if (eMode == EMode.XSD)
    {
      s_aLogger.info ("Starting validation against XML Schema");
      s_aLogger.info ("Result: " + validateXMLSchema (ruleFile, xmlFile));
    }
    else
    {
      s_aLogger.info ("Starting validation against Schematron");
      s_aLogger.info ("Result: " + validateXMLSchematron (ruleFile, eMode, xmlFile, svrlFile));
    }
    s_aLogger.info ("Finished.");
    s_aLogger.info ("=========================================");
  }

  public static boolean validateXMLSchema (@Nonnull final File xsdPath, @Nonnull final File xmlPath)
  {
    try
    {
      System.setProperty ("jdk.xml.maxOccurLimit", "9999999");
      final Validator aValidator = XMLSchemaCache.getInstance ().getValidator (new FileSystemResource (xsdPath));
      aValidator.validate (TransformSourceFactory.create (xmlPath));
    }
    catch (final IOException | SAXException e)
    {
      s_aLogger.info ("Exception: " + e.getMessage ());
      return false;
    }
    return true;
  }

  public static boolean validateXMLSchematron (@Nonnull final File schPath,
                                               @Nonnull final EMode eMode,
                                               @Nonnull final File xmlPath,
                                               @Nullable final File svrlPath)
  {
    final FileSystemResource aXML = new FileSystemResource (xmlPath);
    final FileSystemResource aSCH = new FileSystemResource (schPath);

    // Use pure implementation or XSLT to do the conversion?
    final ISchematronResource aSchematron = eMode == EMode.PURE ? new SchematronResourcePure (aSCH)
                                                                : eMode == EMode.XSLT ? new SchematronResourceXSLT (aSCH)
                                                                                      : new SchematronResourceSCH (aSCH);
    final SchematronOutputType aSOT = SchematronHelper.applySchematron (aSchematron, aXML);
    if (aSOT == null)
    {
      s_aLogger.info ("Schematron file " + aSchematron + " is malformed!");
      return false;
    }

    // Write SVRL
    if (svrlPath != null)
      new SVRLMarshaller (false).write (aSOT, new FileSystemResource (svrlPath));

    final ICommonsList <SVRLFailedAssert> aFailedAsserts = SVRLHelper.getAllFailedAssertions (aSOT);
    if (aFailedAsserts.isNotEmpty ())
    {
      s_aLogger.info ("XML does not comply to Schematron!" +
                      (svrlPath != null ? " See SVRL for details: " + svrlPath : ""));
      return false;
    }

    s_aLogger.info ("XML complies to Schematron!");
    return true;
  }
}
