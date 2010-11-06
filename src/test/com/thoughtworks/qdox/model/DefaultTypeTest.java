package com.thoughtworks.qdox.model;


public class DefaultTypeTest extends TypeTest
{

    public DefaultTypeTest( String s )
    {
        super( s );
    }

    public JavaSource newJavaSource()
    {
        return new JavaSource();
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

    public void setClassLibrary( JavaSource source, ClassLibrary library )
    {
        source.setClassLibrary( library );
    }

    public void addImport( JavaSource source, String imp )
    {
        source.addImport( imp );
    }

}
