package com.thoughtworks.qdox.directorywalker;

import java.io.File;

public interface Filter {

	boolean filter(File file);

}
