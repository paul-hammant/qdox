package com.thoughtworks.qdox.model;

public class DefaultJavaPackageTest extends JavaPackageTest<DefaultJavaPackage>
{

    public DefaultJavaPackage newJavaPackage( String name )
    {
        return new DefaultJavaPackage( name );
    }

}
