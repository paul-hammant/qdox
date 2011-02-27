package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

public abstract class JavaPackageTest<P extends JavaPackage> extends TestCase {

    public abstract P newJavaPackage(String name);
    
	public void testToStringJavaLang() throws Exception {
		P pckg = newJavaPackage("java.lang");
		assertEquals("package java.lang", pckg.toString());
	}
}
