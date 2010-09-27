package com.thoughtworks.qdox.library;

import java.io.Serializable;

import com.thoughtworks.qdox.model.JavaClass;

public interface ClassLibrary
    extends Serializable
{
    public JavaClass getJavaClass( String name );
}
