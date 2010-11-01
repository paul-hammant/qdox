package com.thoughtworks.qdox.model;

import com.thoughtworks.qdox.library.SortedClassLibraryBuilder;

public class DefaultJavaSourceTest extends JavaSourceTest
{

    public DefaultJavaSourceTest( String s )
    {
        super( s );
    }

    public JavaSource newJavaSource()
    {
        return new JavaSource(new SortedClassLibraryBuilder().getClassLibrary());
    }

    public JavaClass newJavaClass()
    {
        return new JavaClass();
    }

    public void setClassLibrary( JavaSource source, ClassLibrary library )
    {
        source.setClassLibrary( library );
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
        source.setPackage( pckg );
    }

    public void addImport( JavaSource source, String imp )
    {
        source.addImport( imp );
    }
}
