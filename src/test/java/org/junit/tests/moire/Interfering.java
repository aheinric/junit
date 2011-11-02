package org.junit.tests.moire;

import org.junit.Test;
/**
 * A test suite containing a pair of tests that interfere on the forward run.
 * @author alexanderheinricher
 *
 */
public class Interfering {
	static boolean flag = false;
	@Test public void interferer() { flag = true; }
	@Test public void interfered() { if (flag) throw new Error(); }
}