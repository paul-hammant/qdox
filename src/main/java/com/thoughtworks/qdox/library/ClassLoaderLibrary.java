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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.builder.Builder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.impl.DefaultJavaPackage;
import com.thoughtworks.qdox.parser.JavaLexer;
import com.thoughtworks.qdox.parser.ParseException;
import com.thoughtworks.qdox.parser.impl.BinaryClassParser;
import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;

/**
 * <strong>Important!! Be sure to add a classloader with the bootstrap classes.</strong>
 * <p>
 * Normally you can generate your classLibrary like this:<br>
 * <code>
 *  ClassLibrary classLibrary = new ClassLibrary();
 *  classLibrary.addDefaultLoader();
 * </code>
 * </p>
 * <p>
 * If you want full control over the classLoaders you might want to create your library like:<br>
 * <code>
 * ClassLibrary classLibrary = new ClassLibrary( ClassLoader.getSystemClassLoader() )
 * </code>
 * </p>
 * 
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 * @author Robert Scholte
 * @since 2.0
 */
public class ClassLoaderLibrary
    extends AbstractClassLibrary
{
    private transient List<ClassLoader> classLoaders = new LinkedList<ClassLoader>();

    private boolean defaultClassLoadersAdded = false;

    private boolean debugLexer;

    private boolean debugParser;
    
    private ErrorHandler errorHandler;
    
    public ClassLoaderLibrary( AbstractClassLibrary parent )
    {
        super( parent );
    }

    public ClassLoaderLibrary( AbstractClassLibrary parent, ClassLoader classLoader )
    {
        super( parent );
        this.classLoaders.add( classLoader );
    }

    public void addClassLoader( ClassLoader classLoader )
    {
        classLoaders.add( classLoader );
    }

    public void addDefaultLoader()
    {
        if ( !defaultClassLoadersAdded )
        {
            classLoaders.add( getClass().getClassLoader() );
            classLoaders.add( Thread.currentThread().getContextClassLoader() );
        }
        defaultClassLoadersAdded = true;
    }

    @Override
    protected JavaClass resolveJavaClass( final String name )
    {
        JavaClass result = null;
        for ( ClassLoader classLoader : classLoaders )
        {
            String resource = name;
            if ( resource.indexOf( '$' ) > 0 )
            {
                resource = resource.split( "$" )[0];
            }
            resource = resource.replace( '.', '/' ) + ".java";
            InputStream sourceStream = classLoader.getResourceAsStream( resource );
            if ( sourceStream != null )
            {
                Builder builder = getModelBuilder();
                JavaLexer lexer = new JFlexLexer( sourceStream );
                Parser parser = new Parser( lexer, builder );
                parser.setDebugLexer( debugLexer );
                parser.setDebugParser( debugParser );
                try
                {
                    if ( parser.parse() )
                    {
                        result = builder.getSource().getClassByName( name );
                        break;
                    }
                }
                catch ( ParseException pe )
                {
                    pe.setSourceInfo( resource );
                    if ( errorHandler != null )
                    {
                        errorHandler.handle( pe );
                    }
                    else
                    {
                        throw pe;
                    }
                }
            }
            if ( result == null )
            {
                try
                {
                    Class<?> clazz = classLoader.loadClass( name );
                    if ( clazz.getDeclaringClass() != null )
                    {
                        clazz = clazz.getDeclaringClass();
                    }
                    Builder builder = getModelBuilder();
                    BinaryClassParser parser = new BinaryClassParser( clazz, builder );
                    if ( parser.parse() )
                    {
                        result = builder.getSource().getClassByName( name );
                        break;
                    }
                }
                catch ( ClassNotFoundException e )
                {
                }
            }
        }
        return result;
    }
    
    @Override
    protected JavaPackage resolveJavaPackage(String name) {
        DefaultJavaPackage result = null;
    	Package pckg = Package.getPackage(name);
    	if(pckg != null) {
    		result = new DefaultJavaPackage(name);
    		result.setClassLibrary( this );
    	}
    	return result;
    }

    private void readObject( ObjectInputStream in )
        throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        classLoaders = new LinkedList<ClassLoader>();
        if ( defaultClassLoadersAdded )
        {
            defaultClassLoadersAdded = false;
            addDefaultLoader();
        }
    }
    
    @Override
    protected boolean containsClassReference( String name )
    {
        boolean result = false;
        for(Iterator<ClassLoader> iter = classLoaders.iterator();!result && iter.hasNext(); )
        {
            ClassLoader classLoader = (ClassLoader) iter.next();
            try
            {
                Class<?> clazz = classLoader.loadClass( name );
                result = ( clazz != null );
            }
            catch ( ClassNotFoundException e )
            {
            }
        }
        return result;
    }
    
    /**
     * Set to {@code true} to enable debug logging for the lexer
     * 
     * @param debugLexer the debug logging flag
     */
    public void setDebugLexer( boolean debugLexer )
    {
        this.debugLexer = debugLexer;
    }
    
    /**
     * Set to {@code true} to enable debug logging for the parser
     * 
     * @param debugParser the debug logging flag
     */
    public void setDebugParser( boolean debugParser )
    {
        this.debugParser = debugParser;
    }
    
    public void setErrorHandler( ErrorHandler errorHandler )
    {
        this.errorHandler = errorHandler;
    }
}
