package com.thoughtworks.qdox.model.impl;

import com.thoughtworks.qdox.library.ClassLoaderLibrary;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultJavaTypeIT
{
    private ClassLoaderLibrary library;

    @BeforeEach
    public void setUp()
    {
        library = new ClassLoaderLibrary( null );
        library.addDefaultLoader();
    }
    
    
    @Test
    public void testSuperClass()
    {
        JavaClass hashSetClass = library.getJavaClass( "java.util.HashSet" );
        Assertions.assertTrue(hashSetClass instanceof DefaultJavaClass);
        JavaType hashSetSuperClass = hashSetClass.getSuperClass();
        Assertions.assertEquals("java.util.AbstractSet", hashSetSuperClass.getFullyQualifiedName());
        Assertions.assertEquals("java.util.AbstractSet", hashSetSuperClass.getCanonicalName());
        Assertions.assertEquals("java.util.AbstractSet", hashSetSuperClass.getValue());
    }
}
