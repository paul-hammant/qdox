package com.thoughtworks.qdox;

import junit.framework.TestSuite;
import junit.framework.Test;

public class PackageTestSuite extends TestSuite {

	public static Test suite() {
	    TestSuite suite = new TestSuite();
		suite.addTestSuite(JavaDocBuilderTest.class);
		suite.addTestSuite(MultipleJavaDocBuilderTest.class);
		return suite;
	}

}
