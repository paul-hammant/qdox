package com.thoughtworks.qdox.mytest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;

public class TestImplements {

	@Test
	public void test() {
		JavaProjectBuilder builder = new JavaProjectBuilder();
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File fileItf1 = new File(classLoader.getResource("test/Itf1.java").getFile());
			File fileItf2 = new File(classLoader.getResource("test/Itf2.java").getFile());
			File a = new File(classLoader.getResource("test/A.java").getFile());

			builder.addSource(fileItf1);
			builder.addSource(fileItf2);
			builder.addSource(a);
			JavaClass classA = builder.getClassByName("A$B");
			assertTrue(classA.getImplements().equals(Arrays.asList(builder.getClassByName("Itf2"))));
		} catch (IOException e) {
			fail("file not found");
		}
	}
}
