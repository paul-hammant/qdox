package com.thoughtworks.qdox.model.impl;

import java.util.List;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethodTest;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.impl.DefaultJavaMethod;

public class DefaultJavaMethodTest
    extends JavaMethodTest<DefaultJavaMethod>
{

    @Override
	public DefaultJavaMethod newJavaMethod()
    {
        return new DefaultJavaMethod();
    }

    @Override
	public DefaultJavaMethod newJavaMethod( JavaClass returns, String name )
    {
        return new DefaultJavaMethod( returns, name );
    }

    @Override
	public void setExceptions( DefaultJavaMethod method, List<JavaClass> exceptions )
    {
        method.setExceptions( exceptions );
    }

    @Override
	public void setComment( DefaultJavaMethod method, String comment )
    {
        method.setComment( comment );
    }

    @Override
	public void setName( DefaultJavaMethod method, String name )
    {
        method.setName( name );
    }

    @Override
	public void setModifiers( DefaultJavaMethod method, List<String> modifiers )
    {
        method.setModifiers( modifiers );
    }

    @Override
	public void setReturns( DefaultJavaMethod method, JavaClass type )
    {
        method.setReturns( type );
    }

    @Override
	public void setDeclaringClass( DefaultJavaMethod method, JavaClass clazz )
    {
        method.setDeclaringClass( clazz );
    }

    @Override
    public void setParameters( DefaultJavaMethod method, List<JavaParameter> parameters )
    {
        method.setParameters( parameters );
    }

    @Override
	public void setSourceCode( DefaultJavaMethod method, String code )
    {
        method.setSourceCode( code );
    }
}
