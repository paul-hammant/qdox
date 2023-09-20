package com.thoughtworks.qdox.model.impl;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructorTest;
import com.thoughtworks.qdox.model.JavaParameter;

import java.util.List;


public class DefaultJavaConstructorTest extends JavaConstructorTest<DefaultJavaConstructor>
{

    @Override
    protected DefaultJavaConstructor newJavaConstructor( String name )
    {
        DefaultJavaConstructor result = new DefaultJavaConstructor();
        result.setName( name );
        return result;
    }
    
    @Override
    protected void setModifiers( DefaultJavaConstructor constructor, List<String> modifiers )
    {
        constructor.setModifiers( modifiers );
        
    }
    
    @Override
    protected void setParameters( DefaultJavaConstructor constructor, List<JavaParameter> parameters )
    {
        constructor.setParameters( parameters );
    }
    
    @Override
    protected void setDeclaringClass( DefaultJavaConstructor constructor, JavaClass parentClass )
    {
        constructor.setDeclaringClass( parentClass );
    }
}
