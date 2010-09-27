package com.thoughtworks.qdox.library;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

/**
 * This library resolves JavaClasses in the order in which class sources are added.
 * 
 * @author Robert Scholte
 */
public class OrderedClassLibraryBuilder
{

    private ClassLibrary classLibrary;

    private boolean debugLexer;

    private boolean debugParser;

    public OrderedClassLibraryBuilder()
    {
        classLibrary = new ClassNameLibrary();
    }

    public OrderedClassLibraryBuilder addClassLoader( ClassLoader classLoader )
    {
        if ( !( classLibrary instanceof ClassLoaderLibrary ) )
        {
            classLibrary = new ClassLoaderLibrary( classLibrary );
        }
        ( (ClassLoaderLibrary) classLibrary ).addClassLoader( classLoader );
        return this;
    }

    public OrderedClassLibraryBuilder addSourceFolder( File sourceFolder )
    {
        if ( !( classLibrary instanceof SourceFolderLibrary ) )
        {
            classLibrary = new SourceFolderLibrary( classLibrary );
        }
        ( (SourceFolderLibrary) classLibrary ).addSourceFolder( sourceFolder );
        return this;
    }

    public OrderedClassLibraryBuilder addSource( InputStream stream )
    {
        if ( !( classLibrary instanceof SourceLibrary ) )
        {
            classLibrary = new SourceLibrary( classLibrary );
        }
        SourceLibrary sourceLibrary = (SourceLibrary) classLibrary;
        sourceLibrary.addSource( stream );
        sourceLibrary.setDebugLexer( debugLexer );
        sourceLibrary.setDebugParser( debugParser );
        return this;
    }

    public OrderedClassLibraryBuilder addSource( Reader reader )
    {
        if ( !( classLibrary instanceof SourceLibrary ) )
        {
            classLibrary = new SourceLibrary( classLibrary );
        }
        ( (SourceLibrary) classLibrary ).addSource( reader );
        return this;
    }
    
    public OrderedClassLibraryBuilder setDebugLexer( boolean debugLexer )
    {
        this.debugLexer = debugLexer;
        return this;
    }
    
    public OrderedClassLibraryBuilder setDebugParser( boolean debugParser )
    {
        this.debugParser = debugParser;
        return this;
    }

    public ClassLibrary getClassLibrary()
    {
        return classLibrary;
    }

}
