package com.thoughtworks.qdox.model;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.thoughtworks.qdox.DataProvider;

public class JavaMethodTest extends TestCase {

	private JavaMethod mth;

	public JavaMethodTest(String s) {
		super(s);
	}

	protected void setUp() throws Exception {
		super.setUp();
		mth = new JavaMethod();
	}

	public void testToStringSimple() throws Exception {
		mth.setName("doSomething");
		mth.setReturns(new Type(new ArrayList(), "java.lang.String", new ArrayList(), ""));
		assertEquals("java.lang.String doSomething();\n", mth.toString());
	}

	public void testToStringOneParam() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ArrayList(), ""));
		mth.setParameters(new JavaParameter[]{ new JavaParameter(DataProvider.createType("String"), "thingy", 0) });
		assertEquals("void blah(String thingy);\n", mth.toString());
	}

	public void testToStringTwoParams() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ArrayList(), ""));
		mth.setParameters(new JavaParameter[]{
			new JavaParameter(DataProvider.createType("int"), "count", 0),
			new JavaParameter(DataProvider.createType("MyThing"), "t", 0)
		});
		assertEquals("void blah(int count, MyThing t);\n", mth.toString());
	}

	public void testToStringThreeParams() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ArrayList(), ""));
		mth.setParameters(new JavaParameter[]{
			new JavaParameter(DataProvider.createType("int"), "count", 0),
			new JavaParameter(DataProvider.createType("MyThing"), "t", 0),
			new JavaParameter(DataProvider.createType("java.lang.Meat"), "beef", 0)
		});
		assertEquals("void blah(int count, MyThing t, java.lang.Meat beef);\n", mth.toString());
	}

	public void testToStringModifiersWithAccessLevelFirst() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ArrayList(), ""));
		mth.setModifiers(new String[] {"synchronized", "public", "final"});
		assertEquals("public synchronized final void blah();\n", mth.toString());
	}

	public void testToStringOneException() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ArrayList(), ""));
		mth.setExceptions(new Type[] {DataProvider.createType("RuntimeException")});
		assertEquals("void blah() throws RuntimeException;\n", mth.toString());
	}

	public void testToStringTwoException() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ArrayList(), ""));
		mth.setExceptions(new Type[] {DataProvider.createType("RuntimeException"), DataProvider.createType("java.lang.SheepException")});
		assertEquals("void blah() throws RuntimeException, java.lang.SheepException;\n", mth.toString());
	}

	public void testToStringThreeException() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ArrayList(), ""));
		mth.setExceptions(new Type[] {DataProvider.createType("RuntimeException"), DataProvider.createType("java.lang.SheepException"), DataProvider.createType("CowException")});
		assertEquals("void blah() throws RuntimeException, java.lang.SheepException, CowException;\n", mth.toString());
	}

	public void testToStringConstructor() throws Exception {
		mth.setName("Blah");
		mth.setModifiers(new String[] { "public" });
		mth.setConstructor(true);
		assertEquals("public Blah();\n", mth.toString());
	}

	public void testToStringWithComment() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ArrayList(), ""));
		mth.setComment("Hello");
		String expect = ""
			+ "/**\n"
			+ " * Hello\n"
			+ " */\n"
			+ "void blah();\n";
		assertEquals(expect, mth.toString());
	}

	public void testToString1dArray() throws Exception {
		mth.setName("doSomething");
		mth.setReturns(new Type(new ArrayList(), "java.lang.String", new ArrayList(), ""));
		mth.setDimensions(1);
		assertEquals("java.lang.String[] doSomething();\n", mth.toString());
	}

	public void testToString2dArray() throws Exception {
		mth.setName("doSomething");
		mth.setReturns(new Type(new ArrayList(), "java.lang.String", new ArrayList(), ""));
		mth.setDimensions(2);
		assertEquals("java.lang.String[][] doSomething();\n", mth.toString());
	}

	public void testToStringParamArray() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ArrayList(), ""));
		mth.setParameters(new JavaParameter[]{
			new JavaParameter(DataProvider.createType("int"), "count", 2),
			new JavaParameter(DataProvider.createType("MyThing"), "t", 1)
		});
		assertEquals("void blah(int[][] count, MyThing[] t);\n", mth.toString());
	}


}
