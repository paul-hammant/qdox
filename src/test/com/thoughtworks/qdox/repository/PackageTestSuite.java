package com.thoughtworks.qdox.repository;

import junit.framework.TestSuite;
import junit.framework.Test;

public class PackageTestSuite extends TestSuite {

	public static Test suite() {
    TestSuite suite = new TestSuite();
		suite.addTestSuite(RepositoryTest.class);
		return suite;
	}

}
