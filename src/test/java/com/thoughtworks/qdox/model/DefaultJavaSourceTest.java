package com.thoughtworks.qdox.model;

import java.util.List;


public class DefaultJavaSourceTest extends JavaSourceTest<DefaultJavaSource>
{

    public DefaultJavaSourceTest( String s )
    {
        super( s );
    }

    public DefaultJavaSource newJavaSource(com.thoughtworks.qdox.library.ClassLibrary classLibrary)
    {
        return new DefaultJavaSource(classLibrary);
    }

    public JavaClass newJavaClass()
    {
        return new DefaultJavaClass();
    }

    public void setName( JavaClass clazz, String name )
    {
        ((DefaultJavaClass) clazz).setName( name );
    }

    public JavaPackage newJavaPackage( String name )
    {
        return new DefaultJavaPackage(name);
    }

    public void setPackage( DefaultJavaSource source, JavaPackage pckg )
    {
        source.setPackage( pckg );
    }

    @Override
    public void setClasses( DefaultJavaSource source, List<JavaClass> classes )
    {
        for(JavaClass cls: classes) {
            source.addClass( cls );
        }
    }
    
    @Override
    public void setImports( DefaultJavaSource source, List<String> imports )
    {
        for(String imprt : imports) {
            source.addImport( imprt );
        }
    }
}
