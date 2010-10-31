package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

import java.util.Collection;

public class ClassLibraryTest extends TestCase {

    public ClassLibraryTest(String s) {
        super(s);
    }

    public JavaClass getClassByName(String name) {
        JavaClass clazz = new JavaClass();
        clazz.setName("MyClass");
        return clazz;
    }

    public JavaClass[] getClasses() {
        return new JavaClass[0];
    }
  
    public void testAdd() throws Exception {
        ClassLibrary c = new ClassLibrary();
        c.add("com.blah.Ping");
        c.add("com.moo.Poo");
        assertTrue(c.contains("com.blah.Ping"));
        assertTrue(c.contains("com.moo.Poo"));
        assertTrue(!c.contains("com.not.You"));
    }

    /**
     * @deprecated Moved to ClassLoaderLibraryTest
     */
    public void testNoClassLoaders() throws Exception {
        ClassLibrary c = new ClassLibrary();
        assertTrue(!c.contains("java.lang.String"));
    }

    /**
     * @deprecated Moved to ClassLoaderLibraryTest
     */
    public void testWithClassLoader() throws Exception {
        ClassLibrary c = new ClassLibrary();
        c.addClassLoader(getClass().getClassLoader());
        assertTrue(c.contains("java.lang.String"));
        assertTrue(c.contains("java.util.Collection"));
        assertTrue(!c.contains("java.util.GoatCrusher"));
    }

    /**
     * @deprecated Moved to ClassLoaderLibraryTest
     */
    public void testDefaultClassLoader() throws Exception {
        ClassLibrary c = new ClassLibrary();
        c.addDefaultLoader();
        assertTrue(c.contains("java.lang.String"));
        assertTrue(c.contains("java.util.Collection"));
        assertTrue(c.contains("java.util.Map$Entry"));
        assertTrue(!c.contains("java.util.GoatCrusher"));
    }
}
