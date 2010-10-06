package com.thoughtworks.qdox.library;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;

import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.ModelBuilderFactory;

/**
 * This builder helps to construct a library. 
 * All kinds of sourcetypes are supported and it´s up to the implementation how to bind these types.
 * For instance: The {@link SortedClassLibraryBuilder} bundles all classloaders, all sourcefolders and all sources.
 * The {@link OrderedClassLibraryBuilder} on the other hand keeps track of the order in which sourcetypes are added.  
 * 
 * @author Robert Scholte
 * @since 2.0
 */
public interface ClassLibraryBuilder extends Serializable
{
    /**
     * Append a classloader and return itseld
     * 
     * @param classLoader the classloader to add
     * @return this ClassLibrary instance
     */
    public ClassLibraryBuilder appendClassLoader( ClassLoader classLoader );

    /**
     * Trigger the library to add the default ClassLoaders
     * 
     * @return this ClassLibrary instance
     */
    public ClassLibraryBuilder appendDefaultClassLoaders();

    /**
     * 
     * @param sourceFolder
     * @return this ClassLibrary instance
     */
    public ClassLibraryBuilder appendSourceFolder( File sourceFolder );

    /**
     * 
     * @param stream
     * @return this ClassLibrary instance
     */
    public ClassLibraryBuilder appendSource( InputStream stream );

    /**
     * 
     * @param reader
     * @return this ClassLibrary instance
     */
    public ClassLibraryBuilder appendSource( Reader reader );

    /**
     * 
     * @param url
     * @return this ClassLibrary instance
     * @throws IOException
     */
    public ClassLibraryBuilder appendSource( URL url ) throws IOException;

    /**
     * 
     * @param file
     * @return this ClassLibrary instance
     * @throws IOException
     */
    public ClassLibraryBuilder appendSource( File file ) throws IOException;

    public JavaSource addSource( InputStream stream );

    public JavaSource addSource( Reader reader );
    
    public JavaSource addSource( URL url ) throws IOException;
    
    public JavaSource addSource( File file ) throws IOException;
    
    /**
     * 
     * @param debugLexer
     * @return this ClassLibrary instance
     */
    public ClassLibraryBuilder setDebugLexer( boolean debugLexer );

    /**
     * 
     * @param debugParser
     * @return this ClassLibrary instance
     */
    public ClassLibraryBuilder setDebugParser( boolean debugParser );
    
    /**
     * 
     * @param encoding
     * @return this ClassLibrary instance
     */
    public ClassLibraryBuilder setEncoding( String encoding );
    
    /**
     * Define the {@link ModelBuilderFactory} which the parsers should use to construct the JavaModel Objects
     * 
     * @param factory
     * @return this ClassLibrary instance
     */
    public ClassLibraryBuilder setModelBuilderFactory( ModelBuilderFactory factory );

    /**
     * Get the library based on the strategy of the implementation
     * 
     * @return the constructed ClassLibrary
     */
    public ClassLibrary getClassLibrary();

}