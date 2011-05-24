package com.thoughtworks.qdox.model;

import static org.mockito.Mockito.*;

import com.thoughtworks.qdox.library.ClassLibrary;

public class DefaultJavaPackageTest extends JavaPackageTest<DefaultJavaPackage>
{

    public DefaultJavaPackage newJavaPackage( String name )
    {
        DefaultJavaPackage result = new DefaultJavaPackage( name );
        result.setClassLibrary( mock(ClassLibrary.class) );
        return result;
    }

}
