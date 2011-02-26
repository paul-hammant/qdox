package com.thoughtworks.qdox.model;

import java.util.List;

public class DefaultJavaMethodTest
    extends JavaMethodTest
{

    public DefaultJavaMethodTest( String s )
    {
        super( s );
    }

    public JavaClass newJavaClass()
    {
        return new DefaultJavaClass();
    }

    public JavaClass newJavaClass( String fullname )
    {
        return new DefaultJavaClass( fullname );
    }

    public JavaMethod newJavaMethod()
    {
        return new DefaultJavaMethod();
    }

    public JavaMethod newJavaMethod( Type returns, String name )
    {
        return new DefaultJavaMethod( returns, name );
    }

    public JavaParameter newJavaParameter( Type type, String name )
    {
        return new DefaultJavaParameter( type, name );
    }

    public JavaParameter newJavaParameter( Type type, String name, boolean varArgs )
    {
        return new DefaultJavaParameter( type, name, varArgs );
    }

    public JavaSource newJavaSource(com.thoughtworks.qdox.library.ClassLibrary classLibrary )
    {
        return new DefaultJavaSource(classLibrary);
    }

    public Type newType( String fullname )
    {
        return new Type( fullname );
    }

    public Type newType( String fullname, int dimensions )
    {
        return new Type( fullname, dimensions );
    }

    public void setExceptions( JavaMethod method, List<Type> exceptions )
    {
        ((AbstractBaseMethod) method).setExceptions( exceptions );
    }

    public void setComment( JavaMethod method, String comment )
    {
        ((AbstractBaseMethod) method).setComment( comment );
    }

    public void setConstructor( JavaMethod method, boolean isConstructor )
    {
        ((DefaultJavaMethod) method).setConstructor( isConstructor );
    }

    public void setName( JavaMethod method, String name )
    {
        ((AbstractBaseMethod) method).setName( name );
    }

    public void setModifiers( JavaMethod method, List<String> modifiers )
    {
        ((AbstractBaseMethod) method).setModifiers( modifiers );
    }

    public void setReturns( JavaMethod method, Type type )
    {
        ((DefaultJavaMethod) method).setReturns( type );
    }

    public void addClass( JavaSource source, JavaClass clazz )
    {
        ((DefaultJavaClass) clazz).setSource( source );
        ((DefaultJavaSource) source).addClass( clazz );
    }

    public void addMethod( JavaClass clazz, JavaMethod method )
    {
        ((DefaultJavaClass) clazz).addMethod( method );
        ((AbstractBaseMethod) method).setParentClass( clazz );
    }

    public void addParameter( JavaMethod method, JavaParameter parameter )
    {
        ((AbstractBaseMethod) method).addParameter( parameter );
        ((DefaultJavaParameter) parameter).setParentMethod( method );
    }

    public void setSourceCode( JavaMethod method, String code )
    {
        ((AbstractBaseMethod) method).setSourceCode( code );
    }
}
