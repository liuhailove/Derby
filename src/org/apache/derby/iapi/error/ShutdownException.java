package org.apache.derby.iapi.error;

/**
A ShutdownException is a runtime exception that is used
to notify code that the system has/is being shut down.
*/

public class ShutdownException extends RuntimeException
{
	public ShutdownException()
	{
		super("");
	}
}
