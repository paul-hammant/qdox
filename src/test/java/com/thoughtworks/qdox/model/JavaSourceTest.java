package com.thoughtworks.qdox.model;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.library.SortedClassLibraryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class JavaSourceTest<S extends JavaSource> {

    private S source;

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

    @BeforeEach
    public void setUp() throws Exception {
        source = newJavaSource(new SortedClassLibraryBuilder().appendDefaultClassLoaders().getClassLibrary());
    }

    @Test
    public void testToStringOneClass() {
        JavaClass cls = mock(JavaClass.class);
        when(cls.getName()).thenReturn( "MyClass" );
        setClasses( source, Collections.singletonList( cls ) );
        String expected = ""
                + "class MyClass {\n"
                + "\n"
                + "}\n";
        Assertions.assertEquals(expected, source.toString());
    }

    @Test
    public void testToStringMultipleClass() {
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
        Assertions.assertEquals(expected, source.toString());
    }

    @Test
    public void testToStringPackage() {
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
        Assertions.assertEquals(expected, source.toString());
    }

    @Test
    public void testToStringImport() {
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
        Assertions.assertEquals(expected, source.toString());
    }

    @Test
    public void testToStringMultipleImports() {
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
        Assertions.assertEquals(expected, source.toString());
    }

    @Test
    public void testToStringImportAndPackage() {
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
        Assertions.assertEquals(expected, source.toString());
    }

    @Test
    public void testGetClassNamePrefix() {
        Assertions.assertEquals("", source.getClassNamePrefix());
        setPackage(source, newJavaPackage("foo.bar"));
        Assertions.assertEquals("foo.bar.", source.getClassNamePrefix());
       }
}
