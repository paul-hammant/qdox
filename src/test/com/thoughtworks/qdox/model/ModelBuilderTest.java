package com.thoughtworks.qdox.model;

import junit.framework.TestCase;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;

import java.util.Arrays;

public class ModelBuilderTest extends TestCase {

	private ModelBuilder builder;

	public ModelBuilderTest(String s) {
		super(s);
	}

	protected void setUp() throws Exception {
		super.setUp();
		builder = new ModelBuilder();
	}

	public void testNumberOfClassesGrows() throws Exception {
		assertEquals(0, builder.getSource().getClasses().length);
    	builder.beginClass(new ClassDef());
    	builder.endClass();
		assertEquals(1, builder.getSource().getClasses().length);
		builder.beginClass(new ClassDef());
    	builder.endClass();
		assertEquals(2, builder.getSource().getClasses().length);
	}

	public void testSimpleClass() throws Exception {
		ClassDef cls = new ClassDef();
		cls.name = "Thingy";
    	builder.beginClass(cls);
    	builder.endClass();

		ClassDef cls2 = new ClassDef();
		cls2.name = "ThingyThing";
    	builder.beginClass(cls2);
    	builder.endClass();

		JavaSource source = builder.getSource();

		assertEquals("Thingy", source.getClasses()[0].getName());
		assertEquals("ThingyThing", source.getClasses()[1].getName());
		assertEquals(source, source.getClasses()[0].getParentSource());
	}
	
	public void testInterface() throws Exception {
		ClassDef cls = new ClassDef();
    	builder.beginClass(cls);
    	builder.endClass();

		ClassDef cls2 = new ClassDef();
		cls2.isInterface = true;
    	builder.beginClass(cls2);
    	builder.endClass();
		
		JavaSource source = builder.getSource();

		assertEquals(false, source.getClasses()[0].isInterface());
		assertEquals(true, source.getClasses()[1].isInterface());
	}

	public void testClassExtends() throws Exception {
		ClassDef cls = new ClassDef();
    	builder.beginClass(cls);
    	builder.endClass();

		ClassDef cls2 = new ClassDef();
		cls2.extendz.add("Another");
    	builder.beginClass(cls2);
    	builder.endClass();
    	builder.addImport("com.thoughtworks.*");

		JavaSource source = builder.getSource();

		assertEquals("java.lang.Object", source.getClasses()[0].getSuperClass().getValue());
		assertEquals("Another", source.getClasses()[1].getSuperClass().getValue());

		assertEquals(0, source.getClasses()[0].getImplements().length);
		assertEquals(0, source.getClasses()[1].getImplements().length);

		//Add another class and see if Another gets resolved
		builder.addPackage("com.thoughtworks");
		ClassDef anotherCls = new ClassDef();
		anotherCls.name = "Another";		
		builder.beginClass(anotherCls);
    	builder.endClass();
		
		assertEquals("com.thoughtworks.Another", source.getClasses()[1].getSuperClass().getValue());
	}

	public void testInterfaceExtends() throws Exception {
		ClassDef cls = new ClassDef();
		cls.isInterface = true;
    	builder.beginClass(cls);
    	builder.endClass();

		ClassDef cls2 = new ClassDef();
		cls2.isInterface = true;
		cls2.extendz.add("Another");
    	builder.beginClass(cls2);
    	builder.endClass();

		JavaSource source = builder.getSource();

		assertEquals(0, source.getClasses()[0].getImplements().length);
		assertEquals(1, source.getClasses()[1].getImplements().length);
		assertEquals("Another", source.getClasses()[1].getImplements()[0].getValue());

		assertNull(source.getClasses()[0].getSuperClass());
		assertNull(source.getClasses()[1].getSuperClass());
	}

	public void testInterfaceExtendsMultiple() throws Exception {
		ClassDef cls = new ClassDef();
		cls.isInterface = true;
		cls.extendz.add("Another");
		cls.extendz.add("java.io.Serializable");
		cls.extendz.add("BottleOpener");
    	builder.beginClass(cls);
    	builder.endClass();

		JavaSource source = builder.getSource();

		// sorted
		Arrays.sort(source.getClasses()[0].getImplements());
		assertEquals(3, source.getClasses()[0].getImplements().length);
		assertEquals("Another", source.getClasses()[0].getImplements()[0].getValue());
		assertEquals("BottleOpener", source.getClasses()[0].getImplements()[1].getValue());
		assertEquals("java.io.Serializable", source.getClasses()[0].getImplements()[2].getValue());

		assertNull(source.getClasses()[0].getSuperClass());
	}

