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
        source.setClassLibrary(new ClassLibrary(null));
    }

    public void testToStringOneClass() throws Exception {
        JavaClass cls = new JavaClass(null);
        cls.setName("MyClass");
        source.addClass(cls);
        String expected = ""
                + "class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, source.toString());
    }

    public void testToStringMultipleClass() throws Exception {
        JavaClass cls1 = new JavaClass(null);
        cls1.setName("MyClass1");
        source.addClass(cls1);
        JavaClass cls2 = new JavaClass(null);
        cls2.setName("MyClass2");
        source.addClass(cls2);
        JavaClass cls3 = new JavaClass(null);
        cls3.setName("MyClass3");
        source.addClass(cls3);

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
        JavaClass cls = new JavaClass(null);
        cls.setName("MyClass");
        source.addClass(cls);
        source.setPackage("com.thing");
        String expected = ""
                + "package com.thing;\n"
                + "\n"
                + "class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, source.toString());
    }

    public void testToStringImport() throws Exception {
        JavaClass cls = new JavaClass(null);
        cls.setName("MyClass");
        source.addClass(cls);
        source.addImport("java.util.*");
        String expected = ""
                + "import java.util.*;\n"
                + "\n"
                + "class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, source.toString());
    }

    public void testToStringMultipleImports() throws Exception {
        JavaClass cls = new JavaClass(null);
        cls.setName("MyClass");
        source.addClass(cls);
        source.addImport("java.util.*");
        source.addImport("com.blah.Thing");
        source.addImport("xxx");
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
        JavaClass cls = new JavaClass(null);
        cls.setName("MyClass");
        source.addClass(cls);
        source.addImport("java.util.*");
        source.setPackage("com.moo");
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

    public void testResolveJavaPrimitive() throws Exception {
        source.addImport("bogus.int");
        source.addImport("bogus.double");
        String[] primitives = new String[]{
            "boolean", "byte", "char", "double",
            "float", "int", "long", "short", "void"
        };
        for (int i = 0; i < primitives.length; i++) {
            assertEquals(primitives[i], source.resolveType(primitives[i]));
        }
    }

    public void testResolveFullyQualifiedName() throws Exception {
        source.addImport("foo.Bar");
        assertEquals("open.Bar", source.resolveType("open.Bar"));
    }

    public void testResolveFullyQualifiedImport() throws Exception {
        source.addImport("foo.Bar");
        assertEquals("foo.Bar", source.resolveType("Bar"));
    }

    public void testResolveChooseFirstMatchingImport() throws Exception {
        source.addImport("bogus.package.MyType");
        source.addImport("com.thoughtworks.qdox.model.Type");
        source.addImport("another.package.Type");
        assertEquals("com.thoughtworks.qdox.model.Type", source.resolveType("Type"));
    }

    public void testResolveSamePackage() throws Exception {
        source.setPackage("foo");
        source.getClassLibrary().add("foo.Bar");
        assertEquals("foo.Bar", source.resolveType("Bar"));
    }

    public void testResolveFullyQualifiedOverridesSamePackage() throws Exception {
        source.setPackage("foo");
        source.getClassLibrary().add("foo.Bar");
        assertEquals("open.Bar", source.resolveType("open.Bar"));
    }

    public void testResolveWildcard() throws Exception {
        source.getClassLibrary().add("foo.Bar");
        source.addImport("foo.*");
        assertEquals("foo.Bar", source.resolveType("Bar"));
    }

    public void testResolveWildcardsLast() throws Exception {
        source.getClassLibrary().add("foo.Bar");
        source.addImport("foo.*");
        source.addImport("com.thoughtworks.qdox.model.Type");
        source.addImport("another.package.Type");
        assertEquals("com.thoughtworks.qdox.model.Type", source.resolveType("Type"));
    }

    public void testResolveJavaLangClass() throws Exception {
        source.getClassLibrary().add("java.lang.System");
        assertEquals("java.lang.System", source.resolveType("System"));
    }

}
