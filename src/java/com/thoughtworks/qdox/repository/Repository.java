package com.thoughtworks.qdox.repository;

import com.thoughtworks.qdox.directorywalker.FileVisitor;

import java.io.File;

public class Repository implements FileVisitor {

	private int sourceCount = 0;

	public int getSourceCount() {
		return sourceCount;
	}

	public void visitFile(File file) {
		sourceCount++;
	}

}
