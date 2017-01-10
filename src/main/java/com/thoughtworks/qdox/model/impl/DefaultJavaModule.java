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
    
    /** {@inheritDoc} */
    public String getCodeBlock()
    {
        return getModelWriter().writeModuleDescriptor( this.descriptor ).toString();
    }
    
    /** {@inheritDoc} */
    public String getName()
    {
        return name;
    }

    /** {@inheritDoc} */
    public boolean isNamed()
    {
        return name != null;
    }

    /** {@inheritDoc} */
    public JavaModuleDescriptor getDescriptor()
    {
        return descriptor;
    }

}
