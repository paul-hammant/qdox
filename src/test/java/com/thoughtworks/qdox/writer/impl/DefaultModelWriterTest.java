package com.thoughtworks.qdox.writer.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.expression.Expression;

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
        String expected = "HADEAN;\n";
        
        //run
        modelWriter.writeField( enumConstant );
        
        //verify
        assertEquals( expected, modelWriter.toString() );
    }

    @Test
    public void testEnumConstantWithArgument()
    {
        // setup
        JavaField enumConstant = mock( JavaField.class );
        when( enumConstant.isEnumConstant() ).thenReturn( true );
        when( enumConstant.getName() ).thenReturn( "PENNY" );
        Expression arg = mock( Expression.class );
        when( arg.getParameterValue()).thenReturn( "1" );
        when( enumConstant.getEnumConstantArguments() ).thenReturn( Collections.singletonList( arg ) );
        
        //expectation
        String expected = "PENNY( 1 );\n";
        
        //run
        modelWriter.writeField( enumConstant );
        
        //verify
        assertEquals( expected, modelWriter.toString() );
    }

    @Test
    public void testEnumConstantWithArguments()
    {
        // setup
        JavaField enumConstant = mock( JavaField.class );
        when( enumConstant.isEnumConstant() ).thenReturn( true );
        when( enumConstant.getName() ).thenReturn( "EARTH" );

        List<Expression> args = new ArrayList<Expression>();
        Expression mass = mock( Expression.class );
        when( mass.getParameterValue()).thenReturn( "5.976e+24" );
        args.add( mass );
        Expression radius = mock( Expression.class );
        when( radius.getParameterValue()).thenReturn( "6.37814e6" );
        args.add( radius );
        when( enumConstant.getEnumConstantArguments() ).thenReturn( args );
        
        //expectation
        String expected = "EARTH( 5.976e+24, 6.37814e6 );\n";
        
        //run
        modelWriter.writeField( enumConstant );
        
        //verify
        assertEquals( expected, modelWriter.toString() );
    }
    
    @Test
    public void testEnumConstantClass()
    {
        // setup
        JavaField enumConstant = mock( JavaField.class );
        when( enumConstant.isEnumConstant() ).thenReturn( true );
        when( enumConstant.getName() ).thenReturn( "PLUS" );
        
        JavaClass cls = mock( JavaClass.class );
        JavaMethod eval = mock( JavaMethod.class );
        JavaType doubleType = mock( JavaType.class );
        when( doubleType.getGenericCanonicalName() ).thenReturn( "double" );
        when( eval.getReturnType() ).thenReturn( doubleType );
        when( eval.getName() ).thenReturn( "eval" );
        List<JavaParameter> params = new ArrayList<JavaParameter>();
        JavaParameter x = mock( JavaParameter.class );
        when( x.getGenericCanonicalName() ).thenReturn( "double" );
        when( x.getName() ).thenReturn( "x" );
        params.add( x );
        JavaParameter y = mock( JavaParameter.class );
        when( y.getGenericCanonicalName() ).thenReturn( "double" );
        when( y.getName() ).thenReturn( "y" );
        params.add( y );
        when( eval.getParameters() ).thenReturn( params );
        when( cls.getMethods() ).thenReturn( Collections.singletonList( eval ) );
        when( enumConstant.getEnumConstantClass() ).thenReturn( cls );
        
        //expectation
        String expected = "PLUS {\n" + 
        		"\n" + 
        		"\tdouble eval(double x, double y);\n" + 
        		"\n" + 
        		"}\n" + 
        		";\n";
        
        //run
        modelWriter.writeField( enumConstant );
        
        //verify
        assertEquals( expected, modelWriter.toString() );
    }
    
    @Test
    public void testJavaParameter()
    {
        JavaParameter prm = mock( JavaParameter.class );
        
        when( prm.getGenericCanonicalName() ).thenReturn( "java.lang.String" );
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
