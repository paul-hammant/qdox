package com.thoughtworks.qdox.library;


public class OrderedClassLibraryBuilderTest
    extends ClassLibraryBuilderTest
{
    protected ClassLibraryBuilder getClassLibraryBuilder()
    {
        return new OrderedClassLibraryBuilder();
    }
}
