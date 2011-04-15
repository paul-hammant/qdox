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
import java.io.Serializable;
import java.net.URL;

import com.thoughtworks.qdox.builder.ModelBuilderFactory;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * This builder helps to construct a library. 
 * All kinds of sourcetypes are supported and it's up to the implementation how to bind these types.
 * For instance: The {@link SortedClassLibraryBuilder} bundles all classloaders, all sourcefolders and all sources.
 * The {@link OrderedClassLibraryBuilder} on the other hand keeps track of the order in which sourcetypes are added.  
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public interface ClassLibraryBuilder extends Serializable
{
    /**
     * Append a classloader and return itself
     * 
     * @param classLoader the classloader to add
     * @return this ClassLibraryBuilder instance
     */
    public ClassLibraryBuilder appendClassLoader( ClassLoader classLoader );

    /**
     * Add the defaultClassLoaders and return itse
     * 
     * @return this ClassLibraryBuilder instance
     */
    public ClassLibraryBuilder appendDefaultClassLoaders();

    /**
     * 
     * @param sourceFolder
     * @return this ClassLibraryBuilder instance
     */
    public ClassLibraryBuilder appendSourceFolder( File sourceFolder );

    /**
     * 
     * @param stream
     * @return this ClassLibraryBuilder instance
     */
    public ClassLibraryBuilder appendSource( InputStream stream );

    /**
     * 
     * @param reader
     * @return this ClassLibraryBuilder instance
     */
    public ClassLibraryBuilder appendSource( Reader reader );

    /**
     * 
     * @param url
     * @return this ClassLibraryBuilder instance
     * @throws IOException
     */
    public ClassLibraryBuilder appendSource( URL url ) throws IOException;

    /**
     * 
     * @param file
     * @return this ClassLibraryBuilder instance
     * @throws IOException
     */
    public ClassLibraryBuilder appendSource( File file ) throws IOException;

    /**
     * 
     * @param stream
     * @return the created JavaSource
     */
    public JavaSource addSource( InputStream stream );

    /**
     * Add the source content of the reader to the ClassLibrary and return the generated JavaSource
     * 
     * @param reader
     * @return the created JavaSource
     */
    public JavaSource addSource( Reader reader );
    
    /**
     * 
     * @param url
     * @return the created JavaSource
     * @throws IOException
     */
    public JavaSource addSource( URL url ) throws IOException;
    
    /**
     * 
     * @param file
     * @return the created JavaSource
     * @throws IOException
     */
    public JavaSource addSource( File file ) throws IOException;
    
    /**
     * 
     * @param debugLexer
     * @return this ClassLibraryBuilder instance
     */
    public ClassLibraryBuilder setDebugLexer( boolean debugLexer );

    /**
     * 
     * @param debugParser
     * @return this ClassLibraryBuilder instance
     */
    public ClassLibraryBuilder setDebugParser( boolean debugParser );
    
    /**
     * 
     * @param encoding
     * @return this ClassLibraryBuilder instance
     */
    public ClassLibraryBuilder setEncoding( String encoding );
    
    /**
     * Define the {@link ModelBuilderFactory} which the parsers should use to construct the JavaModel Objects
     * 
     * @param factory
     * @return this ClassLibraryBuilder instance
     */
    public ClassLibraryBuilder setModelBuilderFactory( ModelBuilderFactory factory );

    /**
     * Get the library based on the strategy of the implementation
     * 
     * @return the constructed ClassLibrary
     */
    public ClassLibrary getClassLibrary();

    /**
     * Can handle ParseExceptions instead of crashing.
     * Has only effect on the appendSource() methods
     * 
     * @param errorHandler
     */
    public ClassLibraryBuilder setErrorHander( ErrorHandler errorHandler );

}