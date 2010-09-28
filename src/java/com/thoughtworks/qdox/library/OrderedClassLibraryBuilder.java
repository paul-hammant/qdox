package com.thoughtworks.qdox.library;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    
    private ModelBuilderFactory modelBuilderFactory;

    private boolean debugLexer;

    private boolean debugParser;
    
    public OrderedClassLibraryBuilder()
    {
        modelBuilderFactory = new ModelBuilderFactory()
        {
            public ModelBuilder newInstance()
            {
                return new ModelBuilder();
            }
        };
        classLibrary = new ClassNameLibrary();
    }

    public OrderedClassLibraryBuilder( ModelBuilderFactory modelBuilderFactory )
    {
        this.modelBuilderFactory = modelBuilderFactory;
        classLibrary = new ClassNameLibrary();
        
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#addClassLoader(java.lang.ClassLoader)
     */
    public ClassLibraryBuilder appendClassLoader( ClassLoader classLoader )
    {
        if ( !( classLibrary instanceof ClassLoaderLibrary ) )
        {
            classLibrary = new ClassLoaderLibrary( classLibrary );
        }
        ( (ClassLoaderLibrary) classLibrary ).addClassLoader( classLoader );
        return this;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#addSourceFolder(java.io.File)
     */
    public ClassLibraryBuilder appendSourceFolder( File sourceFolder )
    {
        if ( !( classLibrary instanceof SourceFolderLibrary ) )
        {
            classLibrary = new SourceFolderLibrary( modelBuilderFactory, classLibrary );
        }
        SourceFolderLibrary sourceFolderLibrary = (SourceFolderLibrary) classLibrary;
        sourceFolderLibrary.setDebugLexer( debugLexer );
        sourceFolderLibrary.setDebugParser( debugParser );
        sourceFolderLibrary.addSourceFolder( sourceFolder );
        return this;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#addSource(java.io.InputStream)
     */
    public ClassLibraryBuilder appendSource( InputStream stream )
    {
        if ( !( classLibrary instanceof SourceLibrary ) )
        {
            classLibrary = new SourceLibrary( modelBuilderFactory, classLibrary );
        }
        SourceLibrary sourceLibrary = (SourceLibrary) classLibrary;
        sourceLibrary.setDebugLexer( debugLexer );
        sourceLibrary.setDebugParser( debugParser );
        sourceLibrary.addSource( stream );
        return this;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#addSource(java.io.Reader)
     */
    public ClassLibraryBuilder appendSource( Reader reader )
    {
        if ( !( classLibrary instanceof SourceLibrary ) )
        {
            classLibrary = new SourceLibrary( modelBuilderFactory, classLibrary );
        }
        SourceLibrary sourceLibrary = (SourceLibrary) classLibrary;
        sourceLibrary.setDebugLexer( debugLexer );
        sourceLibrary.setDebugParser( debugParser );
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

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#getClassLibrary()
     */
    public ClassLibrary getClassLibrary()
    {
        return classLibrary;
    }

    public ClassLibraryBuilder appendSource( URL url, String encoding ) throws IOException
    {
        return appendSource(new InputStreamReader(url.openStream(), encoding));
    }

    public JavaClass addSource( InputStream stream )
    {
        if ( !( classLibrary instanceof SourceLibrary ) )
        {
            classLibrary = new SourceLibrary( modelBuilderFactory, classLibrary );
        }
        SourceLibrary sourceLibrary = (SourceLibrary) classLibrary;
        sourceLibrary.setDebugLexer( debugLexer );
        sourceLibrary.setDebugParser( debugParser );
        return sourceLibrary.addSource( stream );
    }

    public JavaClass addSource( Reader reader )
    {
        if ( !( classLibrary instanceof SourceLibrary ) )
        {
            classLibrary = new SourceLibrary( modelBuilderFactory, classLibrary );
        }
        SourceLibrary sourceLibrary = (SourceLibrary) classLibrary;
        sourceLibrary.setDebugLexer( debugLexer );
        sourceLibrary.setDebugParser( debugParser );
        return sourceLibrary.addSource( reader );
    }

    public JavaClass addSource( URL url, String encoding ) throws IOException
    {
        return addSource(new InputStreamReader(url.openStream(), encoding));
    }

}
