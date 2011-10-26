package com.thoughtworks.qdox.writer.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;

public class DefaultModelWriterTest {

	private DefaultModelWriter modelWriter;
	
	@Before
	public void onSetup(){
		modelWriter = new DefaultModelWriter();
	}
	
	@Test
    public void testCommentToString() {
        // setup
    	JavaAnnotatedElement annotatedElement = mock(JavaAnnotatedElement.class);
    	when(annotatedElement.getComment()).thenReturn("Hello");

        // expectation
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " */\n";

        // run
        modelWriter.commentHeader(annotatedElement);

        // verify
        assertEquals(expected, modelWriter.toString());
    }
    
    @Test
    public void testMultilineCommentToString() {
    	JavaAnnotatedElement annotatedElement = mock(JavaAnnotatedElement.class);
    	when(annotatedElement.getComment()).thenReturn("Hello\nWorld");

        // expectation
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " * World\n"
                + " */\n";

        // run
        modelWriter.commentHeader(annotatedElement);

        // verify
        assertEquals(expected, modelWriter.toString());
    	
    }

    @Test
    public void testEmptyCommentToString() {
        // setup
    	JavaAnnotatedElement annotatedElement = mock(JavaAnnotatedElement.class);
    	when(annotatedElement.getComment()).thenReturn("");

        // expectation
        String expected = ""
                + "/**\n"
                + " */\n";

        // run
        modelWriter.commentHeader(annotatedElement);

        // verify
        assertEquals(expected, modelWriter.toString());
    }

    @Test
    public void testNoCommentToString() {
        // setup
        JavaAnnotatedElement annotatedElement = mock(JavaAnnotatedElement.class);

        // expectation
        String expected = "";

        // run
        modelWriter.commentHeader(annotatedElement);

        // verify
        assertEquals(expected, modelWriter.toString());
    }

    @Test
    public void testCommentWithTagToString() {
        // setup
    	JavaAnnotatedElement annotatedElement = mock(JavaAnnotatedElement.class);
    	when(annotatedElement.getComment()).thenReturn("Hello");
        DocletTag monkeyTag = mock(DocletTag.class);
        when(monkeyTag.getName()).thenReturn( "monkey" );
        when(monkeyTag.getValue()).thenReturn( "is in the tree" );
    	when(annotatedElement.getTags()).thenReturn(Collections.singletonList( monkeyTag ));

        // expectation
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " *\n"
                + " * @monkey is in the tree\n"
                + " */\n";

        // run
        modelWriter.commentHeader(annotatedElement);

        // verify
        assertEquals(expected, modelWriter.toString());
    }

    @Test
    public void testCommentWithMultipleTagsToString() {
        // setup
    	JavaAnnotatedElement annotatedElement = mock(JavaAnnotatedElement.class);
    	when(annotatedElement.getComment()).thenReturn("Hello");
        List<DocletTag> tags = new LinkedList<DocletTag>();
        DocletTag monkeyTag = mock(DocletTag.class);
        when(monkeyTag.getName()).thenReturn( "monkey" );
        when(monkeyTag.getValue()).thenReturn( "is in the tree" );
        tags.add( monkeyTag );
        DocletTag seeTag = mock( DocletTag.class );
        when(seeTag.getName()).thenReturn( "see" );
        when(seeTag.getValue()).thenReturn("the doctor" );
        tags.add(seeTag);
        when(annotatedElement.getTags()).thenReturn(tags);

        // expectation
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " *\n"
                + " * @monkey is in the tree\n"
                + " * @see the doctor\n"
                + " */\n";

        // run
        modelWriter.commentHeader(annotatedElement);

        // verify
        assertEquals(expected, modelWriter.toString());
    }

    @Test
    public void testTagButNoCommentToString() {
        // setup
    	JavaAnnotatedElement annotatedElement = mock(JavaAnnotatedElement.class);
        DocletTag monkeyTag = mock(DocletTag.class);
        when(monkeyTag.getName()).thenReturn( "monkey" );
        when(monkeyTag.getValue()).thenReturn( "is in the tree" );
        when(annotatedElement.getTags()).thenReturn(Collections.singletonList( monkeyTag ));

        // expectation
        String expected = ""
                + "/**\n"
                + " * @monkey is in the tree\n"
                + " */\n";

        // run
        modelWriter.commentHeader(annotatedElement);

        // verify
        assertEquals(expected, modelWriter.toString());
    }

    @Test
    public void testTagWithNoValueToString() {
        // setup
    	JavaAnnotatedElement annotatedElement = mock(JavaAnnotatedElement.class);
        DocletTag monkeyTag = mock(DocletTag.class);
        when(monkeyTag.getName()).thenReturn( "monkey" );
        when(monkeyTag.getValue()).thenReturn( "" );
        when(annotatedElement.getTags()).thenReturn(Collections.singletonList( monkeyTag ));

        // expectation
        String expected = ""
                + "/**\n"
                + " * @monkey\n"
                + " */\n";

        // run
        modelWriter.commentHeader(annotatedElement);

        // verify
        assertEquals(expected, modelWriter.toString());
    }
    
    
    //enum Eon { HADEAN, ARCHAEAN, PROTEROZOIC, PHANEROZOIC }

    @Test
    public void testEnumConstant()
    {
        //setup
        JavaField enumConstant = mock(JavaField.class);
        when(enumConstant.isEnumConstant()).thenReturn( true );
        when(enumConstant.getName()).thenReturn( "HADEAN" );
        
        //expectation
        String expected = ""
            + "HADEAN";
        
        //run
        modelWriter.writeField( enumConstant );
        
        //verify
        assertEquals( expected, modelWriter.toString() );
    }
    
    public void testJavaParameter()
    {
        JavaParameter prm = mock( JavaParameter.class );
        JavaType type = mock( JavaType.class );
        
        when( type.getCanonicalName() ).thenReturn( "java.lang.String" );
        when( prm.getType() ).thenReturn( type );
        when( prm.getName() ).thenReturn( "argument" );
        
        modelWriter.writeParameter( prm );
        
        String expected = "java.lang.String argument";
        assertEquals( expected, modelWriter.toString() );
    }
    
    @Test
    public void testJavaParameterVarArgs()
    {
        JavaParameter prm = mock( JavaParameter.class );
        
        when( prm.getGenericCanonicalName() ).thenReturn( "java.lang.String" );
        when( prm.getName() ).thenReturn( "argument" );
        when( prm.isVarArgs() ).thenReturn( true );
        
        modelWriter.writeParameter( prm );
        
        String expected = "java.lang.String... argument";
        assertEquals( expected, modelWriter.toString() );
    }

}
