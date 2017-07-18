package com.thoughtworks.qdox.model;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.library.OrderedClassLibraryBuilder;

public abstract class JavaTypeTest<T extends JavaType>
{

    public abstract T newType( String fullname );

    public abstract T newType( String fullname, int dimensions );

    public abstract T newType( String fullname, int dimensions, JavaClass clazz );
    
    public abstract JavaClass newJavaClass( ClassLibrary library );
    
    @Test
    public void testToString()
    {
        assertEquals( "int", newType( "int" ).toString() );
        assertEquals( "int[]", newType( "int", 1 ).toString() );
        assertEquals( "long[][][]", newType( "long", 3 ).toString() );
    }

    @Test
    public void testFullyQualifiedName()
    {
        assertEquals( "int", newType( "int" ).getFullyQualifiedName() );
        assertEquals( "int[]", newType( "int", 1 ).getFullyQualifiedName() );
        assertEquals( "long[][][]", newType( "long", 3 ).getFullyQualifiedName() );
    }

    @Test
    public void testEquals()
    {
        JavaClass javaSource = newJavaClass( new OrderedClassLibraryBuilder().appendDefaultClassLoaders().getClassLibrary() );
        assertEquals( newType( "long", 0, javaSource ), newType( "long", 0, javaSource ) );
        assertThat( newType( "long", 0, javaSource ), not( newType( "int" ) ) );
        assertThat( newType( "long", 1 ), not( newType( "long" ) ) );
        assertThat( newType( "long" ), not( newType( "long", 2 ) ) );
        assertFalse( newType( "int" ).equals( null ) );
    }

    @Test
    public void testToStringVoid()
    {
        assertEquals( "void", newType("void").toString() );
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