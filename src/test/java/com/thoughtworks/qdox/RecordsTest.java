package com.thoughtworks.qdox;

import java.io.StringReader;

import org.junit.Test;

/**
 * Examples from <a href="https://docs.oracle.com/en/java/javase/16/language/records.html">https://docs.oracle.com/en/java/javase/16/language/records.html</a>
 * 
 * @author Robert Scholte
 */
public class RecordsTest
{
    private JavaProjectBuilder builder = new JavaProjectBuilder();
    
    @Test
    public void withTwoFields() {
        String source = "record Rectangle(double length, double width) { }";
        builder.addSource( new StringReader(source) );
    }
    
    @Test
    public void withCanonicalConstructor() {
        String source = "record Rectangle(double length, double width) {\n"
            + "    public Rectangle(double length, double width) {\n"
            + "        if (length <= 0 || width <= 0) {\n"
            + "            throw new java.lang.IllegalArgumentException(\n"
            + "                String.format(\"Invalid dimensions: %f, %f\", length, width));\n"
            + "        }\n"
            + "        this.length = length;\n"
            + "        this.width = width;\n"
            + "    }\n"
            + "}";
        builder.addSource( new StringReader(source) );
    }
    
    @Test
    public void withCompactConstructor() {
        String source = "record Rectangle(double length, double width) {\n"
            + "    public Rectangle {\n"
            + "        if (length <= 0 || width <= 0) {\n"
            + "            throw new java.lang.IllegalArgumentException(\n"
            + "                String.format(\"Invalid dimensions: %f, %f\", length, width));\n"
            + "        }\n"
            + "    }\n"
            + "}";
        builder.addSource( new StringReader(source) );
    }

    @Test
    public void withPublicAccessorMethod() {
        String source = "record Rectangle(double length, double width) {\n"
            + " \n"
            + "    // Public accessor method\n"
            + "    public double length() {\n"
            + "        System.out.println(\"Length is \" + length);\n"
            + "        return length;\n"
            + "    }\n"
            + "}";
        builder.addSource( new StringReader(source) );
    }
    
    @Test
    public void withStaticMembers() {
        String source = "record Rectangle(double length, double width) {\n"
            + "    \n"
            + "    // Static field\n"
            + "    static double goldenRatio;\n"
            + "\n"
            + "    // Static initializer\n"
            + "    static {\n"
            + "        goldenRatio = (1 + Math.sqrt(5)) / 2;\n"
            + "    }\n"
            + "\n"
            + "    // Static method\n"
            + "    public static Rectangle createGoldenRectangle(double width) {\n"
            + "        return new Rectangle(width, width * goldenRatio);\n"
            + "    }\n"
            + "}";
        builder.addSource( new StringReader(source) );
    }
    
    @Test
    public void withNonStaticMembers() {
        String source = "record Rectangle(double length, double width) {\n"
            + "\n"
            + "    // Field declarations must be static:\n"
            + "    BiFunction<Double, Double, Double> diagonal;\n"
            + "\n"
            + "    // Instance initializers are not allowed in records:\n"
            + "    {\n"
            + "        diagonal = (x, y) -> Math.sqrt(x*x + y*y);\n"
            + "    }\n"
            + "}";
        builder.addSource( new StringReader(source) );
    }

    @Test
    public void withNestedRecord() {
        String source = "record Rectangle(double length, double width) {\n"
            + "\n"
            + "    // Nested record class\n"
            + "    record RotationAngle(double angle) {\n"
            + "        public RotationAngle {\n"
            + "            angle = Math.toRadians(angle);\n"
            + "        }\n"
            + "    }\n"
            + "    \n"
            + "    // Public instance method\n"
            + "    public Rectangle getRotatedRectangleBoundingBox(double angle) {\n"
            + "        RotationAngle ra = new RotationAngle(angle);\n"
            + "        double x = Math.abs(length * Math.cos(ra.angle())) +\n"
            + "                   Math.abs(width * Math.sin(ra.angle()));\n"
            + "        double y = Math.abs(length * Math.sin(ra.angle())) +\n"
            + "                   Math.abs(width * Math.cos(ra.angle()));\n"
            + "        return new Rectangle(x, y);\n"
            + "    }\n"
            + "}";
        builder.addSource( new StringReader(source) );
    }

    @Test
    public void withGenerics() {
        String source = "record Triangle<C extends Coordinate> (C top, C left, C right) { }";
        builder.addSource( new StringReader(source) );
    }

    @Test
    public void withInterface() {
        String source = "record Customer(String... data) implements Billable { }";
        builder.addSource( new StringReader(source) );
    }

    @Test
    public void withAnnotatedParameters() {
        String source = "record Rectangle(\n"
            + "    @GreaterThanZero double length,\n"
            + "    @GreaterThanZero double width) { }";
        builder.addSource( new StringReader(source) );
    }

    @Test
    public void withAnnotation() {
        String source = "@Deprecated\n"
            + "record Line(int lenght) { }";
        builder.addSource( new StringReader(source) );
    }

    
    @Test
    public void recordAsTypeAndIdentifiers() {
        String source = "package record.record.record;\n"
            + "\n"
            + "public class record\n"
            + "{\n"
            + "    private Object record;\n"
            + "    \n"
            + "    public record() {\n"
            + "    }\n"
            + "    \n"
            + "    private Object record(Object record) {\n"
            + "        return record;\n"
            + "    }\n"
            + "}";
            builder.addSource( new StringReader(source) );
    }

    @Test
    public void parametersContainingRecord() {
        String source = "interface Example{\n"
            + " void apply(Object recordList);"
            + "}";
        builder.addSource( new StringReader(source) );
    }
}
