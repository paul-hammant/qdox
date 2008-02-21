/**
 * $Id: FieldsTest.java 16 2008-02-20 17:57:08Z louis $
 */

package com.thoughtworks.qdox;

import junit.framework.TestCase;
import com.thoughtworks.qdox.model.*;

import java.io.StringReader;

public class FieldsTest extends TestCase
{
    private JavaDocBuilder builder = new JavaDocBuilder();

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
}
