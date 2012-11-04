package org.molgenis.omicsconnect.plugins;

import java.util.Collections;
import java.util.List;

import org.molgenis.omicsconnect.plugins.ProtocolViewerController.JSDataSet;

public class ProtocolViewer
{
	private List<JSDataSet> dataSets;

	public List<JSDataSet> getDataSets()
	{
		return dataSets != null ? dataSets : Collections.<JSDataSet> emptyList();
	}

	public void setDataSets(List<JSDataSet> dataSets)
	{
		this.dataSets = dataSets;
	}
}
