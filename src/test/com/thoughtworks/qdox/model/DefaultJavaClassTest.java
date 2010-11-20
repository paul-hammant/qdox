package com.thoughtworks.qdox.model;

import java.util.Arrays;
import java.util.List;

import com.thoughtworks.qdox.library.SortedClassLibraryBuilder;

public class DefaultJavaClassTest
    extends JavaClassTest
{

    public DefaultJavaClassTest( String s )
    {
        super( s );
    }

    public JavaClass newJavaClass()
    {
        return new JavaClass();
    }

    public JavaClass newJavaClass( String name )
    {
        return new JavaClass( name );
    }

    public JavaField newJavaField(JavaClass parentClass)
    {
        DefaultJavaField result = new DefaultJavaField();
        result.setParentClass( parentClass );
        return result;
    }

    public JavaMethod newJavaMethod()
    {
        return new JavaMethod();
    }

    public JavaMethod newJavaMethod( String name )
    {
        return new JavaMethod( name );
    }

    public JavaMethod newJavaMethod( Type returns, String name )
    {
        return new JavaMethod( returns, name );
    }

    public JavaPackage newJavaPackage( String name )
    {
        return new DefaultJavaPackage( name );
    }

    public JavaParameter newJavaParameter( Type type, String name )
    {
        return new DefaultJavaParameter( type, name );
    }

    public JavaParameter newJavaParameter( Type type, String name, boolean varArgs )
    {
        return new DefaultJavaParameter( type, name, varArgs );
    }

    public JavaSource newJavaSource()
    {
        return new DefaultJavaSource(new SortedClassLibraryBuilder().getClassLibrary());
    }

    public Type newType( String fullname )
    {
        return new Type( fullname );
    }

    // Add-methods
    public void addClass( JavaClass clazz, JavaClass innerClazz )
    {
        clazz.addClass( innerClazz );
        innerClazz.setParentClass( clazz );
    }

    public void addClass( JavaPackage pckg, JavaClass clazz )
    {
        ((DefaultJavaPackage) pckg).addClass( clazz );
        clazz.setJavaPackage( pckg );
    }

    public void addClass( JavaSource source, JavaClass clazz )
    {
        clazz.setSource( source );
        source.addClass( clazz );
    }

    public void addField( JavaClass clazz, JavaField field )
    {
        clazz.addField( field );
        ((DefaultJavaField) field).setParentClass( clazz );
    }

    public void addMethod( JavaClass clazz, JavaMethod method )
    {
        clazz.addMethod( method );
        method.setParentClass( clazz );
    }

    public void addParameter( JavaMethod method, JavaParameter parameter )
    {
        method.addParameter( parameter );
        ((DefaultJavaParameter) parameter).setParentMethod( method );
    }

    // Set-methods
    public void setComment( JavaClass clazz, String comment )
    {
        clazz.setComment( comment );
    }

    public void setComment( JavaField field, String comment )
    {
        ((DefaultJavaField) field).setComment( comment );
    }

    public void setComment( JavaMethod method, String comment )
    {
        method.setComment( comment );
    }

    public void setEnum( JavaClass clazz, boolean isEnum )
    {
        clazz.setEnum( isEnum );
    }

    public void setImplementz( JavaClass clazz, List<Type> implementz )
    {
        clazz.setImplementz( implementz );
    }

    public void setInterface( JavaClass clazz, boolean isInterface )
    {
        clazz.setInterface( isInterface );
    }

    public void setModifiers( JavaClass clazz, List<String> modifiers )
    {
        clazz.setModifiers( modifiers );
    }

    public void setModifiers( JavaField field, List<String> modifiers )
    {
        ((DefaultJavaField) field).setModifiers( modifiers );
    }

    public void setName( JavaClass clazz, String name )
    {
        clazz.setName( name );
    }

    public void setName( JavaField field, String name )
    {
        ((DefaultJavaField) field).setName( name );
    }

    public void setName( JavaMethod method, String name )
    {
        method.setName( name );
    }

    public void setPackage( JavaSource source, JavaPackage pckg )
    {
        ((DefaultJavaSource) source).setPackage( pckg );
    }

    public void setReturns( JavaMethod clazz, Type returns )
    {
        clazz.setReturns( returns );
    }

    public void setSuperClass( JavaClass clazz, Type type )
    {
        clazz.setSuperClass( type );
    }

    public void setType( JavaField field, Type type )
    {
        ((DefaultJavaField) field).setType( type );
    }

}
