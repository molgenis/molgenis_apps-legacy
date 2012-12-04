package org.molgenis.cbm;

import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.CbmNode;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class CbmXmlParser
{
	public CbmNode load(File xmlFile, File xsdFile) throws JAXBException, SAXException, ParserConfigurationException,
			IOException
	{
		JAXBContext jaxbContext = JAXBContext.newInstance(CbmNode.class.getPackage().getName());
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(xsdFile);
		unmarshaller.setSchema(schema);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(xmlFile);

		JAXBElement<CbmNode> result = unmarshaller.unmarshal(doc.getFirstChild(), CbmNode.class);
		return result.getValue();
	}
}
