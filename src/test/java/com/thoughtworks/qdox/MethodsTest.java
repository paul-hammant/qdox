package com.thoughtworks.qdox;

import java.io.StringReader;

import junit.framework.TestCase;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;

public class MethodsTest
    extends TestCase
{

    private JavaProjectBuilder builder = new JavaProjectBuilder();

    public void testSupportsJava5VarArgsParameter()
    {
        JavaMethod javaMethod = buildMethod( "void doStuff(AThing param1, BThing... param2);" );

        JavaParameter standardParam = javaMethod.getParameterByName( "param1" );
        JavaParameter varArgsParam = javaMethod.getParameterByName( "param2" );

        assertFalse( "param1 should NOT be var args", standardParam.isVarArgs() );
        assertTrue( "param2 should be var args", varArgsParam.isVarArgs() );
    }

    public void testVarArgsParametersAreAlsoArrays()
    {
        JavaMethod javaMethod = buildMethod( "void doStuff(AThing param1, BThing[] param2, CThing... param3);" );

        JavaClass standardType = javaMethod.getParameterByName( "param1" ).getJavaClass();
        JavaClass arrayType = javaMethod.getParameterByName( "param2" ).getJavaClass();
        JavaClass varArgsType = javaMethod.getParameterByName( "param3" ).getJavaClass();

        assertFalse( "param1 should NOT be array", standardType.isArray() );
        assertTrue( "param2 should be array", arrayType.isArray() );
        assertFalse( "param3 should NOT be array", varArgsType.isArray() );
    }

    public void testSupportDefaultMethods()
    {
        JavaMethod javaMethod = buildMethod( "default String additionalStuff() { return \"\"; }" );
        assertTrue( javaMethod.isDefault() );
    }

    public void testVarArgsIncludedInToString()
    {
        JavaMethod javaMethod = buildMethod( "void doStuff(AThing param1, BThing... param2);" );

        assertEquals( "void doStuff(AThing param1, BThing... param2);\n", javaMethod.getCodeBlock() );
    }

    private JavaMethod buildMethod( String methodSource )
    {
        String source = "interface Something { " + methodSource + " }";
        JavaSource javaSource = builder.addSource( new StringReader( source ) );
        JavaClass javaClass = javaSource.getClasses().get( 0 );
        JavaMethod javaMethod = javaClass.getMethods().get( 0 );
        return javaMethod;
    }
}