	public void testClassImplements() throws Exception {
		ClassDef cls = new ClassDef();
    	builder.beginClass(cls);
    	builder.endClass();

		ClassDef cls2 = new ClassDef();
		cls2.implementz.add("SomeInterface");
    	builder.beginClass(cls2);
    	builder.endClass();

		JavaSource source = builder.getSource();

		assertEquals(0, source.getClasses()[0].getImplements().length);
		assertEquals(1, source.getClasses()[1].getImplements().length);

		assertEquals("SomeInterface", source.getClasses()[1].getImplements()[0].getValue());

		assertEquals("java.lang.Object", source.getClasses()[0].getSuperClass().getValue());
		assertEquals("java.lang.Object", source.getClasses()[1].getSuperClass().getValue());
	}

	public void testClassImplementsMultiple() throws Exception {
		ClassDef cls = new ClassDef();
		cls.implementz.add("SomeInterface");
		cls.implementz.add("XX");
    	builder.beginClass(cls);
    	builder.endClass();

		JavaSource source = builder.getSource();

		assertEquals(2, source.getClasses()[0].getImplements().length);

		Arrays.sort(source.getClasses()[0].getImplements());
		assertEquals("SomeInterface", source.getClasses()[0].getImplements()[0].getValue());
		assertEquals("XX", source.getClasses()[0].getImplements()[1].getValue());
	}

	public void testClassExtendsAndImplements() throws Exception {
		ClassDef cls = new ClassDef();
		cls.extendz.add("SubClass");
		cls.implementz.add("SomeInterface");
		cls.implementz.add("XX");
    	builder.beginClass(cls);
    	builder.endClass();

		JavaSource source = builder.getSource();

		assertEquals(2, source.getClasses()[0].getImplements().length);

		Arrays.sort(source.getClasses()[0].getImplements());
		assertEquals("SomeInterface", source.getClasses()[0].getImplements()[0].getValue());
		assertEquals("XX", source.getClasses()[0].getImplements()[1].getValue());

		assertEquals("SubClass", source.getClasses()[0].getSuperClass().getValue());
	}

	public void testClassModifiers() throws Exception {
    	builder.beginClass(new ClassDef());
    	builder.endClass();

		ClassDef cls2 = new ClassDef();
		cls2.modifiers.add("public");
		cls2.modifiers.add("final");
    	builder.beginClass(cls2);
    	builder.endClass();

		JavaSource source = builder.getSource();

		assertEquals(0, source.getClasses()[0].getModifiers().length);
		assertEquals(2, source.getClasses()[1].getModifiers().length);

		// sorted
		String[] modifiers = source.getClasses()[1].getModifiers();
		Arrays.sort(modifiers);
		assertEquals("final", modifiers[0]);
		assertEquals("public", modifiers[1]);
	}

	public void testAddMethodsToCorrectClass() throws Exception {
		builder.beginClass(new ClassDef());
		builder.addMethod(new MethodDef());
		builder.endClass();
		
		builder.beginClass(new ClassDef());
		builder.addMethod(new MethodDef());
		builder.addMethod(new MethodDef());
		builder.addMethod(new MethodDef());
		builder.endClass();

		builder.beginClass(new ClassDef());
		builder.addMethod(new MethodDef());
		builder.addMethod(new MethodDef());
		builder.endClass();

		JavaSource source = builder.getSource();
		assertEquals(1, source.getClasses()[0].getMethods().length);
		assertEquals(3, source.getClasses()[1].getMethods().length);
		assertEquals(2, source.getClasses()[2].getMethods().length);
	}

