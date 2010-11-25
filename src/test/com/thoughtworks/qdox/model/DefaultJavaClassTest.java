package com.thoughtworks.qdox.model;

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
        return new DefaultJavaClass();
    }

    public JavaClass newJavaClass( String name )
    {
        return new DefaultJavaClass( name );
    }

    public JavaField newJavaField(JavaClass parentClass)
    {
        DefaultJavaField result = new DefaultJavaField();
        result.setParentClass( parentClass );
        return result;
    }

    public JavaMethod newJavaMethod()
    {
        return new DefaultJavaMethod();
    }

    public JavaMethod newJavaMethod( String name )
    {
        return new DefaultJavaMethod( name );
    }

    public JavaMethod newJavaMethod( Type returns, String name )
    {
        return new DefaultJavaMethod( returns, name );
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
        ((DefaultJavaClass) clazz).addClass( innerClazz );
        ((DefaultJavaClass) innerClazz).setParentClass( clazz );
    }

    public void addClass( JavaPackage pckg, JavaClass clazz )
    {
        ((DefaultJavaPackage) pckg).addClass( clazz );
        ((DefaultJavaClass) clazz).setJavaPackage( pckg );
    }

    public void addClass( JavaSource source, JavaClass clazz )
    {
        ((DefaultJavaClass) clazz).setSource( source );
        source.addClass( clazz );
    }

    public void addField( JavaClass clazz, JavaField field )
    {
        ((DefaultJavaClass)clazz).addField( field );
        ((DefaultJavaField) field).setParentClass( clazz );
    }

    public void addMethod( JavaClass clazz, JavaMethod method )
    {
        ((DefaultJavaClass)clazz).addMethod( method );
        ((DefaultJavaMethod) method).setParentClass( clazz );
    }

    public void addParameter( JavaMethod method, JavaParameter parameter )
    {
        ((DefaultJavaMethod) method).addParameter( parameter );
        ((DefaultJavaParameter) parameter).setParentMethod( method );
    }

    // Set-methods
    public void setComment( JavaClass clazz, String comment )
    {
        ((DefaultJavaClass) clazz).setComment( comment );
    }

    public void setComment( JavaField field, String comment )
    {
        ((DefaultJavaField) field).setComment( comment );
    }

    public void setComment( JavaMethod method, String comment )
    {
        ((DefaultJavaMethod) method).setComment( comment );
    }

    public void setEnum( JavaClass clazz, boolean isEnum )
    {
        ((DefaultJavaClass) clazz).setEnum( isEnum );
    }

    public void setImplementz( JavaClass clazz, List<Type> implementz )
    {
        ((DefaultJavaClass) clazz).setImplementz( implementz );
    }

    public void setInterface( JavaClass clazz, boolean isInterface )
    {
        ((DefaultJavaClass) clazz).setInterface( isInterface );
    }

    public void setModifiers( JavaClass clazz, List<String> modifiers )
    {
        ((DefaultJavaClass) clazz).setModifiers( modifiers );
    }

    public void setModifiers( JavaField field, List<String> modifiers )
    {
        ((DefaultJavaField) field).setModifiers( modifiers );
    }

    public void setName( JavaClass clazz, String name )
    {
        ((DefaultJavaClass) clazz).setName( name );
    }

    public void setName( JavaField field, String name )
    {
        ((DefaultJavaField) field).setName( name );
    }

    public void setName( JavaMethod method, String name )
    {
        ((DefaultJavaMethod) method).setName( name );
    }

    public void setPackage( JavaSource source, JavaPackage pckg )
    {
        ((DefaultJavaSource) source).setPackage( pckg );
    }

    public void setReturns( JavaMethod method, Type returns )
    {
        ((DefaultJavaMethod) method).setReturns( returns );
    }

    public void setSuperClass( JavaClass clazz, Type type )
    {
        ((DefaultJavaClass) clazz).setSuperClass( type );
    }

    public void setType( JavaField field, Type type )
    {
        ((DefaultJavaField) field).setType( type );
    }

}
