package com.thoughtworks.qdox.library;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import com.thoughtworks.qdox.model.JavaClass;

public interface ClassLibraryBuilder
{

    public ClassLibraryBuilder appendClassLoader( ClassLoader classLoader );

    public ClassLibraryBuilder appendSourceFolder( File sourceFolder );

    public ClassLibraryBuilder appendSource( InputStream stream );

    public ClassLibraryBuilder appendSource( Reader reader );

    public ClassLibraryBuilder appendSource( URL reader, String encoding ) throws IOException;

    public JavaClass addSource( InputStream stream );

    public JavaClass addSource( Reader reader );
    
    public JavaClass addSource( URL reader, String encoding ) throws IOException;

    public ClassLibraryBuilder setDebugLexer( boolean debugLexer );

    public ClassLibraryBuilder setDebugParser( boolean debugParser );

    public ClassLibrary getClassLibrary();

}