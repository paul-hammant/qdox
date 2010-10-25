package com.thoughtworks.qdox.model;

public class DefaultJavaParameterTest extends JavaParameterTest
{

    public DefaultJavaParameterTest( String s )
    {
        super( s );
    }

    public Type newType( String typeName )
    {
        return new Type(typeName);
    }

    public JavaParameter newJavaParameter( Type type, String name )
    {
        return new JavaParameter(type, name);
    }

    public JavaMethod newJavaMethod()
    {
        return new JavaMethod();
    }

    public void addParameter( JavaMethod method, JavaParameter parameter )
    {
        method.addParameter( parameter );
        parameter.setParentMethod( method );
    }

}
