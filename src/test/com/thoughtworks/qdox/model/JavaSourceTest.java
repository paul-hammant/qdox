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
        JavaClass cls = new JavaClass();
        cls.setName("MyClass");
        source.addClass(cls);
        String expected = ""
                + "class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, source.toString());
    }

    public void testToStringMultipleClass() throws Exception {
        JavaClass cls1 = new JavaClass();
        cls1.setName("MyClass1");
        source.addClass(cls1);
        JavaClass cls2 = new JavaClass();
        cls2.setName("MyClass2");
        source.addClass(cls2);
        JavaClass cls3 = new JavaClass();
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
        JavaClass cls = new JavaClass();
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
        JavaClass cls = new JavaClass();
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
        JavaClass cls = new JavaClass();
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
        JavaClass cls = new JavaClass();
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

    public void testGetClassNamePrefix() {
        assertEquals("", source.getClassNamePrefix());
        source.setPackage("foo.bar");
        assertEquals("foo.bar.", source.getClassNamePrefix());
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

    public void testDontResolveMissingClasses() throws Exception {
        assertEquals(null, source.resolveType("not.Found"));
    }

    public void testResolveFullyQualifiedName() throws Exception {
        source.getClassLibrary().add("open.Bar");
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

    public void testResolveFullyQualifiedTrumpsSamePackage() throws Exception {
        source.setPackage("foo");
        source.getClassLibrary().add("foo.Bar");
        source.getClassLibrary().add("open.Bar");
        assertEquals("open.Bar", source.resolveType("open.Bar"));
    }

    public void testResolveWildcard() throws Exception {
        source.getClassLibrary().add("foo.Bar");
        source.addImport("foo.*");
        assertEquals("foo.Bar", source.resolveType("Bar"));
    }

    public void testResolveJavaLangClass() throws Exception {
        source.getClassLibrary().add("java.lang.System");
        assertEquals("java.lang.System", source.resolveType("System"));
    }

    public void testResolveSamePackageTrumpsWildcard() throws Exception {
        source.addImport("com.thoughtworks.qdox.model.Type");
        source.addImport("foo.*");
        source.getClassLibrary().add("foo.Type");
        assertEquals("com.thoughtworks.qdox.model.Type", source.resolveType("Type"));
    }

    public void testResolveFullyQualifiedInnerClass() throws Exception {
        source.setPackage("foo");
        source.getClassLibrary().add("foo.Bar$Fnord");
        assertEquals("foo.Bar$Fnord", source.resolveType("foo.Bar.Fnord"));
    }

    public void testResolvePartiallySpecifiedInnerClass() throws Exception {
        source.setPackage("foo");
        source.addImport("java.util.*");
        source.getClassLibrary().add("foo.Bar$Fnord");
        source.getClassLibrary().addDefaultLoader();
        assertEquals("foo.Bar$Fnord", source.resolveType("Bar.Fnord"));
        assertEquals("java.util.Map$Entry", source.resolveType("Map.Entry"));
    }

}
