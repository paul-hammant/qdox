package com.thoughtworks.qdox.model;

public interface JavaClassParent {

    /**
     * Resolve a type-name within the context of this source or class.
     * @param typeName name of a type
     * @return the fully-qualified name of the type, or null if it cannot
     *     be resolved
     */
    public String resolveType(String typeName);

    public ClassLibrary getClassLibrary();

    public String asClassNamespace();

    public JavaSource getParentSource();

    public void addClass(JavaClass cls);

}
