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
        mth.setParameters(new JavaParameter[]{
            new JavaParameter(new Type("int"), "count"),
            new JavaParameter(new Type("MyThing"), "t")
        });
    }

    public void testToStringSimple() throws Exception {
        mth.setName("doSomething");
        mth.setReturns(new Type("java.lang.String"));
        assertEquals("java.lang.String doSomething();\n", mth.toString());
    }

    public void testToStringOneParam() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.setParameters(new JavaParameter[]{new JavaParameter(new Type("String"), "thingy")});
        assertEquals("void blah(String thingy);\n", mth.toString());
    }

    public void testToStringTwoParams() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.setParameters(new JavaParameter[]{
            new JavaParameter(new Type("int"), "count"),
            new JavaParameter(new Type("MyThing"), "t")
        });
        assertEquals("void blah(int count, MyThing t);\n", mth.toString());
    }

    public void testToStringThreeParams() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.setParameters(new JavaParameter[]{
            new JavaParameter(new Type("int"), "count"),
            new JavaParameter(new Type("MyThing"), "t"),
            new JavaParameter(new Type("java.lang.Meat"), "beef")
        });
        assertEquals("void blah(int count, MyThing t, java.lang.Meat beef);\n", mth.toString());
    }

    public void testToStringModifiersWithAccessLevelFirst() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.setModifiers(new String[]{"synchronized", "public", "final"});
        assertEquals("public synchronized final void blah();\n", mth.toString());
    }

    public void testToStringOneException() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.setExceptions(new Type[]{new Type("RuntimeException")});
        assertEquals("void blah() throws RuntimeException;\n", mth.toString());
    }

    public void testToStringTwoException() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.setExceptions(new Type[]{new Type("RuntimeException"), new Type("java.lang.SheepException", 1)});
        assertEquals("void blah() throws RuntimeException, java.lang.SheepException;\n", mth.toString());
    }

    public void testToStringThreeException() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.setExceptions(new Type[]{new Type("RuntimeException"), new Type("java.lang.SheepException", 1), new Type("CowException", 1)});
        assertEquals("void blah() throws RuntimeException, java.lang.SheepException, CowException;\n", mth.toString());
    }

    public void testToStringConstructor() throws Exception {
        mth.setName("Blah");
        mth.setModifiers(new String[]{"public"});
        mth.setConstructor(true);
        assertEquals("public Blah();\n", mth.toString());
    }

    public void testToStringWithComment() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.setComment("Hello");
        String expect = ""
                + "/**\n"
                + " * Hello\n"
                + " */\n"
                + "void blah();\n";
        assertEquals(expect, mth.toString());
    }

    public void testToString1dArray() throws Exception {
        mth.setName("doSomething");
        mth.setReturns(new Type("java.lang.String", 1));
        assertEquals("java.lang.String[] doSomething();\n", mth.toString());
    }

    public void testToString2dArray() throws Exception {
        mth.setName("doSomething");
        mth.setReturns(new Type("java.lang.String", 2));
        assertEquals("java.lang.String[][] doSomething();\n", mth.toString());
    }

    public void testToStringParamArray() throws Exception {
        mth.setName("blah");
        mth.setReturns(new Type("void"));
        mth.setParameters(new JavaParameter[]{
            new JavaParameter(new Type("int", 2), "count"),
            new JavaParameter(new Type("MyThing", 1), "t")
        });
        assertEquals("void blah(int[][] count, MyThing[] t);\n", mth.toString());
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
        mth.setParameters(new JavaParameter[]{
            new JavaParameter(new Type("int", 1), "blah"),
            new JavaParameter(new Type("java.lang.String", 2), "thing"),
            new JavaParameter(new Type("X", 3), "")
        });
        mth.setReturns(new Type("void"));

        JavaMethod m2 = new JavaMethod();
        m2.setName("thing");
        m2.setParameters(new JavaParameter[]{
            new JavaParameter(new Type("int", 1), "blah"),
            new JavaParameter(new Type("java.lang.String", 2), "anotherName"),
            new JavaParameter(new Type("X", 3), "blah")
        });
        m2.setReturns(new Type("void"));

        JavaMethod m3 = new JavaMethod();
        m3.setName("thing");
        m3.setParameters(new JavaParameter[]{
            new JavaParameter(new Type("int", 1), "blah"),
            new JavaParameter(new Type("java.lang.String", 2), "thing"),
        });
        m3.setReturns(new Type("void"));

        JavaMethod m4 = new JavaMethod();
        m4.setName("thing");
        m4.setParameters(new JavaParameter[]{
            new JavaParameter(new Type("int", 1), "blah"),
            new JavaParameter(new Type("java.lang.String", 2), "thing"),
            new JavaParameter(new Type("TTTTTTTT", 3), "blah") // name
        });
        m4.setReturns(new Type("void"));

        JavaMethod m5 = new JavaMethod();
        m5.setName("thing");
        m5.setParameters(new JavaParameter[]{
            new JavaParameter(new Type("int", 1), "blah"),
            new JavaParameter(new Type("java.lang.String", 2), "thing"),
            new JavaParameter(new Type("X", 9), "blah") // dimension
        });
        m5.setReturns(new Type("void"));

        assertEquals(mth, m2);
        assertEquals(m2, mth);
        assertNotEquals(mth, m3);
        assertNotEquals(mth, m4);
        assertNotEquals(mth, m5);
    }

    public void testHashCode() throws Exception {
        mth.setName("thing");
        mth.setParameters(new JavaParameter[]{
            new JavaParameter(new Type("int", 1), "blah"),
            new JavaParameter(new Type("java.lang.String", 2), "thing"),
            new JavaParameter(new Type("X", 3), "")
        });
        mth.setReturns(new Type("void"));

        JavaMethod m2 = new JavaMethod();
        m2.setName("thing");
        m2.setParameters(new JavaParameter[]{
            new JavaParameter(new Type("int", 1), "blah"),
            new JavaParameter(new Type("java.lang.String", 2), "anotherName"),
            new JavaParameter(new Type("X", 3), "blah")
        });
        m2.setReturns(new Type("void"));

        JavaMethod m3 = new JavaMethod();
        m3.setName("thing");
        m3.setParameters(new JavaParameter[]{
            new JavaParameter(new Type("int", 1), "blah"),
            new JavaParameter(new Type("java.lang.String", 2), "thing"),
        });
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
        mth.setParameters(new JavaParameter[]{
            new JavaParameter(new Type("int"), "x"),
            new JavaParameter(new Type("long", 2), "y")
        });
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

    public void testParentClass() throws Exception {
        assertSame(clazz, mth.getParentClass());
    }

    public void testCanGetParameterByName() throws Exception {
        JavaParameter paramX =
                new JavaParameter(new Type("int"), "x");
        JavaParameter[] parameters = new JavaParameter[]{
            paramX,
            new JavaParameter(new Type("string"), "y")
        };
        mth.setParameters(parameters);

        assertEquals(paramX, mth.getParameterByName("x"));
        assertEquals(null, mth.getParameterByName("z"));
    }

    private void assertNotEquals(Object o1, Object o2) {
        assertTrue(o1.toString() + " should not equals " + o2.toString(), !o1.equals(o2));
    }

}
