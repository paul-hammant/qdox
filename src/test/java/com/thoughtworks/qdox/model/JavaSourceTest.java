package com.thoughtworks.qdox.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.library.SortedClassLibraryBuilder;

public abstract class JavaSourceTest<S extends JavaSource> extends TestCase {

    private S source;

    public JavaSourceTest(String s) {
        super(s);
    }

    //constructors
    public abstract S newJavaSource(ClassLibrary classLibrary);
    
    //setters
    public abstract void setClasses(S source, List<JavaClass> classes);
    public abstract void setImports(S source, List<String> imports);
    public abstract void setPackage(S source, JavaPackage pckg);

    public JavaPackage newJavaPackage(String name) {
        JavaPackage result = mock( JavaPackage.class );
        when( result.getName() ).thenReturn( name );
        return result;
    }

    protected void setUp() throws Exception {
        super.setUp();
        source = newJavaSource(new SortedClassLibraryBuilder().appendDefaultClassLoaders().getClassLibrary());
    }

    public void testToStringOneClass() throws Exception {
        JavaClass cls = mock(JavaClass.class);
        when(cls.getName()).thenReturn( "MyClass" );
        setClasses( source, Collections.singletonList( cls ) );
        String expected = ""
                + "class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, source.toString());
    }

    public void testToStringMultipleClass() throws Exception {
        List<JavaClass> classes = new ArrayList<JavaClass>();
        JavaClass cls1 = mock(JavaClass.class);
        when(cls1.getName()).thenReturn( "MyClass1" );
        classes.add( cls1 );
        JavaClass cls2 = mock(JavaClass.class);
        when(cls2.getName()).thenReturn( "MyClass2" );
        classes.add( cls2 );
        JavaClass cls3 = mock(JavaClass.class);
        when(cls3.getName()).thenReturn( "MyClass3" );
        classes.add( cls3 );
        
        setClasses( source, classes );

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
        JavaClass cls = mock(JavaClass.class);
        when(cls.getName()).thenReturn("MyClass");
        
        setClasses(source, Collections.singletonList( cls ));
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
        JavaClass cls = mock(JavaClass.class);
        when(cls.getName()).thenReturn("MyClass");
        setClasses(source, Collections.singletonList( cls ));
        setImports(source, Collections.singletonList("java.util.*"));
        String expected = ""
                + "import java.util.*;\n"
                + "\n"
                + "class MyClass {\n"
                + "\n"
                + "}\n";
        assertEquals(expected, source.toString());
    }

    public void testToStringMultipleImports() throws Exception {
        JavaClass cls = mock(JavaClass.class);
        when(cls.getName()).thenReturn("MyClass");
        setClasses(source, Collections.singletonList( cls ));
        List<String> imports = new ArrayList<String>();
        imports.add("java.util.*");
        imports.add("com.blah.Thing");
        imports.add("xxx");
        setImports( source, imports );
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
        JavaClass cls = mock(JavaClass.class);
        when(cls.getName()).thenReturn("MyClass");
        setClasses(source, Collections.singletonList( cls ));
        setImports(source, Collections.singletonList( "java.util.*" ));
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
        List<String> imports = new ArrayList<String>();
        imports.add("bogus.int");
        imports.add("bogus.double");
        setImports( source, imports );
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

    public void testResolveFullyQualifiedName() throws Exception
    {
        assertNotNull( source.getJavaClassLibrary().getJavaClass( "open.Bar" ) );
        assertEquals( "open.Bar", source.resolveType( "open.Bar" ) );
    }
    
    public void testResolveFullyQualifiedImport() throws Exception {
        setImports( source, Collections.singletonList( "foo.Bar" ) );
        assertNotNull( source.getJavaClassLibrary().getJavaClass( "foo.Bar" ) );
        assertEquals( "foo.Bar", source.resolveType( "Bar" ) );
    }

    public void testResolveChooseFirstMatchingImport() throws Exception {
        List<String> imports = new ArrayList<String>();
        imports.add( "bogus.package.MyType" );
        imports.add( "com.thoughtworks.qdox.model.Type" );
        imports.add( "another.package.Type" );
        setImports( source, imports );
        assertNotNull( source.getJavaClassLibrary().getJavaClass( "bogus.package.MyType" ) );
        assertNotNull( source.getJavaClassLibrary().getJavaClass( "com.thoughtworks.qdox.model.Type" ) );
        assertNotNull( source.getJavaClassLibrary().getJavaClass( "another.package.Type" ) );
        assertEquals( "com.thoughtworks.qdox.model.Type", source.resolveType( "Type" ) );
    }

    public void testResolveSamePackage() throws Exception {
        setPackage(source, newJavaPackage("foo"));
        assertNotNull( source.getJavaClassLibrary().getJavaClass("foo.Bar") );
        assertEquals("foo.Bar", source.resolveType("Bar"));
    }

    public void testResolveFullyQualifiedTrumpsSamePackage() throws Exception {
        setPackage(source, newJavaPackage("foo"));
        assertNotNull( source.getJavaClassLibrary().getJavaClass("foo.Bar") );
        assertNotNull( source.getJavaClassLibrary().getJavaClass("open.Bar") );
        assertEquals("open.Bar", source.resolveType("open.Bar"));
    }

    public void testResolveFullyQualifiedTrumpsWildCard() throws Exception {
        List<String> imports = new ArrayList<String>();
        imports.add("bar.Bar");
        imports.add("foo.Bar");
        setImports( source, imports );
        assertNotNull( source.getJavaClassLibrary().getJavaClass("foo.*") );
        assertNotNull( source.getJavaClassLibrary().getJavaClass("bar.Bar") );
        assertEquals("bar.Bar", source.resolveType("Bar"));
    }

    public void testResolveWildcard() throws Exception {
        assertNotNull( source.getJavaClassLibrary().getJavaClass("foo.Bar") );
        setImports(source, Collections.singletonList("foo.*"));
        assertEquals("foo.Bar", source.resolveType("Bar"));
    }

    public void testResolveJavaLangClass() throws Exception {
        assertNotNull( source.getJavaClassLibrary().getJavaClass("java.lang.System") );
        assertEquals("java.lang.System", source.resolveType("System"));
    }

    public void testResolveSamePackageTrumpsWildcard() throws Exception {
        List<String> imports = new ArrayList<String>();
        imports.add("com.thoughtworks.qdox.model.Type");
        imports.add("foo.*");
        setImports( source, imports );
        assertNotNull( source.getJavaClassLibrary().getJavaClass("com.thoughtworks.qdox.model.Type") );
        assertNotNull( source.getJavaClassLibrary().getJavaClass("foo.Type") );
        assertEquals("com.thoughtworks.qdox.model.Type", source.resolveType("Type"));
    }

    public void testResolveFullyQualifiedInnerClass() throws Exception {
        setPackage(source, newJavaPackage("foo"));
        assertNotNull( source.getJavaClassLibrary().getJavaClass("foo.Bar$Fnord") );
        assertEquals("foo.Bar$Fnord", source.resolveType("foo.Bar.Fnord"));
    }

    public void testResolvePartiallySpecifiedInnerClass() throws Exception {
        setPackage(source, newJavaPackage("foo"));
        setImports(source, Collections.singletonList("java.util.*"));
        assertNotNull( source.getJavaClassLibrary().getJavaClass("foo.Bar$Fnord") );
        assertEquals("foo.Bar$Fnord", source.resolveType("Bar.Fnord"));
        assertEquals("java.util.Map$Entry", source.resolveType("Map.Entry"));
    }
    
}
