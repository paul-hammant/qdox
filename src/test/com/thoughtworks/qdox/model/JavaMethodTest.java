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
		mth.setReturns(new Type(new ArrayList(), "java.lang.String", new ClassLibrary(), "", 0));
		assertEquals("java.lang.String doSomething();\n", mth.toString());
	}

	public void testToStringOneParam() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ClassLibrary(), "", 0));
		mth.setParameters(new JavaParameter[]{ new JavaParameter(DataProvider.createType("String", 0), "thingy") });
		assertEquals("void blah(String thingy);\n", mth.toString());
	}

	public void testToStringTwoParams() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ClassLibrary(), "", 0));
		mth.setParameters(new JavaParameter[]{
			new JavaParameter(DataProvider.createType("int", 0), "count"),
			new JavaParameter(DataProvider.createType("MyThing", 0), "t")
		});
		assertEquals("void blah(int count, MyThing t);\n", mth.toString());
	}

	public void testToStringThreeParams() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ClassLibrary(), "", 0));
		mth.setParameters(new JavaParameter[]{
			new JavaParameter(DataProvider.createType("int", 0), "count"),
			new JavaParameter(DataProvider.createType("MyThing", 0), "t"),
			new JavaParameter(DataProvider.createType("java.lang.Meat", 0), "beef")
		});
		assertEquals("void blah(int count, MyThing t, java.lang.Meat beef);\n", mth.toString());
	}

	public void testToStringModifiersWithAccessLevelFirst() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ClassLibrary(), "", 0));
		mth.setModifiers(new String[] {"synchronized", "public", "final"});
		assertEquals("public synchronized final void blah();\n", mth.toString());
	}

	public void testToStringOneException() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ClassLibrary(), "", 0));
		mth.setExceptions(new Type[] {DataProvider.createType("RuntimeException", 0)});
		assertEquals("void blah() throws RuntimeException;\n", mth.toString());
	}

	public void testToStringTwoException() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ClassLibrary(), "", 0));
		mth.setExceptions(new Type[] {DataProvider.createType("RuntimeException", 0), DataProvider.createType("java.lang.SheepException", 1)});
		assertEquals("void blah() throws RuntimeException, java.lang.SheepException;\n", mth.toString());
	}

	public void testToStringThreeException() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ClassLibrary(), "", 0));
		mth.setExceptions(new Type[] {DataProvider.createType("RuntimeException", 0), DataProvider.createType("java.lang.SheepException", 1), DataProvider.createType("CowException", 1)});
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
		mth.setReturns(new Type(new ArrayList(), "void", new ClassLibrary(), "", 0));
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
		mth.setReturns(new Type(new ArrayList(), "java.lang.String", new ClassLibrary(), "", 1));
		assertEquals("java.lang.String[] doSomething();\n", mth.toString());
	}

	public void testToString2dArray() throws Exception {
		mth.setName("doSomething");
		mth.setReturns(new Type(new ArrayList(), "java.lang.String", new ClassLibrary(), "", 2));
		assertEquals("java.lang.String[][] doSomething();\n", mth.toString());
	}

	public void testToStringParamArray() throws Exception {
		mth.setName("blah");
		mth.setReturns(new Type(new ArrayList(), "void", new ClassLibrary(), "", 0));
		mth.setParameters(new JavaParameter[]{
			new JavaParameter(DataProvider.createType("int",2 ), "count"),
			new JavaParameter(DataProvider.createType("MyThing", 1), "t")
		});
		assertEquals("void blah(int[][] count, MyThing[] t);\n", mth.toString());
	}

	public void testEquals() throws Exception {
		mth.setName("thing");
		mth.setReturns(new Type("void", 0));

		JavaMethod m2 = new JavaMethod();
		m2.setName("thing");
		m2.setReturns(new Type("void", 0));

		JavaMethod m3 = new JavaMethod();
		m3.setName("thingy");
		m3.setReturns(new Type("void", 0));

		JavaMethod m4 = new JavaMethod();
		m4.setName("thing");
		m4.setReturns(new Type("int", 0));

		assertEquals(mth, m2);
		assertEquals(m2, mth);
		assertNotEquals(mth, m3);
		assertNotEquals(mth, m4);
	}

	public void testEqualsWithParameters() throws Exception {
		mth.setName("thing");
		mth.setParameters(new JavaParameter[] {
			new JavaParameter(new Type("int", 1), "blah"),
			new JavaParameter(new Type("java.lang.String", 2), "thing"),
			new JavaParameter(new Type("X", 3), "")
		});
		mth.setReturns(new Type("void", 0));

		JavaMethod m2 = new JavaMethod();
		m2.setName("thing");
		m2.setParameters(new JavaParameter[] {
			new JavaParameter(new Type("int", 1), "blah"),
			new JavaParameter(new Type("java.lang.String", 2), "anotherName"),
			new JavaParameter(new Type("X", 3), "blah")
		});
		m2.setReturns(new Type("void", 0));

		JavaMethod m3 = new JavaMethod();
		m3.setName("thing");
		m3.setParameters(new JavaParameter[] {
			new JavaParameter(new Type("int", 1), "blah"),
			new JavaParameter(new Type("java.lang.String", 2), "thing"),
		});
		m3.setReturns(new Type("void", 0));

		JavaMethod m4 = new JavaMethod();
		m4.setName("thing");
		m4.setParameters(new JavaParameter[] {
			new JavaParameter(new Type("int", 1), "blah"),
			new JavaParameter(new Type("java.lang.String", 2), "thing"),
			new JavaParameter(new Type("TTTTTTTT", 3), "blah") // name
		});
		m4.setReturns(new Type("void", 0));

		JavaMethod m5 = new JavaMethod();
		m5.setName("thing");
		m5.setParameters(new JavaParameter[] {
			new JavaParameter(new Type("int", 1), "blah"),
			new JavaParameter(new Type("java.lang.String", 2), "thing"),
			new JavaParameter(new Type("X", 9), "blah") // dimension
		});
		m5.setReturns(new Type("void", 0));

		assertEquals(mth, m2);
		assertEquals(m2, mth);
		assertNotEquals(mth, m3);
		assertNotEquals(mth, m4);
		assertNotEquals(mth, m5);
	}

	public void testHashCode() throws Exception {
		mth.setName("thing");
		mth.setParameters(new JavaParameter[] {
			new JavaParameter(new Type("int", 1), "blah"),
			new JavaParameter(new Type("java.lang.String", 2), "thing"),
			new JavaParameter(new Type("X", 3), "")
		});
		mth.setReturns(new Type("void", 0));

		JavaMethod m2 = new JavaMethod();
		m2.setName("thing");
		m2.setParameters(new JavaParameter[] {
			new JavaParameter(new Type("int", 1), "blah"),
			new JavaParameter(new Type("java.lang.String", 2), "anotherName"),
			new JavaParameter(new Type("X", 3), "blah")
		});
		m2.setReturns(new Type("void", 0));

		JavaMethod m3 = new JavaMethod();
		m3.setName("thing");
		m3.setParameters(new JavaParameter[] {
			new JavaParameter(new Type("int", 1), "blah"),
			new JavaParameter(new Type("java.lang.String", 2), "thing"),
		});
		m3.setReturns(new Type("void", 0));

		assertEquals(mth.hashCode(), m2.hashCode());
		assertTrue(mth.hashCode() != m3.hashCode());
	}

	private void assertNotEquals(Object o1, Object o2) {
		assertTrue(o1.toString() + " should not equals " + o2.toString(), !o1.equals(o2));
	}

}
