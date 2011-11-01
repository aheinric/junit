package org.junit.moire;

/**
 * An implementation of MoireResult representing a forward complex failure.
 * @author alexanderheinricher
 *
 */
public class ComplexTest implements MoireResult {
	private String testClass;
	private String method;
	public ComplexTest(String f)
	{
		testClass = f.substring(f.indexOf("(")+1, f.indexOf(")"));
		method = f.substring(0, f.indexOf("("));
	}
	public String getResultType() {
		// TODO Auto-generated method stub
		return "Complex";
	}
	public String getMessage()
	{
		return this.toString();
	}
	public String toString()
	{
		return testClass + "." + method + "() fails due to interference from more than one other test";
	}
}
