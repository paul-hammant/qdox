package com.thoughtworks.qdox.model;

public class DefaultJavaPackageTest extends JavaPackageTest
{

    public JavaPackage newJavaPackage( String name )
    {
        return new JavaPackage( name );
    }

}
