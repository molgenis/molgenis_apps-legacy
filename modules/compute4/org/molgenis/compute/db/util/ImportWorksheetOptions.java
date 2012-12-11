package org.molgenis.compute.db.util;

import org.molgenis.util.cmdline.CmdLineException;
import org.molgenis.util.cmdline.CmdLineParser;
import org.molgenis.util.cmdline.Option;

public class ImportWorksheetOptions
{
	public ImportWorksheetOptions(String[] args) throws CmdLineException
	{
		CmdLineParser parser = new CmdLineParser(this);
		parser.parse(args);
	}

	@Option(name = "McId", param = Option.Param.STRING, type = Option.Type.REQUIRED_ARGUMENT, usage = "This is the run identifier for this run")
	public String McId = null;

	@Option(name = "worksheet_file", param = Option.Param.STRING, type = Option.Type.REQUIRED_ARGUMENT, usage = "This is the worksheet file path")
	public String worksheet_file = null;

	@Option(name = "workflow_name", param = Option.Param.STRING, type = Option.Type.REQUIRED_ARGUMENT, usage = "This is the workflow name as in database")
	public String workflow_name = null;

    @Option(name = "backend_name", param = Option.Param.STRING, type = Option.Type.REQUIRED_ARGUMENT,
            usage = "This is the back-end name, where tasks should be submitted")
    public String backend_name = null;

}
