package com.thoughtworks.qdox;

import java.io.Reader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.ModelBuilder;
import com.thoughtworks.qdox.parser.Lexer;
import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;
import com.thoughtworks.qdox.directorywalker.DirectoryScanner;
import com.thoughtworks.qdox.directorywalker.FileVisitor;
import com.thoughtworks.qdox.directorywalker.SuffixFilter;

public class MultipleJavaDocBuilder {
	private Map classes = new HashMap();
	private List classesParsed = new ArrayList();
	private List sources = new ArrayList();

	private void addClasses(JavaSource source) {
		JavaClass[] javaClasses = source.getClasses();
		for (int classIndex = 0; classIndex < javaClasses.length; classIndex++) {
			JavaClass javaClass = javaClasses[classIndex];
			classes.put(javaClass.getFullyQualifiedName(), javaClass);
		}
	}

	public JavaClass getClassByName(String name) {
		return (JavaClass)classes.get(name);
	}

	public void addSource(Reader reader) {
		ModelBuilder builder = new ModelBuilder(classesParsed);
		Lexer lexer = new JFlexLexer(reader);
		Parser parser = new Parser(lexer, builder);
		parser.parse();
		JavaSource source = builder.getSource();
		sources.add(source);
		addClasses(source);
	}

	public JavaSource[] getSources() {
		return (JavaSource[])sources.toArray(new JavaSource[sources.size()]);
	}

	public void addSourceTree(File file) {
		DirectoryScanner scanner = new DirectoryScanner(file);
		scanner.addFilter(new SuffixFilter(".java"));
		scanner.scan(new FileVisitor() {
			public void visitFile(File currentFile) {
				try {
					addSource(new FileReader(currentFile));
				}
				catch (FileNotFoundException e) {
					throw new RuntimeException("Cannot read file : " + currentFile.getName());
				}
			}
		});
	}

}
