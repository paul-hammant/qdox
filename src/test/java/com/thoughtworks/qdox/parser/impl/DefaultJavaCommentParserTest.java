package com.thoughtworks.qdox.parser.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.answers.ReturnsElementsOf;

import com.thoughtworks.qdox.builder.Builder;
import com.thoughtworks.qdox.parser.structs.TagDef;

public class DefaultJavaCommentParserTest
{

    private Collection<Integer> lexValues = new LinkedList<Integer>();
    private Collection<String> textValues = new LinkedList<String>();

    private DefaultJavaCommentLexer lexer;
    private Builder builder;

    @Before
    public void setUp() {
        lexer = mock(DefaultJavaCommentLexer.class);
        builder = mock(Builder.class);
        lexValues.clear();
        textValues.clear();
    }
    
    @After
    public void tearDown()
    {
        verifyNoMoreInteractions( builder );
    }
    
    @Test
    public void testOneLineJavaDoc() throws Exception {

        // setup values
        setupLex(DefaultJavaCommentParser.JAVADOCSTART);
        setupLex(DefaultJavaCommentParser.JAVADOCLINE, "This is great!");
        setupLex(DefaultJavaCommentParser.JAVADOCEND);
        setupLex(0);

        // execute
        DefaultJavaCommentParser parser = new DefaultJavaCommentParser(lexer, builder);
        parser.parse();

        // verify
        verify(builder).addJavaDoc( "This is great!" );
    }
    
    @Test
    public void testOneJavaDocTag() throws Exception {

        // setup values
        setupLex(DefaultJavaCommentParser.JAVADOCSTART);
        setupLex(DefaultJavaCommentParser.JAVADOCTAG, "@This");
        setupLex(DefaultJavaCommentParser.JAVADOCLINE, "is great!");
        setupLex(DefaultJavaCommentParser.JAVADOCEND);
        setupLex(0);

        // execute
        DefaultJavaCommentParser parser = new DefaultJavaCommentParser(lexer, builder);
        parser.parse();
        
        ArgumentCaptor<TagDef> tagCaptor = ArgumentCaptor.forClass( TagDef.class );

        // verify
        verify(builder).addJavaDocTag( tagCaptor.capture() );
        TagDef tag = tagCaptor.getValue();
        assertEquals( "This", tag.getName() );
        assertEquals( "is great!", tag.getText() );
    }

    @Test
    public void testOneJavaDocTagWithNoValue() throws Exception {

        // setup values
        setupLex(DefaultJavaCommentParser.JAVADOCSTART);
        setupLex(DefaultJavaCommentParser.JAVADOCTAG, "@eatme");
        setupLex(DefaultJavaCommentParser.JAVADOCLINE, "");
        setupLex(DefaultJavaCommentParser.JAVADOCEND);
        setupLex(0);

        // execute
        DefaultJavaCommentParser parser = new DefaultJavaCommentParser(lexer, builder);
        parser.parse();
        
        ArgumentCaptor<TagDef> tagCaptor = ArgumentCaptor.forClass( TagDef.class );

        // verify
        verify(builder).addJavaDocTag( tagCaptor.capture() );
        TagDef tag = tagCaptor.getValue();
        assertEquals( "eatme", tag.getName() );
        assertEquals( "", tag.getText() );
    }

    @Test
    public void testOneMultiLineJavaDocTag() throws Exception {

        // setup values
        setupLex(DefaultJavaCommentParser.JAVADOCSTART);
        setupLex(DefaultJavaCommentParser.JAVADOCTAG, "@This");
        setupLex(DefaultJavaCommentParser.JAVADOCLINE, "is great! Mmmkay.");
        setupLex(DefaultJavaCommentParser.JAVADOCEND);
        setupLex(0);

        // execute
        DefaultJavaCommentParser parser = new DefaultJavaCommentParser(lexer, builder);
        parser.parse();
        
        ArgumentCaptor<TagDef> tagCaptor = ArgumentCaptor.forClass( TagDef.class );

        // verify
        verify( builder ).addJavaDocTag( tagCaptor.capture() );
        TagDef tag = tagCaptor.getValue();
        assertEquals( "This", tag.getName() );
        assertEquals( "is great! Mmmkay.", tag.getText() );
    }

