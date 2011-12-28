package com.thoughtworks.qdox.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.qdox.library.ClassLoaderLibrary;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaType;

public class DefaultJavaTypeIT
{
    private ClassLoaderLibrary library;

    @Before
    public void setUp()
    {
        library = new ClassLoaderLibrary( null );
        library.addDefaultLoader();
    }
    
    
    @Test
    public void testSuperClass()
    {
        JavaClass hashSetClass = library.getJavaClass( "java.util.HashSet" );
        assertTrue( hashSetClass instanceof DefaultJavaClass );
        JavaType hashSetSuperClass = hashSetClass.getSuperClass();
        assertEquals( "java.util.AbstractSet", hashSetSuperClass.getFullyQualifiedName() );
        assertEquals( "java.util.AbstractSet", hashSetSuperClass.getCanonicalName() );
        assertEquals( "java.util.AbstractSet", hashSetSuperClass.getValue() );
    }
}
