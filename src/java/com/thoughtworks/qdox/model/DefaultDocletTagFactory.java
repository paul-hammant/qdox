package com.thoughtworks.qdox.model;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DefaultDocletTagFactory implements DocletTagFactory {

    public DocletTag createDocletTag(
        String tag, String text, 
        AbstractJavaEntity context, int lineNumber
    ) {
        return new DefaultDocletTag(tag, text, context, lineNumber);
    }

    public DocletTag createDocletTag(String tag, String text) {
        return createDocletTag(tag, text, null, 0);
    }
    
}
