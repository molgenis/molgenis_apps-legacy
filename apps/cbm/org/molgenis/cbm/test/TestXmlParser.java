package org.molgenis.cbm.test;

import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.CbmNode;
import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.CollectionProtocol;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.molgenis.cbm.CbmXmlParser;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

public class TestXmlParser
{
	@Test
	public void test1() throws JAXBException, SAXException, ParserConfigurationException, IOException
	{
		// load file
		String xmlFile = "/Users/mswertz/Downloads/cbm-demo.xml";
		String xsdFile = "/Users/mswertz/Downloads/CBM.xsd";

		// unmarshal
		CbmNode node = new CbmXmlParser().load(new File(xmlFile), new File(xsdFile));

		for (CollectionProtocol prot : node.getProtocols().getCollectionProtocol())
		{
			System.out.println(prot.getIdentifier());
		}

	}
}
