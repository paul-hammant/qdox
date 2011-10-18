package com.thoughtworks.qdox.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.qdox.library.ClassLoaderLibrary;
import com.thoughtworks.qdox.model.JavaClass;

public class DefaultJavaClassIT
{
    private ClassLoaderLibrary library;

    @Before
    public void setUp()
    {
        library = new ClassLoaderLibrary( null );
        library.addDefaultLoader();
    }

    @Test
    public void testSuperJavaClass()
    {
        JavaClass clazz = library.getJavaClass( "java.util.HashSet" );
        assertTrue( clazz instanceof DefaultJavaClass<?> );
        JavaClass superClass = clazz.getSuperJavaClass();
        assertEquals( "java.util.AbstractSet", superClass.getFullyQualifiedName() );
        assertEquals( "java.util.AbstractSet", superClass.getCanonicalName() );
        assertEquals( "java.util.AbstractSet", superClass.getValue() );
    }

    @Test
    public void testIsAJavaClass()
    {
        JavaClass hashSetClass = library.getJavaClass( "java.util.HashSet" );
        assertTrue( hashSetClass instanceof DefaultJavaClass<?> );
        
        JavaClass setClass = library.getJavaClass( "java.util.Set" );
        assertTrue( hashSetClass.isA( setClass ) );
        assertTrue( hashSetClass.isA( "java.util.Set" ) );
    }

}
