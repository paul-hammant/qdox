package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.JavaClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InnerClassCommentTest {

    @Test
    public void testInnerClassWithComment() {
        String source = "package com.example;" +
                "/**\n" +
                " * Outer class\n" +
                " */\n" +
                "public class Outer {\n" +
                "\n" +
                "    /**\n" +
                "     * Inner class\n" +
                "     */\n" +
                "    @Deprecated\n" +
                "    public static class Inner {\n" +
                "    }\n" +
                "}";

        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSource(new java.io.StringReader(source));

        JavaClass outerClass = builder.getClassByName("com.example.Outer");
        Assertions.assertNotNull(outerClass);
        Assertions.assertEquals("Outer class", outerClass.getComment());

        JavaClass innerClass = outerClass.getNestedClassByName("Inner");
        Assertions.assertNotNull(innerClass);
        Assertions.assertEquals("Inner class", innerClass.getComment());
        Assertions.assertTrue(innerClass.isStatic());
        Assertions.assertEquals(1, innerClass.getAnnotations().size());
        Assertions.assertEquals("java.lang.Deprecated", innerClass.getAnnotations().get(0).getType().getFullyQualifiedName());
    }
}