	public void testInnerClass() throws Exception {
		builder.addPackage("xyz");
		
		ClassDef outerDef = new ClassDef();
		outerDef.name = "Outer";
		builder.beginClass(outerDef);

		ClassDef innerDef = new ClassDef();
		innerDef.name = "Inner";
		builder.beginClass(innerDef);
		
		MethodDef fooDef = new MethodDef();
		fooDef.name = "foo";
		builder.addMethod(fooDef);
		builder.endClass();
		
		MethodDef barDef = new MethodDef();
		barDef.name = "bar";
		builder.addMethod(barDef);
		builder.endClass();
		
		JavaSource source = builder.getSource();
		assertEquals(1, source.getClasses().length);
		JavaClass outerClass = source.getClasses()[0];
		assertEquals("xyz.Outer", outerClass.getFullyQualifiedName());
		assertEquals(1, outerClass.getMethods().length);
		assertEquals("bar", outerClass.getMethods()[0].getName());
		assertEquals(1, outerClass.getClasses().length);
		JavaClass innerClass = outerClass.getClasses()[0];
		assertEquals("xyz.Outer.Inner", innerClass.getFullyQualifiedName());
		assertEquals(1, innerClass.getMethods().length);
		assertEquals("foo", innerClass.getMethods()[0].getName());
	}

	public void testSimpleMethod() throws Exception {
		builder.beginClass(new ClassDef());
		MethodDef mth = new MethodDef();
		mth.name = "doSomething";
		mth.returns = "void";
		builder.addMethod(mth);
		builder.endClass();

		JavaSource source = builder.getSource();
		JavaMethod result = source.getClasses()[0].getMethods()[0];
		assertEquals("doSomething", result.getName());
		assertEquals("void", result.getReturns().getValue());
		assertEquals(source, result.getReturns().getParentSource());
		assertEquals(0, result.getModifiers().length);
		assertEquals(0, result.getParameters().length);
		assertEquals(0, result.getExceptions().length);
	}

	public void testMethodNoArray() throws Exception {
		builder.beginClass(new ClassDef());
		MethodDef mth = new MethodDef();
		mth.name = "doSomething";
		mth.returns = "void";
		mth.dimensions = 0;
		builder.addMethod(mth);
		builder.endClass();

		JavaSource source = builder.getSource();
		JavaMethod result = source.getClasses()[0].getMethods()[0];
		assertEquals(0, result.getReturns().getDimensions());
	}

	public void testMethod1dArray() throws Exception {
		builder.beginClass(new ClassDef());
		MethodDef mth = new MethodDef();
		mth.name = "doSomething";
		mth.returns = "void";
		mth.dimensions = 1;
		builder.addMethod(mth);
		builder.endClass();

		JavaSource source = builder.getSource();
		JavaMethod result = source.getClasses()[0].getMethods()[0];
		assertEquals(1, result.getReturns().getDimensions());
	}

	public void testMethod2dArray() throws Exception {
		builder.beginClass(new ClassDef());
		MethodDef mth = new MethodDef();
		mth.name = "doSomething";
		mth.returns = "void";
		mth.dimensions = 2;
		builder.addMethod(mth);
		builder.endClass();

		JavaSource source = builder.getSource();
		JavaMethod result = source.getClasses()[0].getMethods()[0];
		assertEquals(2, result.getReturns().getDimensions());
	}

	public void testMethodParameters() throws Exception {
		builder.beginClass(new ClassDef());
		MethodDef mth = new MethodDef();

		FieldDef f1 = new FieldDef();
		f1.name = "count";
		f1.type = "int";
		f1.modifiers.add("final");
		mth.params.add(f1);

		FieldDef f2 = new FieldDef();
		f2.name = "name";
		f2.type = "String";
		mth.params.add(f2);

		builder.addMethod(mth);
		builder.endClass();
		
		JavaSource source = builder.getSource();
		JavaMethod result = source.getClasses()[0].getMethods()[0];
		assertEquals(2, result.getParameters().length);
		assertEquals("count", result.getParameters()[0].getName());
		assertEquals("int", result.getParameters()[0].getType().getValue());
		assertEquals("name", result.getParameters()[1].getName());
		assertEquals("String", result.getParameters()[1].getType().getValue());
	}

	public void testMethodParametersWithArrays() throws Exception {
		builder.beginClass(new ClassDef());
		MethodDef mth = new MethodDef();

		FieldDef f1 = new FieldDef();
		f1.name = "count";
		f1.type = "int";
		f1.modifiers.add("final");
		f1.dimensions = 1;
		mth.params.add(f1);

		FieldDef f2 = new FieldDef();
		f2.name = "name";
		f2.type = "String";
		f2.dimensions = 2;
		mth.params.add(f2);

		builder.addMethod(mth);
		builder.endClass();

		JavaSource source = builder.getSource();
		JavaMethod result = source.getClasses()[0].getMethods()[0];
		assertEquals(1, result.getParameters()[0].getType().getDimensions());
		assertEquals(2, result.getParameters()[1].getType().getDimensions());
	}

