package com.thoughtworks.qdox;

import com.thoughtworks.qdox.directorywalker.DirectoryScanner;
import com.thoughtworks.qdox.directorywalker.FileVisitor;
import com.thoughtworks.qdox.directorywalker.SuffixFilter;
import com.thoughtworks.qdox.model.ClassLibrary;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaClassCache;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.ModelBuilder;
import com.thoughtworks.qdox.parser.Lexer;
import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JavaDocBuilder implements Serializable, JavaClassCache{

	private Map classes = new HashMap();
	private ClassLibrary classLibrary;
	private List sources = new ArrayList();

	public JavaDocBuilder() {
		classLibrary = new ClassLibrary(this);
		classLibrary.addDefaultLoader();
	}

	private void addClasses(JavaSource source) {
		JavaClass[] javaClasses = source.getClasses();
		for (int classIndex = 0; classIndex < javaClasses.length; classIndex++) {
			JavaClass cls = javaClasses[classIndex];
			classes.put(cls.getFullyQualifiedName(), cls);
			cls.setJavaClassCache(this);
		}
	}

	public JavaClass getClassByName(String name) {
		return (JavaClass) classes.get(name);
	}

	public JavaSource addSource(Reader reader) {
		ModelBuilder builder = new ModelBuilder(classLibrary);
		Lexer lexer = new JFlexLexer(reader);
		Parser parser = new Parser(lexer, builder);
		parser.parse();
		JavaSource source = builder.getSource();
		sources.add(source);
		addClasses(source);
		return source;
	}

	public void addSource(File file) throws FileNotFoundException {
		JavaSource source = addSource(new FileReader(file));
		source.setFile(file);
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
					addSource(currentFile);
				}
				catch (FileNotFoundException e) {
					throw new RuntimeException("Cannot read file : " + currentFile.getName());
				}
			}
		});
	}

	public List search(Searcher searcher) {
		List results = new LinkedList();
		for (Iterator iterator = classLibrary.all().iterator(); iterator.hasNext();) {
			String clsName = (String)iterator.next();
			JavaClass cls = getClassByName(clsName);
			if (searcher.eval(cls)) {
				results.add(cls);
			}
		}
		return results;
	}

	public ClassLibrary getClassLibrary() {
		return classLibrary;
	}

	public void save(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		try {
			out.writeObject(this);
		}
		finally {
			out.close();
			fos.close();
		}
	}

	/**
	 * Note that after loading JavaDocBuilder classloaders need to be re-added.
	 */
	public static JavaDocBuilder load(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream in = new ObjectInputStream(fis);
		JavaDocBuilder builder = null;
		try {
			builder = (JavaDocBuilder)in.readObject();
		}
		catch (ClassNotFoundException e) {
			throw new Error("Couldn't load class : " + e.getMessage());
		}
		finally {
			in.close();
			fis.close();
		}
		return builder;
	}

}
