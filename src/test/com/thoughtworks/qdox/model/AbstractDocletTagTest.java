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

    DocletTag createDocletTag(String tag, String text) {
    	return getDocletTagFactory().createDocletTag(tag, text);
	}
    
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
        DocletTag tag = createDocletTag("x", "one two three four");
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
        DocletTag tag = createDocletTag(
            "x", "hello=world dog=cat fork=spoon"
        );
        Map map = tag.getNamedParameterMap();
        assertEquals(3, map.size());
        assertEquals("world", map.get("hello"));
        assertEquals("cat", map.get("dog"));
        assertEquals("spoon", map.get("fork"));
        assertNull(map.get("goat"));
    }
    
    public void testQuotedParameters() throws Exception {
        DocletTag tag = createDocletTag("x", "one=\"hello world bye bye\" two=hello");
        assertEquals("hello world bye bye", tag.getNamedParameter("one"));
        assertEquals("hello", tag.getNamedParameter("two"));

        tag = createDocletTag("x", "one=\"hello joe's world bye bye\" two=hello");
        assertEquals("hello joe's world bye bye", tag.getNamedParameter("one"));
        assertEquals("hello", tag.getNamedParameter("two"));

        tag = createDocletTag("x", "one='hello joe\"s world bye bye' two=hello");
        assertEquals("hello joe\"s world bye bye", tag.getNamedParameter("one"));
        assertEquals("hello", tag.getNamedParameter("two"));

        tag = createDocletTag("x", "one=\"hello chris' world bye bye\" two=hello");
        assertEquals("hello chris' world bye bye", tag.getNamedParameter("one"));
        assertEquals("hello", tag.getNamedParameter("two"));
    }


    public void testJiraQdox28() {
        DocletTag tag = createDocletTag("key", "quote'ed");
        assertEquals("quote", tag.getParameters()[0]);
        assertEquals(2, tag.getParameters().length);
        assertEquals("ed", tag.getParameters()[1]);
    }
 
    public void testJiraQdox45() {
        DocletTag tag = createDocletTag("key", "param = \"value\"");
        assertEquals("value", tag.getNamedParameter("param"));
    }

    public void testJiraQdox50() {
    	DocletTag tag = createDocletTag("key", "param=\" value\"");
        assertEquals(" value", tag.getNamedParameter("param"));
    }
    
}
