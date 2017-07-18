package com.thoughtworks.qdox.library;

import junit.framework.TestCase;

public class ClassLoaderLibraryTest
    extends TestCase
{

    private ClassLoaderLibrary classLoaderLibrary;
    
    @Override
	protected void setUp()
        throws Exception
    {
        classLoaderLibrary = new ClassLoaderLibrary(null);
    }
    
    public void testNoClassLoaders() {
        assertNull( classLoaderLibrary.getJavaClass( "java.lang.String") );
    }
    
    public void testWithClassLoader() {
        classLoaderLibrary.addClassLoader(getClass().getClassLoader());
        assertNotNull(classLoaderLibrary.getJavaClass("java.lang.String"));
        assertNotNull(classLoaderLibrary.getJavaClass("java.util.Collection"));
        assertNull(classLoaderLibrary.getJavaClass("java.util.GoatCrusher"));
    }
    
    public void testDefaultClassLoader() {
        classLoaderLibrary.addDefaultLoader();
        assertNotNull(classLoaderLibrary.getJavaClass( "java.lang.String"));
        assertNotNull(classLoaderLibrary.getJavaClass("java.util.Collection"));
        assertNotNull(classLoaderLibrary.getJavaClass("java.util.Map$Entry"));
        assertNull(classLoaderLibrary.getJavaClass("java.util.GoatCrusher"));
    }

    public void testModuleInfo()
    {
        assertNull( classLoaderLibrary.getJavaModules() );
    }
}
