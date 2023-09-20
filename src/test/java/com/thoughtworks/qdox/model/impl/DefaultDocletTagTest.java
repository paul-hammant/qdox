package com.thoughtworks.qdox.model.impl;

import com.thoughtworks.qdox.model.AbstractDocletTagTest;
import com.thoughtworks.qdox.model.DocletTagFactory;
import com.thoughtworks.qdox.model.util.SerializationUtils;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultDocletTagTest extends AbstractDocletTagTest {

    private final DocletTagFactory docletTagFactory = new DefaultDocletTagFactory();

    @Override
	protected DocletTagFactory getDocletTagFactory() {
        return docletTagFactory;
    }

    @Test
    public void testJiraQdox60() {
        DefaultDocletTag tag = new DefaultDocletTag("author", "<a href=\"mailto:dev@excalibur.apache.org\">Excalibur Development Team</a>");

        tag = (DefaultDocletTag) SerializationUtils.serializedCopy(tag);

        Map<String, String> paramMap = tag.getNamedParameterMap();
        assertEquals(0, paramMap.size());        
    }
    
}
