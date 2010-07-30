package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

public class JavaMethodTest extends TestCase {

    private JavaMethod mth;
    private JavaSource source;
    private JavaClass clazz;

    public JavaMethodTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        super.setUp();
        source = new JavaSource();
        clazz = new JavaClass();
        source.addClass(clazz);
        mth = new JavaMethod();
        clazz.addMethod(mth);
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
        mth.setName("blah");
        mth.setModifiers(new String[]{"protected", "final"});
        mth.setReturns(new Type("void"));
        mth.setExceptions(new Type[] {
            new Type("FishException"),
            new Type("FruitException"),
        });
        mth.addParameter(new JavaParameter(new Type("int"), "count"));
        mth.addParameter(new JavaParameter(new Type("MyThing"), "t"));
    }

//    public void testSignatureWithVarArgs() throws Exception {
//        mth.setName( "method" );
//        mth.addParameter( new JavaParameter(new Type("java.lang.String"), "param", true) );
//        assertEquals( mth, clazz.getMethodBySignature( "method", new Type[] { new Type("java.lang.String", true)} ) );
//    }
    
    public void testGetCodeBlockSimple() throws Exception {
        mth.setName("doSomething");
        mth.setReturns(new Type("java.lang.String"));
        assertEquals("java.lang.String doSomething();\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockOneParam() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.addParameter(new JavaParameter(new Type("String"), "thingy"));
        assertEquals("void blah(String thingy);\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockTwoParams() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.addParameter(new JavaParameter(new Type("int"), "count"));
        mth.addParameter(new JavaParameter(new Type("MyThing"), "t"));
        assertEquals("void blah(int count, MyThing t);\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockThreeParams() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.addParameter(new JavaParameter(new Type("int"), "count"));
        mth.addParameter(new JavaParameter(new Type("MyThing"), "t"));
        mth.addParameter(new JavaParameter(new Type("java.lang.Meat"), "beef"));
        assertEquals("void blah(int count, MyThing t, java.lang.Meat beef);\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockModifiersWithAccessLevelFirst() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.setModifiers(new String[]{"synchronized", "public", "final"});
        assertEquals("public synchronized final void blah();\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockOneException() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.setExceptions(new Type[]{new Type("RuntimeException")});
        assertEquals("void blah() throws RuntimeException;\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockTwoException() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.setExceptions(new Type[]{new Type("RuntimeException"), new Type("java.lang.SheepException", 1)});
        assertEquals("void blah() throws RuntimeException, java.lang.SheepException;\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockThreeException() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.setExceptions(new Type[]{new Type("RuntimeException"), new Type("java.lang.SheepException", 1), new Type("CowException", 1)});
        assertEquals("void blah() throws RuntimeException, java.lang.SheepException, CowException;\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockConstructor() throws Exception {
        mth.setName("Blah");
        mth.setModifiers(new String[]{"public"});
        mth.setConstructor(true);
        assertEquals("public Blah();\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockWithComment() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.setComment("Hello");
        String expect = ""
                + "/**\n"
                + " * Hello\n"
                + " */\n"
                + "void blah();\n";
        assertEquals(expect, mth.getCodeBlock());
    }

    public void testGetCodeBlock1dArray() throws Exception {
        mth.setName("doSomething");
        mth.setReturns(new Type("java.lang.String", 1));
        assertEquals("java.lang.String[] doSomething();\n", mth.getCodeBlock());
    }

    public void testGetCodeBlock2dArray() throws Exception {
        mth.setName("doSomething");
        mth.setReturns(new Type("java.lang.String", 2));
        assertEquals("java.lang.String[][] doSomething();\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockParamArray() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.addParameter(new JavaParameter(new Type("int", 2), "count"));
        mth.addParameter(new JavaParameter(new Type("MyThing", 1), "t"));
        assertEquals("void blah(int[][] count, MyThing[] t);\n", mth.getCodeBlock());
    }

    public void testGetCodeBlockWithBody() throws Exception {
        mth.setName("doStuff");
        mth.setReturns(new Type("java.lang.String"));
        mth.setSourceCode("  int x = 2;\n  return STUFF;\n");

        assertEquals("" +
                "java.lang.String doStuff() {\n" +
                "  int x = 2;\n" +
                "  return STUFF;\n" +
                "}\n",
                mth.getCodeBlock());
    }
    
    public void testEquals() throws Exception {
        mth.setName("thing");
        mth.setReturns(new Type("void"));

        JavaMethod m2 = new JavaMethod();
        m2.setName("thing");
        m2.setReturns(new Type("void"));

        JavaMethod m3 = new JavaMethod();
        m3.setName("thingy");
        m3.setReturns(new Type("void"));

        JavaMethod m4 = new JavaMethod();
        m4.setName("thing");
        m4.setReturns(new Type("int"));

        JavaMethod c1 = new JavaMethod();
        c1.setName("thing");
        c1.setConstructor(true);
        
        JavaMethod c2 = new JavaMethod();
        c2.setName("Thong");
        c2.setConstructor(true);
        
        JavaMethod c3 = new JavaMethod();
        c3.setName("Thong");
        c3.setConstructor(true);
        
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
        mth.setName("thing");
        mth.addParameter(new JavaParameter(new Type("int", 1), "blah"));
        mth.addParameter(new JavaParameter(new Type("java.lang.String", 2), "thing"));
        mth.addParameter(new JavaParameter(new Type("X", 3), ""));
        mth.setReturns(new Type("void"));

        JavaMethod m2 = new JavaMethod();
        m2.setName("thing");
        m2.addParameter(new JavaParameter(new Type("int", 1), "blah"));
        m2.addParameter(new JavaParameter(new Type("java.lang.String", 2), "anotherName"));
        m2.addParameter(new JavaParameter(new Type("X", 3), "blah"));
        m2.setReturns(new Type("void"));

        JavaMethod m3 = new JavaMethod();
        m3.setName("thing");
        m3.addParameter(new JavaParameter(new Type("int", 1), "blah"));
        m3.addParameter(new JavaParameter(new Type("java.lang.String", 2), "thing"));
        m3.setReturns(new Type("void"));

        JavaMethod m4 = new JavaMethod();
        m4.setName("thing");
        m4.addParameter(new JavaParameter(new Type("int", 1), "blah"));
        m4.addParameter(new JavaParameter(new Type("java.lang.String", 2), "thing"));
        m4.addParameter(new JavaParameter(new Type("TTTTTTTT", 3), "blah")); //name
        m4.setReturns(new Type("void"));

        JavaMethod m5 = new JavaMethod();
        m5.setName("thing");
        m5.addParameter(new JavaParameter(new Type("int", 1), "blah"));
        m5.addParameter(new JavaParameter(new Type("java.lang.String", 2), "thing"));
        m5.addParameter(new JavaParameter(new Type("X", 9), "blah")); // dimension
        m5.setReturns(new Type("void"));

        assertEquals(mth, m2);
        assertEquals(m2, mth);
        assertNotEquals(mth, m3);
        assertNotEquals(mth, m4);
        assertNotEquals(mth, m5);
    }

    public void testHashCode() throws Exception {
        mth.setName("thing");
        mth.addParameter(new JavaParameter(new Type("int", 1), "blah"));
        mth.addParameter(new JavaParameter(new Type("java.lang.String", 2), "thing"));
        mth.addParameter(new JavaParameter(new Type("X", 3), ""));
        mth.setReturns(new Type("void"));

        JavaMethod m2 = new JavaMethod();
        m2.setName("thing");
        m2.addParameter(new JavaParameter(new Type("int", 1), "blah"));
        m2.addParameter(new JavaParameter(new Type("java.lang.String", 2), "anotherName"));
        m2.addParameter(new JavaParameter(new Type("X", 3), "blah"));
        m2.setReturns(new Type("void"));

        JavaMethod m3 = new JavaMethod();
        m3.setName("thing");
        m3.addParameter(new JavaParameter(new Type("int", 1), "blah"));
        m3.addParameter(new JavaParameter(new Type("java.lang.String", 2), "thing"));
        m3.setReturns(new Type("void"));

        JavaMethod c1 = new JavaMethod();
        c1.setName("Thong");
        c1.setConstructor(true);
        
        JavaMethod c2 = new JavaMethod();
        c2.setName("Thong");
        c2.setConstructor(true);
        
        assertEquals(mth.hashCode(), m2.hashCode());
        assertTrue(mth.hashCode() != m3.hashCode());
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    public void testSignatureMatches() throws Exception {
        mth.setName("thing");
        mth.addParameter(new JavaParameter(new Type("int"), "x"));
        mth.addParameter(new JavaParameter(new Type("long", 2), "y"));
        mth.setReturns(new Type("void"));

        Type[] correctTypes = new Type[]{
            new Type("int"),
            new Type("long", 2)
        };

        Type[] wrongTypes1 = new Type[]{
            new Type("int", 2),
            new Type("long")
        };

        Type[] wrongTypes2 = new Type[]{
            new Type("int"),
            new Type("long", 2),
            new Type("double")
        };

        assertTrue(mth.signatureMatches("thing", correctTypes));
        assertFalse(mth.signatureMatches("xxx", correctTypes));
        assertFalse(mth.signatureMatches("thing", wrongTypes1));
        assertFalse(mth.signatureMatches("thing", wrongTypes2));
    }
    
    public void testVarArgSignatureMatches() throws Exception {
        mth.setName("thing");
        mth.addParameter(new JavaParameter(new Type("int"), "x"));
        mth.addParameter(new JavaParameter(new Type("long", 2), "y", true));
        mth.setReturns(new Type("void"));

        Type[] correctTypes = new Type[]{
            new Type("int"),
            new Type("long", 2)
        };

        Type[] wrongTypes1 = new Type[]{
            new Type("int", 2),
            new Type("long")
        };

        Type[] wrongTypes2 = new Type[]{
            new Type("int"),
            new Type("long", 2),
            new Type("double")
        };

        assertTrue(mth.signatureMatches("thing", correctTypes, true));
        assertFalse(mth.signatureMatches("thing", correctTypes, false));
        assertFalse(mth.signatureMatches("xxx", correctTypes, true));
        assertFalse(mth.signatureMatches("thing", wrongTypes1, true));
        assertFalse(mth.signatureMatches("thing", wrongTypes2, true));
    }

    public void testParentClass() throws Exception {
        assertSame(clazz, mth.getParentClass());
    }

    public void testCanGetParameterByName() throws Exception {
        JavaParameter paramX = new JavaParameter(new Type("int"), "x");
        mth.addParameter(paramX);
        mth.addParameter(new JavaParameter(new Type("string"), "y"));
        
        assertEquals(paramX, mth.getParameterByName("x"));
        assertEquals(null, mth.getParameterByName("z"));
    }

    public void testToString() throws Exception {
    	JavaClass cls = new JavaClass("java.lang.Object");
    	JavaMethod mthd = new JavaMethod(new Type("boolean"),"equals");
    	cls.addMethod(mthd);
    	mthd.setModifiers(new String[]{"public"});
    	mthd.addParameter(new JavaParameter(new Type("java.lang.Object"), null));
    	assertEquals("public boolean java.lang.Object.equals(java.lang.Object)", mthd.toString());
    }
    
    public void testConstructorToString() throws Exception {
        JavaClass cls = new JavaClass("a.b.Executor");
        JavaMethod constructor = new JavaMethod(null,"Executor");
        constructor.setConstructor( true );
        cls.addMethod(constructor);
        assertEquals("a.b.Executor()", constructor.toString());
    }

    
    private void assertNotEquals(Object o1, Object o2) {
        assertTrue(o1.toString() + " should not equals " + o2.toString(), !o1.equals(o2));
    }
}
