package org.junit.moire;

import org.junit.runner.notification.Failure;

/**
 * An implementation of MoireResults representing a normal failing test.
 * @author alexanderheinricher
 *
 */
public class FailingTest implements MoireResult {
	private String testClass;
	private String method;
	public FailingTest(String f)
	{
		testClass = f.substring(f.indexOf("(")+1, f.indexOf(")"));
		method = f.substring(0, f.indexOf("("));
	}
	public String getResultType()
	{
		return "Failed";
	}
	
	public String getMessage()
	{
		return this.toString();
	}
	
	public String toString()
	{
		return testClass + "." + method + "() fails";
	}
}
