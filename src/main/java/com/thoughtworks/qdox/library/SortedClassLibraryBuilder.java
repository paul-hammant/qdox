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
 * @author Robert Scholte
 * @since 2.0
 */
public class SortedClassLibraryBuilder
    implements ClassLibraryBuilder
{
    private final ClassNameLibrary classNameLibrary;

    private final ClassLoaderLibrary classLoaderLibrary;

    private final SourceFolderLibrary sourceFolderLibrary;

    private final SourceLibrary sourceLibrary;
    
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
    
    public ClassLibraryBuilder appendDefaultClassLoaders()
    {
        classLoaderLibrary.addDefaultLoader();
        return this;
    }

    public ClassLibraryBuilder appendSourceFolder( File sourceFolder )
    {
        sourceFolderLibrary.addSourceFolder( sourceFolder );
        return this;
    }

    public ClassLibraryBuilder appendSource( InputStream stream ) throws IOException
    {
        sourceLibrary.addSource( stream );
        return this;
    }

    public ClassLibraryBuilder appendSource( Reader reader )
    {
        sourceLibrary.addSource( reader );
        return this;
    }
    
    public ClassLibraryBuilder appendSource( URL url )
    throws IOException
    {
        sourceLibrary.addSource( url );
        return this;
    }
    
    public ClassLibraryBuilder appendSource( File file )
        throws IOException
    {
        sourceLibrary.addSource( file );
        return this;
    }

    public ClassLibraryBuilder setDebugLexer( boolean debugLexer )
    {
        classLoaderLibrary.setDebugLexer( debugLexer );
        sourceFolderLibrary.setDebugLexer( debugLexer );
        sourceLibrary.setDebugLexer( debugLexer );
        return this;
    }

    public ClassLibraryBuilder setDebugParser( boolean debugParser )
    {
        classLoaderLibrary.setDebugParser( debugParser );
        sourceFolderLibrary.setDebugParser( debugParser );
        sourceLibrary.setDebugParser( debugParser );
        return this;
    }
    
    public ClassLibraryBuilder setEncoding( String encoding )
    {
        sourceFolderLibrary.setEncoding( encoding );
        sourceLibrary.setEncoding( encoding );
        return this;
    }
    
    public ClassLibraryBuilder setErrorHander( ErrorHandler errorHandler )
    {
        classLoaderLibrary.setErrorHandler( errorHandler );
        sourceFolderLibrary.setErrorHandler( errorHandler );
        sourceLibrary.setErrorHandler( errorHandler );
        return this;
    }
    
    public ClassLibraryBuilder setModelBuilderFactory( ModelBuilderFactory factory )
    {
        classNameLibrary.setModelBuilderFactory( factory );
        classLoaderLibrary.setModelBuilderFactory( factory );
        sourceFolderLibrary.setModelBuilderFactory( factory );
        sourceLibrary.setModelBuilderFactory( factory );
        return this;
    }
    
    
    public ClassLibraryBuilder setModelWriterFactory( ModelWriterFactory modelWriterFactory )
    {
        classNameLibrary.setModelWriterFactory( modelWriterFactory );
        classLoaderLibrary.setModelWriterFactory( modelWriterFactory );
        sourceFolderLibrary.setModelWriterFactory( modelWriterFactory );
        sourceLibrary.setModelWriterFactory( modelWriterFactory );
        return this;
    }

    public ClassLibrary getClassLibrary()
    {
        return sourceLibrary;
    }

    public JavaSource addSource( InputStream stream ) throws IOException
    {
        return sourceLibrary.addSource( stream );
    }

    public JavaSource addSource( Reader reader )
    {
        return sourceLibrary.addSource( reader );
    }

    public JavaSource addSource( URL url )
        throws IOException
    {
        return sourceLibrary.addSource( url );
    }
    
    public JavaSource addSource( File file )
        throws IOException
    {
        return sourceLibrary.addSource( file );
    }

    public JavaModule addSourceFolder( File sourceFolder )
    {
        return sourceFolderLibrary.addSourceFolder( sourceFolder );
    }
}
