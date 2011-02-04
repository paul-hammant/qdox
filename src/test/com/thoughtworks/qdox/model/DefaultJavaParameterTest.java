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
        return new DefaultJavaParameter(type, name);
    }

    public JavaMethod newJavaMethod()
    {
        return new DefaultJavaMethod();
    }

    public void addParameter( JavaMethod method, JavaParameter parameter )
    {
        ((AbstractBaseMethod) method).addParameter( parameter );
        ((DefaultJavaParameter) parameter).setParentMethod( method );
    }

}
