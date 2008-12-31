package com.thoughtworks.qdox.directorywalker;

import java.io.File;

public class SuffixFilter implements Filter {
    private String suffixFilter;

    public SuffixFilter(String suffixFilter) {
        this.suffixFilter = suffixFilter;
    }

    public boolean filter(File file) {
        return file.getName().endsWith(this.suffixFilter);
    }
}
