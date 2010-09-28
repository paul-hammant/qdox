package com.thoughtworks.qdox.library;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import com.thoughtworks.qdox.model.JavaClass;

/**
 * @author Robert Scholte
 */
public class SortedClassLibraryBuilder
    implements ClassLibraryBuilder
{
    private ClassNameLibrary classNameLibrary;

    private ClassLoaderLibrary classLoaderLibrary;

    private SourceFolderLibrary sourceFolderLibrary;

    private SourceLibrary sourceLibrary;

    public SortedClassLibraryBuilder()
    {
        classNameLibrary = new ClassNameLibrary();
        classLoaderLibrary = new ClassLoaderLibrary( classNameLibrary );
        sourceFolderLibrary = new SourceFolderLibrary( classLoaderLibrary );
        sourceLibrary = new SourceLibrary( sourceFolderLibrary );
    }

    public ClassLibraryBuilder appendClassLoader( ClassLoader classLoader )
    {
        classLoaderLibrary.addClassLoader( classLoader );
        return this;
    }

    public ClassLibraryBuilder appendSourceFolder( File sourceFolder )
    {
        sourceFolderLibrary.addSourceFolder( sourceFolder );
        return this;
    }

    public ClassLibraryBuilder appendSource( InputStream stream )
    {
        sourceLibrary.addSource( stream );
        return this;
    }

    public ClassLibraryBuilder appendSource( Reader reader )
    {
        sourceLibrary.addSource( reader );
        return this;
    }

    public ClassLibraryBuilder setDebugLexer( boolean debugLexer )
    {
        sourceFolderLibrary.setDebugLexer( debugLexer );
        sourceLibrary.setDebugLexer( debugLexer );
        return this;
    }

    public ClassLibraryBuilder setDebugParser( boolean debugParser )
    {
        sourceFolderLibrary.setDebugParser( debugParser );
        sourceLibrary.setDebugParser( debugParser );
        return this;
    }

    public ClassLibrary getClassLibrary()
    {
        return sourceLibrary;
    }

    public ClassLibraryBuilder appendSource( URL url, String encoding )
        throws IOException
    {
        return appendSource( new InputStreamReader( url.openStream(), encoding ) );
    }

    public JavaClass addSource( InputStream stream )
    {
        return sourceLibrary.addSource( stream );
    }

    public JavaClass addSource( Reader reader )
    {
        return sourceLibrary.addSource( reader );
    }

    public JavaClass addSource( URL url, String encoding )
        throws IOException
    {
        return addSource( new InputStreamReader( url.openStream(), encoding ) );
    }

}
