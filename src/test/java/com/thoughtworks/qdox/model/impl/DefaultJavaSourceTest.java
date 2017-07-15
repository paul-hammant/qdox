package com.thoughtworks.qdox.model.impl;

import java.util.List;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSourceTest;

public class DefaultJavaSourceTest extends JavaSourceTest<DefaultJavaSource>
{

    public DefaultJavaSourceTest( String s )
    {
        super( s );
    }

    @Override
	public DefaultJavaSource newJavaSource( ClassLibrary classLibrary )
    {
        return new DefaultJavaSource(classLibrary);
    }

    @Override
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
