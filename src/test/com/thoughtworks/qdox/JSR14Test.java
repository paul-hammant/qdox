package com.thoughtworks.qdox;

import java.io.StringReader;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;

import junit.framework.TestCase;
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
public class JSR14Test extends TestCase {
	
	private JavaDocBuilder builder = new JavaDocBuilder();

	public void testSimpleSingleParameterizedTypeMethod() throws Exception {
    	JavaMethod javaMethod = buildMethod("java.util.List<String> getList();");
    	assertEquals("java.util.List<java.lang.String>", javaMethod.getReturns().getGenericValue());
    }

	public void testSimpleWildcardTypeMethod() throws Exception {
    	JavaMethod javaMethod = buildMethod("java.util.List<?> getList();");
    	assertEquals("java.util.List<?>", javaMethod.getReturns().getGenericValue());
    }

	public void testSimpleExtendsWildcardTypeMethod() throws Exception {
    	JavaMethod javaMethod = buildMethod("java.util.List<? extends Number> getList();");
    	assertEquals("java.util.List<? extends java.lang.Number>", javaMethod.getReturns().getGenericValue());
    }
	
	public void testSimpleSuperWildcardTypeMethod() throws Exception {
    	JavaMethod javaMethod = buildMethod("java.util.List<? super Integer> getList();");
    	assertEquals("java.util.List<? super java.lang.Integer>", javaMethod.getReturns().getGenericValue());
    }

    public void testSimpleMultiParameterizedTypeMethod() throws Exception {
    	JavaMethod javaMethod = buildMethod("java.util.Map<String, Object> getMap();");
    	assertEquals("java.util.Map<java.lang.String,java.lang.Object>", javaMethod.getReturns().getGenericValue());
    }

    public void testComplexSingleParameterizedTypeMethod() throws Exception {
    	JavaMethod javaMethod = buildMethod("java.util.List<java.util.Set<String>> getList();");
    	assertEquals("java.util.List<java.util.Set<java.lang.String>>", javaMethod.getReturns().getGenericValue());
    }
    
    public void testComplexMultiParameterizedTypeMethod() throws Exception {
    	JavaMethod javaMethod = buildMethod("java.util.Map<String, java.util.Iterator<Number>> getMap();");
    	assertEquals("java.util.Map<java.lang.String,java.util.Iterator<java.lang.Number>>", javaMethod.getReturns().getGenericValue());
    }
    
    private JavaMethod buildMethod(String methodSource) {
        String source = "interface Something { " + methodSource + " }";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses()[0];
        JavaMethod javaMethod = javaClass.getMethods()[0];
        return javaMethod;
    }
    
    public void testSimpleSingleParameterizedTypeField() throws Exception {
        String source = "public class Something { " +
		"public java.util.List<String> aList;" + 
		" }";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses()[0];
        JavaField javaField = javaClass.getFieldByName("aList");
        assertEquals("java.util.List<java.lang.String>", javaField.getType().getGenericValue());
    }
    
    public void testSimpleMultiParameterizedTypeField() { 
        String source = "public class Something { " +
        		"public java.util.Map<String, Object> aMap;" + 
        		" }";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses()[0];
        JavaField javaField = javaClass.getFieldByName("aMap");
        assertEquals("java.util.Map<java.lang.String,java.lang.Object>", javaField.getType().getGenericValue());
    }
    
    public void testSimpleWildcardTypeField() throws Exception {
    	String source = "public class Something { " +
		"public java.util.List<?> aList;" + 
		" }";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses()[0];
    	JavaField javaField = javaClass.getFieldByName("aList");
    	assertEquals("java.util.List<?>", javaField.getType().getGenericValue());
    }

	public void testSimpleExtendsWildcardTypeField() throws Exception {
		String source = "public class Something { " +
		"public java.util.List<? extends Number> aList;" + 
		" }";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses()[0];
    	JavaField javaField = javaClass.getFieldByName("aList");
    	assertEquals("java.util.List<? extends java.lang.Number>", javaField.getType().getGenericValue());
    }
	
