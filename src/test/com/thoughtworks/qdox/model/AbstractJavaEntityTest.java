package com.thoughtworks.qdox.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.io.IndentBuffer;

import junit.framework.TestCase;

public class AbstractJavaEntityTest extends TestCase {

    private AbstractJavaEntity newAbstractJavaEntity() {
        return new AbstractJavaEntity()
        {
            public int compareTo( Object arg0 )
            {
                return 0;
            }
            
            public String getCodeBlock()
            {
                return null;
            }
        };
    }
    
    public AbstractJavaEntityTest(String s) {
        super(s);
    }

    public void testGetTagsByNameMethod() throws Exception {
        AbstractBaseJavaEntity entity = newAbstractJavaEntity();
        List<DocletTag> tags = new LinkedList<DocletTag>();
        tags.add(new DefaultDocletTag("monkey", "is good"));
        tags.add(new DefaultDocletTag("monkey", "is funny"));
        tags.add(new DefaultDocletTag("horse", "not so much"));
        entity.setTags(tags);

        assertEquals(2, entity.getTagsByName("monkey").size());
        assertEquals(1, entity.getTagsByName("horse").size());
        assertEquals(0, entity.getTagsByName("non existent tag").size());
    }

    public void testGetSingleTagByName() throws Exception {

        AbstractBaseJavaEntity entity = newAbstractJavaEntity();
        List<DocletTag> tags = new LinkedList<DocletTag>();
        tags.add(new DefaultDocletTag("monkey", "is good"));
        tags.add(new DefaultDocletTag("monkey", "is funny"));
        tags.add(new DefaultDocletTag("horse", "not so much"));
        entity.setTags(tags);

        assertEquals("is good", entity.getTagByName("monkey").getValue());
        assertEquals("not so much", entity.getTagByName("horse").getValue());
        assertNull(entity.getTagByName("cow"));

    }

    public void testPublicModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(Arrays.asList(new String[]{"public"}));
        assertTrue(entity.isPublic());
    }

    public void testPrivateModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(Arrays.asList(new String[]{"private"}));
        assertTrue(entity.isPrivate());
    }

    public void testAbstractModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(Arrays.asList(new String[]{"public", "abstract"}));
        assertTrue(entity.isAbstract());
        assertTrue(!entity.isPrivate());
    }

    public void testProtectedModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(Arrays.asList(new String[]{"protected", "abstract", "synchronized", "transient"}));
        assertTrue(entity.isProtected());
        assertTrue(entity.isSynchronized());
        assertTrue(entity.isTransient());
    }

    public void testStaticModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(Arrays.asList(new String[]{"public", "static", "final"}));
        assertTrue(entity.isStatic());
        assertTrue(entity.isFinal());
    }

    public void testQDOX30() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(Arrays.asList(new String[]{"native", "volatile", "strictfp"}));
        assertTrue(entity.isNative());
        assertTrue(entity.isVolatile());
        assertTrue(entity.isStrictfp());
    }

    public void testGetTagsReturnsEmptyArrayInsteadOfNull() throws Exception {
    	AbstractJavaEntity entity = newAbstractJavaEntity();
        assertEquals(0, entity.getTags().size());
    }
}
