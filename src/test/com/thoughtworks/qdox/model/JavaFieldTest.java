package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

import com.thoughtworks.qdox.DataProvider;

public class JavaFieldTest extends TestCase {

	public JavaFieldTest(String s) {
		super(s);
	}

	public void testToString() throws Exception {
		JavaField fld = new JavaField();
		fld.setName("count");
		fld.setType(DataProvider.createType("int"));
		assertEquals("int count;\n", fld.toString());
	}

	public void testToStringWithModifiers() throws Exception {
		JavaField fld = new JavaField();
		fld.setName("count");
		fld.setType(DataProvider.createType("int"));
		fld.setModifiers(new String[] {"public", "final"});
		assertEquals("public final int count;\n", fld.toString());
	}

	public void testToStringWithComment() throws Exception {
		JavaField fld = new JavaField();
		fld.setName("count");
		fld.setType(DataProvider.createType("int"));
		fld.setComment("Hello");
		String expected = ""
			+ "/**\n"
			+ " * Hello\n"
			+ " */\n"
			+ "int count;\n";
		assertEquals(expected, fld.toString());
	}

	public void testToString1dArray() throws Exception {
		JavaField fld = new JavaField();
		fld.setName("count");
		fld.setType(DataProvider.createType("int"));
		fld.setDimensions(1);
		String expected = "int[] count;\n";
		assertEquals(expected, fld.toString());
	}

	public void testToString2dArray() throws Exception {
		JavaField fld = new JavaField();
		fld.setName("count");
		fld.setType(DataProvider.createType("int"));
		fld.setDimensions(2);
		String expected = "int[][] count;\n";
		assertEquals(expected, fld.toString());
	}

}
