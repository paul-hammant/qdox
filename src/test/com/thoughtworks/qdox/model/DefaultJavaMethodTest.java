package com.thoughtworks.qdox.model;

import java.util.List;

public class DefaultJavaMethodTest
    extends JavaMethodTest<DefaultJavaMethod>
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

    public DefaultJavaMethod newJavaMethod()
    {
        return new DefaultJavaMethod();
    }

    public DefaultJavaMethod newJavaMethod( Type returns, String name )
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

    public void setExceptions( DefaultJavaMethod method, List<Type> exceptions )
    {
        method.setExceptions( exceptions );
    }

    public void setComment( DefaultJavaMethod method, String comment )
    {
        method.setComment( comment );
    }

    public void setConstructor( DefaultJavaMethod method, boolean isConstructor )
    {
        method.setConstructor( isConstructor );
    }

    public void setName( DefaultJavaMethod method, String name )
    {
        method.setName( name );
    }

    public void setModifiers( DefaultJavaMethod method, List<String> modifiers )
    {
        method.setModifiers( modifiers );
    }

    public void setReturns( DefaultJavaMethod method, Type type )
    {
        method.setReturns( type );
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

    public void setSourceCode( DefaultJavaMethod method, String code )
    {
        method.setSourceCode( code );
    }
}
