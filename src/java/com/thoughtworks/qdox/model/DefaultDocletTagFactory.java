package com.thoughtworks.qdox.model;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultDocletTagFactory implements DocletTagFactory {
    public DocletTag createDocletTag(String tag, String text, int lineNumber, JavaSource javaSource) {
        return new DefaultDocletTag(tag, text, lineNumber, javaSource);
    }

    public DocletTag createDocletTag(String tag, String text) {
        return createDocletTag(tag, text, 0, null);
    }
}
