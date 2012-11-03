package org.molgenis.omicsconnect;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.molgenis.observ.ObservableFeature;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EMeasureEntityWriter implements Closeable
{
	private Writer writer;

	public EMeasureEntityWriter(OutputStream os)
	{
		if (os == null) throw new IllegalArgumentException("writer is null");
		this.writer = new OutputStreamWriter(os, Charset.forName("UTF-8"));
	}

	public void writeObservableFeatures(List<ObservableFeature> observableFeatures) throws IOException
	{
		StringBuilder strBuilder = new StringBuilder();

		strBuilder
				.append("<QualityMeasureDocument xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"REPC_MT000100UV01.Organizer\" xsi:schemaLocation=\"urn:hl7-org:v3 multicacheschemas/REPC_MT000100UV01.xsd\" classCode=\"CONTAINER\" moodCode=\"DEF\" xmlns=\"urn:hl7-org:v3\">");

		for (ObservableFeature observableFeature : observableFeatures)
		{
			appendObservableFeature(observableFeature, strBuilder);
		}

		strBuilder.append("</QualityMeasureDocument>");

		String xmlFormatted = this.format(strBuilder.toString());
		writer.write(xmlFormatted);
	}

	private StringBuilder appendObservableFeature(ObservableFeature m, StringBuilder strBuilder)
	{
		strBuilder.append("\t<subjectOf>\n" + "\t\t<measureAttribute>");
		String code = m.getName();
		String codeSystem = "TBD"; // FIXME hardcoded reference
		String displayName = m.getDescription();
		String datatype = m.getDataType();
		String codeDatatype = "dunno"; // FIXME hardcoded reference
		String codeSystemDatatype = "TBD"; // FIXME hardcoded reference
		String displayNameDatatype = "This should be the mappingsname"; // FIXME
																		// hardcoded
																		// reference
		strBuilder.append("<code code=\"").append(code).append("\" codeSystem=\"").append(codeSystem).append("\"")
				.append(" displayName=\"").append(displayName).append("\" />");

		strBuilder.append("<value xsi:type=\"").append(datatype).append("\" code=\"").append(codeDatatype)
				.append("\" codeSystem=\"").append(codeSystemDatatype).append("\"").append(" displayName=\"")
				.append(displayNameDatatype).append("\" />");

		strBuilder.append("\t\t</measureAttribute>\n" + "\t</subjectOf>");
		return strBuilder;
	}

	private String format(String unformattedXml) throws IOException
	{
		final Document document = parseXmlFile(unformattedXml);

		OutputFormat format = new OutputFormat(document);
		format.setLineWidth(65);
		format.setIndenting(true);
		format.setIndent(2);
		Writer out = new StringWriter();
		XMLSerializer serializer = new XMLSerializer(out, format);
		serializer.serialize(document);

		return out.toString();
	}

	private Document parseXmlFile(String in) throws IOException
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(in));
			return db.parse(is);
		}
		catch (ParserConfigurationException e)
		{
			throw new RuntimeException(e);
		}
		catch (SAXException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException
	{
		this.writer.close();
	}
}