	public void testMethodExceptions() throws Exception {
		builder.beginClass(new ClassDef());
		MethodDef mth = new MethodDef();

		mth.exceptions.add("RuntimeException");
		mth.exceptions.add("java.io.IOException");

		builder.addMethod(mth);
		builder.endClass();
		
		JavaSource source = builder.getSource();
		JavaMethod result = source.getClasses()[0].getMethods()[0];
		assertEquals(2, result.getExceptions().length);
		// sorted
		Arrays.sort(result.getExceptions());
		assertEquals("RuntimeException", result.getExceptions()[0].getValue());
		assertEquals("java.io.IOException", result.getExceptions()[1].getValue());
	}

	public void testMethodModifiers() throws Exception {
		builder.beginClass(new ClassDef());
		MethodDef mth = new MethodDef();

		mth.modifiers.add("public");
		mth.modifiers.add("final");
		mth.modifiers.add("synchronized");

		builder.addMethod(mth);
		builder.endClass();

		JavaSource source = builder.getSource();
		JavaMethod result = source.getClasses()[0].getMethods()[0];
		assertEquals(3, result.getModifiers().length);
		// sorted
		String[] modifiers = result.getModifiers();
		Arrays.sort(modifiers);
		assertEquals("final", modifiers[0]);
		assertEquals("public", modifiers[1]);
		assertEquals("synchronized", modifiers[2]);
	}

	public void testSimpleField() throws Exception {
		builder.beginClass(new ClassDef());

		FieldDef fld = new FieldDef();
		fld.name = "count";
		fld.type = "int";
		builder.addField(fld);
		builder.endClass();

		JavaSource source = builder.getSource();

		JavaField result = source.getClasses()[0].getFields()[0];
		assertNotNull(result);
		assertEquals("count", result.getName());
		assertEquals("int", result.getType().getValue());

	}

	public void testFieldWithModifiers() throws Exception {
		builder.beginClass(new ClassDef());

		FieldDef fld = new FieldDef();
		fld.modifiers.add("blah");
		fld.modifiers.add("blah2");
		builder.addField(fld);
		builder.endClass();

		JavaSource source = builder.getSource();

		JavaField result = source.getClasses()[0].getFields()[0];
		assertNotNull(result);
		assertNotNull(result.getModifiers());
		Arrays.sort(result.getModifiers());
		assertEquals("blah2", result.getModifiers()[0]);
		assertEquals("blah", result.getModifiers()[1]);
	}

	public void testFieldNoArray() throws Exception {
		builder.beginClass(new ClassDef());

		FieldDef fld = new FieldDef();
		fld.name = "count";
		fld.type = "int";
		fld.dimensions = 0;
		builder.addField(fld);
		builder.endClass();

		JavaSource source = builder.getSource();

		JavaField result = source.getClasses()[0].getFields()[0];
		assertEquals(0, result.getType().getDimensions());

	}

	public void testField1dArray() throws Exception {
		builder.beginClass(new ClassDef());

		FieldDef fld = new FieldDef();
		fld.name = "count";
		fld.type = "int";
		fld.dimensions = 1;
		builder.addField(fld);
		builder.endClass();

		JavaSource source = builder.getSource();

		JavaField result = source.getClasses()[0].getFields()[0];
		assertEquals(1, result.getType().getDimensions());

	}

	public void testField2dArray() throws Exception {
		builder.beginClass(new ClassDef());

		FieldDef fld = new FieldDef();
		fld.name = "count";
		fld.type = "int";
		fld.dimensions = 2;
		builder.addField(fld);
		builder.endClass();

		JavaSource source = builder.getSource();

		JavaField result = source.getClasses()[0].getFields()[0];
		assertEquals(2, result.getType().getDimensions());
	}

	public void testSimpleConstructor() throws Exception {
		builder.beginClass(new ClassDef());

		MethodDef mth = new MethodDef();
		mth.name = "MyClass";
		mth.constructor = true;
		builder.addMethod(mth);

		MethodDef mth2 = new MethodDef();
		mth2.name = "method";
		mth2.returns = "void";
		builder.addMethod(mth2);
		builder.endClass();

		JavaSource source = builder.getSource();

		JavaMethod result1 = source.getClasses()[0].getMethods()[0];
		JavaMethod result2 = source.getClasses()[0].getMethods()[1];

		assertTrue(result1.isConstructor());
		assertNull(result1.getReturns());
		assertTrue(!result2.isConstructor());
		assertNotNull(result2.getReturns());
	}

