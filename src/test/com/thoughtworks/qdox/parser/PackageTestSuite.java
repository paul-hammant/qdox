package com.thoughtworks.qdox.parser;

import junit.framework.TestSuite;
import junit.framework.Test;

public class PackageTestSuite extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(LexerTest.class);
		suite.addTestSuite(ParserTest.class);
		return suite;
	}

}
