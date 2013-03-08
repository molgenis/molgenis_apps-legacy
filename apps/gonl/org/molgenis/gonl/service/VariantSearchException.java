package org.molgenis.gonl.service;

public class VariantSearchException extends Exception
{
	private static final long serialVersionUID = 1L;

	public VariantSearchException(String message)
	{
		super(message);
	}

	public VariantSearchException(Throwable cause)
	{
		super(cause);
	}
}