	public void testJavaDocOnClass() throws Exception {
		builder.addJavaDoc("Hello");
		builder.beginClass(new ClassDef());
		builder.endClass();

		JavaSource source = builder.getSource();
		assertEquals("Hello", source.getClasses()[0].getComment());
	}

	public void testJavaDocSpiradiclyOnManyClasses() throws Exception {

		builder.addJavaDoc("Hello");
		builder.beginClass(new ClassDef());
		builder.endClass();

		builder.beginClass(new ClassDef());
		builder.endClass();

		builder.addJavaDoc("World");
		builder.beginClass(new ClassDef());
		builder.endClass();

		JavaSource source = builder.getSource();

		assertEquals("Hello", source.getClasses()[0].getComment());
		assertNull(source.getClasses()[1].getComment());
		assertEquals("World", source.getClasses()[2].getComment());
	}

	public void testJavaDocOnMethod() throws Exception {
		builder.beginClass(new ClassDef());

		builder.addJavaDoc("Hello");
		builder.addMethod(new MethodDef());
		builder.endClass();

		JavaSource source = builder.getSource();

		assertNull(source.getClasses()[0].getComment());
		assertEquals("Hello", source.getClasses()[0].getMethods()[0].getComment());
	}

	public void testJavaDocOnField() throws Exception {
		builder.beginClass(new ClassDef());

		builder.addJavaDoc("Hello");
		builder.addField(new FieldDef());
		builder.endClass();

		JavaSource source = builder.getSource();

		assertNull(source.getClasses()[0].getComment());
		assertEquals("Hello", source.getClasses()[0].getFields()[0].getComment());
	}

	public void testJavaDocOnMethodsAndFields() throws Exception {
		builder.addJavaDoc("Thing");
		builder.beginClass(new ClassDef());

		builder.addField(new FieldDef());// f0

		builder.addJavaDoc("Hello");
		builder.addMethod(new MethodDef());//m0

		builder.addJavaDoc("Hello field");
		builder.addField(new FieldDef());//f1

		builder.addMethod(new MethodDef());//m1

		builder.addJavaDoc("World");
		builder.addMethod(new MethodDef());//m2

		builder.endClass();

		JavaSource source = builder.getSource();

		assertEquals("Thing", source.getClasses()[0].getComment());
		assertNull(source.getClasses()[0].getFields()[0].getComment());
		assertEquals("Hello field", source.getClasses()[0].getFields()[1].getComment());
		assertEquals("Hello", source.getClasses()[0].getMethods()[0].getComment());
		assertNull(source.getClasses()[0].getMethods()[1].getComment());
		assertEquals("World", source.getClasses()[0].getMethods()[2].getComment());
	}

	public void testDocletTag() throws Exception {
		builder.addJavaDoc("Hello");
		builder.addJavaDocTag("cheese", "is good");
		builder.beginClass(new ClassDef());

		builder.endClass();

		JavaSource source = builder.getSource();

		assertEquals("Hello", source.getClasses()[0].getComment());
		assertEquals(1, source.getClasses()[0].getTags().length);
		assertEquals("cheese", source.getClasses()[0].getTags()[0].getName());
		assertEquals("is good", source.getClasses()[0].getTags()[0].getValue());
	}

	public void testDocletTagWithNoComment() throws Exception {
		builder.addJavaDoc(""); // parser will always call this method to signify start of javadoc
		builder.addJavaDocTag("cheese", "is good");
		builder.beginClass(new ClassDef());

		builder.endClass();

		JavaSource source = builder.getSource();

		assertEquals("", source.getClasses()[0].getComment());
		assertEquals(1, source.getClasses()[0].getTags().length);
		assertEquals("cheese", source.getClasses()[0].getTags()[0].getName());
		assertEquals("is good", source.getClasses()[0].getTags()[0].getValue());
	}

