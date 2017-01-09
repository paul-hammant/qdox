package com.thoughtworks.qdox.writer.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaInitializer;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaModule;
import com.thoughtworks.qdox.model.JavaModuleDescriptor;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaExports;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaOpens;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaProvides;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaRequires;
import com.thoughtworks.qdox.model.JavaModuleDescriptor.JavaUses;
import com.thoughtworks.qdox.model.JavaPackage;
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
    
    @Test
    public void testStaticInitializer()
    {
        
        JavaInitializer init = mock( JavaInitializer.class );
        when( init.isStatic() ).thenReturn( true );
        when( init.getBlockContent() ).thenReturn( "//test" );
        
        modelWriter.writeInitializer( init );
        
        String expected = "static {\n" +
        		"\t//test\n" +
        		"}\n";
        assertEquals( expected, modelWriter.toString() );
    }

    @Test
    public void testInstanceInitializer()
    {
        
        JavaInitializer init = mock( JavaInitializer.class );
        when( init.isStatic() ).thenReturn( false );
        when( init.getBlockContent() ).thenReturn( "//test" );
        
        modelWriter.writeInitializer( init );
        
        String expected = "{\n" +
                "\t//test\n" +
                "}\n";
        assertEquals( expected, modelWriter.toString() );
    }
    
    @Test
    public void testAnnotation()
    {
        
        JavaClass annType = mock( JavaClass.class );
        when( annType.getGenericCanonicalName() ).thenReturn( "Anno" );
        JavaAnnotation ann = mock( JavaAnnotation.class );
        when( ann.getType() ).thenReturn( annType );
        
        modelWriter.writeAnnotation( ann );
        
        String expected = "@Anno\n";
        assertEquals( expected, modelWriter.toString() );
    }
    
    @Test
    public void testModuleDescriptor()
    {
        JavaModuleDescriptor descriptor = mock(JavaModuleDescriptor.class);
        when(descriptor.getName()).thenReturn( "M.N" );
        
        modelWriter.writeModuleDescriptor( descriptor );
        
        String expected = "module M.N {\n\n}\n";
        assertEquals( expected, modelWriter.toString() );
    }

    @Test
    public void testOpenModuleDescriptor()
    {
        JavaModuleDescriptor descriptor = mock(JavaModuleDescriptor.class);
        when(descriptor.getName()).thenReturn( "M.N" );
        when(descriptor.isOpen()).thenReturn( true );
        
        modelWriter.writeModuleDescriptor( descriptor );
        
        String expected = "open module M.N {\n\n}\n";
        assertEquals( expected, modelWriter.toString() );
    }

    @Test
    public void testModuleRequires()
    {
        JavaRequires requires1 = mock( JavaRequires.class );
        JavaModule moduleAB = mock(JavaModule.class);
        when( moduleAB.getName() ).thenReturn( "A.B" );
        when( requires1.getModule() ).thenReturn( moduleAB );
        when( requires1.getModifiers() ).thenReturn( Collections.<String>emptyList() );
        modelWriter.writeModuleRequires( requires1 );
        assertEquals( "requires A.B;\n", modelWriter.toString() );

        modelWriter = new DefaultModelWriter();
        JavaRequires requires2 = mock( JavaRequires.class );
        JavaModule moduleCD = mock(JavaModule.class);
        when( moduleCD.getName() ).thenReturn( "C.D" );
        when( requires2.getModule() ).thenReturn( moduleCD);
        when( requires2.getModifiers() ).thenReturn( Collections.singleton( "public" ) );
        modelWriter.writeModuleRequires( requires2 );
        assertEquals( "requires public C.D;\n", modelWriter.toString() );
        
        modelWriter = new DefaultModelWriter();
        JavaRequires requires3 = mock( JavaRequires.class );
        JavaModule moduleEF = mock(JavaModule.class);
        when( moduleEF.getName() ).thenReturn( "E.F" );
        when( requires3.getModule() ).thenReturn( moduleEF );
        when( requires3.getModifiers() ).thenReturn( Collections.singleton( "static" ) );
        modelWriter.writeModuleRequires( requires3 );
        assertEquals( "requires static E.F;\n", modelWriter.toString() );
        
        modelWriter = new DefaultModelWriter();
        JavaRequires requires4 = mock( JavaRequires.class );
        JavaModule moduleGH = mock(JavaModule.class);
        when( moduleGH.getName() ).thenReturn( "G.H" );
        when( requires4.getModule() ).thenReturn( moduleGH );
        when( requires4.getModifiers() ).thenReturn( Arrays.asList( "public", "static" ) );
        modelWriter.writeModuleRequires( requires4 );
        assertEquals( "requires public static G.H;\n", modelWriter.toString() );
    }
    
    @Test
    public void testModuleExports()
    {
        JavaExports exports1 = mock( JavaExports.class );
        JavaPackage sourcePQ = mock(JavaPackage.class);
        when(sourcePQ.getName()).thenReturn( "P.Q" );
        when( exports1.getSource() ).thenReturn( sourcePQ );
        modelWriter.writeModuleExports( exports1 );
        assertEquals( "exports P.Q;\n", modelWriter.toString() );

        modelWriter = new DefaultModelWriter();
        JavaExports exports2 = mock( JavaExports.class );
        JavaPackage sourceRS = mock(JavaPackage.class);
        when(sourceRS.getName()).thenReturn( "R.S" );
        when( exports2.getSource() ).thenReturn( sourceRS );
        
        JavaModule moduleT1U1 = mock( JavaModule.class );
        when( moduleT1U1.getName() ).thenReturn( "T1.U1" );
        JavaModule moduleT2U2 = mock( JavaModule.class );
        when( moduleT2U2.getName() ).thenReturn( "T2.U2" );
        when(exports2.getTargets()).thenReturn( Arrays.asList( moduleT1U1, moduleT2U2 ) );
        modelWriter.writeModuleExports( exports2 );
        assertEquals( "exports R.S to T1.U1, T2.U2;\n", modelWriter.toString() );
    }
    
    @Test
    public void testModuleOpens()
    {
        modelWriter = new DefaultModelWriter();
        JavaOpens opens1 = mock( JavaOpens.class );
        JavaPackage source1 = mock(JavaPackage.class);
        when(source1.getName()).thenReturn( "P.Q" );
        when( opens1.getSource() ).thenReturn( source1 );
        modelWriter.writeModuleOpens( opens1 );
        assertEquals( "opens P.Q;\n", modelWriter.toString() );

        modelWriter = new DefaultModelWriter();
        JavaOpens opens2 = mock( JavaOpens.class );
        JavaPackage source2 = mock(JavaPackage.class);
        when(source2.getName()).thenReturn( "R.S" );
        when( opens2.getSource() ).thenReturn( source2 );
        
        JavaModule moduleT1U1 = mock( JavaModule.class );
        when( moduleT1U1.getName() ).thenReturn( "T1.U1" );
        JavaModule moduleT2U2 = mock( JavaModule.class );
        when( moduleT2U2.getName() ).thenReturn( "T2.U2" );
        when( opens2.getTargets()).thenReturn( Arrays.asList( moduleT1U1, moduleT2U2 ) );
        modelWriter.writeModuleOpens( opens2 );
        assertEquals( "opens R.S to T1.U1, T2.U2;\n", modelWriter.toString() );
    }
    
    @Test
    public void testModuleProvides()
    {
        JavaProvides provides = mock( JavaProvides.class );
        JavaClass service = mock( JavaClass.class );
        when( service.getName() ).thenReturn( "X.Y" );
        JavaClass providerZ1Z2 = mock( JavaClass.class );
        when( providerZ1Z2.getName() ).thenReturn( "Z1.Z2" );
        JavaClass providerZ3Z4 = mock( JavaClass.class );
        when( providerZ3Z4.getName() ).thenReturn( "Z3.Z4" );
        when( provides.getService() ).thenReturn( service );
        when( provides.getProviders() ).thenReturn( Arrays.asList( providerZ1Z2, providerZ3Z4 ) );
        modelWriter.writeModuleProvides( provides );
        assertEquals( "provides X.Y with Z1.Z2, Z3.Z4;\n", modelWriter.toString() );
    }

    @Test
    public void testModuleUses()
    {
        JavaUses uses = mock( JavaUses.class );
        JavaClass service = mock( JavaClass.class );
        when( service.getName() ).thenReturn( "V.W" );
        when( uses.getService() ).thenReturn( service );
        modelWriter.writeModuleUses( uses );
        assertEquals( "uses V.W;\n", modelWriter.toString() );
    }
}
