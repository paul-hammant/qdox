package com.thoughtworks.qdox;

import junit.framework.TestCase;

import java.io.Reader;
import java.io.StringReader;

import com.thoughtworks.qdox.model.JavaSource;

public class JavaDocBuilderTest extends TestCase {

	public JavaDocBuilderTest(String s) {
		super(s);
	}

	public void testBuildFromStream() throws Exception {
		Reader reader = new StringReader("class X {}");
		JavaDocBuilder builder = new JavaDocBuilder();
		JavaSource result = builder.build(reader);
		assertEquals("X", result.getClasses()[0].getName());
	}

}
