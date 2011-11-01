package org.junit.moire;
/**
 * An implementation of MoireResult that represents a failure caused by interference when the suite is run in reverse.
 * @author alexanderheinricher
 *
 */
public class RevInterferingTest implements MoireResult {
	private String firstClass;
	private String firstMethod;
	private String secondClass;
	private String secondMethod;
	public RevInterferingTest(String interfering, String interfered)
	{
		firstClass = interfering.substring(interfering.indexOf("(")+1, interfering.indexOf(")"));
		firstMethod = interfering.substring(0, interfering.indexOf("("));
		secondClass = interfered.substring(interfered.indexOf("(")+1, interfered.indexOf(")"));
		secondMethod = interfered.substring(0, interfered.indexOf("("));
	}
	public String getResultType() {
		return "Reverse Interference";
	}
	
	public String getMessage()
	{
		return this.toString();
	}
	
	public String toString()
	{
		return firstClass + "." + firstMethod + "() interferes with " + secondClass + "." + secondMethod + "() when the test suite is run in reverse";
	}

}
