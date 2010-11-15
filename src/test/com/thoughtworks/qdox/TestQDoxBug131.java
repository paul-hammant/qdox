package com.thoughtworks.qdox;

import java.io.StringReader;

import junit.framework.TestCase;

import com.thoughtworks.qdox.model.JavaClass;

public class TestQDoxBug131 extends TestCase {
    public void testname() throws Exception {
         String sourceCode = "package com.acme.qdox;\n" + 
         		"\n" + 
         		"public class QDoxBugClass {\n" + 
         		"    final public static String C1 = \"C1\", C2 = \"C2\";\n" + 
         		"    final public static String[] ALL = { C1, C2 };    \n" + 
         		"    /*\n" + 
         		"    Comment\n" + 
         		"    */\n" + 
         		"    public void method() {\n" + 
         		"        System.out.println(\"This will contain the comment\");\n" + 
         		"    }\n" + 
         		"}\n" + 
         		"";
         JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSource(new StringReader(sourceCode));
        JavaClass aClass = builder.getClassByName("com.acme.qdox.QDoxBugClass");
        assertEquals("\n        System.out.println(\"This will contain the comment\");\n    ", 
                aClass.getMethods().get(0).getSourceCode());
    }
}
