package com.thoughtworks.qdox.directorywalker;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class DirectoryScanner {
    
    private File file;
    private Collection<Filter> filters = new HashSet<Filter>();

    public DirectoryScanner(File file) {
        this.file = file;
    }

    public List<File> scan() {
        final List<File> result = new LinkedList<File>();
        walk( new FileVisitor() {
            public void visitFile(File file) {
                result.add(file);
            }
        }, this.file);
        return result;
    }

    private void walk(FileVisitor visitor, File current) {
        if (current.isDirectory()) {
            File[] currentFiles = current.listFiles();
            for (int i = 0; i < currentFiles.length; i++) {
                walk(visitor, currentFiles[i]);
            }
        } else {
            for (Filter filter : this.filters) {
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
