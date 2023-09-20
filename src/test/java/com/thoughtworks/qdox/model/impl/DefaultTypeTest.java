package com.thoughtworks.qdox.model.impl;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaTypeTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultTypeTest
    extends JavaTypeTest<DefaultJavaType>
{

    @Override
	public JavaClass newJavaClass( ClassLibrary library )
    {
        return new DefaultJavaClass(new DefaultJavaSource( library ));
    }

    @Override
	public DefaultJavaType newType( String fullname )
    {
        return new DefaultJavaType( fullname );
    }

    @Override
	public DefaultJavaType newType( String fullname, int dimensions )
    {
        return new DefaultJavaType( fullname, dimensions );
    }

    @Override
	public DefaultJavaType newType( String fullname, int dimensions, JavaClass clazz )
    {
        return new DefaultJavaType( fullname, dimensions );
    }

    @Test
    public void testArrayType()
    {
        DefaultJavaType type = newType( "int", 1 );
        Assertions.assertTrue(type.isArray());
    }

    @Test
    public void testComponentType()
    {
        Assertions.assertNull(newType( "int" ).getComponentType());
        Assertions.assertEquals("int", newType( "int", 1 ).getComponentType().getFullyQualifiedName());
        Assertions.assertEquals("long", newType( "long", 3 ).getComponentType().getFullyQualifiedName());
    }
}