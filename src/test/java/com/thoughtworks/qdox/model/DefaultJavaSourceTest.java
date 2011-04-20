package com.thoughtworks.qdox.model;

import java.util.List;

import com.thoughtworks.qdox.library.ClassLibrary;

public class DefaultJavaSourceTest extends JavaSourceTest<DefaultJavaSource>
{

    public DefaultJavaSourceTest( String s )
    {
        super( s );
    }

    public DefaultJavaSource newJavaSource( ClassLibrary classLibrary )
    {
        return new DefaultJavaSource(classLibrary);
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
