package com.thoughtworks.qdox.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.ModelBuilder;
import com.thoughtworks.qdox.model.ModelBuilderFactory;
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
    private Map javaSourceMap = new OrderedMap(); // <java.lang.String, com.thoughtworks.qdox.model.JavaSource>

    private boolean debugLexer;

    private boolean debugParser;
    
    private String encoding;

    public SourceLibrary( )
    {
        super();
    }

    public SourceLibrary( ClassLibrary parent )
    {
        super( parent );
    }
    
    public JavaClass addSource( Reader reader )
        throws ParseException
    {
        JavaClass clazz = parse( reader );
        if ( clazz != null )
        {
            javaClassMap.put( clazz.getFullyQualifiedName(), clazz );
            javaSourceMap.put( clazz.getFullyQualifiedName(), clazz.getSource() );
        }
        return clazz;
    }

    public JavaClass addSource( InputStream stream )
        throws ParseException
    {
        JavaClass clazz = parse( stream );
        if (clazz != null) {
            javaClassMap.put( clazz.getFullyQualifiedName(), clazz );
            javaSourceMap.put( clazz.getFullyQualifiedName(), clazz.getSource() );
        }
        return clazz;
    }
    
    public JavaClass addSource( URL url )
        throws ParseException, IOException
    {
        return addSource( new InputStreamReader( url.openStream(), encoding) );
    }

    public JavaClass addSource( File file )
        throws ParseException, IOException
    {
        return addSource( new FileInputStream( file ) );
    }

    protected JavaClass parse( Reader reader )
        throws ParseException
    {
        return parse( new JFlexLexer( reader ) );
    }

    protected JavaClass parse( InputStream stream )
        throws ParseException
    {
        return parse( new JFlexLexer( stream ) );
    }

    private JavaClass parse( Lexer lexer )
        throws ParseException
    {
        JavaClass result = null;
        ModelBuilder builder = getModelBuilder();
        Parser parser = new Parser( lexer, builder );
        parser.setDebugLexer( debugLexer );
        parser.setDebugParser( debugParser );
        if ( parser.parse() && builder.getSource().getClasses().length > 0 )
        {
            result = builder.getSource().getClasses()[0];
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
    
    public JavaClass[] getClasses()
    {
        JavaClass[] result;
        JavaClass[] usedClasses = super.getClasses();
        JavaClass[] unusedClasses = (JavaClass[]) javaClassMap.values().toArray( new JavaClass[0] );
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
    
    public JavaSource[] getSources()
    {
        JavaSource[] result;
        JavaSource[] usedSources = super.getSources();
        JavaSource[] unusedSources = (JavaSource[]) javaSourceMap.values().toArray( new JavaSource[0] );
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
