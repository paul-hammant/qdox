package com.thoughtworks.qdox.model;

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
}
