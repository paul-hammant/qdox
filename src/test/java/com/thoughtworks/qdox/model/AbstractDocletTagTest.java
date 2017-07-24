package com.thoughtworks.qdox.model;

import java.util.Map;

import junit.framework.TestCase;

public abstract class AbstractDocletTagTest extends TestCase {

    public AbstractDocletTagTest(String s) {
        super(s);
    }

    protected abstract DocletTagFactory getDocletTagFactory();

    DocletTag createDocletTag(String tag, String text) {
    	return getDocletTagFactory().createDocletTag(tag, text);
	}
    
    public void testIndexedParameter() {
        DocletTag tag = createDocletTag("x", "one two three four");
        assertEquals("one", tag.getParameters().get(0));
        assertEquals("two", tag.getParameters().get(1));
        assertEquals("three", tag.getParameters().get(2));
        assertEquals("four", tag.getParameters().get(3));
        assertEquals(4, tag.getParameters().size());
    }

    public void testNamedParameter() {
        DocletTag tag = 
            getDocletTagFactory().createDocletTag(
                "x", "hello=world dog=cat fork=spoon"
            );
        assertEquals("world", tag.getNamedParameter("hello"));
        assertEquals("cat", tag.getNamedParameter("dog"));
        assertEquals("spoon", tag.getNamedParameter("fork"));
        assertNull(tag.getNamedParameter("goat"));
    }

    public void testNamedParameterMap() {
        DocletTag tag = createDocletTag(
            "x", "hello=world dog=cat fork=spoon"
        );
        Map<String, String> map = tag.getNamedParameterMap();
        assertEquals(3, map.size());
        assertEquals("world", map.get("hello"));
        assertEquals("cat", map.get("dog"));
        assertEquals("spoon", map.get("fork"));
        assertNull(map.get("goat"));
    }
    
    public void testQuotedParameters() {
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
        assertEquals("quote", tag.getParameters().get(0));
        assertEquals(2, tag.getParameters().size());
        assertEquals("ed", tag.getParameters().get(1));
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
