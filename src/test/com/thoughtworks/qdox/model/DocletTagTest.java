package com.thoughtworks.qdox.model;

import junit.framework.TestCase;
import com.thoughtworks.qdox.JavaDocBuilder;

import java.io.StringReader;

public class DocletTagTest extends TestCase {

	public DocletTagTest(String s) {
		super(s);
	}

  public void testValueRemainsInTact() throws Exception {
		String in = ""
			+ "package x;\n"
			+ "/**\n"
			+ " * @tag aa bbb ccc dd=e f='g' i = \"xx\"\n"
			+ " */\n"
			+ "class X {}";

		JavaDocBuilder builder = new JavaDocBuilder();
		builder.addSource(new StringReader(in));
		DocletTag tag = builder.getClassByName("x.X").getTagByName("tag");

		assertEquals("aa bbb ccc dd=e f='g' i = \"xx\"", tag.getValue());
	}

	public void testIndexedParameter() throws Exception {
		DocletTag tag = new DocletTag("x", "one two three four");
		assertEquals("one", tag.getParameters()[0]);
		assertEquals("two", tag.getParameters()[1]);
		assertEquals("three", tag.getParameters()[2]);
		assertEquals("four", tag.getParameters()[3]);
		assertEquals(4, tag.getParameters().length);
	}

	public void testNamedParameter() throws Exception {
		DocletTag tag = new DocletTag("x", "hello=world dog=cat fork=spoon");
		assertEquals("world", tag.getNamedParameter("hello"));
		assertEquals("cat", tag.getNamedParameter("dog"));
		assertEquals("spoon", tag.getNamedParameter("fork"));
		assertNull(tag.getNamedParameter("goat"));
	}

	public void testInvalidNamedParameter() throws Exception {
		DocletTag tag = new DocletTag("x", "= =z x=c y= o");
		assertEquals("c", tag.getNamedParameter("x"));
		assertEquals("", tag.getNamedParameter("y"));
		assertNull(tag.getNamedParameter("z"));
		assertNull(tag.getNamedParameter("="));
		assertNull(tag.getNamedParameter(""));
	}

	public void testIntermingledIndexedAndNamedParameter() throws Exception {
		DocletTag tag = new DocletTag("x", "thing hello=world duck");

		assertEquals("thing", tag.getParameters()[0]);
		assertEquals("hello=world", tag.getParameters()[1]);
		assertEquals("duck", tag.getParameters()[2]);

		assertEquals("world", tag.getNamedParameter("hello"));

		assertEquals(3, tag.getParameters().length);
		assertNull(tag.getNamedParameter("goat"));
		assertNull(tag.getNamedParameter("duck"));
	}


}
