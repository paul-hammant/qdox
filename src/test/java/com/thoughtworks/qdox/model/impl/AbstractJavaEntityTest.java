package com.thoughtworks.qdox.model.impl;

import com.thoughtworks.qdox.model.DocletTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        Assertions.assertEquals(2, entity.getTagsByName("monkey").size());
        Assertions.assertEquals(1, entity.getTagsByName("horse").size());
        Assertions.assertEquals(0, entity.getTagsByName("non existent tag").size());
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

        Assertions.assertEquals("is good", entity.getTagByName("monkey").getValue());
        Assertions.assertEquals("not so much", entity.getTagByName("horse").getValue());
        Assertions.assertNull(entity.getTagByName("cow"));

    }

    @Test
    public void testPublicModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers( Collections.singletonList( "public" ) );
        Assertions.assertTrue(entity.isPublic());
    }

    @Test
    public void testPrivateModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(Collections.singletonList( "private" ));
        Assertions.assertTrue(entity.isPrivate());
    }

    @Test
    public void testAbstractModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(Arrays.asList(new String[]{"public", "abstract"}));
        Assertions.assertTrue(entity.isAbstract());
        Assertions.assertTrue(!entity.isPrivate());
    }

    @Test
    public void testProtectedModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(Arrays.asList(new String[]{"protected", "abstract", "synchronized", "transient"}));
        Assertions.assertTrue(entity.isProtected());
        Assertions.assertTrue(entity.isSynchronized());
        Assertions.assertTrue(entity.isTransient());
    }

    @Test
    public void testStaticModifer() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(Arrays.asList(new String[]{"public", "static", "final"}));
        Assertions.assertTrue(entity.isStatic());
        Assertions.assertTrue(entity.isFinal());
    }

    @Test
    public void testQDOX30() {
        AbstractJavaEntity entity = newAbstractJavaEntity();
        entity.setModifiers(Arrays.asList(new String[]{"native", "volatile", "strictfp"}));
        Assertions.assertTrue(entity.isNative());
        Assertions.assertTrue(entity.isVolatile());
        Assertions.assertTrue(entity.isStrictfp());
    }

    @Test
    public void testGetTagsReturnsEmptyArrayInsteadOfNull() {
    	AbstractJavaEntity entity = newAbstractJavaEntity();
        Assertions.assertEquals(0, entity.getTags().size());
    }
}
