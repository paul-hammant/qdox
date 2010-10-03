package com.thoughtworks.qdox.library;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;

import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.ModelBuilderFactory;

public interface ClassLibraryBuilder extends Serializable
{

    public ClassLibraryBuilder appendClassLoader( ClassLoader classLoader );

    public ClassLibraryBuilder appendDefaultClassLoaders();

    public ClassLibraryBuilder appendSourceFolder( File sourceFolder );

    public ClassLibraryBuilder appendSource( InputStream stream );

    public ClassLibraryBuilder appendSource( Reader reader );

    public ClassLibraryBuilder appendSource( URL url ) throws IOException;

    public ClassLibraryBuilder appendSource( File file ) throws IOException;

    public JavaSource addSource( InputStream stream );

    public JavaSource addSource( Reader reader );
    
    public JavaSource addSource( URL url ) throws IOException;
    
    public JavaSource addSource( File file ) throws IOException;
    
    public ClassLibraryBuilder setDebugLexer( boolean debugLexer );

    public ClassLibraryBuilder setDebugParser( boolean debugParser );
    
    public ClassLibraryBuilder setEncoding( String encoding );
    
    public ClassLibraryBuilder setModelBuilderFactory( ModelBuilderFactory factory );

    public ClassLibrary getClassLibrary();

}