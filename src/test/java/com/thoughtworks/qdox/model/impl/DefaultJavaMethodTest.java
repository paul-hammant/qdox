package com.thoughtworks.qdox.model.impl;

import java.util.List;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethodTest;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.impl.DefaultJavaMethod;

public class DefaultJavaMethodTest
    extends JavaMethodTest<DefaultJavaMethod>
{

    public DefaultJavaMethod newJavaMethod()
    {
        return new DefaultJavaMethod();
    }

    public DefaultJavaMethod newJavaMethod( JavaClass returns, String name )
    {
        return new DefaultJavaMethod( returns, name );
    }

    public void setExceptions( DefaultJavaMethod method, List<JavaClass> exceptions )
    {
        method.setExceptions( exceptions );
    }

    public void setComment( DefaultJavaMethod method, String comment )
    {
        method.setComment( comment );
    }

    public void setName( DefaultJavaMethod method, String name )
    {
        method.setName( name );
    }

    public void setModifiers( DefaultJavaMethod method, List<String> modifiers )
    {
        method.setModifiers( modifiers );
    }

    public void setReturns( DefaultJavaMethod method, JavaClass type )
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
