package com.thoughtworks.qdox.ant;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public abstract class AbstractQdoxTask extends Task {
	private Vector filesets = new Vector();
	protected HashMap fileMap = new HashMap();
	protected ArrayList allSources = new ArrayList();
	protected ArrayList allClasses = new ArrayList();

	public void addFileset(FileSet set) {
		filesets.addElement(set);
	}

	protected void buildFileMap() {
		for (int i = 0; i < filesets.size(); i++) {
			FileSet fs = (FileSet) filesets.elementAt(i);
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			String[] srcFiles = ds.getIncludedFiles();
			buildFileMap(fs.getDir(getProject()) , srcFiles);
		}
	}

	protected void buildFileMap(File directory, String[] sourceFiles) {
		for (int i = 0; i < sourceFiles.length; i++) {
			File src = new File(directory, sourceFiles[i]);
			fileMap.put(src.getAbsolutePath(), src);
		}
	}

	public void execute() throws BuildException {
		validateAttributes();
		buildFileMap();
		JavaDocBuilder builder = new JavaDocBuilder();
		mergeBuilderSources(builder);
		JavaSource[] sources = builder.getSources();
		processSources(sources);
	}

	private void mergeBuilderSources(JavaDocBuilder builder) {
		for (Iterator iterator = fileMap.keySet().iterator(); iterator.hasNext();) {
			String sourceFile = (String) iterator.next();
			builder.addSourceTree((File)fileMap.get(sourceFile));
			
		}
	}
	
	protected void processSources(JavaSource[] sources) {
		for (int i = 0; i < sources.length; i++) {
			JavaSource source = sources[i];
			allSources.add(source);
			JavaClass[] classes = source.getClasses();
			processClasses(classes);
		}
	}

	protected void processClasses(JavaClass[] classes) {
		for (int j = 0; j < classes.length; j++) {
			JavaClass clazz = classes[j];
			allClasses.add(clazz);
		}
	}

	protected void validateAttributes() throws BuildException {
		if (filesets.size() == 0) {
			throw new BuildException("Specify at least one source fileset.");
		}
	}

}
