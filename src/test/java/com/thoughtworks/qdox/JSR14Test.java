package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

/**
 * QDOX-54 Support for retrieval of generic type information (JSR 14)
 * 
 * Some core-classes have been changed, but most interfaces are kept the same.
 * Most important is the method Type.getGenericValue(), which does exactly what it says.
 * The WildcardType is added as a subclass of Type. This way we can easily define these types of Types
 * 
 * 
 * @author Robert Scholte
 *
 */
public class JSR14Test {
	
	private JavaProjectBuilder builder = new JavaProjectBuilder();

    @Test
    public void testSimpleSingleParameterizedTypeMethod() {
    	JavaMethod javaMethod = buildMethod("java.util.List<String> getList();");
    	Assertions.assertEquals("java.util.List", javaMethod.getReturns().getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<java.lang.String>", javaMethod.getReturns().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", javaMethod.getReturns().getValue());
        Assertions.assertEquals("java.util.List<String>", javaMethod.getReturns().getGenericValue());
    }

    @Test
    public void testSimpleWildcardTypeMethod() {
    	JavaMethod javaMethod = buildMethod("java.util.List<?> getList();");
    	Assertions.assertEquals("java.util.List<?>", javaMethod.getReturns().getGenericValue());
    }

    @Test
    public void testSimpleExtendsWildcardTypeMethod() {
    	JavaMethod javaMethod = buildMethod("java.util.List<? extends Number> getList();");
    	Assertions.assertEquals("java.util.List", javaMethod.getReturns().getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<? extends java.lang.Number>", javaMethod.getReturns().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", javaMethod.getReturns().getValue());
        Assertions.assertEquals("java.util.List<? extends Number>", javaMethod.getReturns().getGenericValue());
    }

    @Test
    public void testSimpleSuperWildcardTypeMethod() {
    	JavaMethod javaMethod = buildMethod("java.util.List<? super Integer> getList();");
    	Assertions.assertEquals("java.util.List", javaMethod.getReturns().getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<? super java.lang.Integer>", javaMethod.getReturns().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", javaMethod.getReturns().getValue());
        Assertions.assertEquals("java.util.List<? super Integer>", javaMethod.getReturns().getGenericValue());
    }

