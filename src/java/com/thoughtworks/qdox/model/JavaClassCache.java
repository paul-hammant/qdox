package com.thoughtworks.qdox.model;

import java.io.Serializable;

public interface JavaClassCache extends Serializable {
    public JavaClass[] getClasses();
    public JavaClass getClassByName(String name);
    public void putClassByName(String name, JavaClass javaClass);
}
