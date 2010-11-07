package com.thoughtworks.qdox.model;

public class DefaultJavaMethodTest
    extends JavaMethodTest
{

    public DefaultJavaMethodTest( String s )
    {
        super( s );
    }

    public JavaClass newJavaClass()
    {
        return new JavaClass();
    }

    public JavaClass newJavaClass( String fullname )
    {
        return new JavaClass( fullname );
    }

    public JavaMethod newJavaMethod()
    {
        return new JavaMethod();
    }

    public JavaMethod newJavaMethod( Type returns, String name )
    {
        return new JavaMethod( returns, name );
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

    public void setExceptions( JavaMethod method, Type[] exceptions )
    {
        method.setExceptions( exceptions );
    }

    public void setComment( JavaMethod method, String comment )
    {
        method.setComment( comment );
    }

    public void setConstructor( JavaMethod method, boolean isConstructor )
    {
        method.setConstructor( isConstructor );
    }

    public void setName( JavaMethod method, String name )
    {
        method.setName( name );
    }

    public void setModifiers( JavaMethod method, String[] modifiers )
    {
        method.setModifiers( modifiers );
    }

    public void setReturns( JavaMethod method, Type type )
    {
        method.setReturns( type );
    }

    public void addClass( JavaSource source, JavaClass clazz )
    {
        clazz.setSource( source );
        source.addClass( clazz );
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

    public void setSourceCode( JavaMethod method, String code )
    {
        method.setSourceCode( code );
    }
}
