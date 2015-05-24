package org.apache.derby.shared.common.sanity;

import java.util.Hashtable;

/**
 * The SanityService provides assertion checking and debug
 * control.
 * <p>
 * Assertions and debug checks
 * can only be used for testing conditions that might occur
 * in development code but not in production code.	
 * <b>They are compiled out of production code.</b>
 * <p>
 * Uses of assertions should not add AssertFailure catches or
 * throws clauses; AssertFailure is under RuntimeException
 * in the java exception hierarchy. Our outermost system block
 * will bring the system down when it detects an assertion
 * failure.
 * <p>
 * In addition to ASSERTs in code, classes can choose to implement
 * an isConsistent method that would be used by ASSERTs, UnitTests,
 * and any other code wanting to check the consistency of an object.
 * <p>
 * Assertions are meant to be used to verify the state of the system
 * and bring the system down if the state is not correct. Debug checks
 * are meant to display internal information about a running system.
 * <p>
 * @see org.apache.derby.shared.common.sanity.AssertFailure
 */
public class SanityManager
{
	/**
	 * The build tool may be configured to alter
	 * this source file to reset the static final variables
	 * so that assertion and debug checks can be compiled out
	 * of the code.
	 */
	
	public static final boolean ASSERT=SanityState.ASSERT;
	public static final boolean DEBUG = SanityState.DEBUG;
	
	public static final String DEBUGDEBUG="DumpSanityDebug";
	
	/**
	 * debugStream holds a pointer to the debug stream for writing out
	 * debug messages.  It is cached at the first debug write request.
	 */
	static private java.io.PrintWriter debugStream =new java.io.PrintWriter(System.err);
	/**
	 * DebugFlags holds the values of all debug flags in
	 * the configuration file.
	 */
	static private Hashtable<String,Boolean>DebugFlags=new Hashtable<String,Boolean>();
	/**
	 * AllDebugOn and AllDebugOff override individual flags
	 */
	static private boolean AllDebugOn=false;
	static private boolean AllDebugOff=false;
	
	//
	// class interface
	//

	/**
	 * ASSERT checks the condition, and if it is
	 * false, throws AssertFailure.
	 * A message about the assertion failing is
	 * printed.
	 * <p>
	 * @see org.apache.derby.shared.common.sanity.AssertFailure
	 */
	public static final void ASSERT(boolean mustBeTrue)
	{
		if(DEBUG)
		{
			if(!mustBeTrue)
			{
				if(DEBUG)
				{
					AssertFailure af=new AssertFailure("ASSERT FAILURE");
					if(DEBUG_ON("AssertFailureTrace"))
					{
						showTrace(af);
					}
					throw af;
				}
				else
					throw new AssertFailure("ASSERT FAILURE");
			}
		}
	}
	/**
	 * ASSERT checks the condition, and if it is
	 * false, throws AssertFailure. The message will
	 * be printed and included in the assertion.
	 * <p>
	 * @see org.apache.derby.shared.common.sanity.AssertFailure
	 */
	public static final void ASSERT(boolean mustBeTrue,String msgIfFail)
	{
		if(DEBUG)
		{
			if(!mustBeTrue)
			{
				if(DEBUG)
				{
					AssertFailure af=new AssertFailure("ASSERT FAILURE "+msgIfFail);
					if(DEBUG_ON("AssertFailureTrace"))
					{
						showTrace(af);
					}
					throw af;s
				}
				else
					throw new AssertFailure("ASSERT FAILURE " +msgIfFail);
			}
		}
	}
	/**
	 * THROWASSERT throws AssertFailure. This is used in cases where
	 * the caller has already detected the assertion failure (such as
	 * in the default case of a switch). This method should be used,
	 * rather than throwing AssertFailure directly, to allow us to 
	 * centralize all sanity checking.  The message argument will
	 * be printed and included in the assertion.
     * <p>
	 * @param msgIfFail message to print with the assertion
	 *
	 * @see org.apache.derby.shared.common.sanity.AssertFailure
	 */
	
}
