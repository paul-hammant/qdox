package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.model.JavaParameter;
import junit.framework.TestCase;

import java.io.StringReader;

public class MethodsTest extends TestCase {

    private JavaDocBuilder builder = new JavaDocBuilder();

    public void testSupportsJava5VarArgsParameter() {
        JavaMethod javaMethod = buildMethod("void doStuff(AThing param1, BThing... param2);");

        JavaParameter standardParam = javaMethod.getParameterByName("param1");
        JavaParameter varArgsParam = javaMethod.getParameterByName("param2");

        assertFalse("param1 should NOT be var args", standardParam.isVarArgs());
        assertTrue("param2 should be var args", varArgsParam.isVarArgs());
    }

    public void testVarArgsParametersAreAlsoArrays() {
        JavaMethod javaMethod = buildMethod("void doStuff(AThing param1, BThing[] param2, CThing... param3);");

        Type standardType = javaMethod.getParameterByName("param1").getType();
        Type arrayType = javaMethod.getParameterByName("param2").getType();
        Type varArgsType = javaMethod.getParameterByName("param3").getType();

        assertFalse("param1 should NOT be array", standardType.isArray());
        assertTrue("param2 should be array", arrayType.isArray());
        assertFalse("param3 should NOT be array", varArgsType.isArray());
    }

    public void testVarArgsIncludedInToString() {
        JavaMethod javaMethod = buildMethod("void doStuff(AThing param1, BThing... param2);");

        assertEquals("void doStuff(AThing param1, BThing... param2);\n", javaMethod.toString());
    }

    private JavaMethod buildMethod(String methodSource) {
        String source = "interface Something { " + methodSource + " }";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses()[0];
        JavaMethod javaMethod = javaClass.getMethods()[0];
        return javaMethod;
    }
}
