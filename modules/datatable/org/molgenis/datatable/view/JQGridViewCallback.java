package org.molgenis.datatable.view;

import org.molgenis.datatable.model.TupleTable;
import org.molgenis.framework.server.MolgenisRequest;

public interface JQGridViewCallback
{
	void beforeLoadConfig(MolgenisRequest request, TupleTable tupleTable);
}
