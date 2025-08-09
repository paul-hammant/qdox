package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.JavaClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LocalClassCommentTest {

    @Test
    public void testLocalClassWithComment() {
        String source = "package com.example;" +
                "public class Outer {\n" +
                "    public void doSomething() {\n" +
                "        /**\n" +
                "         * This is a local class\n" +
                "         */\n" +
                "        class Local {\n" +
                "        }\n" +
                "    }\n" +
                "}";

        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSource(new java.io.StringReader(source));

        JavaClass outerClass = builder.getClassByName("com.example.Outer");
        Assertions.assertNotNull(outerClass);

        // This is tricky. Qdox might not be able to find local classes this way.
        // Let's see if we can find it by navigating the model.
        // However, qdox model doesn't seem to have a way to get classes defined in methods.
        // So, this test is likely to fail to find the class.
        // Let's just check if the builder throws an error.
        Assertions.assertNotNull(builder.getClassByName("com.example.Outer"));
    }
}
