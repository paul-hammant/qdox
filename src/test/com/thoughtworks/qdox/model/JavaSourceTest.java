package com.thoughtworks.qdox.model;

import com.thoughtworks.qdox.library.SortedClassLibraryBuilder;

import junit.framework.TestCase;

public abstract class JavaSourceTest<S extends JavaSource> extends TestCase {

    private S source;

    public JavaSourceTest(String s) {
        super(s);
    }

    //constructors
    public abstract S newJavaSource(com.thoughtworks.qdox.library.ClassLibrary classLibrary);
    
    
    //setters
    public abstract void setPackage(S source, JavaPackage pckg);

    public abstract JavaClass newJavaClass();
    public abstract JavaPackage newJavaPackage(String name);

    public abstract void setName(JavaClass clazz, String name);
    
    public abstract void addClass(JavaSource source, JavaClass clazz);
    public abstract void addImport(JavaSource source, String imp);

    protected void setUp() throws Exception {
        super.setUp();
        source = newJavaSource(new SortedClassLibraryBuilder().getClassLibrary());
    }

    public void testToStringOneClass() throws Exception {
        JavaClass cls = newJavaClass();
        setName(cls, "MyClass");
        addClass(source, cls);
        String expected = ""
                + "class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, source.toString());
    }

    public void testToStringMultipleClass() throws Exception {
        JavaClass cls1 = newJavaClass();
        setName(cls1, "MyClass1");
        addClass(source, cls1);
        JavaClass cls2 = newJavaClass();
        setName(cls2, "MyClass2");
        addClass(source, cls2);
        JavaClass cls3 = newJavaClass();
        setName(cls3, "MyClass3");
        addClass(source, cls3);

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
        JavaClass cls = newJavaClass();
        setName(cls, "MyClass");
        addClass(source, cls);
        setPackage(source, newJavaPackage("com.thing"));
        String expected = ""
                + "package com.thing;\n"
                + "\n"
                + "class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, source.toString());
    }

    public void testToStringImport() throws Exception {
        JavaClass cls = newJavaClass();
        setName(cls, "MyClass");
        addClass(source, cls);
        addImport(source, "java.util.*");
        String expected = ""
                + "import java.util.*;\n"
                + "\n"
                + "class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, source.toString());
    }

    public void testToStringMultipleImports() throws Exception {
        JavaClass cls = newJavaClass();
        setName(cls, "MyClass");
        addClass(source, cls);
        addImport(source, "java.util.*");
        addImport(source, "com.blah.Thing");
        addImport(source, "xxx");
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
        JavaClass cls = newJavaClass();
        setName(cls, "MyClass");
        addClass(source, cls);
        addImport(source, "java.util.*");
        setPackage(source, newJavaPackage("com.moo"));
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
        setPackage(source, newJavaPackage("foo.bar"));
        assertEquals("foo.bar.", source.getClassNamePrefix());
       }
    
    public void testResolveJavaPrimitive() throws Exception {
        addImport(source, "bogus.int");
        addImport(source, "bogus.double");
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
        source.getJavaClassLibrary().getJavaClass("open.Bar");
        assertEquals("open.Bar", source.resolveType("open.Bar"));
    }
    
    public void testResolveFullyQualifiedImport() throws Exception {
        addImport(source, "foo.Bar");
        source.getJavaClassLibrary().getJavaClass("foo.Bar");
        assertEquals("foo.Bar", source.resolveType("Bar"));
    }

    public void testResolveChooseFirstMatchingImport() throws Exception {
        addImport(source, "bogus.package.MyType");
        addImport(source, "com.thoughtworks.qdox.model.Type");
        addImport(source, "another.package.Type");
        source.getJavaClassLibrary().getJavaClass("bogus.package.MyType");
        source.getJavaClassLibrary().getJavaClass("com.thoughtworks.qdox.model.Type");
        source.getJavaClassLibrary().getJavaClass("another.package.Type");
        assertEquals("com.thoughtworks.qdox.model.Type", source.resolveType("Type"));
    }

    public void testResolveSamePackage() throws Exception {
        setPackage(source, newJavaPackage("foo"));
        source.getJavaClassLibrary().getJavaClass("foo.Bar");
        assertEquals("foo.Bar", source.resolveType("Bar"));
    }

    public void testResolveFullyQualifiedTrumpsSamePackage() throws Exception {
        setPackage(source, newJavaPackage("foo"));
        source.getJavaClassLibrary().getJavaClass("foo.Bar");
        source.getJavaClassLibrary().getJavaClass("open.Bar");
        assertEquals("open.Bar", source.resolveType("open.Bar"));
    }

    public void testResolveFullyQualifiedTrumpsWildCard() throws Exception {
        addImport(source, "bar.Bar");
        addImport(source, "foo.Bar");
        source.getJavaClassLibrary().getJavaClass("foo.*");
        source.getJavaClassLibrary().getJavaClass("bar.Bar");
        assertEquals("bar.Bar", source.resolveType("Bar"));
    }

    public void testResolveWildcard() throws Exception {
        source.getJavaClassLibrary().getJavaClass("foo.Bar");
        addImport(source, "foo.*");
        assertEquals("foo.Bar", source.resolveType("Bar"));
    }

    public void testResolveJavaLangClass() throws Exception {
        source.getJavaClassLibrary().getJavaClass("java.lang.System");
        assertEquals("java.lang.System", source.resolveType("System"));
    }

    public void testResolveSamePackageTrumpsWildcard() throws Exception {
        addImport(source, "com.thoughtworks.qdox.model.Type");
        addImport(source, "foo.*");
        source.getJavaClassLibrary().getJavaClass("com.thoughtworks.qdox.model.Type");
        source.getJavaClassLibrary().getJavaClass("foo.Type");
        assertEquals("com.thoughtworks.qdox.model.Type", source.resolveType("Type"));
    }

    public void testResolveFullyQualifiedInnerClass() throws Exception {
        setPackage(source, newJavaPackage("foo"));
        source.getJavaClassLibrary().getJavaClass("foo.Bar$Fnord");
        assertEquals("foo.Bar$Fnord", source.resolveType("foo.Bar.Fnord"));
    }

    public void testResolvePartiallySpecifiedInnerClass() throws Exception {
        setPackage(source, newJavaPackage("foo"));
        addImport(source, "java.util.*");
        source.getJavaClassLibrary().getJavaClass("foo.Bar$Fnord");
        assertEquals("foo.Bar$Fnord", source.resolveType("Bar.Fnord"));
        assertEquals("java.util.Map$Entry", source.resolveType("Map.Entry"));
    }
    
}
