package com.thoughtworks.qdox.io;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;

public interface ModelWriter
{
    public ModelWriter writeSource( JavaSource source );

    public ModelWriter writeClass( JavaClass clazz );
    
    public ModelWriter writeField( JavaField field );
    
    public ModelWriter writeMethod( JavaMethod method );
}