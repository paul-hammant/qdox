package com.thoughtworks.qdox.model.impl;

import java.util.Map;

import com.thoughtworks.qdox.model.AbstractDocletTagTest;
import com.thoughtworks.qdox.model.DefaultDocletTagFactory;
import com.thoughtworks.qdox.model.DocletTagFactory;
import com.thoughtworks.qdox.model.util.SerializationUtils;

/**
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultDocletTagTest extends AbstractDocletTagTest {

    public DefaultDocletTagTest(String name) {
        super(name);
    }

    private final DocletTagFactory docletTagFactory = new DefaultDocletTagFactory();

    protected DocletTagFactory getDocletTagFactory() {
        return docletTagFactory;
    }
    
    public void testJiraQdox60() throws Exception {
        DefaultDocletTag tag = new DefaultDocletTag("author", "<a href=\"mailto:dev@excalibur.apache.org\">Excalibur Development Team</a>");

        tag = (DefaultDocletTag) SerializationUtils.serializedCopy(tag);

        Map paramMap = tag.getNamedParameterMap();
        assertEquals(0, paramMap.size());        
    }
    
}
