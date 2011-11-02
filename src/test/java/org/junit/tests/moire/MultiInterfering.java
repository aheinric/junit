package org.junit.tests.moire;

import org.junit.Test;

/**
 * A test suite with tests that interfere with each other regardless of how they are run.
 * @author alexanderheinricher
 *
 */
public class MultiInterfering {
	static boolean flag = false;
	@Test public void firstInterferer() { if(!flag){flag = true;} else{throw new Error();} }
	@Test public void secondInterferer() { if(!flag){flag = true;} else{throw new Error();} }

}
