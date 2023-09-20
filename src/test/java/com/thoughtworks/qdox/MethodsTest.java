package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

public class MethodsTest {

    private JavaProjectBuilder builder = new JavaProjectBuilder();

    @Test
    public void testSupportsJava5VarArgsParameter()
    {
        JavaMethod javaMethod = buildMethod( "void doStuff(AThing param1, BThing... param2);" );

        JavaParameter standardParam = javaMethod.getParameterByName( "param1" );
        JavaParameter varArgsParam = javaMethod.getParameterByName( "param2" );

        Assertions.assertFalse(standardParam.isVarArgs(), "param1 should NOT be var args");
        Assertions.assertTrue(varArgsParam.isVarArgs(), "param2 should be var args");
    }

    @Test
    public void testVarArgsParametersAreAlsoArrays()
    {
        JavaMethod javaMethod = buildMethod( "void doStuff(AThing param1, BThing[] param2, CThing... param3);" );

        JavaClass standardType = javaMethod.getParameterByName( "param1" ).getJavaClass();
        JavaClass arrayType = javaMethod.getParameterByName( "param2" ).getJavaClass();
        JavaClass varArgsType = javaMethod.getParameterByName( "param3" ).getJavaClass();

        Assertions.assertFalse(standardType.isArray(), "param1 should NOT be array");
        Assertions.assertTrue(arrayType.isArray(), "param2 should be array");
        Assertions.assertFalse(varArgsType.isArray(), "param3 should NOT be array");
    }

    @Test
    public void testSupportDefaultMethods()
    {
        JavaMethod javaMethod = buildMethod( "default String additionalStuff() { return \"\"; }" );
        Assertions.assertTrue(javaMethod.isDefault());
    }

    @Test
    public void testVarArgsIncludedInToString()
    {
        JavaMethod javaMethod = buildMethod( "void doStuff(AThing param1, BThing... param2);" );

        Assertions.assertEquals("void doStuff(AThing param1, BThing... param2);\n", javaMethod.getCodeBlock());
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
