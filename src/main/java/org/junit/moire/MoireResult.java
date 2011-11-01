package org.junit.moire;

/**
 * An interface describing the results of a Moire test.
 * 
 * @author alexanderheinricher
 *
 */
public interface MoireResult {
	/**
	 * Returns a string code representing which result type the Moire test returned.
	 * @return  One of ?? values
	 * 
	 * <p>-"Passed":  No tests failed on either the forward or reverse runs.</p>
	 * <p>-"Failed":  A test failed when run in isolation.</p>
	 * <p>-"Interference":  A test failed when paired with another test that was run before it in the suite.</p>
	 * <p>-"Reverse Interference":  A test failed when paired with another test
	 * that was run before it when the test suite was reversed.  
	 * No tests failed when the suite was run in normal order.</p>
	 * <p>-"Complex":  A test failed, but no single other test was to blame,
	 * meaning more than one test run before it in the suite caused the failure.</p>
	 * <p>-"Reverse Complex":  A test failed in the reverse run, but no single other test was to blame,
	 * meaning more than one test run before it in the suite caused the failure.  
	 * No tests failed when the suite was run in normal order.</p>
	 */
	public abstract String getResultType();
	
	/**
	 * Retrieves the details of the result.
	 * @return A string detailing the results of the moire test.
	 */
	public abstract String getMessage();
}
