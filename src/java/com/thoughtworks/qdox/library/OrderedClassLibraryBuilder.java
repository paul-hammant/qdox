package com.thoughtworks.qdox.library;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.ModelBuilder;
import com.thoughtworks.qdox.model.ModelBuilderFactory;

/**
 * This library resolves JavaClasses in the order in which class sources are added.
 * 
 * @author Robert Scholte
 */
public class OrderedClassLibraryBuilder implements ClassLibraryBuilder
{

    private ClassLibrary classLibrary;
    
    private boolean debugLexer;

    private boolean debugParser;
    
    private String encoding;
    
    private boolean appendDefaultClassLoaders;
    
    private ModelBuilderFactory modelBuilderFactory;
    
    public OrderedClassLibraryBuilder()
    {
        classLibrary = new ClassNameLibrary();
    }

    /**
     * If the appendDefaultClassLoaders has been set and hans't been used, add those
     * classloaders and reset the value to false.
     * Next add the classloader
     */
    public ClassLibraryBuilder appendClassLoader( ClassLoader classLoader )
    {
        if ( !( classLibrary instanceof ClassLoaderLibrary ) )
        {
            classLibrary = new ClassLoaderLibrary( classLibrary );
        }
        ClassLoaderLibrary classLoaderLibrary = (ClassLoaderLibrary) classLibrary;
        if ( appendDefaultClassLoaders ) {
            classLoaderLibrary.addDefaultLoader();
            appendDefaultClassLoaders = false;
        }
        classLoaderLibrary.addClassLoader( classLoader );
        classLoaderLibrary.setModelBuilderFactory( modelBuilderFactory );
        return this;
    }

    /**
     * This method can be called both before or after appendClassLoader.
     * If the current classLibrary is a ClassLoaderLibrary, add it immediately
     * Otherwise keep this setting only for the first next ClassLoaderLibrary
     */
    public ClassLibraryBuilder appendDefaultClassLoaders()
    {
        if ( classLibrary instanceof ClassLoaderLibrary )
        {
            ClassLoaderLibrary classLoaderLibrary = (ClassLoaderLibrary) classLibrary;
            classLoaderLibrary.addDefaultLoader();
        }
        else {
            appendDefaultClassLoaders = true;
        }
        return this;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#addSourceFolder(java.io.File)
     */
    public ClassLibraryBuilder appendSourceFolder( File sourceFolder )
    {
        if ( !( classLibrary instanceof SourceFolderLibrary ) )
        {
            classLibrary = new SourceFolderLibrary( classLibrary );
        }
        SourceFolderLibrary sourceFolderLibrary = (SourceFolderLibrary) classLibrary;
        prepareSourceLibrary( sourceFolderLibrary );
        sourceFolderLibrary.addSourceFolder( sourceFolder );
        return this;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#addSource(java.io.InputStream)
     */
    public ClassLibraryBuilder appendSource( InputStream stream )
    {
        SourceLibrary sourceLibrary = getSourceLibrary();
        sourceLibrary.addSource( stream );
        return this;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#addSource(java.io.Reader)
     */
    public ClassLibraryBuilder appendSource( Reader reader )
    {
        SourceLibrary sourceLibrary = getSourceLibrary();
        sourceLibrary.addSource( reader );
        return this;
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#setDebugLexer(boolean)
     */
    public ClassLibraryBuilder setDebugLexer( boolean debugLexer )
    {
        this.debugLexer = debugLexer;
        return this;
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#setDebugParser(boolean)
     */
    public ClassLibraryBuilder setDebugParser( boolean debugParser )
    {
        this.debugParser = debugParser;
        return this;
    }
    
    public ClassLibraryBuilder setEncoding( String encoding )
    {
        this.encoding = encoding;
        return this;
    }
    
    public ClassLibraryBuilder setModelBuilderFactory( ModelBuilderFactory modelBuilderFactory )
    {
        this.modelBuilderFactory = modelBuilderFactory;
        return this;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#getClassLibrary()
     */
    public ClassLibrary getClassLibrary()
    {
        return classLibrary;
    }

    public ClassLibraryBuilder appendSource( URL url ) throws IOException
    {
        SourceLibrary sourceLibrary = getSourceLibrary();
        sourceLibrary.addSource( url );
        return this;
    }
    
    public ClassLibraryBuilder appendSource( File file )
        throws IOException
    {
        SourceLibrary sourceLibrary = getSourceLibrary();
        sourceLibrary.addSource( file );
        return this;
    }

    public JavaClass addSource( InputStream stream )
    {
        SourceLibrary sourceLibrary = getSourceLibrary();
        return sourceLibrary.addSource( stream );
    }

    public JavaClass addSource( Reader reader )
    {
        SourceLibrary sourceLibrary = getSourceLibrary();
        return sourceLibrary.addSource( reader );
    }

    public JavaClass addSource( URL url ) throws IOException
    {
        SourceLibrary sourceLibrary = getSourceLibrary();
        return sourceLibrary.addSource( url );
    }
    
    public JavaClass addSource( File file )
        throws IOException
    {
        SourceLibrary sourceLibrary = getSourceLibrary();
        return sourceLibrary.addSource( file );
    }
    
    private void prepareSourceLibrary( SourceLibrary sourceLibrary ) {
        sourceLibrary.setModelBuilderFactory( modelBuilderFactory );
        sourceLibrary.setDebugLexer( debugLexer );
        sourceLibrary.setDebugParser( debugParser );
        sourceLibrary.setEncoding( encoding );
    }
    
    private SourceLibrary getSourceLibrary() {
        if ( !( classLibrary instanceof SourceLibrary ) )
        {
            classLibrary = new SourceLibrary( classLibrary );
        }
        SourceLibrary sourceLibrary = (SourceLibrary) classLibrary;
        prepareSourceLibrary( sourceLibrary );
        return sourceLibrary;
    }

}
