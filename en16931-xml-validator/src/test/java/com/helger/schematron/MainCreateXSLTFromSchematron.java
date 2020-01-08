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
package com.helger.schematron;

import java.io.File;
import java.io.OutputStream;

import javax.xml.XMLConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.file.FileHelper;
import com.helger.schematron.svrl.CSVRL;
import com.helger.schematron.xslt.ISchematronXSLTBasedProvider;
import com.helger.schematron.xslt.SchematronResourceSCH;
import com.helger.xml.XMLHelper;
import com.helger.xml.namespace.MapBasedNamespaceContext;
import com.helger.xml.serialize.write.XMLWriter;
import com.helger.xml.serialize.write.XMLWriterSettings;

import eu.cen.en16931.xmlvalidator.XMLValidator;

public class MainCreateXSLTFromSchematron
{
  private static final Logger logger = LoggerFactory.getLogger (XMLValidator.class);

  public static void main (final String [] args)
  {
    final File aSchFile = new File ("../../../schematron/EN16931-EDIFACT-validation.sch");
    final File aXsltFile = new File ("../../../stylesheet/EN16931-EDIFACT-validation-compiled.xsl");

    logger.info ("Reading Schematron");
    final SchematronResourceSCH aSch = SchematronResourceSCH.fromFile (aSchFile);
    final ISchematronXSLTBasedProvider aXsltProvider = aSch.getXSLTProvider ();

    // Write the resulting XSLT file to disk
    final MapBasedNamespaceContext aNSContext = new MapBasedNamespaceContext ().addMapping ("svrl",
                                                                                            CSVRL.SVRL_NAMESPACE_URI);
    // Add all namespaces from XSLT document root
    final String sNSPrefix = XMLConstants.XMLNS_ATTRIBUTE + ":";
    XMLHelper.getAllAttributesAsMap (aXsltProvider.getXSLTDocument ().getDocumentElement ())
             .forEach ( (sAttrName, sAttrValue) -> {
               if (sAttrName.startsWith (sNSPrefix))
                 aNSContext.addMapping (sAttrName.substring (sNSPrefix.length ()), sAttrValue);
             });

    final XMLWriterSettings aXWS = new XMLWriterSettings ();
    aXWS.setNamespaceContext (aNSContext).setPutNamespaceContextPrefixesInRoot (true);

    logger.info ("Writing XSLT");
    final OutputStream aOS = FileHelper.getOutputStream (aXsltFile);
    XMLWriter.writeToStream (aXsltProvider.getXSLTDocument (), aOS, aXWS);

    logger.info ("Done");
  }
}
