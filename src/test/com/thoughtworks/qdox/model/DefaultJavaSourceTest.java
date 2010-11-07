package com.thoughtworks.qdox.model;


public class DefaultJavaSourceTest extends JavaSourceTest
{

    public DefaultJavaSourceTest( String s )
    {
        super( s );
    }

    public JavaSource newJavaSource(com.thoughtworks.qdox.library.ClassLibrary classLibrary)
    {
        return new DefaultJavaSource(classLibrary);
    }

    public JavaClass newJavaClass()
    {
        return new JavaClass();
    }

    public void setName( JavaClass clazz, String name )
    {
        clazz.setName( name );
    }

    public void addClass( JavaSource source, JavaClass clazz )
    {
        clazz.setSource( source );
        source.addClass( clazz );
    }

    public JavaPackage newJavaPackage( String name )
    {
        return new JavaPackage(name);
    }

    public void setPackage( JavaSource source, JavaPackage pckg )
    {
        ((DefaultJavaSource) source).setPackage( pckg );
    }

    public void addImport( JavaSource source, String imp )
    {
        ((DefaultJavaSource) source).addImport( imp );
    }
}
