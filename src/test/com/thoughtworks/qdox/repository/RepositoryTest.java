package com.thoughtworks.qdox.repository;

import junit.framework.TestCase;

import java.io.File;

import com.thoughtworks.qdox.model.JavaSource;

public class RepositoryTest extends TestCase {

	class MockFile extends File {

		private String contents;
		public MockFile(String pathname) {
			super(pathname);
		}

		public MockFile(String pathname, String contents) {
			super(pathname);
			this.contents = contents;
		}

	}
	public RepositoryTest(String s) {
		super(s);
	}

	public void testFileCount() throws Exception {
		Repository repository = new Repository();
		repository.visitFile(new MockFile("A.java"));
		repository.visitFile(new MockFile("B.java"));
		repository.visitFile(new MockFile("C.java"));
		assertEquals(3, repository.getSourceCount());
	}

}
