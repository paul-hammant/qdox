/**
 * $Id: FieldsTest.java 16 2008-02-20 17:57:08Z louis $
 */

package com.thoughtworks.qdox;

import java.io.StringReader;

import junit.framework.TestCase;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

public class FieldsTest extends TestCase
{
    private JavaProjectBuilder builder = new JavaProjectBuilder();

    public void testAssignmentViaBitShift() {
        String source = ""
                        + "public class X {\n"
                        + "    Object b = new Object();\n"
                        + "    int a = 1 << 30;\n"
                        + "}\n";

        builder.addSource(new StringReader(source));
        JavaClass fooClass = builder.getClassByName("X");
        assertEquals("X", fooClass.getName());
        assertEquals("a", fooClass.getFieldByName("a").getName());
        assertEquals("1 << 30", fooClass.getFieldByName("a").getInitializationExpression().trim());
    }

    // from QDOX-114
    public void testNewArrayWithBitShift() {
        String source = ""
                        + "public class X {\n"
                        + "    int a[] = new int[1 << 16];\n"
                        + "}\n";

        builder.addSource(new StringReader(source));
        JavaClass fooClass = builder.getClassByName("X");
        assertEquals("X", fooClass.getName());
        assertEquals("a", fooClass.getFieldByName("a").getName());
        assertEquals("new int[1 << 16]", fooClass.getFieldByName("a").getInitializationExpression().trim());
    }
    
    //from QDOX-127
    //fails because returned value is " null"
    //which is already better then mentioned in the issue
    public void testCommentBeforeInitialization() {
    	String source = "public class X{\n" +
    			"// Attributes\n" +
    			"\n" +
    			"/**" +
    			"* Some decription" +
    			"*/" +
    			"private String uDI =   null;\n" +
    			"}";
    	
    	JavaClass cls = builder.addSource(new StringReader(source)).getClasses().get(0);
    	JavaField field = cls.getFields().get(0);
    	assertEquals("null", field.getInitializationExpression());
    }
    
    public void testTwoDocletTags() {
        String source = "public class Foo {" +
        		"    /**\r\n" + 
        		"     * @parameter implementation=source2.sub.MyBla\r\n" + 
        		"     * @required\r\n" + 
        		"     */\r\n" + 
        		"    private Bla bla;" +
        		"}";
        JavaClass cls = builder.addSource( new StringReader( source ) ).getClasses().get(0);
        JavaField field = cls.getFields().get(0);
        assertEquals( "", field.getComment() );
        assertEquals( 2, field.getTags().size() );
    }
    
    public void testCommentAndTwoDocletTags() {
        String source = "public class Foo {" +
                "    /**\r\n" + 
                "     * Being Lazy Always\r\n" + 
                "     * @parameter implementation=source2.sub.MyBla\r\n" + 
                "     * @required\r\n" + 
                "     */\r\n" + 
                "    private Bla bla;" +
                "}";
        JavaClass cls = builder.addSource( new StringReader( source ) ).getClasses().get(0);
        JavaField field = cls.getFields().get(0);
        assertEquals( "Being Lazy Always", field.getComment() );
        assertEquals( 2, field.getTags().size() );
    }

    public void testMultiCommentAndTwoDocletTags() {
        String source = "public class Foo {" +
                "    /**\r\n" + 
                "     * Being\r\n" + 
                "     * Lazy\r\n" + 
                "     * Always\r\n" + 
                "     * \r\n" + 
                "     * @parameter implementation=source2.sub.MyBla\r\n" + 
                "     * @required\r\n" + 
                "     */\r\n" + 
                "    private Bla bla;" +
                "}";
        JavaClass cls = builder.addSource( new StringReader( source ) ).getClasses().get(0);
        JavaField field = cls.getFields().get(0);
        assertEquals( "Being\r\nLazy\r\nAlways", field.getComment() );
        assertEquals( 2, field.getTags().size() );
    }

}