	public void testSimpleSuperWildcardTypeField() throws Exception {
		String source = "public class Something { " +
		"public java.util.List<? super Integer> aList;" + 
		" }";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses()[0];
    	JavaField javaField = javaClass.getFieldByName("aList");
    	assertEquals("java.util.List<? super java.lang.Integer>", javaField.getType().getGenericValue());
    }
	
	public void testComplexSingleParameterizedTypeField() throws Exception {
		String source = "public class Something { " +
		"public java.util.List<java.util.Set<String>> aList;" + 
		" }";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses()[0];
    	JavaField javaField = javaClass.getFieldByName("aList");
    	assertEquals("java.util.List<java.util.Set<java.lang.String>>", javaField.getType().getGenericValue());
    }
    
    public void testComplexMultiParameterizedTypeField() throws Exception {
		String source = "public class Something { " +
		"public java.util.List<java.util.Set<String>> aList;" + 
		" }";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses()[0];
    	JavaField javaField = javaClass.getFieldByName("aList");
    	assertEquals("java.util.List<java.util.Set<java.lang.String>>", javaField.getType().getGenericValue());
    }
    
    public void testSimpleSingleParameterizedTypeParameter() throws Exception {
    	JavaMethod javaMethod = buildMethod("void setList(java.util.List<String> aList);");
    	assertEquals("java.util.List<java.lang.String>", javaMethod.getParameterByName("aList").getType().getGenericValue());
    }

	public void testSimpleWildcardTypeParameter() throws Exception {
    	JavaMethod javaMethod = buildMethod("void setList(java.util.List<?> aList);");
    	assertEquals("java.util.List<?>", javaMethod.getParameterByName("aList").getType().getGenericValue());
    }

	public void testSimpleExtendsWildcardTypeParameter() throws Exception {
    	JavaMethod javaMethod = buildMethod("void setList(java.util.List<? extends Number> aList);");
    	assertEquals("java.util.List<? extends java.lang.Number>", javaMethod.getParameterByName("aList").getType().getGenericValue());
    }
	
	public void testSimpleSuperWildcardTypeParameter() throws Exception {
    	JavaMethod javaMethod = buildMethod("void setList(java.util.List<? super Integer> aList);");
    	assertEquals("java.util.List<? super java.lang.Integer>", javaMethod.getParameterByName("aList").getType().getGenericValue());
    }

    public void testSimpleMultiParameterizedTypeParameter() throws Exception {
    	JavaMethod javaMethod = buildMethod("void setMap(java.util.Map<String, Object> aMap);");
    	assertEquals("java.util.Map<java.lang.String,java.lang.Object>", javaMethod.getParameterByName("aMap").getType().getGenericValue());
    }

    public void testComplexSingleParameterizedTypeParameter() throws Exception {
    	JavaMethod javaMethod = buildMethod("void setList(java.util.List<java.util.Set<String>> aList);");
    	assertEquals("java.util.List<java.util.Set<java.lang.String>>", javaMethod.getParameterByName("aList").getType().getGenericValue());
    }
    
    public void testComplexMultiParameterizedTypeParameter() throws Exception {
    	JavaMethod javaMethod = buildMethod("void setMap(java.util.Map<String, java.util.Iterator<Number>> aMap);");
    	assertEquals("java.util.Map<java.lang.String,java.util.Iterator<java.lang.Number>>", javaMethod.getParameterByName("aMap").getType().getGenericValue());
    }
    
    public void testSimpleSingleParameterizedTypeClassExtends() throws Exception {
        String source = "public class Something extends java.util.List<String> {}";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses()[0];
        Type superClass = javaClass.getSuperClass();
        assertEquals("java.util.List<java.lang.String>", superClass.getGenericValue());
    }
    
    public void testSimpleMultiParameterizedTypeClassExtends() { 
        String source = "public class Something extends java.util.Map<String, Object> {}";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses()[0];
        Type superClass = javaClass.getSuperClass();
        assertEquals("java.util.Map<java.lang.String,java.lang.Object>", superClass.getGenericValue());
    }
    
