package com.thoughtworks.qdox.directorywalker;

import junit.framework.TestSuite;
import junit.framework.Test;

public class PackageTestSuite extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(DirectoryScannerTest.class);
		suite.addTestSuite(FilterTest.class);
		return suite;
	}

}
