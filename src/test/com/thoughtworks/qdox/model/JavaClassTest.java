package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

import com.thoughtworks.qdox.DataProvider;

import java.util.Collections;

public class JavaClassTest extends TestCase {

	private JavaClass cls;

	public JavaClassTest(String s) {
		super(s);
	}

	protected void setUp() throws Exception {
		super.setUp();
		cls = new JavaClass();
	}

	public void testToStringSimpleClass() throws Exception {
		cls.setName("MyClass");
		String expected = ""
			+ "class MyClass {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, cls.toString());
	}

	public void testToStringSimpleInterface() throws Exception {
		cls.setName("MyClass");
		cls.setInterface(true);
		String expected = ""
			+ "interface MyClass {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, cls.toString());
	}

	public void testToStringClassExtends() throws Exception {
		cls.setName("MyClass");
		cls.setSuperClass(DataProvider.createType("SuperClass"));
		String expected = ""
			+ "class MyClass extends SuperClass {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, cls.toString());
	}

	public void testToStringInterfaceExtends() throws Exception {
		cls.setName("MyClass");
		cls.setImplementz(type(new String[] {"SomeInterface"}));
		cls.setInterface(true);
		String expected = ""
			+ "interface MyClass extends SomeInterface {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, cls.toString());
	}

	public void testToStringInterfaceExtendsTwo() throws Exception {
		cls.setName("MyClass");
		cls.setImplementz(type(new String[] {"SomeInterface", "AnotherInterface"}));
		cls.setInterface(true);
		String expected = ""
			+ "interface MyClass extends SomeInterface, AnotherInterface {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, cls.toString());
	}

	public void testToStringInterfaceExtendsThree() throws Exception {
		cls.setName("MyClass");
		cls.setImplementz(type(new String[] {"SomeInterface", "AnotherInterface", "Thingy"}));
		cls.setInterface(true);
		String expected = ""
			+ "interface MyClass extends SomeInterface, AnotherInterface, Thingy {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, cls.toString());
	}

	public void testToStringClassImplements() throws Exception {
		cls.setName("MyClass");
		cls.setImplementz(type(new String[] {"SomeInterface"}));
		String expected = ""
			+ "class MyClass implements SomeInterface {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, cls.toString());
	}

	public void testToStringClassImplementsTwo() throws Exception {
		cls.setName("MyClass");
		cls.setImplementz(type(new String[] {"SomeInterface", "AnotherInterface", "Xx"}));
		String expected = ""
			+ "class MyClass implements SomeInterface, AnotherInterface, Xx {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, cls.toString());
	}

	public void testToStringClassImplementsAndExtends() throws Exception {
		cls.setName("MyClass");
		cls.setImplementz(type(new String[] {"SomeInterface", "AnotherInterface", "Xx"}));
		cls.setSuperClass(DataProvider.createType("SubMarine"));
		String expected = ""
			+ "class MyClass extends SubMarine implements SomeInterface, AnotherInterface, Xx {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, cls.toString());
	}

	public void testToStringModifers() throws Exception {
		cls.setName("MyClass");
		cls.setModifiers(new String[] {"public", "final"});
		String expected = ""
			+ "public final class MyClass {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, cls.toString());
	}

	public void testToStringModifersProtectionAlwaysFirst() throws Exception {
		cls.setName("MyClass");
		cls.setModifiers(new String[] {"final", "public"});
		String expected = ""
			+ "public final class MyClass {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, cls.toString());

		cls.setModifiers(new String[] {"abstract", "protected"});
		expected = ""
			+ "protected abstract class MyClass {\n"
			+ "\n"
			+ "}\n";
        assertEquals(expected, cls.toString());
	}

	public void testToStringClassWithOneMethod() throws Exception {
		cls.setName("MyClass");
		JavaMethod mth = new JavaMethod();
		mth.setName("doStuff");
		mth.setReturns(DataProvider.createType("void"));
		cls.addMethod(mth);
		String expected = ""
			+ "class MyClass {\n"
			+ "\n"
			+ "\tvoid doStuff();\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, cls.toString());
	}

