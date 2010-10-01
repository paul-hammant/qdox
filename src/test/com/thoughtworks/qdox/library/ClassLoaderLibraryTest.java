package com.thoughtworks.qdox.library;

import junit.framework.TestCase;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

public class ClassLoaderLibraryTest
    extends TestCase
{

    private ClassLoaderLibrary classLoaderLibrary;
    
    protected void setUp()
        throws Exception
    {
        classLoaderLibrary = new ClassLoaderLibrary(new ClassLibrary()
        {
            public JavaClass getJavaClass( String name )
            {
                return null;
            }
            public JavaSource[] getSources()
            {
                return null;
            }
            public JavaClass[] getClasses()
            {
                return null;
            }
            public boolean exists( String name )
            {
                return false;
            }
        });
    }
    
    public void testNoClassLoaders() throws Exception {
        assertNull( classLoaderLibrary.getJavaClass( "java.lang.String") );
    }
    
    public void testWithClassLoader() throws Exception {
        classLoaderLibrary.addClassLoader(getClass().getClassLoader());
        assertNotNull(classLoaderLibrary.getJavaClass("java.lang.String"));
        assertNotNull(classLoaderLibrary.getJavaClass("java.util.Collection"));
        assertNull(classLoaderLibrary.getJavaClass("java.util.GoatCrusher"));
    }
    
    public void testDefaultClassLoader() throws Exception {
        classLoaderLibrary.addDefaultLoader();
        assertNotNull(classLoaderLibrary.getJavaClass( "java.lang.String"));
        assertNotNull(classLoaderLibrary.getJavaClass("java.util.Collection"));
        assertNotNull(classLoaderLibrary.getJavaClass("java.util.Map$Entry"));
        assertNull(classLoaderLibrary.getJavaClass("java.util.GoatCrusher"));
    }

}
