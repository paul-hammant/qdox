package com.thoughtworks.qdox;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

/**
 * Examples from <a href="https://docs.oracle.com/en/java/javase/16/language/sealed-classes-and-interfaces.html">https://docs.oracle.com/en/java/javase/16/language/sealed-classes-and-interfaces.html</a>
 * @author Robert Scholte
 */
public class SealedClassesTest
{
    private JavaProjectBuilder builder = new JavaProjectBuilder();
    
    @Test
    public void sealedClass() {
        String source = "public sealed class Shape\r\n"
            + "    permits Circle, Square, Rectangle {\r\n"
            + "}";
        builder.addSource( new StringReader(source) );
    }

    @Test
    public void nonSealedClass() {
        String source = "public non-sealed class Square extends Shape {\r\n"
            + "   public double side;\r\n"
            + "}";
        builder.addSource( new StringReader(source) );
    }

    @Test
    public void sealedInterface() {
        String source = "public sealed interface Shape permits Polygon {}";
        builder.addSource( new StringReader(source) );
    }

    @Test
    public void nonSealedInterface() {
        String source = "public non-sealed interface Polygon extends Shape { }";
        builder.addSource( new StringReader(source) );
    }
}
