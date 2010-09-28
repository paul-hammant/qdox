package com.thoughtworks.qdox.library;

import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Map;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.ModelBuilder;
import com.thoughtworks.qdox.parser.Lexer;
import com.thoughtworks.qdox.parser.ParseException;
import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;

public class SourceLibrary
    extends AbstractClassLibrary
{
    // parser and unused javaclasses
    private Map javaClassesMap = new Hashtable(); // <java.lang.String, com.thoughtworks.qdox.model.JavaClass>

    private boolean debugLexer;

    private boolean debugParser;

    public SourceLibrary()
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
        javaClassesMap.put( clazz.getFullyQualifiedName(), clazz );
        return clazz;
    }

    public JavaClass addSource( InputStream stream )
        throws ParseException
    {
        JavaClass clazz = parse( stream );
        javaClassesMap.put( clazz.getFullyQualifiedName(), clazz );
        return clazz;
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
        ModelBuilder builder = new ModelBuilder();
        Parser parser = new Parser( lexer, builder );
        parser.setDebugLexer( debugLexer );
        parser.setDebugParser( debugParser );
        if ( parser.parse() )
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
}
