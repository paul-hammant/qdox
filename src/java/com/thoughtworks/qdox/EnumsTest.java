package com.thoughtworks.qdox;

import junit.framework.TestCase;

import java.io.StringReader;

import com.thoughtworks.qdox.model.JavaClass;

public class EnumsTest extends TestCase {

    public void testDoesNotBreakParserWhenEncounteringJava5Enum() {
        // NOTE: This is temporary - currently the parser only needs to not barf when it encounters an enum.
        // Later versions of QDox will actually expose this enum in the model: See QDOX-79

        String source = ""
                + "public enum Enum1 { a, b }"
                + "class X { "
                + "  enum Enum2 { c, /** some doc */ d } "
                + "  int someField; "
                + "}";

        JavaDocBuilder javaDocBuilder = new JavaDocBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        JavaClass cls = javaDocBuilder.getClassByName("X");
        assertEquals("int", cls.getFieldByName("someField").getType().getValue()); // sanity check
    }
}
