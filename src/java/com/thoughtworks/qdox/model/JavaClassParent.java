package com.thoughtworks.qdox.model;

import com.thoughtworks.qdox.JavaClassContext;
import com.thoughtworks.qdox.library.ClassLibrary;

public interface JavaClassParent {

    /**
     * Resolve a type-name within the context of this source or class.
     * @param typeName name of a type
     * @return the fully-qualified name of the type, or null if it cannot
     *     be resolved
     */
    public String resolveType(String typeName);

    public JavaClassContext getJavaClassContext();

    public String getClassNamePrefix();

    public JavaSource getParentSource();

    public void addClass(JavaClass cls);
    
    public JavaClass getNestedClassByName(String name);
    
    public com.thoughtworks.qdox.library.ClassLibrary getJavaClassLibrary(); 
    
    public com.thoughtworks.qdox.model.ClassLibrary getClassLibrary();

}
