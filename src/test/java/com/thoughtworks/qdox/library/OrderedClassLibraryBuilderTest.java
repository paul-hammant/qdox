package com.thoughtworks.qdox.library;


public class OrderedClassLibraryBuilderTest
    extends ClassLibraryBuilderTest
{
    @Override
	protected ClassLibraryBuilder getClassLibraryBuilder()
    {
        return new OrderedClassLibraryBuilder();
    }
}
