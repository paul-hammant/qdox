package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

public class JavaSourceTest extends TestCase {

	private JavaSource source;

	public JavaSourceTest(String s) {
		super(s);
	}

	protected void setUp() throws Exception {
		super.setUp();
		source = new JavaSource();
	}

	public void testToStringOneClass() throws Exception {
		JavaClass cls = new JavaClass();
		cls.setName("MyClass");
		source.setClasses(new JavaClass[] {cls});
		String expected = ""
			+ "class MyClass {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, source.toString());
	}

	public void testToStringMultipleClass() throws Exception {
		JavaClass cls1 = new JavaClass();
		cls1.setName("MyClass1");
		JavaClass cls2 = new JavaClass();
		cls2.setName("MyClass2");
		JavaClass cls3 = new JavaClass();
		cls3.setName("MyClass3");
		source.setClasses(new JavaClass[] {cls1, cls2, cls3});
		String expected = ""
			+ "class MyClass1 {\n"
			+ "\n"
			+ "}\n"
			+ "\n"
			+ "class MyClass2 {\n"
			+ "\n"
			+ "}\n"
			+ "\n"
			+ "class MyClass3 {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, source.toString());
	}

	public void testToStringPackage() throws Exception {
		JavaClass cls = new JavaClass();
		cls.setName("MyClass");
		source.setClasses(new JavaClass[] {cls});
		source.setPackge("com.thing");
		String expected = ""
			+ "package com.thing;\n"
			+ "\n"
			+ "class MyClass {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, source.toString());
	}

	public void testToStringImport() throws Exception {
		JavaClass cls = new JavaClass();
		cls.setName("MyClass");
		source.setClasses(new JavaClass[] {cls});
		source.setImports(new String[] {"java.util.*"});
		String expected = ""
			+ "import java.util.*;\n"
			+ "\n"
			+ "class MyClass {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, source.toString());
	}

	public void testToStringMultipleImports() throws Exception {
		JavaClass cls = new JavaClass();
		cls.setName("MyClass");
		source.setClasses(new JavaClass[] {cls});
		source.setImports(new String[] {"java.util.*", "com.blah.Thing", "xxx"});
		String expected = ""
			+ "import java.util.*;\n"
			+ "import com.blah.Thing;\n"
			+ "import xxx;\n"
			+ "\n"
			+ "class MyClass {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, source.toString());
	}

	public void testToStringImportAndPackage() throws Exception {
		JavaClass cls = new JavaClass();
		cls.setName("MyClass");
		source.setClasses(new JavaClass[] {cls});
		source.setImports(new String[] {"java.util.*"});
		source.setPackge("com.moo");
		String expected = ""
			+ "package com.moo;\n"
			+ "\n"
			+ "import java.util.*;\n"
			+ "\n"
			+ "class MyClass {\n"
			+ "\n"
			+ "}\n";
		assertEquals(expected, source.toString());
	}

}
