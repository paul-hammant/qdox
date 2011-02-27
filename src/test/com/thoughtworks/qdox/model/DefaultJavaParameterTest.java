package com.thoughtworks.qdox.model;

public class DefaultJavaParameterTest extends JavaParameterTest<DefaultJavaParameter>
{

    public DefaultJavaParameterTest( String s )
    {
        super( s );
    }

    public Type newType( String typeName )
    {
        return new Type(typeName);
    }

    public DefaultJavaParameter newJavaParameter( Type type, String name )
    {
        return new DefaultJavaParameter(type, name);
    }

    public JavaMethod newJavaMethod()
    {
        return new DefaultJavaMethod();
    }

    @Override
    public void setMethod( DefaultJavaParameter parameter, JavaMethod method )
    {
        parameter.setParentMethod( method );
        ((AbstractBaseMethod) method).addParameter( parameter );
    }
}
