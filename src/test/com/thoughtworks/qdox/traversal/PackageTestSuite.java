package com.thoughtworks.qdox.traversal;

import junit.framework.TestSuite;
import junit.framework.Test;

public class PackageTestSuite extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(TraversalTest.class);
		return suite;
	}

}
