package com.thoughtworks.qdox.model.impl;

import static org.mockito.Mockito.*;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.model.JavaPackageTest;

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
