package org.molgenis.compute.commandline;

import org.molgenis.compute.db.sysexecutor.SysCommandExecutor;
import org.testng.annotations.Test;

public class ComputeCommandLineTest
{

	@Test
	public void main() throws Exception
	{
		System.out.println("Working Directory = " + System.getProperty("user.dir"));

		String path = ComputeCommandLineTest.class.getResource("").getFile();
		String script = path + "createFile.sh";
		String command = "sh " + script;
		SysCommandExecutor cmdExecutor = new SysCommandExecutor();
		cmdExecutor.setWorkingDirectory(path);

		int status = cmdExecutor.runCommand(command);
		System.out.println(command);

		String cmdError = cmdExecutor.getCommandError();
		String cmdOutput = cmdExecutor.getCommandOutput();

		System.out.println(">> OUT/ERR: " + cmdOutput + cmdError);

		System.out.println(">> Exit status " + status);
	}
}
