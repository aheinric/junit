package org.junit.moire;

/**
 * An implementation of MoireResult that represents a passing test suite.
 * @author alexanderheinricher
 *
 */
public class Okay implements MoireResult {
	public String getResultType()
	{
		return "Passed";
	}
	
	public String getMessage()
	{
		return this.toString();
	}
	
	public String toString()
	{
		return "No test interference";
	}
}
