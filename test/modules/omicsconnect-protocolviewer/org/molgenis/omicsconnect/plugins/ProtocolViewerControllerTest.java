package org.molgenis.omicsconnect.plugins;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.observ.DataSet;
import org.molgenis.observ.ObservableFeature;
import org.molgenis.observ.Protocol;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProtocolViewerControllerTest
{

	@Test
	public void handleRequest_getDataset_protocol() throws Exception
	{
		// mock db
		Database db = mock(Database.class);

		DataSet dataSet = new DataSet();
		dataSet.setId(1);
		dataSet.setName("dataset");
		dataSet.setProtocolUsed(2);

		Protocol protocol = new Protocol();
		protocol.setId(2);
		protocol.setName("protocol");
		protocol.setFeatures_Id(Arrays.asList(3, 4));

		ObservableFeature feature1 = new ObservableFeature();
		feature1.setId(3);
		feature1.setName("feature1");

		ObservableFeature feature2 = new ObservableFeature();
		feature2.setId(4);
		feature2.setName("feature2");

		when(db.find(DataSet.class, new QueryRule(DataSet.ID, Operator.EQUALS, 1))).thenReturn(
				Collections.singletonList(dataSet));
		when(db.find(Protocol.class, new QueryRule(Protocol.ID, Operator.EQUALS, 2))).thenReturn(
				Collections.singletonList(protocol));
		when(db.find(ObservableFeature.class, new QueryRule(ObservableFeature.ID, Operator.IN, Arrays.asList(3, 4))))
				.thenReturn(Arrays.asList(feature1, feature2));

		ProtocolViewerController controller = new ProtocolViewerController("test", mock(ScreenController.class));

		// mock request
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		when(httpServletRequest.getMethod()).thenReturn("GET");
		MolgenisRequest request = new MolgenisRequest(httpServletRequest);
		request.set("datasetid", "1");

		request.set("__action", "download_json_getdataset");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			controller.handleRequest(db, request, bos);
			String output = new String(bos.toByteArray(), Charset.forName("UTF-8"));

			String expected = "{\"id\":1,\"name\":\"dataset\",\"protocol\":{\"id\":2,\"name\":\"protocol\",\"features\":[{\"id\":3,\"name\":\"feature1\",\"dataType\":\"string\"},{\"id\":4,\"name\":\"feature2\",\"dataType\":\"string\"}]}}";
			Assert.assertEquals(output, expected);
		}
		finally
		{
			bos.close();
		}
	}

	@Test
	public void handleRequest_getDataset_noProtocol() throws Exception
	{
		// mock db
		Database db = mock(Database.class);

		DataSet dataSet = new DataSet();
		dataSet.setId(1);
		dataSet.setName("dataset");

		when(db.find(DataSet.class, new QueryRule(DataSet.ID, Operator.EQUALS, 1))).thenReturn(
				Collections.singletonList(dataSet));

		ProtocolViewerController controller = new ProtocolViewerController("test", mock(ScreenController.class));

		// mock request
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		when(httpServletRequest.getMethod()).thenReturn("GET");
		MolgenisRequest request = new MolgenisRequest(httpServletRequest);
		request.set("datasetid", "1");

		request.set("__action", "download_json_getdataset");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			controller.handleRequest(db, request, bos);
			String output = new String(bos.toByteArray(), Charset.forName("UTF-8"));

			String expected = "{\"id\":1,\"name\":\"dataset\"}";
			Assert.assertEquals(output, expected);
		}
		finally
		{
			bos.close();
		}
	}
}
