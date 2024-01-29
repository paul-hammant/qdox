package com.thoughtworks.qdox;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

/**
 *
 * @author shalousun
 */
public class PermitsTest {
    @Test
    public void permitsAsTypeAndIdentifiers() {
        String source = "public class MyPermits {\n" +
                "\n" +
                "    private Object permits;\n" +
                "\n" +
                "    public MyPermits(){}\n" +
                "\n" +
                "    public MyPermits(Object permits) {\n" +
                "        this.permits = permits;\n" +
                "    }\n" +
                "\n" +
                "    public Object getPermits() {\n" +
                "        return permits;\n" +
                "    }\n" +
                "\n" +
                "    public void setPermits(Object permits) {\n" +
                "        this.permits = permits;\n" +
                "    }\n" +
                "}";
        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addSource( new StringReader(source) );
    }
}
