package com.thoughtworks.qdox.model;

import java.io.Serializable;

public class DocletTag implements Serializable {

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
