package org.molgenis.compute.test.generator;

import java.util.Hashtable;
import java.util.List;

import org.molgenis.compute.design.Workflow;
import org.molgenis.compute.test.temp.Target;
import org.molgenis.util.Tuple;

/**
 * Created with IntelliJ IDEA. User: georgebyelas Date: 22/08/2012 Time: 11:29
 * To change this template use File | Settings | File Templates.
 */
public interface ComputeGenerator
{
	void generate(Workflow workflow, List<Target> targets, Hashtable<String, String> config);

	void generateWithTuple(Workflow workflow, List<Tuple> targets, Hashtable<String, String> config);

	void generateTasks(Workflow workflow, List<Tuple> targets);
}
