package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

public class JavaPackageTest extends TestCase {

	public void testToStringJavaLang() throws Exception {
		JavaPackage pckg = new JavaPackage("java.lang");
		assertEquals("package java.lang", pckg.toString());
	}
}
