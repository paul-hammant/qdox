package com.thoughtworks.qdox.model.impl;

import java.util.List;

import com.thoughtworks.qdox.library.SortedClassLibraryBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaClassTest;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.impl.DefaultJavaSource;

public class DefaultJavaClassTest
    extends JavaClassTest<DefaultJavaClass>
{

    public DefaultJavaClassTest( String s )
    {
        super( s );
    }

    public DefaultJavaClass newJavaClass()
    {
        return new DefaultJavaClass();
    }

    public DefaultJavaClass newJavaClass( String name )
    {
        return new DefaultJavaClass( name );
    }

    public JavaSource newJavaSource()
    {
        return new DefaultJavaSource(new SortedClassLibraryBuilder().getClassLibrary());
    }

    // Add-methods
    public void addClass( JavaClass clazz, JavaClass innerClazz )
    {
        ((DefaultJavaClass) clazz).addClass( innerClazz );
        ((DefaultJavaClass) innerClazz).setParentClass( clazz );
    }

    public void addClass( JavaSource source, JavaClass clazz )
    {
        ((DefaultJavaClass) clazz).setSource( source );
        ((DefaultJavaSource) source).addClass( clazz );
    }

    // Set-methods
    public void setComment( DefaultJavaClass clazz, String comment )
    {
        clazz.setComment( comment );
    }

    public void setEnum( DefaultJavaClass clazz, boolean isEnum )
    {
        clazz.setEnum( isEnum );
    }

    public void setImplementz( DefaultJavaClass clazz, List<DefaultJavaType> implementz )
    {
        clazz.setImplementz( implementz );
    }

    public void setInterface( DefaultJavaClass clazz, boolean isInterface )
    {
        clazz.setInterface( isInterface );
    }

    public void setModifiers( DefaultJavaClass clazz, List<String> modifiers )
    {
        clazz.setModifiers( modifiers );
    }

    public void setName( DefaultJavaClass clazz, String name )
    {
        clazz.setName( name );
    }

    public void setPackage( DefaultJavaClass clazz, JavaPackage pckg )
    {
        clazz.setJavaPackage( pckg );
    }

    public void setPackage( JavaSource source, JavaPackage pckg )
    {
        ((DefaultJavaSource) source).setPackage( pckg );
    }

    public void setSuperClass( DefaultJavaClass clazz, DefaultJavaType type )
    {
        clazz.setSuperClass( type );
    }

    @Override
    public void setFields( DefaultJavaClass clazz, List<JavaField> fields )
    {
        for(JavaField field : fields) {
            clazz.addField( field );
        }
    }
    
    @Override
    public void setSource( DefaultJavaClass clazz, JavaSource source )
    {
        clazz.setSource( source );
    }
    
    @Override
    public void setMethods( DefaultJavaClass clazz, List<JavaMethod> methods )
    {
        for(JavaMethod method : methods) {
            clazz.addMethod( method );
        }
    }

}
