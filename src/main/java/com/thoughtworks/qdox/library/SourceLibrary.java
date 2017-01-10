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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.builder.Builder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.impl.DefaultJavaPackage;
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
    
    private static final String DEFAULT_ENCODING = System.getProperty("file.encoding"); 
    
    private String encoding;
    
    private ErrorHandler errorHandler;
    
    /**
     * Create a new instance of SourceLibrary and chain it to the parent 
     * 
     * @param parent the parent classLibrary
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
        Builder builder = parse( reader, url );
        JavaSource source = null;
        if( builder != null )
        {
            source = builder.getSource();
            registerJavaSource(source);
        }
        return source;
    }

    /**
     * Add an {@link InputStream} containing java code to this library
     * 
     * @param stream an {@link InputStream} which should contain java code
     * @return The constructed {@link JavaSource} object of this stream
     * @throws ParseException if this content couldn't be parsed to a JavaModel
     * @throws IOException  if an IOException occurs
     */
    public JavaSource addSource( InputStream stream )
        throws ParseException, IOException
    {
        Builder builder = parse( stream, null );
        JavaSource source = null;
        if( builder != null)
        {
            source=  builder.getSource();
            registerJavaSource(source);
        }
        return source;
    }
    
    /**
     * Add a {@link URL} containing java code to this library
     * 
     * @param url a {@link URL} which should contain java code
     * @return The constructed {@link JavaSource} object of this url
     * @throws ParseException if this content couldn't be parsed to a JavaModel
     * @throws IOException  if an IOException occurs
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
     * @throws ParseException if this content couldn't be parsed to a JavaModel
     * @throws IOException  if an IOException occurs
     */
    public JavaSource addSource( File file )
        throws ParseException, IOException
    {
        JavaSource result = null;
        if ( !"package-info.java".equals( file.getName() ) ) 
        {
            if ( "module-info.java".equals( file.getName() ) )
            {
                // No parse specifications yet
                return result;
            }

            Builder builder = parse( new FileInputStream( file ), file.toURI().toURL() );
            
            if ( builder != null )
            {
                result = builder.getSource();
            }
            
            // if an error is handled by the errorHandler the result will be null
            if( result != null )
            {
                if( getJavaPackage( result.getPackageName() ) == null )
                {
                    File packageInfo = new File(file.getParentFile(), "package-info.java");
                    if( packageInfo.exists() )
                    {
                        JavaPackage pckg = parse( new FileInputStream( packageInfo ),
                                                  packageInfo.toURI().toURL() ).getSource().getPackage();
                        context.add( pckg );
                    }
                }
                registerJavaSource(result);
            }
        }
    	return result;
    }

    Builder parse( Reader reader, URL url )
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

    Builder parse( InputStream stream, URL url )
        throws ParseException, UnsupportedEncodingException
    {
        try 
        {
            return parse( new JFlexLexer( new InputStreamReader( stream, getEncoding() ) ), url );
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

    private Builder parse( JavaLexer lexer, URL url )
        throws ParseException
    {
        Builder builder = getModelBuilder();
        builder.setUrl( url );
        Parser parser = new Parser( lexer, builder );
        parser.setDebugLexer( debugLexer );
        parser.setDebugParser( debugParser );
        try {
            if ( parser.parse() )
            {
                return builder;
            }
        }
        catch( ParseException pe )
        {
            if ( url != null )
            {
                pe.setSourceInfo( url.toExternalForm() );
            }
            if( errorHandler != null )
            {
                errorHandler.handle( pe );
            }
            else
            {
                throw pe;
            }
        }
        return null;
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
    protected final void registerJavaSource( JavaSource source )
    {
        if ( source != null )
        {
            context.add( source );
            registerJavaPackage( source.getPackage() );
            for ( JavaClass cls : source.getClasses() )
            {
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
     * @param debugLexer the debug logging flag
     */
    public final void setDebugLexer( boolean debugLexer )
    {
        this.debugLexer = debugLexer;
    }
    
    public final boolean isDebugLexer()
    {
        return debugLexer;
    }
    
    /**
     * Use the Parser in debug mode
     * 
     * @param debugParser  the debug logging flag
     */
    public final void setDebugParser( boolean debugParser )
    {
        this.debugParser = debugParser;
    }
    
    public final boolean isDebugParser()
    {
        return debugParser;
    }
    
    /**
     * Sets the encoding to use when parsing a URL or InputStreamReader
     * 
     * @param encoding the source encoding
     */
    public final void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }
    
    public final String getEncoding()
    {
        return encoding == null ? DEFAULT_ENCODING : encoding;
    }
    
    public final void setErrorHandler( ErrorHandler errorHandler )
    {
        this.errorHandler = errorHandler;
    }
    
    public final ErrorHandler getErrorHandler()
    {
        return errorHandler;
    }
    
    /**
     * Get all classes, including those from parent SourceLibraries
     */
    @Override
    public Collection<JavaClass> getJavaClasses()
    {
        List<JavaClass> result = new LinkedList<JavaClass>();
        List<JavaClass> unusedClasses = context.getClasses();
        Collection<JavaClass> usedClasses = getJavaClasses( new ClassLibraryFilter()
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
    public Collection<JavaPackage> getJavaPackages()
    {
        List<JavaPackage> result = new LinkedList<JavaPackage>();
        List<JavaPackage> unusedPackages = context.getPackages();
        Collection<JavaPackage> usedPackages = getJavaPackages( new ClassLibraryFilter()
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
    public Collection<JavaSource> getJavaSources()
    {
        List<JavaSource> result = new LinkedList<JavaSource>();
        List<JavaSource> unusedSources = context.getSources();
        Collection<JavaSource> usedSources = getJavaSources( new ClassLibraryFilter()
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
