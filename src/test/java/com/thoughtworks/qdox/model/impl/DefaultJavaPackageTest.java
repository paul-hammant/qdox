package com.thoughtworks.qdox.model.impl;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.JavaPackageTest;

import static org.mockito.Mockito.mock;

public class DefaultJavaPackageTest extends JavaPackageTest<DefaultJavaPackage>
{
    @Override
	public DefaultJavaPackage newJavaPackage( String name )
    {
        DefaultJavaPackage result = new DefaultJavaPackage( name );
        result.setClassLibrary( mock(ClassLibrary.class) );
        return result;
    }

}
