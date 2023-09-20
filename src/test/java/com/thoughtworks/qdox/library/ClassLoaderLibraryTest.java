package com.thoughtworks.qdox.library;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClassLoaderLibraryTest {

    private ClassLoaderLibrary classLoaderLibrary;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        classLoaderLibrary = new ClassLoaderLibrary(null);
    }

    @Test
    public void testNoClassLoaders() {
        Assertions.assertNull(classLoaderLibrary.getJavaClass( "java.lang.String"));
    }

    @Test
    public void testWithClassLoader() {
        classLoaderLibrary.addClassLoader(getClass().getClassLoader());
        Assertions.assertNotNull(classLoaderLibrary.getJavaClass("java.lang.String"));
        Assertions.assertNotNull(classLoaderLibrary.getJavaClass("java.util.Collection"));
        Assertions.assertNull(classLoaderLibrary.getJavaClass("java.util.GoatCrusher"));
    }

    @Test
    public void testDefaultClassLoader() {
        classLoaderLibrary.addDefaultLoader();
        Assertions.assertNotNull(classLoaderLibrary.getJavaClass( "java.lang.String"));
        Assertions.assertNotNull(classLoaderLibrary.getJavaClass("java.util.Collection"));
        Assertions.assertNotNull(classLoaderLibrary.getJavaClass("java.util.Map$Entry"));
        Assertions.assertNull(classLoaderLibrary.getJavaClass("java.util.GoatCrusher"));
    }

    @Test
    public void testModuleInfo()
    {
        Assertions.assertNull(classLoaderLibrary.getJavaModules());
    }
}
