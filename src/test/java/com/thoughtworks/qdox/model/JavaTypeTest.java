package com.thoughtworks.qdox.model;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.library.OrderedClassLibraryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class JavaTypeTest<T extends JavaType>
{

    public abstract T newType( String fullname );

    public abstract T newType( String fullname, int dimensions );

    public abstract T newType( String fullname, int dimensions, JavaClass clazz );
    
    public abstract JavaClass newJavaClass( ClassLibrary library );
    
    @Test
    public void testToString()
    {
        Assertions.assertEquals("int", newType( "int" ).toString());
        Assertions.assertEquals("int[]", newType( "int", 1 ).toString());
        Assertions.assertEquals("long[][][]", newType( "long", 3 ).toString());
    }

    @Test
    public void testFullyQualifiedName()
    {
        Assertions.assertEquals("int", newType( "int" ).getFullyQualifiedName());
        Assertions.assertEquals("int[]", newType( "int", 1 ).getFullyQualifiedName());
        Assertions.assertEquals("long[][][]", newType( "long", 3 ).getFullyQualifiedName());
    }

    @Test
    public void testEquals()
    {
        JavaClass javaSource = newJavaClass( new OrderedClassLibraryBuilder().appendDefaultClassLoaders().getClassLibrary() );
        assertThat(newType( "long", 0, javaSource )).isEqualTo( newType( "long", 0, javaSource ) );
        assertThat(newType( "long", 0, javaSource )).isNotEqualTo( newType( "int" ) );
        assertThat(newType( "long", 1 )).isNotEqualTo(newType( "long" ));
        assertThat(newType( "long" )).isNotEqualTo(newType( "long", 2 ));
        assertThat(newType( "int" )).isNotEqualTo(null);
    }

    @Test
    public void testToStringVoid()
    {
        Assertions.assertEquals("void", newType("void").toString());
    }

    @Test
    public void testToStringBoolean()
    {
        Assertions.assertEquals("boolean", newType( "boolean" ).toString());
    }

    @Test
    public void testToStringInt()
    {
        Assertions.assertEquals("int", newType( "int" ).toString());
    }

    @Test
    public void testToStringLong()
    {
        Assertions.assertEquals("long", newType( "long" ).toString());
    }

    @Test
    public void testToStringFloat()
    {
        Assertions.assertEquals("float", newType( "float" ).toString());
    }

    @Test
    public void testToStringDouble()
    {
        Assertions.assertEquals("double", newType( "double" ).toString());
    }

    @Test
    public void testToStringChar()
    {
        Assertions.assertEquals("char", newType( "char" ).toString());
    }

    @Test
    public void testToStringByte()
    {
        Assertions.assertEquals("byte", newType( "byte" ).toString());
    }

}