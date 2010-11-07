package com.thoughtworks.qdox.model;

public interface JavaPackage extends JavaAnnotatedElement
{

    /**
     * Returns all the classes found for the package.
     *
     * @return all the classes found for the package
     */
    public JavaClass[] getClasses();

    public JavaPackage getParentPackage();

    public JavaPackage[] getSubPackages();

    public String getName();

    public int getLineNumber();

}