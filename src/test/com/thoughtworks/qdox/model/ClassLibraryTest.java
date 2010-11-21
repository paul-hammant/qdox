package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

public class ClassLibraryTest extends TestCase {

    public ClassLibraryTest(String s) {
        super(s);
    }

    public JavaClass getClassByName(String name) {
        return new DefaultJavaClass("MyClass");
    }

    public JavaClass[] getClasses() {
        return new JavaClass[0];
    }
  
    //Moved to JavaClassContextText
//    public void testAdd() throws Exception {
//        ClassLibrary c = new ClassLibrary();
//        c.add("com.blah.Ping");
//        c.add("com.moo.Poo");
//        assertTrue(c.hasClassReference("com.blah.Ping"));
//        assertTrue(c.hasClassReference("com.moo.Poo"));
//        assertTrue(!c.hasClassReference("com.not.You"));
//    }

    /**
     * @deprecated Moved to ClassLoaderLibraryTest
     */
    public void testNoClassLoaders() throws Exception {
        ClassLibrary c = new ClassLibrary();
        assertTrue(!c.hasClassReference("java.lang.String"));
    }

    /**
     * @deprecated Moved to ClassLoaderLibraryTest
     */
    public void testWithClassLoader() throws Exception {
        ClassLibrary c = new ClassLibrary();
        c.addClassLoader(getClass().getClassLoader());
        assertTrue(c.hasClassReference("java.lang.String"));
        assertTrue(c.hasClassReference("java.util.Collection"));
        assertTrue(!c.hasClassReference("java.util.GoatCrusher"));
    }

    /**
     * @deprecated Moved to ClassLoaderLibraryTest
     */
    public void testDefaultClassLoader() throws Exception {
        ClassLibrary c = new ClassLibrary();
        c.addDefaultLoader();
        assertTrue(c.hasClassReference("java.lang.String"));
        assertTrue(c.hasClassReference("java.util.Collection"));
        assertTrue(c.hasClassReference("java.util.Map$Entry"));
        assertTrue(!c.hasClassReference("java.util.GoatCrusher"));
    }
}
