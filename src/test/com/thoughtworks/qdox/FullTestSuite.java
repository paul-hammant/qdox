package com.thoughtworks.qdox;

import junit.framework.TestSuite;
import junit.framework.Test;

public class FullTestSuite extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(com.thoughtworks.qdox.parser.PackageTestSuite.suite());
		suite.addTest(com.thoughtworks.qdox.model.PackageTestSuite.suite());
		suite.addTest(com.thoughtworks.qdox.PackageTestSuite.suite());
		suite.addTest(com.thoughtworks.qdox.directorywalker.PackageTestSuite.suite());
		suite.addTest(com.thoughtworks.qdox.ant.PackageTestSuite.suite());
        suite.addTest(com.thoughtworks.qdox.traversal.PackageTestSuite.suite());
		return suite;
	}

}
