package com.thoughtworks.qdox.model.impl;

import java.util.List;

import com.thoughtworks.qdox.library.SortedClassLibraryBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaClassTest;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;

public class DefaultJavaClassTest
    extends JavaClassTest<DefaultJavaClass>
{

    @Override
	public DefaultJavaClass newJavaClass()
    {
        return new DefaultJavaClass();
    }

    @Override
	public DefaultJavaClass newJavaClass( String name )
    {
        return new DefaultJavaClass( name );
    }

    @Override
	public JavaSource newJavaSource()
    {
        return new DefaultJavaSource(new SortedClassLibraryBuilder().getClassLibrary());
    }

    // Add-methods
    @Override
	public void setClasses( DefaultJavaClass clazz, List<JavaClass> innerClasses )
    {
        for( JavaClass innerClazz : innerClasses )
        {
            clazz.addClass( innerClazz );
        }
    }

    @Override
	public void addClass( JavaSource source, JavaClass clazz )
    {
        ((DefaultJavaClass) clazz).setSource( source );
        ((DefaultJavaSource) source).addClass( clazz );
    }

    // Set-methods
    @Override
	public void setComment( DefaultJavaClass clazz, String comment )
    {
        clazz.setComment( comment );
    }
    
    @Override
    public void setDeclaringClass( DefaultJavaClass clazz, JavaClass declaringClazz )
    {
        clazz.setDeclaringClass( declaringClazz );
        
    }

    @Override
	public void setEnum( DefaultJavaClass clazz, boolean isEnum )
    {
        clazz.setEnum( isEnum );
    }

    @Override
	public void setImplementz( DefaultJavaClass clazz, List<JavaClass> implementz )
    {
        clazz.setImplementz( implementz );
    }

    @Override
	public void setInterface( DefaultJavaClass clazz, boolean isInterface )
    {
        clazz.setInterface( isInterface );
    }

    @Override
	public void setModifiers( DefaultJavaClass clazz, List<String> modifiers )
    {
        clazz.setModifiers( modifiers );
    }

    @Override
	public void setName( DefaultJavaClass clazz, String name )
    {
        clazz.setName( name );
    }

    @Override
	public void setPackage( DefaultJavaClass clazz, JavaPackage pckg )
    {
        clazz.setJavaPackage( pckg );
    }

    @Override
	public void setPackage( JavaSource source, JavaPackage pckg )
    {
        ((DefaultJavaSource) source).setPackage( pckg );
    }

    @Override
	public void setSuperClass( DefaultJavaClass clazz, JavaType type )
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
