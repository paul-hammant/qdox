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
import com.thoughtworks.qdox.model.JavaModule;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.writer.ModelWriterFactory;

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
    ClassLibraryBuilder appendClassLoader( ClassLoader classLoader );

    /**
     * Add the defaultClassLoaders and return itse
     * 
     * @return this ClassLibraryBuilder instance
     */
    ClassLibraryBuilder appendDefaultClassLoaders();

    /**
     * 
     * @param sourceFolder the source folder
     * @return this ClassLibraryBuilder instance
     */
    ClassLibraryBuilder appendSourceFolder( File sourceFolder );

    /**
     * 
     * @param stream the Java source as stream
     * @return this ClassLibraryBuilder instance
     * @throws IOException if an IOException is thrown, e.g. unsupported encoding
     */
    ClassLibraryBuilder appendSource( InputStream stream ) throws IOException;

    /**
     * 
     * @param reader the Java source as reader
     * @return this ClassLibraryBuilder instance
     */
    ClassLibraryBuilder appendSource( Reader reader );

    /**
     * 
     * @param url the Java source as URL
     * @return this ClassLibraryBuilder instance
     * @throws IOException if an IOException occurs
     */
    ClassLibraryBuilder appendSource( URL url ) throws IOException;

    /**
     * 
     * @param file the Java source as file
     * @return this ClassLibraryBuilder instance
     * @throws IOException if an IOException occurs
     */
    ClassLibraryBuilder appendSource( File file ) throws IOException;

    /**
     * 
     * @param stream the Java source as stream
     * @return the created JavaSource
     * @throws IOException if an IOException is thrown, e.g. unsupported encoding
     */
    JavaSource addSource( InputStream stream ) throws IOException;

    /**
     * Add the source content of the reader to the ClassLibrary and return the generated JavaSource
     * 
     * @param reader the Java source as reader
     * @return the created JavaSource
     */
    JavaSource addSource( Reader reader );
    
    /**
     * 
     * @param url the Java source as URL
     * @return the created JavaSource
     * @throws IOException if an IOException occurs
     */
    JavaSource addSource( URL url ) throws IOException;
    
    /**
     * 
     * @param file the Java source as file
     * @return the created JavaSource
     * @throws IOException if an IOException occurs
     */
    JavaSource addSource( File file ) throws IOException;
    
    /**
     * Set to {@code true} to enable debug logging for the lexer
     * 
     * @param debugLexer the debug logging flag
     * @return this ClassLibraryBuilder instance
     */
    ClassLibraryBuilder setDebugLexer( boolean debugLexer );

    /**
     * Set to {@code true} to enable debug logging for the parser
     * 
     * @param debugParser the debug logging flag
     * @return this ClassLibraryBuilder instance
     */
    ClassLibraryBuilder setDebugParser( boolean debugParser );
    
    /**
     * 
     * @param encoding set the encoding
     * @return this ClassLibraryBuilder instance
     */
    ClassLibraryBuilder setEncoding( String encoding );
    
    /**
     * Define the {@link ModelBuilderFactory} which the parsers should use to construct the JavaModel Objects
     * 
     * @param factory the modelBuilderFactory
     * @return this ClassLibraryBuilder instance
     */
    ClassLibraryBuilder setModelBuilderFactory( ModelBuilderFactory factory );

    /**
     * Define the {@link ModelWriterFactory} which is used by the classes when calling for the codeBlock.
     * 
     * @param factory the modelWriterFactory
     * @return this ClassLibraryBuilder instance
     */
    ClassLibraryBuilder setModelWriterFactory( ModelWriterFactory factory );
    
    /**
     * Get the library based on the strategy of the implementation
     * 
     * @return the constructed ClassLibrary
     */
    ClassLibrary getClassLibrary();

    /**
     * Can handle ParseExceptions instead of crashing.
     * Has only effect on the appendSource() methods
     * 
     * @param errorHandler the errorHandler
     * @return this ClassLibraryBuilder instance
     */
    ClassLibraryBuilder setErrorHander( ErrorHandler errorHandler );

    /**
     * 
     * @param sourceFolder the sourcefolder
     * @return the module info if the sourcefolder has a module-info.java, otherwise {@code null} 
     * @since 2.0
     */
    JavaModule addSourceFolder( File sourceFolder );

}