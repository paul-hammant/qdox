package com.thoughtworks.qdox.model;

public interface JavaClassParent {

    public String asClassNamespace();

    public JavaSource getParentSource();

    public void addClass(JavaClass cls);

    public JavaClass[] getClasses();

}
