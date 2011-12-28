package com.thoughtworks.qdox.model.impl;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameterTest;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameter;

public class DefaultJavaParameterTest extends JavaParameterTest<DefaultJavaParameter>
{

    public DefaultJavaParameterTest( String s )
    {
        super( s );
    }

    @Override
    protected DefaultJavaParameter newJavaParameter( JavaClass type, String name )
    {
        return new DefaultJavaParameter( type, name );
    }

    @Override
    protected DefaultJavaParameter newJavaParameter( JavaClass type, String name, boolean varArgs )
    {
        return new DefaultJavaParameter( type, name, varArgs );
    }

    @Override
    protected void setMethod( DefaultJavaParameter parameter, JavaMethod method )
    {
        parameter.setParentMethod( method );
    }
}