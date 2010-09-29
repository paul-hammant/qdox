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
import com.thoughtworks.qdox.model.ModelBuilder;
import com.thoughtworks.qdox.model.ModelBuilderFactory;
import com.thoughtworks.qdox.parser.Lexer;
import com.thoughtworks.qdox.parser.ParseException;
import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;

public class SourceLibrary
    extends AbstractClassLibrary
{
    private ModelBuilderFactory modelBuilderFactory;
    
    // parser and unused javaclasses
    private Map javaClassesMap = new Hashtable(); // <java.lang.String, com.thoughtworks.qdox.model.JavaClass>

    private boolean debugLexer;

    private boolean debugParser;
    
    private String encoding;

    public SourceLibrary( ModelBuilderFactory modelBuilderFactory )
    {
        super();
        this.modelBuilderFactory = modelBuilderFactory;
    }

    public SourceLibrary( ModelBuilderFactory modelBuilderFactory, ClassLibrary parent )
    {
        super( parent );
        this.modelBuilderFactory = modelBuilderFactory;
    }

    public JavaClass addSource( Reader reader )
        throws ParseException
    {
        JavaClass clazz = parse( reader );
        if ( clazz != null )
        {
            javaClassesMap.put( clazz.getFullyQualifiedName(), clazz );
        }
        return clazz;
    }

    public JavaClass addSource( InputStream stream )
        throws ParseException
    {
        JavaClass clazz = parse( stream );
        if (clazz != null) {
            javaClassesMap.put( clazz.getFullyQualifiedName(), clazz );
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
        ModelBuilder builder = modelBuilderFactory.newInstance();
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
        return (JavaClass) javaClassesMap.remove( name );
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
}
