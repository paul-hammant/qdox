package com.thoughtworks.qdox.model;

import junit.framework.TestCase;
import com.thoughtworks.qdox.JavaDocBuilder;

import java.io.StringReader;
import java.util.Map;

public abstract class AbstractDocletTagTest extends TestCase {

    public AbstractDocletTagTest(String s) {
        super(s);
    }

    protected abstract DocletTagFactory getDocletTagFactory();

    public void testValueRemainsIntact() throws Exception {
        String in = ""
                + "package x;\n"
                + "/**\n"
                + " * @tag aa count(*) bbb * ccc dd=e f='g' i = \"xx\"\n"
                + " */\n"
                + "class X {}";

        JavaDocBuilder builder = new JavaDocBuilder(getDocletTagFactory());
        builder.addSource(new StringReader(in));
        DocletTag tag = builder.getClassByName("x.X").getTagByName("tag");

        assertEquals("aa count(*) bbb * ccc dd=e f='g' i = \"xx\"", tag.getValue());
    }

    public void testIndexedParameter() throws Exception {
        DocletTag tag = getDocletTagFactory().createDocletTag("x", "one two three four");
        assertEquals("one", tag.getParameters()[0]);
        assertEquals("two", tag.getParameters()[1]);
        assertEquals("three", tag.getParameters()[2]);
        assertEquals("four", tag.getParameters()[3]);
        assertEquals(4, tag.getParameters().length);
    }

    public void testNamedParameter() throws Exception {
        DocletTag tag = 
            getDocletTagFactory().createDocletTag(
                "x", "hello=world dog=cat fork=spoon"
            );
        assertEquals("world", tag.getNamedParameter("hello"));
        assertEquals("cat", tag.getNamedParameter("dog"));
        assertEquals("spoon", tag.getNamedParameter("fork"));
        assertNull(tag.getNamedParameter("goat"));
    }

    public void testNamedParameterMap() throws Exception {
        DocletTag tag = 
        getDocletTagFactory().createDocletTag(
                "x", "hello=world dog=cat fork=spoon"
                );
        Map map = tag.getNamedParameterMap();
        assertEquals(3, map.size());
        assertEquals("world", map.get("hello"));
        assertEquals("cat", map.get("dog"));
        assertEquals("spoon", map.get("fork"));
        assertNull(map.get("goat"));
    }
    
    public void testInvalidNamedParameter() throws Exception {
        DocletTag tag = getDocletTagFactory().createDocletTag("x", "= =z x=c y= o");
        assertEquals("c", tag.getNamedParameter("x"));
        assertEquals("", tag.getNamedParameter("y"));
        assertNull(tag.getNamedParameter("z"));
        assertNull(tag.getNamedParameter("="));
        assertNull(tag.getNamedParameter(""));
    }

    public void testIntermingledIndexedAndNamedParameter() throws Exception {
        DocletTag tag = getDocletTagFactory().createDocletTag("x", "thing hello=world duck");

        assertEquals("thing", tag.getParameters()[0]);
        assertEquals("hello=world", tag.getParameters()[1]);
        assertEquals("duck", tag.getParameters()[2]);

        assertEquals("world", tag.getNamedParameter("hello"));

        assertEquals(3, tag.getParameters().length);
        assertNull(tag.getNamedParameter("goat"));
        assertNull(tag.getNamedParameter("duck"));
    }

    public void testQuotedParameters() throws Exception {
        DocletTag tag = getDocletTagFactory().createDocletTag("x", "one=\"hello world bye bye\" two=hello");
        assertEquals("hello world bye bye", tag.getNamedParameter("one"));
        assertEquals("hello", tag.getNamedParameter("two"));

        tag = new DefaultDocletTag("x", "one=\"hello joe's world bye bye\" two=hello");
        assertEquals("hello joe's world bye bye", tag.getNamedParameter("one"));
        assertEquals("hello", tag.getNamedParameter("two"));

        tag = new DefaultDocletTag("x", "one='hello joe\"s world bye bye' two=hello");
        assertEquals("hello joe\"s world bye bye", tag.getNamedParameter("one"));
        assertEquals("hello", tag.getNamedParameter("two"));

        tag = new DefaultDocletTag("x", "one=\"hello chris' world bye bye\" two=hello");
        assertEquals("hello chris' world bye bye", tag.getNamedParameter("one"));
        assertEquals("hello", tag.getNamedParameter("two"));
    }
    
}
