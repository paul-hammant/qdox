package com.thoughtworks.qdox.model;

public interface JavaClassCache {
    public JavaClass[] getClasses();
    public JavaClass getClassByName(String name);
}
