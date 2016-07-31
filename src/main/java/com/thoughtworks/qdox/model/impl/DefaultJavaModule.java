package com.thoughtworks.qdox.model.impl;

import com.thoughtworks.qdox.model.JavaModule;
import com.thoughtworks.qdox.model.JavaModuleDescriptor;

public class DefaultJavaModule extends AbstractJavaModel
    implements JavaModule
{
    private String name;
    
    private JavaModuleDescriptor descriptor;

    public DefaultJavaModule( String name, JavaModuleDescriptor descriptor )
    {
        this.name = name;
        this.descriptor = descriptor;
    }
    
    public String getCodeBlock()
    {
        return getModelWriter().writeModule( this ).toString();
    }
    
    public String getName()
    {
        return name;
    }

    public boolean isNamed()
    {
        return name != null;
    }

    public JavaModuleDescriptor getDescriptor()
    {
        return descriptor;
    }

}