	public void testMultipleDocletTags() throws Exception {
		builder.addJavaDoc("Hello");
		builder.addJavaDocTag("cheese", "is good");
		builder.addJavaDocTag("food", "is great");
		builder.addJavaDocTag("chairs", "are boring");
		builder.beginClass(new ClassDef());

		builder.endClass();

		JavaSource source = builder.getSource();

		assertEquals("Hello", source.getClasses()[0].getComment());
		assertEquals(3, source.getClasses()[0].getTags().length);
		assertEquals("cheese", source.getClasses()[0].getTags()[0].getName());
		assertEquals("is good", source.getClasses()[0].getTags()[0].getValue());
		assertEquals("food", source.getClasses()[0].getTags()[1].getName());
		assertEquals("is great", source.getClasses()[0].getTags()[1].getValue());
		assertEquals("chairs", source.getClasses()[0].getTags()[2].getName());
		assertEquals("are boring", source.getClasses()[0].getTags()[2].getValue());
	}

	public void testDocletTagsOnMethodsAndFields() throws Exception {
		builder.addJavaDoc("");
		builder.addJavaDocTag("cheese", "is good");
		builder.beginClass(new ClassDef());

		builder.addJavaDoc("");
		builder.addJavaDocTag("food", "is great");
		builder.addMethod(new MethodDef());

		builder.addJavaDoc("");
		builder.addJavaDocTag("chairs", "are boring");
		builder.addField(new FieldDef());
		builder.endClass();

		JavaSource source = builder.getSource();

		assertEquals("cheese", source.getClasses()[0].getTags()[0].getName());
		assertEquals("is good", source.getClasses()[0].getTags()[0].getValue());
		assertEquals("food", source.getClasses()[0].getMethods()[0].getTags()[0].getName());
		assertEquals("is great", source.getClasses()[0].getMethods()[0].getTags()[0].getValue());
		assertEquals("chairs", source.getClasses()[0].getFields()[0].getTags()[0].getName());
		assertEquals("are boring", source.getClasses()[0].getFields()[0].getTags()[0].getValue());
	}

	public void testRetrieveJavaSource() throws Exception {
		builder.beginClass(new ClassDef());
		builder.endClass();

		JavaSource source = builder.getSource();
		assertNotNull(source);
	}

	public void testJavaSourceClassCount() throws Exception {
		builder.beginClass(new ClassDef());
		builder.endClass();
		builder.beginClass(new ClassDef());
		builder.endClass();
		builder.beginClass(new ClassDef());
		builder.endClass();
		JavaSource result = builder.getSource();
		assertEquals(3, result.getClasses().length);
	}

	public void testJavaSourceNoPackage() throws Exception {
		JavaSource result = builder.getSource();
		assertNull(result.getPackage());
	}

	public void testJavaSourceWithPackage() throws Exception {
		builder.addPackage("com.blah.thing");
		JavaSource result = builder.getSource();
		assertEquals("com.blah.thing", result.getPackage());
	}

	public void testJavaSourceNoImports() throws Exception {
		JavaSource result = builder.getSource();
		assertEquals(0, result.getImports().length);
	}

	public void testJavaSourceOneImport() throws Exception {
		builder.addImport("com.blah.Thing");
		JavaSource result = builder.getSource();
		assertEquals(1, result.getImports().length);
		assertEquals("com.blah.Thing", result.getImports()[0]);
	}

	public void testJavaSourceMultipleImports() throws Exception {
		builder.addImport("com.blah.Thing");
		builder.addImport("java.util.List");
		builder.addImport("org.apache.*");
		JavaSource result = builder.getSource();
		assertEquals(3, result.getImports().length);
		assertEquals("com.blah.Thing", result.getImports()[0]);
		assertEquals("java.util.List", result.getImports()[1]);
		assertEquals("org.apache.*", result.getImports()[2]);
	}

    public void testModelHashCodes() {

        ClassDef classDef = new ClassDef();
        assertTrue(classDef.hashCode() == 0);
        classDef.name = "hello";
        assertTrue(classDef.hashCode() > 0);

        MethodDef methodDef = new MethodDef();
        methodDef.name = "hello";
        assertTrue(methodDef.hashCode() > 0);

        FieldDef fieldDef = new FieldDef();
        fieldDef.name = "hello";
        assertTrue(fieldDef.hashCode() > 0);

        JavaParameter javaParameter = new JavaParameter(new Type("q",0), "w");
        assertTrue(javaParameter.hashCode() > 0);

    }


    public void testType() {

        Type type1 = new Type("fred",1);
        Type type2 = new Type("fred",1);
        Type type3 = new Type("wilma",2);
        assertTrue(type1.compareTo(type2) == 0);
        assertFalse(type1.compareTo(type3) == 0);
        assertTrue(type1.compareTo("barney") == 0);
    }


}
