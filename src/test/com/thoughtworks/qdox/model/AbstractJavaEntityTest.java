package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

import java.util.List;
import java.util.LinkedList;

public class AbstractJavaEntityTest extends TestCase {

    public AbstractJavaEntityTest(String s) {
        super(s);
    }

    public void testGetTagsByNameMethod() throws Exception {
        AbstractJavaEntity entity = new JavaField();
        List tags = new LinkedList();
        tags.add(new DocletTag("monkey", "is good"));
        tags.add(new DocletTag("monkey", "is funny"));
        tags.add(new DocletTag("horse", "not so much"));
        entity.setTags(tags);

        assertEquals(2, entity.getTagsByName("monkey").length);
        assertEquals(1, entity.getTagsByName("horse").length);
        assertEquals(0, entity.getTagsByName("non existent tag").length);
    }

    public void testGetSingleTagByName() throws Exception {

        AbstractJavaEntity entity = new JavaField();
        List tags = new LinkedList();
        tags.add(new DocletTag("monkey", "is good"));
        tags.add(new DocletTag("monkey", "is funny"));
        tags.add(new DocletTag("horse", "not so much"));
        entity.setTags(tags);

        assertEquals("is good", entity.getTagByName("monkey").getValue());
        assertEquals("not so much", entity.getTagByName("horse").getValue());
        assertNull(entity.getTagByName("cow"));

    }

    public void testCommentToString() {
        // setup
        AbstractJavaEntity entity = new JavaField();
        IndentBuffer buffer = new IndentBuffer();
        entity.setComment("Hello");

        // expectation
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " */\n";

        // run
        entity.commentHeader(buffer);

        // verify
        assertEquals(expected, buffer.toString());
    }

    public void testNoCommentToString() {
        // setup
        AbstractJavaEntity entity = new JavaField();
        IndentBuffer buffer = new IndentBuffer();

        // expectation
        String expected = "";

        // run
        entity.commentHeader(buffer);

        // verify
        assertEquals(expected, buffer.toString());
    }

    public void testCommentWithTagToString() {
        // setup
        AbstractJavaEntity entity = new JavaField();
        IndentBuffer buffer = new IndentBuffer();
        entity.setComment("Hello");
        List tags = new LinkedList();
        tags.add(new DocletTag("monkey", "is in the tree"));
        entity.setTags(tags);

        // expectation
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " *\n"
                + " * @monkey is in the tree\n"
                + " */\n";

        // run
        entity.commentHeader(buffer);

        // verify
        assertEquals(expected, buffer.toString());
    }

    public void testCommentWithMultipleTagsToString() {
        // setup
        AbstractJavaEntity entity = new JavaField();
        IndentBuffer buffer = new IndentBuffer();
        entity.setComment("Hello");
        List tags = new LinkedList();
        tags.add(new DocletTag("monkey", "is in the tree"));
        tags.add(new DocletTag("see", "the doctor"));
        entity.setTags(tags);

        // expectation
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " *\n"
                + " * @monkey is in the tree\n"
                + " * @see the doctor\n"
                + " */\n";

        // run
        entity.commentHeader(buffer);

        // verify
        assertEquals(expected, buffer.toString());
    }

    public void testTagButNoCommentToString() {
        // setup
        AbstractJavaEntity entity = new JavaField();
        IndentBuffer buffer = new IndentBuffer();
        List tags = new LinkedList();
        tags.add(new DocletTag("monkey", "is in the tree"));
        entity.setTags(tags);

        // expectation
        String expected = ""
                + "/**\n"
                + " * @monkey is in the tree\n"
                + " */\n";

        // run
        entity.commentHeader(buffer);

        // verify
        assertEquals(expected, buffer.toString());
    }

    public void testTagWithNoValueToString() {
        // setup
        AbstractJavaEntity entity = new JavaField();
        IndentBuffer buffer = new IndentBuffer();
        List tags = new LinkedList();
        tags.add(new DocletTag("monkey", ""));
        entity.setTags(tags);

        // expectation
        String expected = ""
                + "/**\n"
                + " * @monkey\n"
                + " */\n";

        // run
        entity.commentHeader(buffer);

        // verify
        assertEquals(expected, buffer.toString());
    }

    public void testPublicModifer() {
        AbstractJavaEntity entity = new JavaField();
        entity.setModifiers(new String[]{"public"});
        assertTrue(entity.isPublic());
    }

    public void testPrivateModifer() {
        AbstractJavaEntity entity = new JavaField();
        entity.setModifiers(new String[]{"private"});
        assertTrue(entity.isPrivate());
    }

    public void testAbstractModifer() {
        AbstractJavaEntity entity = new JavaField();
        entity.setModifiers(new String[]{"public", "abstract"});
        assertTrue(entity.isAbstract());
        assertTrue(!entity.isPrivate());
    }

    public void testProtectedModifer() {
        AbstractJavaEntity entity = new JavaField();
        entity.setModifiers(new String[]{"protected", "abstract", "synchronized", "transient"});
        assertTrue(entity.isProtected());
        assertTrue(entity.isSynchronized());
        assertTrue(entity.isTransient());
    }

    public void testStaticModifer() {
        AbstractJavaEntity entity = new JavaField();
        entity.setModifiers(new String[]{"public", "static", "final"});
        assertTrue(entity.isStatic());
        assertTrue(entity.isFinal());
    }

    public void testGetTagsReturnsEmptyArrayInsteadOfNull() throws Exception {
        AbstractJavaEntity entity = new JavaField();
        assertEquals(0, entity.getTags().length);
    }

}
