package com.thoughtworks.qdox.library;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

/**
 * 
 * 
 * @author Robert Scholte
 *
 */
public class SortedClassLibraryBuilder
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

    public SortedClassLibraryBuilder addClassLoader( ClassLoader classLoader )
    {
        classLoaderLibrary.addClassLoader( classLoader );
        return this;
    }

    public SortedClassLibraryBuilder addSourceFolder( File sourceFolder )
    {
        sourceFolderLibrary.addSourceFolder( sourceFolder );
        return this;
    }

    public SortedClassLibraryBuilder addSource( InputStream stream )
    {
        sourceLibrary.addSource( stream );
        return this;
    }

    public SortedClassLibraryBuilder addSource( Reader reader )
    {
        sourceLibrary.addSource( reader );
        return this;
    }
    
    public SortedClassLibraryBuilder setDebugLexer( boolean debugLexer )
    {
        sourceFolderLibrary.setDebugLexer( debugLexer );
        sourceLibrary.setDebugLexer( debugLexer );
        return this;
    }
    
    public SortedClassLibraryBuilder setDebugParser( boolean debugParser )
    {
        sourceFolderLibrary.setDebugParser( debugParser );
        sourceLibrary.setDebugParser( debugParser );
        return this;
    }

    public ClassLibrary getClassLibrary() {
        return sourceLibrary;
    }
}
