package com.thoughtworks.qdox.model;

import java.util.List;

public class DefaultJavaMethodTest
    extends JavaMethodTest<DefaultJavaMethod>
{

    public DefaultJavaMethodTest( String s )
    {
        super( s );
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

    public void setParentClass( DefaultJavaMethod method, JavaClass clazz )
    {
        method.setParentClass( clazz );
    }

    @Override
    public void setParameters( DefaultJavaMethod method, List<JavaParameter> parameters )
    {
        method.setParameters( parameters );
    }

    public void setSourceCode( DefaultJavaMethod method, String code )
    {
        method.setSourceCode( code );
    }
}
