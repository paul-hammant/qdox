package com.thoughtworks.qdox.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.ModelBuilder;
import com.thoughtworks.qdox.model.util.OrderedMap;
import com.thoughtworks.qdox.parser.Lexer;
import com.thoughtworks.qdox.parser.ParseException;
import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;

public class SourceLibrary
    extends AbstractClassLibrary
{
    // parser and unused javaclasses
    private Map javaClassMap = new OrderedMap(); // <java.lang.String, com.thoughtworks.qdox.model.JavaClass>
    private List javaSourceList = new ArrayList(); // <java.lang.String, com.thoughtworks.qdox.model.JavaSource>
    private Map javaPackageMap = new OrderedMap(); // <java.lang.String, com.thoughtworks.qdox.model.JavaPackage>
    
    private boolean debugLexer;

    private boolean debugParser;
    
    private String encoding;

    public SourceLibrary( )
    {
        super();
    }

    public SourceLibrary( AbstractClassLibrary parent )
    {
        super( parent );
    }
    
    public JavaSource addSource( Reader reader )
        throws ParseException
    {
        JavaSource source = parse( reader );
        registerJavaSource(source);
        return source;
    }

    public JavaSource addSource( InputStream stream )
        throws ParseException
    {
        JavaSource source = parse( stream );
        registerJavaSource(source);
        return source;
    }
    
    private void registerJavaSource(JavaSource source) {
        javaSourceList.add( source );
        if( !javaPackageMap.containsKey( source.getPackageName() ) ) {
            javaPackageMap.put( source.getPackageName(), source.getPackage() );
        }
        for( int clazzIndex = 0; clazzIndex < source.getClasses().length; clazzIndex++ ) {
            registerJavaClass( source.getClasses()[clazzIndex] );
        }
    }
    
    private void registerJavaClass(JavaClass clazz) {
        if (clazz != null) {
            javaClassMap.put( clazz.getFullyQualifiedName(), clazz );
        }
        for( int clazzIndex = 0; clazzIndex < clazz.getNestedClasses().length; clazzIndex++ ) {
            registerJavaClass( clazz.getNestedClasses()[clazzIndex] );
        }
    }
    
    public JavaSource addSource( URL url )
        throws ParseException, IOException
    {
        return addSource( new InputStreamReader( url.openStream(), encoding) );
    }

    public JavaSource addSource( File file )
        throws ParseException, IOException
    {
        return addSource( new FileInputStream( file ) );
    }

    protected JavaSource parse( Reader reader )
        throws ParseException
    {
        return parse( new JFlexLexer( reader ) );
    }

    protected JavaSource parse( InputStream stream )
        throws ParseException
    {
        return parse( new JFlexLexer( stream ) );
    }

    private JavaSource parse( Lexer lexer )
        throws ParseException
    {
        JavaSource result = null;
        ModelBuilder builder = getModelBuilder();
        Parser parser = new Parser( lexer, builder );
        parser.setDebugLexer( debugLexer );
        parser.setDebugParser( debugParser );
        if ( parser.parse() )
        {
            result = builder.getSource();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    protected JavaClass resolveJavaClass( String name )
    {
        // abstractLibrary only calls this when it can't find the source itself.
        // it will take over the reference
        return (JavaClass) javaClassMap.remove( name );
    }

    public void setDebugLexer( boolean debugLexer )
    {
        this.debugLexer = debugLexer;
    }
    
    public void setDebugParser( boolean debugParser )
    {
        this.debugParser = debugParser;
    }
    
    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }
    
    /**
     * Get all classes, including those from parent SourceLibraries
     */
    public JavaClass[] getClasses()
    {
        JavaClass[] result;
        JavaClass[] unusedClasses = (JavaClass[]) javaClassMap.values().toArray( new JavaClass[0] );
        JavaClass[] usedClasses = getJavaClasses( new ClassLibraryFilter()
        {
            public boolean accept( AbstractClassLibrary classLibrary )
            {
                return (classLibrary instanceof SourceLibrary);
            }
        });
        if ( usedClasses.length == 0 ) {
            result = unusedClasses;
        }
        else if ( unusedClasses.length == 0 ) {
            result = usedClasses;
        }
        else {
            int totalClasses = usedClasses.length + unusedClasses.length;
            result = new JavaClass[totalClasses]; 
            System.arraycopy( usedClasses, 0, result, 0, usedClasses.length );
            System.arraycopy( unusedClasses, 0, result, usedClasses.length, unusedClasses.length );
        }
        return result;
    }

    /**
     * Get all packages, including those from parent SourceLibraries
     */
    public JavaPackage[] getPackages()
    {
        JavaPackage[] result;
        JavaPackage[] unusedPackages = (JavaPackage[]) javaPackageMap.values().toArray( new JavaPackage[0] );
        JavaPackage[] usedPackages = getJavaPackages( new ClassLibraryFilter()
        {
            public boolean accept( AbstractClassLibrary classLibrary )
            {
                return (classLibrary instanceof SourceLibrary);
            }
        });
        if ( usedPackages.length == 0 ) {
            result = unusedPackages;
        }
        else if ( unusedPackages.length == 0 ) {
            result = usedPackages;
        }
        else {
            int totalPackages = usedPackages.length + unusedPackages.length;
            result = new JavaPackage[totalPackages]; 
            System.arraycopy( usedPackages, 0, result, 0, usedPackages.length );
            System.arraycopy( unusedPackages, 0, result, usedPackages.length, unusedPackages.length );
        }
        return result;
    }
    
    /**
     * Get all sources, including those from parent SourceLibraries
     */
    public JavaSource[] getSources()
    {
        JavaSource[] result;
        JavaSource[] unusedSources = (JavaSource[]) javaSourceList.toArray( new JavaSource[0] );
        JavaSource[] usedSources = getJavaSources( new ClassLibraryFilter()
        {
            public boolean accept( AbstractClassLibrary classLibrary )
            {
                return (classLibrary instanceof SourceLibrary);
            }
        });
        if ( usedSources.length == 0 ) {
            result = unusedSources;
        }
        else if ( unusedSources.length == 0 ) {
            result = usedSources;
        }
        else {
            int totalSources = usedSources.length + unusedSources.length;
            result = new JavaSource[totalSources]; 
            System.arraycopy( usedSources, 0, result, 0, usedSources.length );
            System.arraycopy( unusedSources, 0, result, usedSources.length, unusedSources.length );
        }
        return result;
    }
    
    protected boolean containsClassByName( String name )
    {
        return javaClassMap.containsKey( name );
    }
}
