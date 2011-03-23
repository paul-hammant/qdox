package com.thoughtworks.qdox.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import com.thoughtworks.qdox.library.ClassLibrary;
import com.thoughtworks.qdox.library.ClassNameLibrary;

public abstract class JavaMethodTest<M extends JavaMethod> extends TestCase {

    private M mth;
    private JavaSource source;
    private JavaClass clazz;

    public JavaMethodTest(String s) {
        super(s);
    }

    //constructors
    public abstract M newJavaMethod();
    public abstract M newJavaMethod(Type returns, String name);

    //setters
    public abstract void setExceptions(M method, List<Type> exceptions);
    public abstract void setComment(M method, String comment);
    public abstract void setConstructor(M method, boolean isConstructor);
    public abstract void setName(M method, String name);
    public abstract void setModifiers(M method, List<String> modifiers);
    public abstract void setReturns(M method, Type type);
    public abstract void setSourceCode(M method, String code);
    
    public abstract JavaClass newJavaClass();
    public abstract JavaClass newJavaClass(String fullname);
    public abstract JavaParameter newJavaParameter(Type type, String name);
    public abstract JavaParameter newJavaParameter(Type type, String name, boolean varArgs);
    public abstract JavaSource newJavaSource(ClassLibrary classLibrary );
    public abstract Type newType(String fullname);
    public abstract Type newType(String fullname, int dimensions);
    
    
    public abstract void addClass(JavaSource source, JavaClass clazz);
    public abstract void addMethod(JavaClass clazz, JavaMethod method);
    public abstract void addParameter(JavaMethod method, JavaParameter parameter);

    protected void setUp() throws Exception {
        source = newJavaSource(new ClassNameLibrary());
        clazz = newJavaClass();
        addClass(source, clazz);
        mth = newJavaMethod();
        addMethod(clazz, mth);
    }

    public void testDeclarationSignatureWithModifiers() {
        createSignatureTestMethod();
        String signature = mth.getDeclarationSignature(true);
        assertEquals("protected final void blah(int count, MyThing t) throws FishException, FruitException", signature);
    }

    public void testDeclarationSignatureWithoutModifiers() {
        createSignatureTestMethod();
        String signature = mth.getDeclarationSignature(false);
        assertEquals("void blah(int count, MyThing t) throws FishException, FruitException", signature);
    }

    public void testCallSignature() {
        createSignatureTestMethod();
        String signature = mth.getCallSignature();
        assertEquals("blah(count, t)", signature);
    }

    private void createSignatureTestMethod() {
        setName(mth, "blah");
        setModifiers(mth, Arrays.asList(new String[]{"protected", "final"}));
        setReturns(mth, newType("void"));
        setExceptions(mth, Arrays.asList( new Type[] {
            newType("FishException"),
            newType("FruitException"),
        } ));
        addParameter(mth, newJavaParameter(newType("int"), "count"));
        addParameter(mth, newJavaParameter(newType("MyThing"), "t"));
    }

//    public void testSignatureWithVarArgs() throws Exception {
//        mth.setName( "method" );
//        mth.addParameter( new JavaParameter(new Type("java.lang.String"), "param", true) );
//        assertEquals( mth, clazz.getMethodBySignature( "method", new Type[] { new Type("java.lang.String", true)} ) );
//    }
    
