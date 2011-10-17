package com.thoughtworks.qdox.model;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.library.ClassLoaderLibrary;
import com.thoughtworks.qdox.model.impl.DefaultJavaSource;


public class DefaultTypeTest extends JavaTypeTest<Type>
{

    public DefaultTypeTest( String s )
    {
        super( s );
    }

    public JavaSource newJavaSource( ClassLibrary library )
    {
        return new DefaultJavaSource(library);
    }

    public Type newType( String fullname )
    {
        return new Type(fullname);
    }

    public Type newType( String fullname, int dimensions )
    {
        return new Type(fullname, dimensions);
    }

    public Type newType( String fullname, int dimensions, JavaSource source )
    {
        return new Type(fullname, dimensions, source);
    }
    
    public void testArrayType() throws Exception {
        Type type = newType("int", 1);
        assertTrue(type.isArray());
    }

    public void testComponentType() throws Exception {
        assertNull( newType("int").getComponentType());
        assertEquals("int", newType("int", 1).getComponentType().getFullyQualifiedName());
        assertEquals("long", newType("long", 3).getComponentType().getFullyQualifiedName());
    }

    public void testTypeHasJavaClass() {
        ClassLoaderLibrary library = new ClassLoaderLibrary( null );
        library.addDefaultLoader();
        JavaSource javaSource = newJavaSource(library);
        JavaClass clazz = newType("java.util.HashSet", 0, javaSource);
        JavaClass superClass = clazz.getSuperJavaClass();
        assertEquals("java.util.AbstractSet", superClass.getFullyQualifiedName());
    }

}