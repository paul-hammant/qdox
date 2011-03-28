package com.thoughtworks.qdox.model;

public class DefaultJavaParameterTest extends JavaParameterTest<DefaultJavaParameter>
{

    protected DefaultJavaParameterTest( String s )
    {
        super( s );
    }

    @Override
    protected DefaultJavaParameter newJavaParameter( Type type, String name )
    {
        return new DefaultJavaParameter(type, name);
    }

    @Override
    protected void setMethod( DefaultJavaParameter parameter, JavaMethod method )
    {
        parameter.setParentMethod( method );
    }
}
