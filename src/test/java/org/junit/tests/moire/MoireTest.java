package org.junit.tests.moire;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.Request;
import org.junit.moire.*;

public class MoireTest
{
	@Test
	public void silentlyPass() {
		MoireResult result = Moire.run(Request.classes(GoodTest.class));
		assertEquals("Passed", result.getResultType());
		assertEquals("No test interference", result.toString());
	}

	@Test
	public void predictablyFailingTest() {
		MoireResult result = Moire.run(Request.classes(BadTest.class));
		assertEquals("Failed", result.getResultType());
		assertEquals("org.junit.tests.moire.BadTest.dos() fails", result.toString());	
	}

	@Test
	public void interferingTest() {
		MoireResult result = Moire.run(Request.classes(Interfering.class));
		assertEquals("Interference", result.getResultType());
		assertEquals("org.junit.tests.moire.Interfering.interferer() interferes with org.junit.tests.moire.Interfering.interfered()", result.toString());
	}
	@Test
	public void reverseInterferingTest()
	{
		Request request = Request.classes(Interfering.class);
		Request reverse = request.sortWith(Moire.reverseOrder(request.getRunner()));
		MoireResult result = Moire.run(reverse);
		assertEquals("Reverse Interference", result.getResultType());
		assertEquals("org.junit.tests.moire.Interfering.interferer() interferes with org.junit.tests.moire.Interfering.interfered() when the test suite is run in reverse", result.toString());
	}
	@Test
	public void MultiInterferingTest()
	{
		MoireResult result = Moire.run(Request.classes(MultiInterfering.class));
		assertEquals("Interference", result.getResultType());
		assertEquals("org.junit.tests.moire.MultiInterfering.firstInterferer() interferes with org.junit.tests.moire.MultiInterfering.secondInterferer()", result.toString());
	}
	@Test
	public void ComplexTest()
	{
		MoireResult result = Moire.run(Request.classes(Complex.class));
		assertEquals("Complex", result.getResultType());
		assertEquals("org.junit.tests.moire.Complex.a() fails due to interference from more than one other test", result.toString());
	}
}
