package com.thoughtworks.qdox.model;

import junit.framework.TestSuite;
import junit.framework.Test;

public class PackageTestSuite extends TestSuite {

	public static Test suite() {
    TestSuite suite = new TestSuite();
		suite.addTestSuite(AbstractJavaEntityTest.class);
		suite.addTestSuite(IndentBufferTest.class);
		suite.addTestSuite(JavaClassTest.class);
		suite.addTestSuite(JavaFieldTest.class);
		suite.addTestSuite(JavaMethodTest.class);
		suite.addTestSuite(JavaSourceTest.class);
		suite.addTestSuite(ModelBuilderTest.class);
        suite.addTestSuite(TypeTest.class);
		return suite;
	}

}
