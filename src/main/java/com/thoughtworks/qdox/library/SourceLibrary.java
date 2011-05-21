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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.builder.ModelBuilder;
import com.thoughtworks.qdox.model.DefaultJavaPackage;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.parser.JavaLexer;
import com.thoughtworks.qdox.parser.ParseException;
import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;

/**
 * This Library will immediately parse the source and keeps its reference to a private context.
 * Once the superclass explicitly asks for an instance if will be moved to the context f the supoerclass.
 * If there's a request to get a certain JavaModel Object from a SourceLibrary, it will check all ancestor SourceLibraries as well.
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public class SourceLibrary
    extends AbstractClassLibrary
{
    // parser and unused JavaSources, JavaClasses and JavaPackages
    private JavaClassContext context = new JavaClassContext();
    
    private boolean debugLexer;

    private boolean debugParser;
    
    private String encoding = System.getProperty("file.encoding");
    
    private ErrorHandler errorHandler;
    
    /**
     * Create a new instance of SourceLibrary and chain it to the parent 
     * 
     * @param parent
     */
    public SourceLibrary( AbstractClassLibrary parent )
    {
        super( parent );
    }
    
    /**
     * Add a {@link Reader} containing java code to this library
     * 
     * @param reader a {@link Reader} which should contain java code
     * @return The constructed {@link JavaSource} object of this reader
     * @throws ParseException if this content couldn't be parsed to a JavaModel
     */
    public JavaSource addSource( Reader reader )
        throws ParseException
    {
        return addSource( reader, null );
    }
    
    private JavaSource addSource( Reader reader, URL url )
    {
        JavaSource source = parse( reader, url );
        registerJavaSource(source);
        return source;
    }

    /**
     * Add an {@link InputStream} containing java code to this library
     * 
     * @param stream an {@link InputStream} which should contain java code
     * @return The constructed {@link JavaSource} object of this stream
     * @throws ParseException if this content couldn't be parsed to a JavaModel
     */
    public JavaSource addSource( InputStream stream )
        throws ParseException
    {
        JavaSource source = parse( stream, null );
        registerJavaSource(source);
        return source;
    }
    
    /**
     * Add a {@link URL} containing java code to this library
     * 
     * @param url a {@link URL} which should contain java code
     * @return The constructed {@link JavaSource} object of this url
     * @throws ParseException if this content couldn't be parsed to a JavaModel
     */
    public JavaSource addSource( URL url )
        throws ParseException, IOException
    {
        return addSource( new InputStreamReader( url.openStream(), encoding), url );
    }

    /**
     * Add a {@link File} containing java code to this library
     * 
     * @param file a {@link File} which should contain java code
     * @return The constructed {@link JavaSource} object of this file
     * @throws ParseException
     * @throws IOException
     */
    public JavaSource addSource( File file )
        throws ParseException, IOException
    {
        JavaSource result = null;
        if ( !"package-info.java".equals( file.getName() ) ) 
        {
            result = parse( new FileInputStream( file ), file.toURI().toURL() );
            // if an error is handled by the errorHandler the result will be null
            if( result != null )
            {
                if( getJavaPackage( result.getPackageName() ) == null )
                {
                    File packageInfo = new File(file.getParentFile(), "package-info.java");
                    if( packageInfo.exists() )
                    {
                        JavaPackage pckg = parse( new FileInputStream( packageInfo ), packageInfo.toURI().toURL() ).getPackage();
                        context.add( pckg );
                    }
                }
                registerJavaSource(result);
            }
        }
    	return result;
    }
    
    protected JavaSource parse( Reader reader, URL url )
        throws ParseException
    {
        try 
        {
            return parse( new JFlexLexer( reader ), url );
        }
        finally 
        {
            try
            {
                reader.close();
            }
            catch ( IOException e ) 
            {
            }
        }
    }

    protected JavaSource parse( InputStream stream, URL url )
        throws ParseException
    {
        try 
        {
            return parse( new JFlexLexer( stream ), url );
        }
        finally 
        {
            try
            {
                stream.close();
            }
            catch ( IOException e ) 
            {
            }
        }
    }

    private JavaSource parse( JavaLexer lexer, URL url )
        throws ParseException
    {
        JavaSource result = null;
        ModelBuilder builder = getModelBuilder();
        builder.setUrl( url );
        Parser parser = new Parser( lexer, builder );
        parser.setDebugLexer( debugLexer );
        parser.setDebugParser( debugParser );
        try {
            if ( parser.parse() )
            {
                result = builder.getSource();
            }
        }
        catch( ParseException pe )
        {
            pe.setSourceInfo( url.toExternalForm() );
            if( errorHandler != null )
            {
                errorHandler.handle( pe );
            }
            else
            {
                throw pe;
            }
        }
        return result;
    }

    @Override
    protected JavaClass resolveJavaClass( String name )
    {
        // abstractLibrary only calls this when it can't find the source itself.
        // it will take over the reference
        return context.removeClassByName( name );
    }
    
    @Override
    protected JavaPackage resolveJavaPackage(String name) {
    	return context.removePackageByName( name );
    }

    /**
     * 
     * @param source the source, might be <code>null</code>
     */
    private void registerJavaSource(JavaSource source) {
        if ( source != null )
        {
            context.add( source );
            registerJavaPackage( source.getPackage() );
            for( JavaClass cls : source.getClasses()) {
                registerJavaClass( cls );
            }
        }
    }
    
    private void registerJavaPackage( JavaPackage pckg )
    {
        String pckgName = ( pckg == null || pckg.getName() == null ? "" : pckg.getName() );
        if( getJavaPackage( pckgName ) == null )
        {
            DefaultJavaPackage packageInfo = new DefaultJavaPackage( pckgName );
            packageInfo.setClassLibrary( this );
            context.add( packageInfo );
        }
    }
    
    private void registerJavaClass(JavaClass cls) {
        if (cls != null) {
            context.add( cls );
            getJavaPackage( cls.getPackageName() ).getClasses().add( cls );
        }
        for( JavaClass innerCls : cls.getNestedClasses()) {
            registerJavaClass( innerCls );
        }
    }

    /**
     * Use the Lexer in debug mode
     * 
     * @param debugLexer 
     */
    public void setDebugLexer( boolean debugLexer )
    {
        this.debugLexer = debugLexer;
    }
    
    /**
     * Use the Parser in debug mode
     * 
     * @param debugParser
     */
    public void setDebugParser( boolean debugParser )
    {
        this.debugParser = debugParser;
    }
    
    /**
     * Sets the encoding to use when parsing a URL or InputStreamReader
     * 
     * @param encoding
     */
    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }
    
    public void setErrorHandler( ErrorHandler errorHandler )
    {
        this.errorHandler = errorHandler;
    }
    
    /**
     * Get all classes, including those from parent SourceLibraries
     */
    @Override
    public List<JavaClass> getJavaClasses()
    {
        List<JavaClass> result = new LinkedList<JavaClass>();
        List<JavaClass> unusedClasses = context.getClasses();
        List<JavaClass> usedClasses = getJavaClasses( new ClassLibraryFilter()
        {
            public boolean accept( AbstractClassLibrary classLibrary )
            {
                return (classLibrary instanceof SourceLibrary);
            }
        });
        result.addAll( usedClasses );
        result.addAll( unusedClasses );
        return Collections.unmodifiableList( result );
    }

    /**
     * Get all packages, including those from parent SourceLibraries
     */
    @Override
    public List<JavaPackage> getJavaPackages()
    {
        List<JavaPackage> result = new LinkedList<JavaPackage>();
        List<JavaPackage> unusedPackages = context.getPackages();
        List<JavaPackage> usedPackages = getJavaPackages( new ClassLibraryFilter()
        {
            public boolean accept( AbstractClassLibrary classLibrary )
            {
                return (classLibrary instanceof SourceLibrary);
            }
        });
        result.addAll( usedPackages );
        result.addAll( unusedPackages );
        return Collections.unmodifiableList( result );
    }
    
    /**
     * Get all sources, including those from parent SourceLibraries
     */
    @Override
    public List<JavaSource> getJavaSources()
    {
        List<JavaSource> result = new LinkedList<JavaSource>();
        List<JavaSource> unusedSources = context.getSources();
        List<JavaSource> usedSources = getJavaSources( new ClassLibraryFilter()
        {
            public boolean accept( AbstractClassLibrary classLibrary )
            {
                return (classLibrary instanceof SourceLibrary);
            }
        });
        result.addAll( usedSources );
        result.addAll( unusedSources );
        return Collections.unmodifiableList( result );
    }

    @Override
    protected boolean containsClassReference( String name )
    {
        return context.getClassByName( name ) != null;
    }
}