    public void testSimpleWildcardTypeClassExtends() throws Exception {
    	String source = "public class Something extends java.util.List<?>{}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses()[0];
    	Type superClass = javaClass.getSuperClass();
    	assertEquals("java.util.List<?>", superClass.getGenericValue());
    }

	public void testSimpleExtendsWildcardTypeClassExtends() throws Exception {
		String source = "public class Something extends java.util.List<? extends Number> {}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses()[0];
    	Type superClass = javaClass.getSuperClass();
    	assertEquals("java.util.List<? extends java.lang.Number>", superClass.getGenericValue());
    }
	
	public void testSimpleSuperWildcardTypeClassExtends() throws Exception {
		String source = "public class Something extends java.util.List<? super Integer> {}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses()[0];
    	Type superClass = javaClass.getSuperClass();
    	assertEquals("java.util.List<? super java.lang.Integer>", superClass.getGenericValue());
    }
	
	public void testComplexSingleParameterizedTypeClassExtends() throws Exception {
		String source = "public class Something extends java.util.List<java.util.Set<String>> {}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses()[0];
    	Type superClass = javaClass.getSuperClass();
    	assertEquals("java.util.List<java.util.Set<java.lang.String>>", superClass.getGenericValue());
    }
    
    public void testComplexMultiParameterizedTypeClassExtends() throws Exception {
		String source = "public class Something extends java.util.List<java.util.Set<String>> {}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses()[0];
    	Type superClass = javaClass.getSuperClass();
    	assertEquals("java.util.List<java.util.Set<java.lang.String>>", superClass.getGenericValue());
    }
    
    public void testSimpleSingleParameterizedTypeClassImplements() throws Exception {
        String source = "public class Something implements java.util.List<String> {}";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses()[0];
        Type implementsClass = javaClass.getImplements()[0];
        assertEquals("java.util.List<java.lang.String>", implementsClass.getGenericValue());
    }
    
    public void testSimpleMultiParameterizedTypeClassImplements() { 
        String source = "public class Something implements java.util.Map<String, Object> {}";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses()[0];
        Type implementsClass = javaClass.getImplements()[0];
        assertEquals("java.util.Map<java.lang.String,java.lang.Object>", implementsClass.getGenericValue());
    }
    
    public void testSimpleWildcardTypeClassImplements() throws Exception {
    	String source = "public class Something implements java.util.List<?>{}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses()[0];
    	Type implementsClass = javaClass.getImplements()[0];
    	assertEquals("java.util.List<?>", implementsClass.getGenericValue());
    }

	public void testSimpleExtendsWildcardTypeClassImplements() throws Exception {
		String source = "public class Something implements java.util.List<? extends Number> {}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses()[0];
    	Type implementsClass = javaClass.getImplements()[0];
    	assertEquals("java.util.List<? extends java.lang.Number>", implementsClass.getGenericValue());
    }
	
	public void testSimpleSuperWildcardTypeClassImplements() throws Exception {
		String source = "public class Something implements java.util.List<? super Integer> {}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses()[0];
    	Type implementsClass = javaClass.getImplements()[0];
    	assertEquals("java.util.List<? super java.lang.Integer>", implementsClass.getGenericValue());
    }
	
	public void testComplexSingleParameterizedTypeClassImplements() throws Exception {
		String source = "public class Something implements java.util.List<java.util.Set<String>> {}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses()[0];
    	Type implementsClass = javaClass.getImplements()[0];
    	assertEquals("java.util.List<java.util.Set<java.lang.String>>", implementsClass.getGenericValue());
    }
    
    public void testComplexMultiParameterizedTypeClassImplements() throws Exception {
		String source = "public class Something implements java.util.List<java.util.Set<String>> {}";
    	JavaSource javaSource = builder.addSource(new StringReader(source));
    	JavaClass javaClass = javaSource.getClasses()[0];
    	Type implementsClass = javaClass.getImplements()[0];
    	assertEquals("java.util.List<java.util.Set<java.lang.String>>", implementsClass.getGenericValue());
    }
    
}
