package com.thoughtworks.qdox.model;

public class DocletTag {

	private String name;
	private String value;

	public DocletTag(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

}
