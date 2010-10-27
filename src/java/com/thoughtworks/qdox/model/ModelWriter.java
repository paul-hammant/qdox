package com.thoughtworks.qdox.model;

public interface ModelWriter
{
    public ModelWriter writeSource( JavaSource source );

    public ModelWriter writeClass( JavaClass clazz );
    
    public ModelWriter writeField( JavaField field );
    
    public ModelWriter writeMethod( JavaMethod method );
}