package com.thoughtworks.qdox;

import java.io.Reader;
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

public class MultipleJavaDocBuilder {
	private Map classes = new HashMap();
	
	public JavaSource[] build(Reader[] files){
		List classesParsed = new ArrayList();
		List sources = new ArrayList();
		for (int readerIndex=0; readerIndex<files.length; readerIndex++){
		ModelBuilder builder = new ModelBuilder(classesParsed);
			Lexer lexer = new JFlexLexer(files[readerIndex]);
			Parser parser = new Parser(lexer, builder);
			parser.parse();
			JavaSource source = builder.getSource();
			sources.add(source);
			addClasses(source);
		}

		return (JavaSource[])sources.toArray(new JavaSource[sources.size()]);
	}

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

}
