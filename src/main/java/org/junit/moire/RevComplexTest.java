package org.junit.moire;
/**
 * An implementation of MoireResult representing the case where multiple tests interfere when the suite is run in reverse.
 * @author alexanderheinricher
 *
 */
public class RevComplexTest implements MoireResult {

	private String testClass;
	private String method;
	public RevComplexTest(String f)
	{
		testClass = f.substring(f.indexOf("(")+1, f.indexOf(")"));
		method = f.substring(0, f.indexOf("("));
	}
	public String getResultType() {
		// TODO Auto-generated method stub
		return "Reverse Complex";
	}
	
	public String getMessage()
	{
		return this.toString();
	}
	
	public String toString()
	{
		return testClass + "." + method + "() fails due to interference from more than one other test when the suite is run in reverse";
	}

}
