package com.thoughtworks.qdox.model;

import java.io.File;
import java.net.URL;

public interface JavaSource extends JavaClassParent
{

    /**
     * 
     * @return
     * @since 2.0
     */
    public ModelWriter getModelWriter();

    /**
     * @since 1.4
     */
    public URL getURL();

    /**
     * @deprecated use getURL
     */
    public File getFile();

    public JavaPackage getPackage();

    /**
     * Retrieve all the import
     * 
     * @return the imports, never null
     */
    public String[] getImports();

    public JavaClass[] getClasses();

    public String getCodeBlock();

    public String resolveType( String typeName );

    public String getClassNamePrefix();

    public JavaSource getParentSource();

    public JavaClass getNestedClassByName( String name );

    public com.thoughtworks.qdox.library.ClassLibrary getJavaClassLibrary();

    public String getPackageName();

}