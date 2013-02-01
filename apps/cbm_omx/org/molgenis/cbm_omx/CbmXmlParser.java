package org.molgenis.cbm_omx;

import gme.cacore_cacore._3_2.gov_nih_nci_cbm_domain.CbmNode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.molgenis.observ.ObservableFeature;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sun.xml.internal.ws.wsdl.writer.document.http.Address;

public class CbmXmlParser
{

	public CbmNode load(File xmlFile, File xsdFile) throws JAXBException, SAXException, ParserConfigurationException,
			IOException
	{
		// set up JAXB

		// Create a JAXB context passing in the class of the object we want to
		// marshal/unmarshal
		JAXBContext jaxbContext = JAXBContext.newInstance(CbmNode.class.getPackage().getName());

		// marshalling Object to XML
		// Create the unmarshaller, this is the nifty little thing that will
		// actually transform the XML back into an object
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(xsdFile);
		unmarshaller.setSchema(schema);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(xmlFile);

		// Unmarshal the XML in the stringWriter back into an object
		JAXBElement<CbmNode> result = unmarshaller.unmarshal(doc.getFirstChild(), CbmNode.class);

		// Print out the contents of the JavaObject we just unmarshalled from
		// the XML
		System.out.println(result.toString());

		for (Field field : Address.class.getFields())
		{
			String featureName = field.getName();

			ObservableFeature newFeatureObject = new ObservableFeature();
		}

		return result.getValue();
	}
}
