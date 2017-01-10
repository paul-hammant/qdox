package com.thoughtworks.qdox.library;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import com.thoughtworks.qdox.builder.ModelBuilderFactory;
import com.thoughtworks.qdox.model.JavaModule;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.writer.ModelWriterFactory;

/**
 * This library resolves JavaClasses in the order in which class sources are added.
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public class OrderedClassLibraryBuilder implements ClassLibraryBuilder
{

    private AbstractClassLibrary classLibrary;
    
    private boolean debugLexer;

    private boolean debugParser;
    
    private String encoding;
    
    private ErrorHandler errorHandler;
    
    private ModelBuilderFactory modelBuilderFactory;
    
    private ModelWriterFactory modelWriterFactory;
    
    /**
     * Default constructor which sets the root classLibrary to ClassNameLibrary.
     * This way every class will be resolved, even if it's not on the classpath.
     * 
     */
    public OrderedClassLibraryBuilder()
    {
        this.classLibrary = new ClassNameLibrary();
    }

    /**
     * Constructor for which you can set the root ClassLibrary
     * If you set this to null, all classes should be available on the classpath.
     * 
     * @param rootClassLibrary the parent Classlibrary
     */
    public OrderedClassLibraryBuilder( AbstractClassLibrary rootClassLibrary )
    {
        this.classLibrary = rootClassLibrary;
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#appendClassLoader(java.lang.ClassLoader)
     */
    public ClassLibraryBuilder appendClassLoader( ClassLoader classLoader )
    {
        if ( !( classLibrary instanceof ClassLoaderLibrary ) )
        {
            classLibrary = newClassLoaderLibrary( classLibrary );
        }
        ClassLoaderLibrary classLoaderLibrary = (ClassLoaderLibrary) classLibrary;
        classLoaderLibrary.addClassLoader( classLoader );
        classLoaderLibrary.setModelBuilderFactory( modelBuilderFactory );
        classLoaderLibrary.setModelWriterFactory( modelWriterFactory );
        classLoaderLibrary.setDebugLexer( debugLexer );
        classLoaderLibrary.setDebugParser( debugParser );
        classLoaderLibrary.setErrorHandler( errorHandler );
        return this;
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#appendDefaultClassLoaders()
     */
    public ClassLibraryBuilder appendDefaultClassLoaders()
    {
        if ( !( classLibrary instanceof ClassLoaderLibrary ) )
        {
            classLibrary = newClassLoaderLibrary( classLibrary );
        }
        ClassLoaderLibrary classLoaderLibrary = (ClassLoaderLibrary) classLibrary;
        classLoaderLibrary.addDefaultLoader();
        classLoaderLibrary.setModelBuilderFactory( modelBuilderFactory );
        classLoaderLibrary.setModelWriterFactory( modelWriterFactory );
        return this;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#addSourceFolder(java.io.File)
     */
    public ClassLibraryBuilder appendSourceFolder( File sourceFolder )
    {
        if ( !( classLibrary instanceof SourceFolderLibrary ) )
        {
            classLibrary = newSourceFolderLibrary( classLibrary );
        }
        SourceFolderLibrary sourceFolderLibrary = (SourceFolderLibrary) classLibrary;
        prepareSourceLibrary( sourceFolderLibrary );
        sourceFolderLibrary.addSourceFolder( sourceFolder );
        return this;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#addSource(java.io.InputStream)
     */
    public ClassLibraryBuilder appendSource( InputStream stream ) throws IOException
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
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#setEncoding(java.lang.String)
     */
    public ClassLibraryBuilder setEncoding( String encoding )
    {
        this.encoding = encoding;
        return this;
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#setErrorHander(com.thoughtworks.qdox.library.ErrorHandler)
     */
    public ClassLibraryBuilder setErrorHander( ErrorHandler errorHandler )
    {
        this.errorHandler = errorHandler;
        return this;
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#setModelBuilderFactory(com.thoughtworks.qdox.builder.ModelBuilderFactory)
     */
    public ClassLibraryBuilder setModelBuilderFactory( ModelBuilderFactory modelBuilderFactory )
    {
        this.modelBuilderFactory = modelBuilderFactory;
        return this;
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#setModelWriterFactory(com.thoughtworks.qdox.writer.ModelWriterFactory)
     */
    public ClassLibraryBuilder setModelWriterFactory( ModelWriterFactory modelWriterFactory )
    {
        this.modelWriterFactory = modelWriterFactory;
        return this;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#getClassLibrary()
     */
    public ClassLibrary getClassLibrary()
    {
        return classLibrary;
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#appendSource(java.net.URL)
     */
    public ClassLibraryBuilder appendSource( URL url ) throws IOException
    {
        SourceLibrary sourceLibrary = getSourceLibrary();
        sourceLibrary.addSource( url );
        return this;
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#appendSource(java.io.File)
     */
    public ClassLibraryBuilder appendSource( File file )
        throws IOException
    {
        SourceLibrary sourceLibrary = getSourceLibrary();
        sourceLibrary.addSource( file );
        return this;
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#addSource(java.io.InputStream)
     */
    public JavaSource addSource( InputStream stream ) throws IOException
    {
        SourceLibrary sourceLibrary = getSourceLibrary();
        return sourceLibrary.addSource( stream );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#addSource(java.io.Reader)
     */
    public JavaSource addSource( Reader reader )
    {
        SourceLibrary sourceLibrary = getSourceLibrary();
        return sourceLibrary.addSource( reader );
    }

    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#addSource(java.net.URL)
     */
    public JavaSource addSource( URL url ) throws IOException
    {
        SourceLibrary sourceLibrary = getSourceLibrary();
        return sourceLibrary.addSource( url );
    }
    
    /*
     * (non-Javadoc)
     * @see com.thoughtworks.qdox.library.ClassLibraryBuilder#addSource(java.io.File)
     */
    public JavaSource addSource( File file )
        throws IOException
    {
        SourceLibrary sourceLibrary = getSourceLibrary();
        return sourceLibrary.addSource( file );
    }
    
    public JavaModule addSourceFolder( File sourceFolder )
    {
        SourceFolderLibrary sourceFolderLibrary = getSourceFolderLibrary();
        return sourceFolderLibrary.addSourceFolder( sourceFolder );
    }

    private void prepareSourceLibrary( SourceLibrary sourceLibrary ) {
        sourceLibrary.setModelBuilderFactory( modelBuilderFactory );
        sourceLibrary.setModelWriterFactory( modelWriterFactory );
        sourceLibrary.setDebugLexer( debugLexer );
        sourceLibrary.setDebugParser( debugParser );
        sourceLibrary.setEncoding( encoding );
        sourceLibrary.setErrorHandler( errorHandler );
    }
    
    protected final SourceLibrary getSourceLibrary() {
        if ( !( classLibrary instanceof SourceLibrary ) )
        {
            classLibrary = newSourceLibrary( classLibrary );
        }
        SourceLibrary sourceLibrary = (SourceLibrary) classLibrary;
        prepareSourceLibrary( sourceLibrary );
        return sourceLibrary;
    }
    
    private SourceFolderLibrary getSourceFolderLibrary()
    {
        if ( !( classLibrary instanceof SourceFolderLibrary ) )
        {
            classLibrary = newSourceFolderLibrary( classLibrary );
        }
        SourceFolderLibrary library = (SourceFolderLibrary) classLibrary;
        prepareSourceLibrary( library );
        return library;
    }
    
    /**
     * Ability to override the implementation of ClassLoaderLibrary
     * 
     * @param parentLibrary the parent library
     * @return a new ClassLoaderLibrary instance
     * @since 2.0
     */
    protected ClassLoaderLibrary newClassLoaderLibrary( AbstractClassLibrary parentLibrary )
    {
        return new ClassLoaderLibrary( parentLibrary );
    }

    /**
     * Ability to override the implementation of SourceLibrary
     * 
     * @param parentLibrary the parent library
     * @return a new SourceLibrary instance
     * @since 2.0
     */
    protected SourceLibrary newSourceLibrary( AbstractClassLibrary parentLibrary )
    {
        return new SourceLibrary( parentLibrary );
    }
    
    /**
     * Ability to override the implementation of SourceFolderLibrary
     * 
     * @param parentLibrary the parent library
     * @return a new SourceFolderLibrary instance
     * @since 2.0
     */
    protected SourceFolderLibrary newSourceFolderLibrary( AbstractClassLibrary parentLibrary )
    {
        return new SourceFolderLibrary( parentLibrary );
    }

    
}
