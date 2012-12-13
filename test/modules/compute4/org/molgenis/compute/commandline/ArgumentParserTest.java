package org.molgenis.compute.commandline;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.testng.annotations.Test;

public class ArgumentParserTest
{
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void parseParametersNull()
	{
		Exiter mockExiter = mock(Exiter.class);

		ArgumentParser.parseParameters(null, mockExiter);
	}

	@Test
	public void parseParametersWrong()
	{
		String[] args = new String[]
		{ "-bla" };

		Exiter mockExiter = mock(Exiter.class);

		ArgumentParser.parseParameters(args, mockExiter);
		verify(mockExiter).exit();
	}
}
