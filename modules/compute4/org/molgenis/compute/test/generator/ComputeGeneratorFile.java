package org.molgenis.compute.test.generator;

import java.util.Hashtable;
import java.util.List;

import org.molgenis.compute.design.Workflow;
import org.molgenis.compute.test.temp.Target;
import org.molgenis.util.Tuple;

/**
 * Created with IntelliJ IDEA. User: georgebyelas Date: 23/08/2012 Time: 12:38
 * To change this template use File | Settings | File Templates.
 */
public class ComputeGeneratorFile implements ComputeGenerator
{
	public void generate(Workflow workflow, List<Target> targets, Hashtable<String, String> config)
	{

	}

	public void generateWithTuple(Workflow workflow, List<Tuple> targets, Hashtable<String, String> config)
	{

	}

	@Override
	public void generateTasks(Workflow workflow, List<Tuple> targets)
	{
		// TODO Auto-generated method stub

	}
}
