package com.thoughtworks.qdox.ant;

import junit.framework.TestSuite;
import junit.framework.Test;

public class PackageTestSuite extends TestSuite {

	public static Test suite() {
        TestSuite suite = new TestSuite();
		suite.addTestSuite(ConsoleLoggingQdoxTaskTestCase.class);
		return suite;
	}

}