    public void testGetCodeBlockSimple() throws Exception {
        setName(mth, "doSomething");
        setReturns(mth, newType("java.lang.String"));
        assertEquals("java.lang.String doSomething();\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockOneParam() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        addParameter(mth, newJavaParameter(newType("String"), "thingy"));
        assertEquals("void blah(String thingy);\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockTwoParams() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        addParameter(mth, newJavaParameter(newType("int"), "count"));
        addParameter(mth, newJavaParameter(newType("MyThing"), "t"));
        assertEquals("void blah(int count, MyThing t);\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockThreeParams() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        addParameter(mth, newJavaParameter(newType("int"), "count"));
        addParameter(mth, newJavaParameter(newType("MyThing"), "t"));
        addParameter(mth, newJavaParameter(newType("java.lang.Meat"), "beef"));
        assertEquals("void blah(int count, MyThing t, java.lang.Meat beef);\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockModifiersWithAccessLevelFirst() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setModifiers(mth, Arrays.asList(new String[]{"synchronized", "public", "final"}));
        assertEquals("public synchronized final void blah();\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockOneException() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setExceptions(mth, Collections.singletonList( newType("RuntimeException") ));
        assertEquals("void blah() throws RuntimeException;\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockTwoException() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setExceptions(mth, Arrays.asList( new Type[]{newType("RuntimeException"), newType("java.lang.SheepException", 1)}));
        assertEquals("void blah() throws RuntimeException, java.lang.SheepException;\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockThreeException() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setExceptions(mth, Arrays.asList( new Type[]{newType("RuntimeException"), newType("java.lang.SheepException", 1), newType("CowException", 1)}));
        assertEquals("void blah() throws RuntimeException, java.lang.SheepException, CowException;\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockConstructor() throws Exception {
        setName(mth, "Blah");
        setModifiers(mth, Arrays.asList(new String[]{"public"}));
        setConstructor(mth, true);
        assertEquals("public Blah();\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockWithComment() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        setComment(mth, "Hello");
        String expect = ""
                + "/**\n"
                + " * Hello\n"
                + " */\n"
                + "void blah();\n";
        assertEquals(expect, mth.getCodeBlock());
    }

    public void testGetCodeBlock1dArray() throws Exception {
        setName(mth, "doSomething");
        setReturns(mth, newType("java.lang.String", 1));
        assertEquals("java.lang.String[] doSomething();\n", mth.getCodeBlock());
    }

    public void testGetCodeBlock2dArray() throws Exception {
        setName(mth, "doSomething");
        setReturns(mth, newType("java.lang.String", 2));
        assertEquals("java.lang.String[][] doSomething();\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockParamArray() throws Exception {
        setName(mth, "blah");
        setReturns(mth, newType("void"));
        addParameter(mth, newJavaParameter(newType("int", 2), "count"));
        addParameter(mth, newJavaParameter(newType("MyThing", 1), "t"));
        assertEquals("void blah(int[][] count, MyThing[] t);\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockWithBody() throws Exception {
        setName(mth, "doStuff");
        setReturns(mth, newType("java.lang.String"));
        setSourceCode(mth, "  int x = 2;\n  return STUFF;\n");

        assertEquals("" +
                "java.lang.String doStuff() {\n" +
                "  int x = 2;\n" +
                "  return STUFF;\n" +
                "}\n",
                mth.getCodeBlock());
    }
    
    public void testEquals() throws Exception {
        setName(mth, "thing");
        setReturns(mth, newType("void"));

        M m2 = newJavaMethod();
        setName(m2, "thing");
        setReturns(m2, newType("void"));

        M m3 = newJavaMethod();
        setName(m3, "thingy");
        setReturns(m3, newType("void"));

        M m4 = newJavaMethod();
        setName(m4, "thing");
        setReturns(m4, newType("int"));

        M c1 = newJavaMethod();
        setName(c1, "thing");
        setConstructor(c1, true);
        
        M c2 = newJavaMethod();
        setName(c2, "Thong");
        setConstructor(c2, true);
        
        M c3 = newJavaMethod();
        setName(c3, "Thong");
        setConstructor(c3, true);
        
        assertEquals(mth, m2);
        assertEquals(m2, mth);
        assertNotEquals(mth, m3);
        assertNotEquals(mth, m4);
        assertNotEquals(m4, c1);
        assertNotEquals(c1, c2);
        assertEquals(c2, c3);
        assertFalse(mth.equals(null));
    }

    public void testEqualsWithParameters() throws Exception {
        setName(mth, "thing");
        addParameter(mth, newJavaParameter(newType("int", 1), "blah"));
        addParameter(mth, newJavaParameter(newType("java.lang.String", 2), "thing"));
        addParameter(mth, newJavaParameter(newType("X", 3), ""));
        setReturns(mth, newType("void"));

        M m2 = newJavaMethod();
        setName(m2, "thing");
        addParameter(m2, newJavaParameter(newType("int", 1), "blah"));
        addParameter(m2, newJavaParameter(newType("java.lang.String", 2), "anotherName"));
        addParameter(m2, newJavaParameter(newType("X", 3), "blah"));
        setReturns(m2, newType("void"));

        M m3 = newJavaMethod();
        setName(m3, "thing");
        addParameter(m3, newJavaParameter(newType("int", 1), "blah"));
        addParameter(m3, newJavaParameter(newType("java.lang.String", 2), "thing"));
        setReturns(m3, newType("void"));

        M m4 = newJavaMethod();
        setName(m4, "thing");
        addParameter(m4, newJavaParameter(newType("int", 1), "blah"));
        addParameter(m4, newJavaParameter(newType("java.lang.String", 2), "thing"));
        addParameter(m4, newJavaParameter(newType("TTTTTTTT", 3), "blah")); //name
        setReturns(m4, newType("void"));

        M m5 = newJavaMethod();
        setName(m5, "thing");
        addParameter(m5, newJavaParameter(newType("int", 1), "blah"));
        addParameter(m5, newJavaParameter(newType("java.lang.String", 2), "thing"));
        addParameter(m5, newJavaParameter(newType("X", 9), "blah")); // dimension
        setReturns(m5, newType("void"));

        assertEquals(mth, m2);
        assertEquals(m2, mth);
        assertNotEquals(mth, m3);
        assertNotEquals(mth, m4);
        assertNotEquals(mth, m5);
    }

    public void testHashCode() throws Exception {
        setName(mth, "thing");
        addParameter(mth, newJavaParameter(newType("int", 1), "blah"));
        addParameter(mth, newJavaParameter(newType("java.lang.String", 2), "thing"));
        addParameter(mth, newJavaParameter(newType("X", 3), ""));
        setReturns(mth, newType("void"));

        M m2 = newJavaMethod();
        setName(m2, "thing");
        addParameter(m2, newJavaParameter(newType("int", 1), "blah"));
        addParameter(m2, newJavaParameter(newType("java.lang.String", 2), "anotherName"));
        addParameter(m2, newJavaParameter(newType("X", 3), "blah"));
        setReturns(m2, newType("void"));

        M m3 = newJavaMethod();
        setName(m3, "thing");
        addParameter(m3, newJavaParameter(newType("int", 1), "blah"));
        addParameter(m3, newJavaParameter(newType("java.lang.String", 2), "thing"));
        setReturns(m3, newType("void"));

        M c1 = newJavaMethod();
        setName(c1, "Thong");
        setConstructor(c1, true);
        
        M c2 = newJavaMethod();
        setName(c2, "Thong");
        setConstructor(c2, true);
        
        assertEquals(mth.hashCode(), m2.hashCode());
        assertTrue(mth.hashCode() != m3.hashCode());
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    public void testSignatureMatches() throws Exception {
        setName(mth, "thing");
        addParameter(mth, newJavaParameter(newType("int"), "x"));
        addParameter(mth, newJavaParameter(newType("long", 2), "y"));
        setReturns(mth, newType("void"));

        Type[] correctTypes = new Type[]{
            newType("int"),
            newType("long", 2)
        };

        Type[] wrongTypes1 = new Type[]{
            newType("int", 2),
            newType("long")
        };

        Type[] wrongTypes2 = new Type[]{
            newType("int"),
            newType("long", 2),
            newType("double")
        };

        assertTrue(mth.signatureMatches("thing", Arrays.asList( correctTypes )));
        assertFalse(mth.signatureMatches("xxx", Arrays.asList( correctTypes )));
        assertFalse(mth.signatureMatches("thing", Arrays.asList( wrongTypes1 )));
        assertFalse(mth.signatureMatches("thing", Arrays.asList( wrongTypes2 )));
    }
    
    public void testVarArgSignatureMatches() throws Exception {
        setName(mth, "thing");
        addParameter(mth, newJavaParameter(newType("int"), "x"));
        addParameter(mth, newJavaParameter(newType("long", 2), "y", true));
        setReturns(mth, newType("void"));

        Type[] correctTypes = new Type[]{
            newType("int"),
            newType("long", 2)
        };

        Type[] wrongTypes1 = new Type[]{
            newType("int", 2),
            newType("long")
        };

        Type[] wrongTypes2 = new Type[]{
            newType("int"),
            newType("long", 2),
            newType("double")
        };

        assertTrue(mth.signatureMatches("thing", Arrays.asList( correctTypes ), true));
        assertFalse(mth.signatureMatches("thing", Arrays.asList( correctTypes ), false));
        assertFalse(mth.signatureMatches("xxx", Arrays.asList( correctTypes ), true));
        assertFalse(mth.signatureMatches("thing", Arrays.asList( wrongTypes1 ), true));
        assertFalse(mth.signatureMatches("thing", Arrays.asList( wrongTypes2 ), true));
    }

    public void testParentClass() throws Exception {
        assertSame(clazz, mth.getParentClass());
    }

    public void testCanGetParameterByName() throws Exception {
        JavaParameter paramX = newJavaParameter(newType("int"), "x");
        addParameter(mth, paramX);
        addParameter(mth, newJavaParameter(newType("string"), "y"));
        
        assertEquals(paramX, mth.getParameterByName("x"));
        assertEquals(null, mth.getParameterByName("z"));
    }

    public void testToString() throws Exception {
    	JavaClass cls = newJavaClass("java.lang.Object");
    	M mthd = newJavaMethod(newType("boolean"),"equals");
    	addMethod(cls, mthd);
    	setModifiers(mthd, Arrays.asList(new String[]{"public"}));
    	addParameter(mthd, newJavaParameter(newType("java.lang.Object"), null));
    	assertEquals("public boolean java.lang.Object.equals(java.lang.Object)", mthd.toString());
    }
    
    public void testConstructorToString() throws Exception {
        JavaClass cls = newJavaClass("a.b.Executor");
        M constructor = newJavaMethod(null,"Executor");
        setConstructor( constructor, true );
        addMethod(cls, constructor);
        assertEquals("a.b.Executor()", constructor.toString());
    }

    public void testConstructorReturnType() throws Exception {
        M constructor = newJavaMethod(null,"Executor");
        setConstructor( constructor, true );
        assertEquals(null, constructor.getReturnType());
    }

    public void testConstructorParameterTypes() throws Exception {
        JavaClass cls = newJavaClass("a.b.Executor");
        M constructor = newJavaMethod(null,"Executor");
        addParameter( constructor,  newJavaParameter( newType("a.b.C"), "param" ) );
        setConstructor( constructor, true );
        addMethod(cls, constructor);
        assertEquals("a.b.C", constructor.getParameterTypes().get(0).toString());
    }

    private void assertNotEquals(Object o1, Object o2) {
        assertTrue(o1.toString() + " should not equals " + o2.toString(), !o1.equals(o2));
    }
}
