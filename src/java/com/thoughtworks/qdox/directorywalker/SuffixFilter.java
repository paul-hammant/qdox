package com.thoughtworks.qdox.directorywalker;

import java.io.File;

/**
 * @author dummy="${replaceme}"
 */
public class SuffixFilter implements Filter {
    private String _suffixFilter;

    public SuffixFilter(String suffixFilter) {
        _suffixFilter = suffixFilter;
    }

    public boolean filter(File file) {
        return file.getName().endsWith(_suffixFilter);
    }
}
