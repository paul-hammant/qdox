package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

public abstract class JavaPackageTest extends TestCase {

    public abstract JavaPackage newJavaPackage(String name);
    
	public void testToStringJavaLang() throws Exception {
		JavaPackage pckg = newJavaPackage("java.lang");
		assertEquals("package java.lang", pckg.toString());
	}
}
