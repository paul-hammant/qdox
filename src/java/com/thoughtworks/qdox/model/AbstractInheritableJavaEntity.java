package com.thoughtworks.qdox.model;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractInheritableJavaEntity extends AbstractJavaEntity {
    public DocletTag getTagByName(String name, boolean inherited) {
        DocletTag[] tags = getTagsByName(name, inherited);
        return tags.length > 0 ? tags[0] : null;
    }

    public abstract DocletTag[] getTagsByName(String name, boolean inherited);
}
