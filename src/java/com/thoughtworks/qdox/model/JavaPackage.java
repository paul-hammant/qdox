package com.thoughtworks.qdox.model;

import java.util.List;

public interface JavaPackage extends JavaAnnotatedElement
{

    /**
     * Returns all the classes found for the package.
     *
     * @return all the classes found for the package
     */
    public List<JavaClass> getClasses();

    public JavaPackage getParentPackage();

    public List<JavaPackage> getSubPackages();

    public String getName();

    public int getLineNumber();

}