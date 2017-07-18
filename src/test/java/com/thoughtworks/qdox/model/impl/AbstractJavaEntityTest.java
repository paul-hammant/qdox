package com.thoughtworks.qdox.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.thoughtworks.qdox.model.DocletTag;

public class AbstractJavaEntityTest {

    private AbstractJavaEntity newAbstractJavaEntity() {
        return new AbstractJavaEntity()
        {
            public String getCodeBlock()
            {
                return null;
            }
        };
    }
    
    @Test
    public void testGetTagsByNameMethod() {
        AbstractBaseJavaEntity entity = newAbstractJavaEntity();
        List<DocletTag> tags = new LinkedList<DocletTag>();
        
        DocletTag monkeyIsGoodTag = mock(DocletTag.class);
        when(monkeyIsGoodTag.getName()).thenReturn( "monkey" );
        DocletTag monkeyIsFunnyTag = mock(DocletTag.class);
        when(monkeyIsFunnyTag.getName()).thenReturn( "monkey" );
        DocletTag horseNotSoMuchTag = mock(DocletTag.class);
        when(horseNotSoMuchTag.getName()).thenReturn( "horse" );
        
        tags.add(monkeyIsGoodTag);
        tags.add(monkeyIsFunnyTag);
        tags.add(horseNotSoMuchTag);
        entity.setTags(tags);

        assertEquals(2, entity.getTagsByName("monkey").size());
        assertEquals(1, entity.getTagsByName("horse").size());
        assertEquals(0, entity.getTagsByName("non existent tag").size());
    }

    @Test
    public void testGetSingleTagByName() {
        AbstractBaseJavaEntity entity = newAbstractJavaEntity();
        List<DocletTag> tags = new LinkedList<DocletTag>();

        DocletTag monkeyIsGoodTag = mock(DocletTag.class);
        when(monkeyIsGoodTag.getName()).thenReturn( "monkey" );
        when(monkeyIsGoodTag.getValue()).thenReturn( "is good" );
        DocletTag monkeyIsFunnyTag = mock(DocletTag.class);
        when(monkeyIsFunnyTag.getName()).thenReturn( "monkey" );
        when(monkeyIsFunnyTag.getValue()).thenReturn( "is funny" );
        DocletTag horseNotSoMuchTag = mock(DocletTag.class);
        when(horseNotSoMuchTag.getName()).thenReturn( "horse" );
        when(horseNotSoMuchTag.getValue()).thenReturn( "not so much" );
        
        tags.add(monkeyIsGoodTag);
        tags.add(monkeyIsFunnyTag);
        tags.add(horseNotSoMuchTag);
        entity.setTags(tags);

        assertEquals("is good", entity.getTagByName("monkey").getValue());
        assertEquals("not so much", entity.getTagByName("horse").getValue());
        assertNull(entity.getTagByName("cow"));

    }

    @Test
    public void testPublicModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers( Collections.singletonList( "public" ) );
        assertTrue( entity.isPublic() );
    }

    @Test
    public void testPrivateModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(Collections.singletonList( "private" ));
        assertTrue(entity.isPrivate());
    }

    @Test
    public void testAbstractModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(Arrays.asList(new String[]{"public", "abstract"}));
        assertTrue(entity.isAbstract());
        assertTrue(!entity.isPrivate());
    }

    @Test
    public void testProtectedModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(Arrays.asList(new String[]{"protected", "abstract", "synchronized", "transient"}));
        assertTrue(entity.isProtected());
        assertTrue(entity.isSynchronized());
        assertTrue(entity.isTransient());
    }

    @Test
    public void testStaticModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(Arrays.asList(new String[]{"public", "static", "final"}));
        assertTrue(entity.isStatic());
        assertTrue(entity.isFinal());
    }

    @Test
    public void testQDOX30() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(Arrays.asList(new String[]{"native", "volatile", "strictfp"}));
        assertTrue(entity.isNative());
        assertTrue(entity.isVolatile());
        assertTrue(entity.isStrictfp());
    }

    @Test
    public void testGetTagsReturnsEmptyArrayInsteadOfNull() {
    	AbstractJavaEntity entity = newAbstractJavaEntity();
        assertEquals(0, entity.getTags().size());
    }
}
