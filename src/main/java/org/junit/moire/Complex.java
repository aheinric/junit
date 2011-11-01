package org.junit.moire;

import org.junit.Test;

/**
 * A test suite that results in a complex failure condition.
 * @author alexanderheinricher
 *
 */
public class Complex {
	static boolean first = false;
	static boolean second = false;
	@Test public void c() { first = true; }
	@Test public void b() {if(first){second = true;}}
	@Test public void a() { if(second){throw new Error();}}
}