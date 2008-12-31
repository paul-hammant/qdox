package com.thoughtworks.qdox.directorywalker;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class DirectoryScanner {
    private File file;
    private Collection filters = new HashSet();

    public DirectoryScanner(File file) {
        this.file = file;
    }

    public File[] scan() {
        final List results = new ArrayList();
        walk(new FileVisitor() {
            public void visitFile(File file) {
                results.add(file);
            }
        }, this.file);
        File[] resultsArray = new File[results.size()];
        results.toArray(resultsArray);
        return resultsArray;
    }

    private void walk(FileVisitor visitor, File current) {
        if (current.isDirectory()) {
            File[] currentFiles = current.listFiles();
            for (int i = 0; i < currentFiles.length; i++) {
                walk(visitor, currentFiles[i]);
            }
        } else {
            for (Iterator iterator = this.filters.iterator(); iterator.hasNext();) {
                Filter filter = (Filter) iterator.next();
                if (!filter.filter(current)) {
                    return;
                }
            }
            visitor.visitFile(current);
        }
    }

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    public void scan(FileVisitor fileVisitor) {
        walk(fileVisitor, this.file);
    }
}
