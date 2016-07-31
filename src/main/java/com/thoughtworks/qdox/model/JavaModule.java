package com.thoughtworks.qdox.model;

public interface JavaModule
{
    String getName();
    
    boolean isNamed();
    
    JavaModuleDescriptor getDescriptor();
    
    
}
