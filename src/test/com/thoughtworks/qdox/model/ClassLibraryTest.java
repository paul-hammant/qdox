package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

import java.util.Collection;

public class ClassLibraryTest extends TestCase implements JavaClassCache {

    public ClassLibraryTest(String s) {
        super(s);
    }

    public JavaClass getClassByName(String name) {
        JavaClass clazz = new JavaClass();
        clazz.setName("MyClass");
        return clazz;
    }

    public void testAdd() throws Exception {
        ClassLibrary c = new ClassLibrary(this);
        c.add("com.blah.Foo");
        c.add("com.moo.Poo");
        assertTrue(c.contains("com.blah.Foo"));
        assertTrue(c.contains("com.moo.Poo"));
        assertTrue(!c.contains("com.not.You"));
    }

    public void testListAll() throws Exception {
        ClassLibrary c = new ClassLibrary(this);
        c.add("com.blah.Foo");
        c.add("com.thing.Foo");
        c.add("com.x.Goat");
        c.add("com.y.Goat");

        Collection all = c.all();
        assertTrue(all.contains("com.blah.Foo"));
        assertTrue(all.contains("com.thing.Foo"));
        assertTrue(all.contains("com.x.Goat"));
        assertTrue(all.contains("com.y.Goat"));

        assertTrue(!all.contains("com.not.True"));
        assertEquals(4, all.size());
    }

    public void testNoClassLoaders() throws Exception {
        ClassLibrary c = new ClassLibrary(this);
        assertTrue(!c.contains("java.lang.String"));
    }

    public void testWithClassLoader() throws Exception {
        ClassLibrary c = new ClassLibrary(this);
        c.addClassLoader(getClass().getClassLoader());
        assertTrue(c.contains("java.lang.String"));
        assertTrue(c.contains("java.util.Collection"));
        assertTrue(!c.contains("java.util.GoatCrusher"));
    }

    public void testDefaultClassLoader() throws Exception {
        ClassLibrary c = new ClassLibrary(this);
        c.addDefaultLoader();
        assertTrue(c.contains("java.lang.String"));
        assertTrue(c.contains("java.util.Collection"));
        assertTrue(!c.contains("java.util.GoatCrusher"));
    }

    public void testAccessingCache() throws Exception {
        ClassLibrary c = new ClassLibrary(this);
        JavaClass jclass = c.getClassByName("MyClass");
        assertNotNull(jclass);
        assertEquals("MyClass", jclass.getName());
    }
}
