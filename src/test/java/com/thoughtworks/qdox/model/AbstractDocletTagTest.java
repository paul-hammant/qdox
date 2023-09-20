package com.thoughtworks.qdox.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public abstract class AbstractDocletTagTest {

    protected abstract DocletTagFactory getDocletTagFactory();

    DocletTag createDocletTag(String tag, String text) {
    	return getDocletTagFactory().createDocletTag(tag, text);
	}

    @Test
    public void testIndexedParameter() {
        DocletTag tag = createDocletTag("x", "one two three four");
        Assertions.assertEquals("one", tag.getParameters().get(0));
        Assertions.assertEquals("two", tag.getParameters().get(1));
        Assertions.assertEquals("three", tag.getParameters().get(2));
        Assertions.assertEquals("four", tag.getParameters().get(3));
        Assertions.assertEquals(4, tag.getParameters().size());
    }

    @Test
    public void testNamedParameter() {
        DocletTag tag = 
            getDocletTagFactory().createDocletTag(
                "x", "hello=world dog=cat fork=spoon"
            );
        Assertions.assertEquals("world", tag.getNamedParameter("hello"));
        Assertions.assertEquals("cat", tag.getNamedParameter("dog"));
        Assertions.assertEquals("spoon", tag.getNamedParameter("fork"));
        Assertions.assertNull(tag.getNamedParameter("goat"));
    }

    @Test
    public void testNamedParameterMap() {
        DocletTag tag = createDocletTag(
            "x", "hello=world dog=cat fork=spoon"
        );
        Map<String, String> map = tag.getNamedParameterMap();
        Assertions.assertEquals(3, map.size());
        Assertions.assertEquals("world", map.get("hello"));
        Assertions.assertEquals("cat", map.get("dog"));
        Assertions.assertEquals("spoon", map.get("fork"));
        Assertions.assertNull(map.get("goat"));
    }

    @Test
    public void testQuotedParameters() {
        DocletTag tag = createDocletTag("x", "one=\"hello world bye bye\" two=hello");
        Assertions.assertEquals("hello world bye bye", tag.getNamedParameter("one"));
        Assertions.assertEquals("hello", tag.getNamedParameter("two"));

        tag = createDocletTag("x", "one=\"hello joe's world bye bye\" two=hello");
        Assertions.assertEquals("hello joe's world bye bye", tag.getNamedParameter("one"));
        Assertions.assertEquals("hello", tag.getNamedParameter("two"));

        tag = createDocletTag("x", "one='hello joe\"s world bye bye' two=hello");
        Assertions.assertEquals("hello joe\"s world bye bye", tag.getNamedParameter("one"));
        Assertions.assertEquals("hello", tag.getNamedParameter("two"));

        tag = createDocletTag("x", "one=\"hello chris' world bye bye\" two=hello");
        Assertions.assertEquals("hello chris' world bye bye", tag.getNamedParameter("one"));
        Assertions.assertEquals("hello", tag.getNamedParameter("two"));
    }


    @Test
    public void testJiraQdox28() {
        DocletTag tag = createDocletTag("key", "quote'ed");
        Assertions.assertEquals("quote", tag.getParameters().get(0));
        Assertions.assertEquals(2, tag.getParameters().size());
        Assertions.assertEquals("ed", tag.getParameters().get(1));
    }

    @Test
    public void testJiraQdox45() {
        DocletTag tag = createDocletTag("key", "param = \"value\"");
        Assertions.assertEquals("value", tag.getNamedParameter("param"));
    }

    @Test
    public void testJiraQdox50() {
    	DocletTag tag = createDocletTag("key", "param=\" value\"");
        Assertions.assertEquals(" value", tag.getNamedParameter("param"));
    }
    
}
