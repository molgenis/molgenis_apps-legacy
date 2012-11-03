package org.molgenis.omicsconnect;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.observ.ObservableFeature;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EMeasureEntityWriterTest
{

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testEMeasure()
	{
		new EMeasureEntityWriter(null);
	}

	@Test
	public void testConvert() throws IOException
	{
		StringWriter strWriter = new StringWriter();
		EMeasureEntityWriter eMeasure = new EMeasureEntityWriter(strWriter);

		List<ObservableFeature> observableFeatures = new ArrayList<ObservableFeature>();
		ObservableFeature observableFeature1 = new ObservableFeature();
		observableFeature1.setName("feature1");
		observableFeature1.setDescription("this is feature1");
		observableFeature1.setDataType("boolean");
		ObservableFeature observableFeature2 = new ObservableFeature();
		observableFeature2.setName("feature2");
		observableFeature2.setDescription("this is feature2");
		observableFeature2.setDataType("string");
		observableFeatures.add(observableFeature1);
		observableFeatures.add(observableFeature2);

		eMeasure.writeObservableFeatures(observableFeatures);

		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<QualityMeasureDocument classCode=\"CONTAINER\" moodCode=\"DEF\"\n"
				+ "  xmlns=\"urn:hl7-org:v3\"\n"
				+ "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
				+ "  xsi:schemaLocation=\"urn:hl7-org:v3 multicacheschemas/REPC_MT000100UV01.xsd\" xsi:type=\"REPC_MT000100UV01.Organizer\">\n"
				+ "  <subjectOf>\n" + "    <measureAttribute>\n"
				+ "      <code code=\"feature1\" codeSystem=\"TBD\" displayName=\"this is feature1\"/>\n"
				+ "      <value code=\"dunno\" codeSystem=\"TBD\"\n"
				+ "        displayName=\"This should be the mappingsname\" xsi:type=\"boolean\"/>\n"
				+ "    </measureAttribute>\n" + "  </subjectOf>\n" + "  <subjectOf>\n" + "    <measureAttribute>\n"
				+ "      <code code=\"feature2\" codeSystem=\"TBD\" displayName=\"this is feature2\"/>\n"
				+ "      <value code=\"dunno\" codeSystem=\"TBD\"\n"
				+ "        displayName=\"This should be the mappingsname\" xsi:type=\"string\"/>\n"
				+ "    </measureAttribute>\n" + "  </subjectOf>\n" + "</QualityMeasureDocument>\n";

		Assert.assertEquals(strWriter.toString(), expected);
	}
}
