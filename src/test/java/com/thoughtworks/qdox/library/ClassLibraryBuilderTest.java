package com.thoughtworks.qdox.library;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.StringReader;

public abstract class ClassLibraryBuilderTest {

    protected abstract ClassLibraryBuilder getClassLibraryBuilder();

    @Test
    public final void testAppendClassLoader()
    {
        ClassLibraryBuilder libraryBuilder = getClassLibraryBuilder();
        ClassLibraryBuilder result = libraryBuilder.appendClassLoader( null );
        Assertions.assertSame(libraryBuilder, result);
    }

    @Test
    public final void testAppendDefaultClassLoaders()
    {
        ClassLibraryBuilder libraryBuilder = getClassLibraryBuilder();
        ClassLibraryBuilder result = libraryBuilder.appendDefaultClassLoaders();
        Assertions.assertSame(libraryBuilder, result);
    }

    @Test
    public final void testAppendSourceFolder()
    {
        ClassLibraryBuilder libraryBuilder = getClassLibraryBuilder();
        ClassLibraryBuilder result = libraryBuilder.appendSourceFolder( null );
        Assertions.assertSame(libraryBuilder, result);
    }

    @Test
    public final void testAppendSourceInputStream() throws Exception
    {
        ClassLibraryBuilder libraryBuilder = getClassLibraryBuilder();
        ClassLibraryBuilder result =
            libraryBuilder.appendSource( new ByteArrayInputStream( "package x.y.z;".getBytes( "UTF-8" ) ) );
        Assertions.assertSame(libraryBuilder, result);
    }

    @Test
    public final void testAppendSourceReader()
    {
        ClassLibraryBuilder libraryBuilder = getClassLibraryBuilder();
        ClassLibraryBuilder result = libraryBuilder.appendSource( new StringReader("package x.y.z;") );
        Assertions.assertSame(libraryBuilder, result);
    }

//    public final void testAppendSourceURL() throws Exception
//    {
//        ClassLibraryBuilder libraryBuilder = getClassLibraryBuilder();
//        ClassLibraryBuilder result = libraryBuilder.appendSource( (URL) null );
//        assertSame( libraryBuilder, result );
//    }
//
//    public final void testAppendSourceFile() throws Exception
//    {
//        ClassLibraryBuilder libraryBuilder = getClassLibraryBuilder();
//        ClassLibraryBuilder result = libraryBuilder.appendSource( (File) null );
//        assertSame( libraryBuilder, result );
//    }

    @Test
    public final void testGetClassLibrary()
    {
        ClassLibraryBuilder libraryBuilder = getClassLibraryBuilder();
        Assertions.assertNotNull(libraryBuilder.getClassLibrary());
    }
}
