package com.thoughtworks.qdox.model;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultDocletTagFactory implements DocletTagFactory {
    public DocletTag createDocletTag(String tag, String text, int lineNumber) {
        return new DefaultDocletTag(tag, text, lineNumber);
    }

    public DocletTag createDocletTag(String tag, String text) {
        return createDocletTag(tag, text, 0);
    }
}
