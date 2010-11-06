package com.thoughtworks.qdox.model;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class AbstractJavaEntityTest extends TestCase {

    private AbstractJavaEntity newAbstractJavaEntity() {
        return new AbstractJavaEntity()
        {
            public int compareTo( Object arg0 )
            {
                return 0;
            }
            
            protected void writeBody( IndentBuffer result )
            {
            }
        };
    }
    
    public AbstractJavaEntityTest(String s) {
        super(s);
    }

    public void testGetTagsByNameMethod() throws Exception {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        List tags = new LinkedList();
        tags.add(new DefaultDocletTag("monkey", "is good"));
        tags.add(new DefaultDocletTag("monkey", "is funny"));
        tags.add(new DefaultDocletTag("horse", "not so much"));
        entity.setTags(tags);

        assertEquals(2, entity.getTagsByName("monkey").length);
        assertEquals(1, entity.getTagsByName("horse").length);
        assertEquals(0, entity.getTagsByName("non existent tag").length);
    }

    public void testGetSingleTagByName() throws Exception {

        AbstractJavaEntity entity = newAbstractJavaEntity();
        List tags = new LinkedList();
        tags.add(new DefaultDocletTag("monkey", "is good"));
        tags.add(new DefaultDocletTag("monkey", "is funny"));
        tags.add(new DefaultDocletTag("horse", "not so much"));
        entity.setTags(tags);

        assertEquals("is good", entity.getTagByName("monkey").getValue());
        assertEquals("not so much", entity.getTagByName("horse").getValue());
        assertNull(entity.getTagByName("cow"));

    }

    public void testCommentToString() {
        // setup
        AbstractJavaEntity entity = newAbstractJavaEntity();
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
    
    public void testMultilineCommentToString() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        IndentBuffer buffer = new IndentBuffer();
        entity.setComment("Hello\nWorld");

        // expectation
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " * World\n"
                + " */\n";

        // run
        entity.commentHeader(buffer);

        // verify
        assertEquals(expected, buffer.toString());
    	
    }

    public void testNoCommentToString() {
        // setup
        AbstractJavaEntity entity = newAbstractJavaEntity();
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
        AbstractJavaEntity entity = newAbstractJavaEntity();
        IndentBuffer buffer = new IndentBuffer();
        entity.setComment("Hello");
        List tags = new LinkedList();
        tags.add(new DefaultDocletTag("monkey", "is in the tree"));
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
        AbstractJavaEntity entity = newAbstractJavaEntity();
        IndentBuffer buffer = new IndentBuffer();
        entity.setComment("Hello");
        List tags = new LinkedList();
        tags.add(new DefaultDocletTag("monkey", "is in the tree"));
        tags.add(new DefaultDocletTag("see", "the doctor"));
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
        AbstractJavaEntity entity = newAbstractJavaEntity();
        IndentBuffer buffer = new IndentBuffer();
        List tags = new LinkedList();
        tags.add(new DefaultDocletTag("monkey", "is in the tree"));
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
        AbstractJavaEntity entity = newAbstractJavaEntity();
        IndentBuffer buffer = new IndentBuffer();
        List tags = new LinkedList();
        tags.add(new DefaultDocletTag("monkey", ""));
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
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(new String[]{"public"});
        assertTrue(entity.isPublic());
    }

    public void testPrivateModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(new String[]{"private"});
        assertTrue(entity.isPrivate());
    }

    public void testAbstractModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(new String[]{"public", "abstract"});
        assertTrue(entity.isAbstract());
        assertTrue(!entity.isPrivate());
    }

    public void testProtectedModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(new String[]{"protected", "abstract", "synchronized", "transient"});
        assertTrue(entity.isProtected());
        assertTrue(entity.isSynchronized());
        assertTrue(entity.isTransient());
    }

    public void testStaticModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(new String[]{"public", "static", "final"});
        assertTrue(entity.isStatic());
        assertTrue(entity.isFinal());
    }

    public void testQDOX30() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(new String[]{"native", "volatile", "strictfp"});
        assertTrue(entity.isNative());
        assertTrue(entity.isVolatile());
        assertTrue(entity.isStrictfp());
    }

    public void testGetTagsReturnsEmptyArrayInsteadOfNull() throws Exception {
        JavaModel entity = newAbstractJavaEntity();
        assertEquals(0, entity.getTags().length);
    }
}
