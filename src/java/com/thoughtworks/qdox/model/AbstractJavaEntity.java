package com.thoughtworks.qdox.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractJavaEntity extends AbstractBaseJavaEntity implements Comparable, JavaModel {

    protected List<String> modifiers = new LinkedList<String>();
    private String comment;
    private List<DocletTag> tags = new LinkedList<DocletTag>();
    
    private JavaClass parentClass;
    /**
     * Return list of modifiers as Strings.
     * (public, private, protected, final, abstract, static)
     */
    public List<String> getModifiers() {
        return modifiers;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaModel#getComment()
     */
    public String getComment() {
        return comment;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaModel#getTags()
     */
    public List<DocletTag> getTags() {
        return tags;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaModel#getTagsByName(java.lang.String)
     */
    public List<DocletTag> getTagsByName(String name) {
        List<DocletTag> specifiedTags = new LinkedList<DocletTag>();
        for ( DocletTag docletTag : tags ) {
            if (docletTag.getName().equals(name)) {
                specifiedTags.add(docletTag);
            }
        }
        return specifiedTags;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaModel#getTagByName(java.lang.String)
     */
    public DocletTag getTagByName(String name) {
        for (DocletTag docletTag : tags) {
            if (docletTag.getName().equals(name)) {
                return docletTag;
            }
        }
        return null;
    }

    /**
     * Convenience method for <code>getTagByName(String).getNamedParameter(String)</code>
     * that also checks for null tag.
     * @since 1.3
     */
    public String getNamedParameter(String tagName, String parameterName) {
        DocletTag tag = getTagByName(tagName);
        if(tag != null) {
            return tag.getNamedParameter(parameterName);
        } else {
            return null;
        }
    }

    void commentHeader(IndentBuffer buffer) {
        if (comment == null && tags.isEmpty()) {
            return;
        } else {
            buffer.write("/**");
            buffer.newline();

            if (comment != null && comment.length() > 0) {
                buffer.write(" * ");
                
                buffer.write(comment.replaceAll("\n", "\n * "));
                
                buffer.newline();
            }

            if (!tags.isEmpty()) {
                if (comment != null && comment.length() > 0) {
                    buffer.write(" *");
                    buffer.newline();
                }
                for (DocletTag docletTag : tags) {
                    buffer.write(" * @");
                    buffer.write(docletTag.getName());
                    if (docletTag.getValue().length() > 0) {
                        buffer.write(' ');
                        buffer.write(docletTag.getValue());
                    }
                    buffer.newline();
                }
            }

            buffer.write(" */");
            buffer.newline();
        }
    }

    public void setModifiers(String[] modifiers) {
        this.modifiers = Arrays.asList(modifiers);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTags(List<DocletTag> tagList) {
        this.tags = tagList;
    }

    //helper methods for querying the modifiers
    public boolean isAbstract() {
        return isModifierPresent("abstract");
    }

    public boolean isPublic() {
        return isModifierPresent("public");
    }

    public boolean isPrivate() {
        return isModifierPresent("private");
    }

    public boolean isProtected() {
        return isModifierPresent("protected");
    }

    public boolean isStatic() {
        return isModifierPresent("static");
    }

    public boolean isFinal() {
        return isModifierPresent("final");
    }

    public boolean isSynchronized() {
        return isModifierPresent("synchronized");
    }

    public boolean isTransient() {
        return isModifierPresent("transient");
    }

	/**
	 * @since 1.4
	 */
    public boolean isVolatile() {
        return isModifierPresent("volatile");
    }

	/**
	 * @since 1.4
	 */
    public boolean isNative() {
        return isModifierPresent("native");
    }

	/**
	 * @since 1.4
	 */
    public boolean isStrictfp() {
        return isModifierPresent("strictfp");
    }

    private boolean isModifierPresent(String modifier) {
        return modifiers.contains(modifier);
    }

    protected void writeNonAccessibilityModifiers(IndentBuffer result) {
        // modifiers (anything else)
        for (Iterator<String> iter = modifiers.iterator(); iter.hasNext();) {
            String modifier = (String) iter.next();
            if (!modifier.startsWith("p")) {
                result.write(modifier);
                result.write(' ');
            }
        }
    }

    protected void writeAccessibilityModifier(IndentBuffer result) {
        for (Iterator<String> iter = modifiers.iterator(); iter.hasNext();) {
            String modifier = (String) iter.next();
            if (modifier.startsWith("p")) {
                result.write(modifier);
                result.write(' ');
            }
        }
    }

    protected void writeAllModifiers(IndentBuffer result) {
        for (Iterator<String> iter = modifiers.iterator(); iter.hasNext();) {
            String modifier = (String) iter.next();
            result.write(modifier);
            result.write(' ');
        }
    }
    
    /* (non-Javadoc)
     * @see com.thoughtworks.qdox.model.JavaModel#getSource()
     */
    public JavaSource getSource() { 
        return parentClass.getParentSource(); 
    }

    public void setParentClass( JavaClass parentClass )
    {
        this.parentClass = parentClass;
    }
    
    public JavaClass getParentClass()
    {
        return parentClass;
    }
}
