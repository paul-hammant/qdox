package com.thoughtworks.qdox.directorywalker;

import java.io.File;
import java.util.*;

public class DirectoryScanner {
	private File _file;
  private Collection _filters = new HashSet();

	public DirectoryScanner(File file) {
		_file = file;
	}

	public File[] scan() {
		final List results = new ArrayList();
		walk(new FileVisitor() {
			public void visitFile(File file) {
				results.add(file);
			}
		}, _file);
		File[] resultsArray = new File[results.size()];
		results.toArray(resultsArray);
		return resultsArray;
	}

	private void walk(FileVisitor visitor, File current) {
		if (current.isDirectory()) {
			File [] currentFiles = current.listFiles();
			for (int i = 0; i < currentFiles.length; i++) {
				walk(visitor, currentFiles[i]);
			}
		}
		else {
			for (Iterator iterator = _filters.iterator(); iterator.hasNext();) {
				Filter filter = (Filter) iterator.next();
				if(!filter.filter(current)) {
					return;
				}
			}
			visitor.visitFile(current);
		}
	}

	public void addFilter(Filter filter) {
		_filters.add(filter);
	}

	public void scan(FileVisitor fileVisitor) {
		walk(fileVisitor, _file);
	}
}
