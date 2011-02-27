package com.thoughtworks.qdox.model;

public class DefaultJavaParameterTest extends JavaParameterTest<DefaultJavaParameter>
{

    public DefaultJavaParameterTest( String s )
    {
        super( s );
    }

    @Override
    public Type newType( String typeName )
    {
        return new Type(typeName);
    }

    @Override
    public DefaultJavaParameter newJavaParameter( Type type, String name )
    {
        return new DefaultJavaParameter(type, name);
    }

    @Override
    public void setMethod( DefaultJavaParameter parameter, JavaMethod method )
    {
        parameter.setParentMethod( method );
    }
}
