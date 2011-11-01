package org.junit.moire;

import org.junit.Test;

/**
 * A test suite that contains a test that fails in its own right.
 * @author alexanderheinricher
 *
 */
public  class BadTest {
	@Test public void uno() {}
	@Test public void dos() { throw new Error(); }
	@Test public void tres() {}
}
