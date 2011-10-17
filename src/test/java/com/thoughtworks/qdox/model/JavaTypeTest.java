package com.thoughtworks.qdox.model;

import static org.junit.Assert.*;
import static org.hamcrest.core.IsNot.*;
import org.junit.Test;

import com.thoughtworks.qdox.model.impl.Type;

public abstract class JavaTypeTest<T extends JavaType>
{

    public abstract T newType( String fullname );

    public abstract T newType( String fullname, int dimensions );

    @Test
    public void testToString()
        throws Exception
    {
        assertEquals( "int", newType( "int" ).toString() );
        assertEquals( "int[]", newType( "int", 1 ).toString() );
        assertEquals( "long[][][]", newType( "long", 3 ).toString() );
    }

    @Test
    public void testFullyQualifiedName()
        throws Exception
    {
        assertEquals( "int", newType( "int" ).getFullyQualifiedName() );
        assertEquals( "int[]", newType( "int", 1 ).getFullyQualifiedName() );
        assertEquals( "long[][][]", newType( "long", 3 ).getFullyQualifiedName() );
    }

    @Test
    public void testEquals()
        throws Exception
    {
        assertEquals( newType( "string" ), newType( "string" ) );
        assertThat( newType( "string" ), not( newType( "int" ) ) );
        assertThat( newType( "long", 1 ), not( newType( "long" ) ) );
        assertThat( newType( "long" ), not( newType( "long", 2 ) ) );
        assertFalse( newType( "int" ).equals( null ) );
    }

    @Test
    public void testToStringVoid()
    {
        assertEquals( "void", Type.VOID.toString() );
    }

    @Test
    public void testToStringBoolean()
    {
        assertEquals( "boolean", newType( "boolean" ).toString() );
    }

    @Test
    public void testToStringInt()
    {
        assertEquals( "int", newType( "int" ).toString() );
    }

    @Test
    public void testToStringLong()
    {
        assertEquals( "long", newType( "long" ).toString() );
    }

    @Test
    public void testToStringFloat()
    {
        assertEquals( "float", newType( "float" ).toString() );
    }

    @Test
    public void testToStringDouble()
    {
        assertEquals( "double", newType( "double" ).toString() );
    }

    @Test
    public void testToStringChar()
    {
        assertEquals( "char", newType( "char" ).toString() );
    }

    @Test
    public void testToStringByte()
    {
        assertEquals( "byte", newType( "byte" ).toString() );
    }

}