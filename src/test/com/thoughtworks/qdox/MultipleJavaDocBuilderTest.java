package com.thoughtworks.qdox;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

public class MultipleJavaDocBuilderTest extends TestCase {

	public MultipleJavaDocBuilderTest(String name) {
		super(name);
	}

	public void testParsingMultipleJavaFiles(){
		MultipleJavaDocBuilder builder = new MultipleJavaDocBuilder();
		JavaSource[] sources = builder.build(new Reader[]{new StringReader(createTestClassList()), new StringReader(createTestClass())});
		assertEquals(2, sources.length);
		
		JavaClass testClassList = sources[0].getClasses()[0];
		assertEquals("TestClassList", testClassList.getName());
		assertEquals("com.thoughtworks.util.TestClass", testClassList.getSuperClass().getValue());
		
		JavaClass testClass = sources[1].getClasses()[0];
		assertEquals("TestClass", testClass.getName());
		
	
		JavaClass testClassListByName = builder.getClassByName("com.thoughtworks.qdox.TestClassList");
		assertEquals("TestClassList", testClassListByName.getName());
		
		JavaClass testClassByName = builder.getClassByName("com.thoughtworks.util.TestClass");
		assertEquals("TestClass", testClassByName.getName());
		
		assertNull(builder.getClassByName("this.class.should.not.Exist"));
	}
	
	private String createTestClassList(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("package com.thoughtworks.qdox;");
		buffer.append("import com.thoughtworks.util.*;");
		buffer.append("public class TestClassList extends TestClass{");
		buffer.append("private int numberOfTests;");
		buffer.append("public int getNumberOfTests(){return numberOfTests;}");
		buffer.append("public void setNumberOfTests(int numberOfTests){this.numberOfTests = numberOfTests;}");
		buffer.append("}");
		return buffer.toString();
	}
	
	private String createTestClass(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("package com.thoughtworks.util;");
		buffer.append("public class TestClass{");
		buffer.append("public void test(){}");
		buffer.append("}");
		return buffer.toString();
	}
	
}
