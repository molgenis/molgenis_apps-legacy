package org.molgenis.compute.db.generator;

import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.compute.design.Workflow;
import org.molgenis.compute.db.temp.Target;
import org.molgenis.util.Tuple;

import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: georgebyelas Date: 22/08/2012 Time: 11:29
 * To change this template use File | Settings | File Templates.
 */
public interface ComputeGenerator
{
    //where it is used
	void generate(Workflow workflow, List<ComputeParameter> parameters, List<Target> targets, Hashtable<String, String> config);

	void generateWithTuple(Workflow workflow, List<Tuple> targets, Hashtable<String, String> config);

	void generateTasks(Workflow workflow, List<ComputeParameter> parameters, List<Tuple> targets, String backend_name);
}
