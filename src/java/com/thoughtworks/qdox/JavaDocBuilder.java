package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.ModelBuilder;
import com.thoughtworks.qdox.parser.Lexer;
import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;

import java.io.Reader;

public class JavaDocBuilder {

	public JavaSource build(Reader reader) {
		Lexer lexer = new JFlexLexer(reader);
		ModelBuilder builder = new ModelBuilder();
		Parser parser = new Parser(lexer, builder);
		parser.parse();
		return builder.getSource();
	}

}