    @Test
    public void testMultipleJavaDocTags() throws Exception {

        // setup values
        setupLex(DefaultJavaCommentParser.JAVADOCSTART);
        setupLex(DefaultJavaCommentParser.JAVADOCTAG, "@This");
        setupLex(DefaultJavaCommentParser.JAVADOCLINE, "is great!");
        setupLex(DefaultJavaCommentParser.JAVADOCTAG, "@mock");
        setupLex(DefaultJavaCommentParser.JAVADOCLINE, "generate");
        setupLex(DefaultJavaCommentParser.JAVADOCEND);
        setupLex(0);

        // execute
        DefaultJavaCommentParser parser = new DefaultJavaCommentParser(lexer, builder);
        parser.parse();

        ArgumentCaptor<TagDef> tagCaptor = ArgumentCaptor.forClass( TagDef.class );

        // verify
        verify(builder, times(2)).addJavaDocTag( tagCaptor.capture() );
        TagDef tag1 = tagCaptor.getAllValues().get( 0 );
        assertEquals( "This", tag1.getName() );
        assertEquals( "is great!", tag1.getText() );
        TagDef tag2 = tagCaptor.getAllValues().get( 1 ) ;
        assertEquals( "mock", tag2.getName() );
        assertEquals( "generate", tag2.getText() );
    }

    @Test
    public void testJavaDocTextAndMultipleJavaDocTags() throws Exception {

        // setup values
        setupLex(DefaultJavaCommentParser.JAVADOCSTART);
        setupLex(DefaultJavaCommentParser.JAVADOCLINE, "Welcome! Here is my class.");
        setupLex(DefaultJavaCommentParser.JAVADOCTAG, "@This");
        setupLex(DefaultJavaCommentParser.JAVADOCLINE, "is great!");
        setupLex(DefaultJavaCommentParser.JAVADOCTAG, "@mock");
        setupLex(DefaultJavaCommentParser.JAVADOCLINE, "generate");
        setupLex(DefaultJavaCommentParser.JAVADOCEND);
        setupLex(0);

        // execute
        DefaultJavaCommentParser parser = new DefaultJavaCommentParser(lexer, builder);
        parser.parse();
        
        ArgumentCaptor<TagDef> tagCaptor = ArgumentCaptor.forClass( TagDef.class );

        // verify
        verify(builder).addJavaDoc( "Welcome! Here is my class." );
        verify(builder, times(2) ).addJavaDocTag( tagCaptor.capture() );
        TagDef tag1 = tagCaptor.getAllValues().get( 0 );
        assertEquals( "This", tag1.getName() );
        assertEquals( "is great!", tag1.getText() );
        TagDef tag2 = tagCaptor.getAllValues().get( 1 ) ;
        assertEquals( "mock", tag2.getName() );
        assertEquals( "generate", tag2.getText() );
    }

    @Test
    public void testJavaDocEmpty() throws Exception {

        // setup values
        setupLex(DefaultJavaCommentParser.JAVADOCSTART);
        setupLex(DefaultJavaCommentParser.JAVADOCLINE, "");
        setupLex(DefaultJavaCommentParser.JAVADOCEND);
        setupLex(0);

        // execute
        DefaultJavaCommentParser parser = new DefaultJavaCommentParser(lexer, builder);
        parser.parse();

        // verify
        verify(builder).addJavaDoc( "" );
    }

    private void setupLex(int token, String value) {
        lexValues.add( token );
        textValues.add( value );
    }

    private void setupLex(int token) throws IOException {
        setupLex(token, null);
        if( token == 0) 
        {
            when( lexer.lex() ).thenAnswer( new ReturnsElementsOf( lexValues ) );
            when( lexer.text() ).thenAnswer( new ReturnsElementsOf( textValues ) );
            when( lexer.getLine() ).thenReturn( -1 );
        }
    }
}
