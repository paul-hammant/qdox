package com.thoughtworks.qdox.model;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultDocletTagFactory implements DocletTagFactory {
    public DocletTag createDocletTag(String tag, String text) {
        return new DefaultDocletTag(tag, text);
    }
}
