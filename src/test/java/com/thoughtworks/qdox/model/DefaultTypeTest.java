package com.thoughtworks.qdox.model;

import com.thoughtworks.qdox.library.ClassLibrary;


public class DefaultTypeTest extends TypeTest
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

    public void addImport( JavaSource source, String imp )
    {
        ((DefaultJavaSource) source).addImport( imp );
    }

}