    @Test
    public void testSimpleMultiParameterizedTypeMethod() {
    	JavaMethod javaMethod = buildMethod("java.util.Map<String, Object> getMap();");
    	Assertions.assertEquals("java.util.Map", javaMethod.getReturns().getFullyQualifiedName());
        Assertions.assertEquals("java.util.Map<java.lang.String,java.lang.Object>", javaMethod.getReturns().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.Map", javaMethod.getReturns().getValue());
        Assertions.assertEquals("java.util.Map<String,Object>", javaMethod.getReturns().getGenericValue());
    }

    @Test
    public void testComplexSingleParameterizedTypeMethod() {
    	JavaMethod javaMethod = buildMethod("java.util.List<java.util.Set<String>> getList();");
    	Assertions.assertEquals("java.util.List", javaMethod.getReturns().getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<java.util.Set<java.lang.String>>", javaMethod.getReturns().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", javaMethod.getReturns().getValue());
        Assertions.assertEquals("java.util.List<java.util.Set<String>>", javaMethod.getReturns().getGenericValue());
    }

    @Test
    public void testComplexMultiParameterizedTypeMethod() {
    	JavaMethod javaMethod = buildMethod("java.util.Map<String, java.util.Iterator<Number>> getMap();");
    	Assertions.assertEquals("java.util.Map", javaMethod.getReturns().getFullyQualifiedName());
        Assertions.assertEquals("java.util.Map<java.lang.String,java.util.Iterator<java.lang.Number>>", javaMethod.getReturns().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.Map", javaMethod.getReturns().getValue());
        Assertions.assertEquals("java.util.Map<String,java.util.Iterator<Number>>", javaMethod.getReturns().getGenericValue());
    }
    
    private JavaMethod buildMethod(String methodSource) {
        String source = "interface Something { " + methodSource + " }";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses().get(0);
        JavaMethod javaMethod = javaClass.getMethods().get(0);
        return javaMethod;
    }

    @Test
    public void testSimpleSingleParameterizedTypeField() {
        String source = "public class Something { " +
		"public java.util.List<String> aList;" + 
		" }";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses().get(0);
        JavaField javaField = javaClass.getFieldByName("aList");
        Assertions.assertEquals("java.util.List", javaField.getType().getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<java.lang.String>", javaField.getType().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", javaField.getType().getValue());
        Assertions.assertEquals("java.util.List<String>", javaField.getType().getGenericValue());
    }

    @Test
    public void testSimpleMultiParameterizedTypeField() {
        String source = "public class Something { " +
        		"public java.util.Map<String, Object> aMap;" + 
        		" }";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses().get(0);
        JavaField javaField = javaClass.getFieldByName("aMap");
        Assertions.assertEquals("java.util.Map", javaField.getType().getFullyQualifiedName());
        Assertions.assertEquals("java.util.Map<java.lang.String,java.lang.Object>", javaField.getType().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.Map", javaField.getType().getValue());
        Assertions.assertEquals("java.util.Map<String,Object>", javaField.getType().getGenericValue());
    }

    @Test
    public void testSimpleWildcardTypeField() {
    	String source = "public class Something { " +
		"public java.util.List<?> aList;" + 
		" }";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaField javaField = javaClass.getFieldByName("aList");
    	Assertions.assertEquals("java.util.List<?>", javaField.getType().getGenericValue());
    }

    @Test
    public void testSimpleExtendsWildcardTypeField() {
		String source = "public class Something { " +
		"public java.util.List<? extends Number> aList;" + 
		" }";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaField javaField = javaClass.getFieldByName("aList");
    	Assertions.assertEquals("java.util.List", javaField.getType().getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<? extends java.lang.Number>", javaField.getType().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", javaField.getType().getValue());
        Assertions.assertEquals("java.util.List<? extends Number>", javaField.getType().getGenericValue());
    }

    @Test
    public void testSimpleSuperWildcardTypeField() {
		String source = "public class Something { " +
		"public java.util.List<? super Integer> aList;" + 
		" }";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaField javaField = javaClass.getFieldByName("aList");
    	Assertions.assertEquals("java.util.List", javaField.getType().getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<? super java.lang.Integer>", javaField.getType().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", javaField.getType().getValue());
        Assertions.assertEquals("java.util.List<? super Integer>", javaField.getType().getGenericValue());
    }

    @Test
    public void testComplexSingleParameterizedTypeField() {
		String source = "public class Something { " +
		"public java.util.List<java.util.Set<String>> aList;" + 
		" }";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaField javaField = javaClass.getFieldByName("aList");
    	Assertions.assertEquals("java.util.List", javaField.getType().getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<java.util.Set<java.lang.String>>", javaField.getType().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", javaField.getType().getValue());
        Assertions.assertEquals("java.util.List<java.util.Set<String>>", javaField.getType().getGenericValue());
    }

    @Test
    public void testComplexMultiParameterizedTypeField() {
		String source = "public class Something { " +
		"public java.util.List<java.util.Set<String>> aList;" + 
		" }";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaField javaField = javaClass.getFieldByName("aList");
    	Assertions.assertEquals("java.util.List", javaField.getType().getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<java.util.Set<java.lang.String>>", javaField.getType().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", javaField.getType().getValue());
        Assertions.assertEquals("java.util.List<java.util.Set<String>>", javaField.getType().getGenericValue());
    }

    @Test
    public void testSimpleSingleParameterizedTypeParameter() {
    	JavaMethod javaMethod = buildMethod("void setList(java.util.List<String> aList);");
    	Assertions.assertEquals("java.util.List", javaMethod.getParameterByName("aList").getType().getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<java.lang.String>", javaMethod.getParameterByName("aList").getType().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", javaMethod.getParameterByName("aList").getType().getValue());
        Assertions.assertEquals("java.util.List<String>", javaMethod.getParameterByName("aList").getType().getGenericValue());
    }

    @Test
    public void testSimpleWildcardTypeParameter() {
    	JavaMethod javaMethod = buildMethod("void setList(java.util.List<?> aList);");
    	Assertions.assertEquals("java.util.List<?>", javaMethod.getParameterByName("aList").getType().getGenericValue());
    }

    @Test
    public void testSimpleExtendsWildcardTypeParameter() {
    	JavaMethod javaMethod = buildMethod("void setList(java.util.List<? extends Number> aList);");
    	Assertions.assertEquals("java.util.List", javaMethod.getParameterByName("aList").getType().getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<? extends java.lang.Number>", javaMethod.getParameterByName("aList").getType().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", javaMethod.getParameterByName("aList").getType().getValue());
        Assertions.assertEquals("java.util.List<? extends Number>", javaMethod.getParameterByName("aList").getType().getGenericValue());
    }

    @Test
    public void testSimpleSuperWildcardTypeParameter() {
    	JavaMethod javaMethod = buildMethod("void setList(java.util.List<? super Integer> aList);");
    	Assertions.assertEquals("java.util.List", javaMethod.getParameterByName("aList").getType().getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<? super java.lang.Integer>", javaMethod.getParameterByName("aList").getType().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", javaMethod.getParameterByName("aList").getType().getValue());
        Assertions.assertEquals("java.util.List<? super Integer>", javaMethod.getParameterByName("aList").getType().getGenericValue());
    }

    @Test
    public void testSimpleMultiParameterizedTypeParameter() {
    	JavaMethod javaMethod = buildMethod("void setMap(java.util.Map<String, Object> aMap);");
    	Assertions.assertEquals("java.util.Map", javaMethod.getParameterByName("aMap").getType().getFullyQualifiedName());
        Assertions.assertEquals("java.util.Map<java.lang.String,java.lang.Object>", javaMethod.getParameterByName("aMap").getType().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.Map", javaMethod.getParameterByName("aMap").getType().getValue());
        Assertions.assertEquals("java.util.Map<String,Object>", javaMethod.getParameterByName("aMap").getType().getGenericValue());
    }

    @Test
    public void testComplexSingleParameterizedTypeParameter() {
    	JavaMethod javaMethod = buildMethod("void setList(java.util.List<java.util.Set<String>> aList);");
    	Assertions.assertEquals("java.util.List", javaMethod.getParameterByName("aList").getType().getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<java.util.Set<java.lang.String>>", javaMethod.getParameterByName("aList").getType().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", javaMethod.getParameterByName("aList").getType().getValue());
        Assertions.assertEquals("java.util.List<java.util.Set<String>>", javaMethod.getParameterByName("aList").getType().getGenericValue());
    }

    @Test
    public void testComplexMultiParameterizedTypeParameter() {
    	JavaMethod javaMethod = buildMethod("void setMap(java.util.Map<String, java.util.Iterator<Number>> aMap);");
    	Assertions.assertEquals("java.util.Map", javaMethod.getParameterByName("aMap").getType().getFullyQualifiedName());
        Assertions.assertEquals("java.util.Map<java.lang.String,java.util.Iterator<java.lang.Number>>", javaMethod.getParameterByName("aMap").getType().getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.Map", javaMethod.getParameterByName("aMap").getType().getValue());
        Assertions.assertEquals("java.util.Map<String,java.util.Iterator<Number>>", javaMethod.getParameterByName("aMap").getType().getGenericValue());
    }

    @Test
    public void testSimpleSingleParameterizedTypeClassExtends() {
        String source = "public class Something extends java.util.List<String> {}";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses().get(0);
        JavaType superClass = javaClass.getSuperClass();
        Assertions.assertEquals("java.util.List", superClass.getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<java.lang.String>", superClass.getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", superClass.getValue());
        Assertions.assertEquals("java.util.List<String>", superClass.getGenericValue());
    }

    @Test
    public void testSimpleMultiParameterizedTypeClassExtends() {
        String source = "public class Something extends java.util.Map<String, Object> {}";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses().get(0);
        JavaType superClass = javaClass.getSuperClass();
        Assertions.assertEquals("java.util.Map", superClass.getFullyQualifiedName());
        Assertions.assertEquals("java.util.Map<java.lang.String,java.lang.Object>", superClass.getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.Map", superClass.getValue());
        Assertions.assertEquals("java.util.Map<String,Object>", superClass.getGenericValue());
    }

    @Test
    public void testSimpleWildcardTypeClassExtends() {
    	String source = "public class Something extends java.util.List<?>{}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaType superClass = javaClass.getSuperClass();
    	Assertions.assertEquals("java.util.List<?>", superClass.getGenericValue());
    }

    @Test
    public void testSimpleExtendsWildcardTypeClassExtends() {
		String source = "public class Something extends java.util.List<? extends Number> {}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaType superClass = javaClass.getSuperClass();
    	Assertions.assertEquals("java.util.List", superClass.getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<? extends java.lang.Number>", superClass.getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", superClass.getValue());
        Assertions.assertEquals("java.util.List<? extends Number>", superClass.getGenericValue());
    }

    @Test
    public void testSimpleSuperWildcardTypeClassExtends() {
		String source = "public class Something extends java.util.List<? super Integer> {}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaType superClass = javaClass.getSuperClass();
    	Assertions.assertEquals("java.util.List", superClass.getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<? super java.lang.Integer>", superClass.getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", superClass.getValue());
        Assertions.assertEquals("java.util.List<? super Integer>", superClass.getGenericValue());
    }

    @Test
    public void testComplexSingleParameterizedTypeClassExtends() {
		String source = "public class Something extends java.util.List<java.util.Set<String>> {}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaType superClass = javaClass.getSuperClass();
    	Assertions.assertEquals("java.util.List", superClass.getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<java.util.Set<java.lang.String>>", superClass.getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", superClass.getValue());
        Assertions.assertEquals("java.util.List<java.util.Set<String>>", superClass.getGenericValue());
    }

    @Test
    public void testComplexMultiParameterizedTypeClassExtends() {
		String source = "public class Something extends java.util.List<java.util.Set<String>> {}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaType superClass = javaClass.getSuperClass();
    	Assertions.assertEquals("java.util.List", superClass.getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<java.util.Set<java.lang.String>>", superClass.getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", superClass.getValue());
        Assertions.assertEquals("java.util.List<java.util.Set<String>>", superClass.getGenericValue());
    }

    @Test
    public void testSimpleSingleParameterizedTypeClassImplements() {
        String source = "public class Something implements java.util.List<String> {}";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses().get(0);
        JavaType implementsClass = javaClass.getImplements().get(0);
        Assertions.assertEquals("java.util.List", implementsClass.getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<java.lang.String>", implementsClass.getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", implementsClass.getValue());
        Assertions.assertEquals("java.util.List<String>", implementsClass.getGenericValue());
    }

    @Test
    public void testSimpleMultiParameterizedTypeClassImplements() {
        String source = "public class Something implements java.util.Map<String, Object> {}";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses().get(0);
        JavaType implementsClass = javaClass.getImplements().get(0);
        Assertions.assertEquals("java.util.Map", implementsClass.getFullyQualifiedName());
        Assertions.assertEquals("java.util.Map<java.lang.String,java.lang.Object>", implementsClass.getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.Map", implementsClass.getValue());
        Assertions.assertEquals("java.util.Map<String,Object>", implementsClass.getGenericValue());
    }

    @Test
    public void testSimpleWildcardTypeClassImplements() {
    	String source = "public class Something implements java.util.List<?>{}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaType implementsClass = javaClass.getImplements().get(0);
    	Assertions.assertEquals("java.util.List<?>", implementsClass.getGenericValue());
    }

    @Test
    public void testSimpleExtendsWildcardTypeClassImplements() {
		String source = "public class Something implements java.util.List<? extends Number> {}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaType implementsClass = javaClass.getImplements().get(0);
    	Assertions.assertEquals("java.util.List", implementsClass.getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<? extends java.lang.Number>", implementsClass.getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", implementsClass.getValue());
        Assertions.assertEquals("java.util.List<? extends Number>", implementsClass.getGenericValue());
    }

    @Test
    public void testSimpleSuperWildcardTypeClassImplements() {
		String source = "public class Something implements java.util.List<? super Integer> {}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaType implementsClass = javaClass.getImplements().get(0);
    	Assertions.assertEquals("java.util.List", implementsClass.getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<? super java.lang.Integer>", implementsClass.getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", implementsClass.getValue());
        Assertions.assertEquals("java.util.List<? super Integer>", implementsClass.getGenericValue());
    }

    @Test
    public void testComplexSingleParameterizedTypeClassImplements() {
		String source = "public class Something implements java.util.List<java.util.Set<String>> {}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaType implementsClass = javaClass.getImplements().get(0);
    	Assertions.assertEquals("java.util.List", implementsClass.getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<java.util.Set<java.lang.String>>", implementsClass.getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", implementsClass.getValue());
        Assertions.assertEquals("java.util.List<java.util.Set<String>>", implementsClass.getGenericValue());
    }

    @Test
    public void testComplexMultiParameterizedTypeClassImplements() {
		String source = "public class Something implements java.util.List<java.util.Set<String>> {}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaType implementsClass = javaClass.getImplements().get(0);
    	Assertions.assertEquals("java.util.List", implementsClass.getFullyQualifiedName());
        Assertions.assertEquals("java.util.List<java.util.Set<java.lang.String>>", implementsClass.getGenericFullyQualifiedName());
        Assertions.assertEquals("java.util.List", implementsClass.getValue());
        Assertions.assertEquals("java.util.List<java.util.Set<String>>", implementsClass.getGenericValue());
    }

    @Test
    public void testSimpleTypeVariable() {
    	String source = "public class Something {\n" +
    			" public <T extends StringBuffer> void doStuff(T param) {}\n" +
    			"}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaMethod javaMethod = javaSource.getClasses().get(0).getMethods().get(0);
    	Assertions.assertEquals(1, javaMethod.getTypeParameters().size());
    	JavaTypeVariable<JavaGenericDeclaration> typeVariable = javaMethod.getTypeParameters().get(0);
        Assertions.assertEquals("T", typeVariable.getName());
        Assertions.assertEquals("T", typeVariable.getFullyQualifiedName());
        Assertions.assertEquals("<T extends StringBuffer>", typeVariable.getGenericValue());
        Assertions.assertEquals("<T extends java.lang.StringBuffer>", typeVariable.getGenericFullyQualifiedName());
        Assertions.assertEquals("T", typeVariable.getValue());
    }

    @Test
    public void testComplexTypeVariable() {
    	String source  = "class Collections {\n" +
    			"public static <T, S extends T> void copy(List<T> dest, List<S> src){}\n" +
    			"}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaMethod javaMethod = javaSource.getClasses().get(0).getMethods().get(0);
    	JavaTypeVariable<JavaGenericDeclaration> typeVariable0 = javaMethod.getTypeParameters().get(0);
        Assertions.assertEquals("T", typeVariable0.getName());
        Assertions.assertEquals("T", typeVariable0.getFullyQualifiedName());
        Assertions.assertEquals("<T>", typeVariable0.getGenericValue());
        Assertions.assertEquals("<T>", typeVariable0.getGenericFullyQualifiedName());
        Assertions.assertEquals("T", typeVariable0.getValue());

        JavaTypeVariable<JavaGenericDeclaration> typeVariable1 = javaMethod.getTypeParameters().get(1);
        Assertions.assertEquals("S", typeVariable1.getName());
        Assertions.assertEquals("S", typeVariable1.getFullyQualifiedName());
        Assertions.assertEquals("<S extends T>", typeVariable1.getGenericValue());
        Assertions.assertEquals("<S extends T>", typeVariable1.getGenericFullyQualifiedName());
        Assertions.assertEquals("S", typeVariable1.getValue());
	}

    @Test
    public void testComplexTypeVariableMultipleBounds() {
    	String source = "class Collections\n" +
    			"public static <T extends Object & Comparable<? super T>>\n" +
    			"T max(Collection<? extends T> coll) {\n" +
    			"return null;}\n";
    	
    }
    
    //for qdox-150
    // second assert is based on java's Method.toString()
    // http://java.sun.com/j2se/1.5.0/docs/api/java/lang/reflect/Method.html#toString()
    // 3rd and 4th are resolved Types, based on <T extends StringBuffer> in method
    @Test
    public void testGenericMethodDeclarationSingleParameter() {
    	String source = "package com.thoughtworks.qdox;" +
    			"import java.util.*;\n" +
    			"public class TestQDOX150 {\n" +
    			" public <T extends StringBuffer> List<StringBuffer> myMethod( T request ) throws Exception {\n" +
    			"  return null;\n" +
    			" }\n" +
    			"}\n";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaMethod javaMethod = javaClass.getMethods().get(0);
    	JavaParameter paramType = javaMethod.getParameters().get(0);
    	JavaType returnType = javaMethod.getReturns();
    	Assertions.assertEquals("myMethod(request)", javaMethod.getCallSignature());
    	Assertions.assertEquals("public java.util.List com.thoughtworks.qdox.TestQDOX150.myMethod(java.lang.StringBuffer) throws java.lang.Exception", javaMethod.toString());
    	Assertions.assertEquals("StringBuffer", paramType.getResolvedValue());
        Assertions.assertEquals("java.lang.StringBuffer", paramType.getResolvedFullyQualifiedName());
    	Assertions.assertEquals("StringBuffer", paramType.getResolvedGenericValue());
    	Assertions.assertEquals("java.util.List", returnType.getFullyQualifiedName());
    	Assertions.assertEquals("java.util.List<java.lang.StringBuffer>", returnType.getGenericFullyQualifiedName());
        Assertions.assertEquals("List", returnType.getValue());
        Assertions.assertEquals("List<StringBuffer>", returnType.getGenericValue());
    	
    }

    @Test
    public void testGenericMethodDeclarationMultipleParameters() {
    	String source = "package com.thoughtworks.qdox;" +
    			"import java.util.*;\n" +
    			"public class TestQDOX150 {\n" +
    			" public <T extends StringBuffer> List<StringBuffer> myMethod( T request, List<T> list ) throws Exception {\n" +
    			"  return null;\n" +
    			" }\n" +
    			"}\n";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses().get(0);
    	JavaMethod javaMethod = javaClass.getMethods().get(0);
    	JavaParameter paramType = javaMethod.getParameters().get(1);
    	Assertions.assertEquals("myMethod(request, list)", javaMethod.getCallSignature());
    	Assertions.assertEquals("public java.util.List com.thoughtworks.qdox.TestQDOX150.myMethod(java.lang.StringBuffer,java.util.List) throws java.lang.Exception", javaMethod.toString());
    	Assertions.assertEquals("List", paramType.getResolvedValue());
        Assertions.assertEquals("java.util.List", paramType.getResolvedFullyQualifiedName());
        Assertions.assertEquals("List<StringBuffer>", paramType.getResolvedGenericValue());
        Assertions.assertEquals("java.util.List<java.lang.StringBuffer>", paramType.getResolvedGenericFullyQualifiedName());
    }

    //for QDOX-167
    @Test
    public void testGenericTypedMethodCall() {
        String source = "import java.util.*;\n" + 
        		"\n" + 
        		"public class MyClass\n" + 
        		"{\n" + 
        		"\n" + 
        		"    private static final Map<String, String> map1 = Collections.<String, String>emptyMap();\n" + 
        		"\n" + 
        		"    private static final Map<?, ?> map2 = Collections. <String, String> emptyMap();\n" + 
        		"\n" + 
        		"}";
        builder.addSource(new StringReader(source));
    }
    
    // For QDox-205
    @Test
    public void testClassTypeParameters() {
        String source1 = "class GenericControllerImpl<T, K, D extends GenericDAO<T, K>>\n" + 
        		"    implements GenericController<T, K>\n {}";
        String source2 = "class GroupControllerImpl extends\n" + 
        		"    GenericControllerImpl<Group, Long, GroupDAO>\n {}";
        String source3 = "interface GenericController<T, K> {}";
        JavaClass genericControllerImpl = builder.addSource(new StringReader(source1)).getClasses().get(0);
        JavaClass groupControllerImpl = builder.addSource(new StringReader(source2)).getClasses().get(0);
        JavaClass genericController = builder.addSource(new StringReader(source3)).getClasses().get(0);
        Assertions.assertEquals(3, genericControllerImpl.getTypeParameters().size());
        Assertions.assertEquals(0, groupControllerImpl.getTypeParameters().size());
        Assertions.assertEquals(2, genericController.getTypeParameters().size());
    }
    
    // For QDOX-206
    @Test
    public void testGenericsAndArrays() {
        JavaMethod method = buildMethod( "public Map<String[], Object[]> test(Map<String[], Object[]> input);" );
        Assertions.assertEquals("Map<java.lang.String[],java.lang.Object[]>", method.getReturns().toGenericString());
        Assertions.assertEquals("Map<java.lang.String[],java.lang.Object[]>", method.getParameters().get(0).getType().toGenericString());
    }
}
