package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class ClassLibraryTest extends TestCase {

	public ClassLibraryTest(String s) {
		super(s);
	}

	public void testAdd() throws Exception {
		ClassLibrary c = new ClassLibrary();
		c.add("com.blah.Foo");
		c.add("com.moo.Poo");
		assertTrue(c.contains("com.blah.Foo"));
		assertTrue(c.contains("com.moo.Poo"));
		assertTrue(!c.contains("com.not.You"));
	}

	public void testSearch() throws Exception {
		ClassLibrary c = new ClassLibrary();
		c.add("com.blah.Foo");
		c.add("com.thing.Foo");
		c.add("com.x.Goat");
		c.add("com.y.Goat");
		c.add("com.moo.Poo");
		c.add("com.me.Spoon");

		Collection imports = new HashSet();
		imports.add("com.blah.Foo");
		imports.add("com.x.*");

		assertEquals("com.blah.Foo", c.findClass(imports, "com.me", "Foo"));
		assertEquals("com.x.Goat", c.findClass(imports, "com.me", "Goat"));
		assertEquals("com.me.Spoon", c.findClass(imports, "com.me", "Spoon"));
		assertNull(c.findClass(imports, "com.me", "Poo"));
	}

	public void testListAll() throws Exception {
		ClassLibrary c = new ClassLibrary();
		c.add("com.blah.Foo");
		c.add("com.thing.Foo");
		c.add("com.x.Goat");
		c.add("com.y.Goat");

		Collection all = c.all();
		assertTrue(all.contains("com.blah.Foo"));
		assertTrue(all.contains("com.thing.Foo"));
		assertTrue(all.contains("com.x.Goat"));
		assertTrue(all.contains("com.y.Goat"));

		assertTrue(!all.contains("com.not.True"));
		assertEquals(4, all.size());
	}

	public void testNoClassLoaders() throws Exception {
		ClassLibrary c = new ClassLibrary();
		assertTrue(!c.contains("java.lang.String"));
	}

	public void testDefaultClassLoaders() throws Exception {
		ClassLibrary c = new ClassLibrary();
		c.addClassLoader(getClass().getClassLoader());
		assertTrue(c.contains("java.lang.String"));
		assertTrue(c.contains("java.util.Collection"));
	}
}
