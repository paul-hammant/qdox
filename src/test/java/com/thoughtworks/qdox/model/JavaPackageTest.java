package com.thoughtworks.qdox.model;

import static org.mockito.Mockito.*;

import junit.framework.TestCase;

public abstract class JavaPackageTest<P extends JavaPackage> extends TestCase {

    public abstract P newJavaPackage(String name);
    
	public void testToStringJavaLang() throws Exception {
		P pckg = newJavaPackage("java.lang");
		assertEquals("package java.lang", pckg.toString());
	}
	
	public void testEquals() throws Exception 
	{
	    P pckg = newJavaPackage( "java.lang" );
	    
	    assertTrue( pckg.equals( pckg ) );
	    assertFalse( pckg.equals( null ) );
	    assertFalse( pckg.equals( new Object() ) );
	    
	    JavaPackage mockPckg = mock(JavaPackage.class);
	    when(mockPckg.getName()).thenReturn( "java.lang" );
	    
	    assertTrue( pckg.equals( mockPckg ) );
	    
	}
}
