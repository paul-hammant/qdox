package com.thoughtworks.qdox.library;

public class SortedClassLibraryBuilderTest
    extends ClassLibraryBuilderTest
{
    @Override
	protected ClassLibraryBuilder getClassLibraryBuilder()
    {
        return new SortedClassLibraryBuilder();
    }
}
