package com.thoughtworks.qdox.model;

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.Serializable;

abstract class AbstractJavaEntity implements Serializable {

	protected String name;
	protected List modifiers = new ArrayList();
	private String comment;
	private List tags = new ArrayList();

	public String getName() {
		return name;
	}

	/**
	 * Return list of modifiers as Strings.
	 * (public, private, protected, final, abstract, static)
	 */
	public String[] getModifiers() {
		return (String[])modifiers.toArray(new String[modifiers.size()]);
	}

	public String getComment() {
		return comment;
	}

	public int getTagCount() {
		return tags.size();
	}

	public DocletTag getTag(int i) {
		return (DocletTag)tags.get(i);
	}

    public DocletTag[] getTags(String name){
        List specifiedTags = new ArrayList();
        for (Iterator iterator = tags.iterator(); iterator.hasNext();) {
            DocletTag docletTag = (DocletTag) iterator.next();
            if (docletTag.getName().equals(name))
                specifiedTags.add(docletTag);
        }
        return (DocletTag[]) specifiedTags.toArray(new DocletTag[specifiedTags.size()]);
    }

	void commentHeader(IndentBuffer buffer) {
		if (comment == null && (tags == null || tags.size() == 0)) {
			return;
		}
		else {
			buffer.write("/**");
			buffer.newline();

			if (comment != null && comment.length() > 0) {
				buffer.write(" * ");
				buffer.write(comment);
				buffer.newline();
			}

			if (tags != null && tags.size() > 0) {
				if (comment != null && comment.length() > 0) {
					buffer.write(" *");
					buffer.newline();
				}
				for (Iterator iterator = tags.iterator(); iterator.hasNext();) {
					DocletTag docletTag = (DocletTag)iterator.next();
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

	public String toString() {
		IndentBuffer result = new IndentBuffer();
		write(result);
		return result.toString();
	}

	protected void write(IndentBuffer result) {
		commentHeader(result);
		writeBody(result);
	}

	protected abstract void writeBody(IndentBuffer result);

	public void setName(String name) {
		this.name = name;
	}

	public void setModifiers(String[] modifiers) {
		this.modifiers= Arrays.asList(modifiers);
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setTags(List tags) {
		this.tags = tags;
	}

	//helper methods for querying the modifiers
	public boolean isAbstract(){
		return isModifierPresent("abstract");
	}
	
	public boolean isPublic(){
		return isModifierPresent("public");
	}

	public boolean isPrivate(){
		return isModifierPresent("private");
	}
	
	public boolean isProtected(){
		return isModifierPresent("protected");
	}
	
	public boolean isStatic(){
		return isModifierPresent("static");
	}

	public boolean isFinal(){
		return isModifierPresent("final");
	}
	
	public boolean isSynchronized(){
		return isModifierPresent("synchronized");
	}
	
	private boolean isModifierPresent(String modifier) {
		return modifiers.contains(modifier);
	}
	
	protected void writeNonAccessibilityModifiers(IndentBuffer result) {
		// modifiers (anything else)
		for (Iterator iter = modifiers.iterator(); iter.hasNext();) {
			String modifier = (String) iter.next();
			if (!modifier.startsWith("p")) {
				result.write(modifier);
				result.write(' ');
			}
		}
	}

	protected void writeAccessibilityModifier(IndentBuffer result) {
		for (Iterator iter = modifiers.iterator(); iter.hasNext();) {
			String modifier = (String) iter.next();
			if (modifier.startsWith("p")) {
				result.write(modifier);
				result.write(' ');
			}
		}
	}

	protected void writeAllModifiers(IndentBuffer result) {
		for (Iterator iter = modifiers.iterator(); iter.hasNext();) {
			String modifier = (String) iter.next();
				result.write(modifier);
				result.write(' ');
		}
	}

}
