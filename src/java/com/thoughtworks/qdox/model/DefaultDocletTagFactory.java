package com.thoughtworks.qdox.model;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultDocletTagFactory implements DocletTagFactory {
    public DefaultDocletTagFactory() {
    }

    public DocletTag createDocletTag(String tag, String text) {
        return new DocletTag(tag, text);
    }
}
