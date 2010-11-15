package com.thoughtworks.qdox.model;

import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractInheritableJavaEntity extends AbstractJavaEntity {
    
    public DocletTag getTagByName(String name, boolean inherited) {
        List<DocletTag> tags = getTagsByName(name, inherited);
        return tags.size() > 0 ? tags.get(0) : null;
    }

    public abstract List<DocletTag> getTagsByName(String name, boolean inherited);
    
}
