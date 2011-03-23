package com.thoughtworks.qdox.library;

public class SortedClassLibraryBuilderTest
    extends ClassLibraryBuilderTest
{
    protected ClassLibraryBuilder getClassLibraryBuilder()
    {
        return new SortedClassLibraryBuilder();
    }
}