	public void testToStringClassWithThreeMethods() throws Exception {
		cls.setName("MyClass");

		{
			JavaMethod mth = new JavaMethod();
			mth.setName("doStuff");
			mth.setReturns(DataProvider.createType("void"));
			cls.addMethod(mth);
		}

		{
			JavaMethod mth = new JavaMethod();
			mth.setName("somethingElse");
			mth.setReturns(DataProvider.createType("Goose"));
			cls.addMethod(mth);
		}

		{
			JavaMethod mth = new JavaMethod();
			mth.setName("eat");
			mth.setReturns(DataProvider.createType("void"));
			cls.addMethod(mth);
		}

		String expected = ""
			+ "class MyClass {\n"
			+ "\n"
			+ "\tvoid doStuff();\n"
			+ "\n"
			+ "\tGoose somethingElse();\n"
			+ "\n"
			+ "\tvoid eat();\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, cls.toString());
	}

	public void testToStringClassWithTwoFields() throws Exception {
		cls.setName("MyClass");

		{
			JavaField fld = new JavaField();
			fld.setName("count");
			fld.setType(DataProvider.createType("int"));
			cls.addField(fld);
		}

		{
			JavaField fld = new JavaField();
			fld.setName("thing");
			fld.setType(DataProvider.createType("String"));
			fld.setModifiers(new String[] {"public"});
			cls.addField(fld);
		}

		String expected = ""
			+ "class MyClass {\n"
			+ "\n"
			+ "\tint count;\n"
			+ "\n"
			+ "\tpublic String thing;\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, cls.toString());
	}

	public void testToStringClassWithComment() throws Exception {
		cls.setName("MyClass");
		cls.setComment("Hello World");

		String expected = ""
			+ "/**\n"
			+ " * Hello World\n"
			+ " */\n"
			+ "class MyClass {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, cls.toString());
	}

	public void testToStringClassWithIndentedCommentsForFieldAndMethod() throws Exception {
		cls.setName("MyClass");
		cls.setComment("Hello World");

		JavaMethod mth = new JavaMethod();
		mth.setReturns(DataProvider.createType("String"));
		mth.setName("thingy");
		mth.setComment("Hello Method");
		cls.addMethod(mth);

		JavaField fld = new JavaField();
		fld.setType(DataProvider.createType("String"));
		fld.setName("thing");
		fld.setComment("Hello Field");
		cls.addField(fld);

		String expected = ""
			+ "/**\n"
			+ " * Hello World\n"
			+ " */\n"
			+ "class MyClass {\n"
			+ "\n"
			+ "\t/**\n"
			+ "\t * Hello Field\n"
			+ "\t */\n"
			+ "\tString thing;\n"
			+ "\n"
			+ "\t/**\n"
			+ "\t * Hello Method\n"
			+ "\t */\n"
			+ "\tString thingy();\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, cls.toString());
	}
	
	public void testIsContainingClass(){
		cls.setName("MyClass");
		assertTrue(!cls.isContainingClass());
		
		cls.setModifiers(new String[]{"public"});
		assertTrue(cls.isContainingClass());
	}
	
	public void testGettingPackageFromClass() throws Exception {
		JavaSource source = new JavaSource();
		source.setPackge("com.thoughtworks.qdox");
		
		cls.setName("MyClass");
		
		source.setClasses(new JavaClass[]{cls});
		
		assertEquals("com.thoughtworks.qdox", cls.getPackage());
		assertEquals("com.thoughtworks.qdox.MyClass", cls.getFullyQualifiedName());
	}

	private Type[] type(String[] typeNames) {
		Type[] result = new Type[typeNames.length];
		for (int i = 0; i < typeNames.length; i++) {
			result[i] = new Type(Collections.EMPTY_LIST, typeNames[i], null, "");
		}
		return result;
	}
}
