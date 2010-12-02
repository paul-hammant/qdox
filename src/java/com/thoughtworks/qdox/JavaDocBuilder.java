package com.thoughtworks.qdox;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.thoughtworks.qdox.directorywalker.DirectoryScanner;
import com.thoughtworks.qdox.directorywalker.FileVisitor;
import com.thoughtworks.qdox.directorywalker.SuffixFilter;
import com.thoughtworks.qdox.model.ClassLibrary;
import com.thoughtworks.qdox.model.DefaultDocletTagFactory;
import com.thoughtworks.qdox.model.DocletTagFactory;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.ModelBuilder;
import com.thoughtworks.qdox.model.ModelBuilderFactory;
import com.thoughtworks.qdox.parser.ParseException;

/**
 * Simple facade to QDox allowing a source tree to be parsed and the resulting object model navigated.
 *
 * <h3>Example</h3>
 * <pre><code>
 * // -- Create JavaDocBuilder
 *
 * JavaDocBuilder builder = new JavaDocBuilder();
 *
 * // -- Add some files
 *
 * // Reading a single source file.
 * builder.addSource(new FileReader("MyFile.java"));
 *
 * // Reading from another kind of input stream.
 * builder.addSource(new StringReader("package test; public class Hello {}"));
 *
 * // Adding all .java files in a source tree (recursively).
 * builder.addSourceTree(new File("mysrcdir"));
 *
 * // -- Retrieve source files
 *
 * JavaSource[] source = builder.getSources();
 *
 * </code></pre>
 *
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 * @author Robert Scholte
 */
public class JavaDocBuilder implements Serializable {
    
    final ModelBuilderFactory builderFactory;
    
    //@todo should be replaced with the new ClassLibrary
    //hold reference to both objects for better refactoring
	private final ClassLibrary oldClassLibrary;

    public static interface ErrorHandler {
        void handle(ParseException parseException);
    }

    public static class DefaultErrorHandler implements ErrorHandler, Serializable {
        public void handle(ParseException parseException) {
            throw parseException;
        }
    }

    public JavaDocBuilder() {
        this(new DefaultDocletTagFactory());
    }

    public JavaDocBuilder(final DocletTagFactory docletTagFactory) {
        this.oldClassLibrary = new ClassLibrary();
        this.oldClassLibrary.addDefaultLoader();
        this.builderFactory = new ModelBuilderFactory()
        {
            public ModelBuilder newInstance()
            {
                return new ModelBuilder(oldClassLibrary, docletTagFactory );
            }
            public ModelBuilder newInstance( com.thoughtworks.qdox.library.ClassLibrary library )
            {
                return new ModelBuilder( library, docletTagFactory );
            }
        };
        this.oldClassLibrary.setModelBuilderFactory(builderFactory);
    }

    public JavaDocBuilder(ClassLibrary classLibrary) {
        this(new DefaultDocletTagFactory(), classLibrary);
    }

    public JavaDocBuilder(final DocletTagFactory docletTagFactory, ClassLibrary classLibrary) {
        this.oldClassLibrary = classLibrary;
        this.builderFactory = new ModelBuilderFactory()
        {
            public ModelBuilder newInstance()
            {
                return new ModelBuilder(oldClassLibrary, docletTagFactory );
            }
            public ModelBuilder newInstance( com.thoughtworks.qdox.library.ClassLibrary library )
            {
                return new ModelBuilder( library, docletTagFactory );
            }
        };
        this.oldClassLibrary.setModelBuilderFactory(builderFactory);
    }

    public JavaClass getClassByName(String name) {
        if (name == null) {
            return null;
        }
        return oldClassLibrary.getJavaClass(name);
    }
    
    

    public JavaSource addSource(Reader reader) {
        return addSource(reader, "UNKNOWN SOURCE");
    }

    public JavaSource addSource(Reader reader, String sourceInfo) {
        return oldClassLibrary.addSource( reader, sourceInfo );
    }

    public JavaSource addSource(File file) throws IOException, FileNotFoundException {
        return oldClassLibrary.addSource( file );
    }

    public JavaSource addSource(URL url) throws IOException, FileNotFoundException {
        return oldClassLibrary.addSource( url );
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        oldClassLibrary.setErrorHandler( errorHandler );
    }

    public JavaSource[] getSources() {
        return oldClassLibrary.getJavaSources().toArray( new JavaSource[0] );
    }

    /**
     * Returns all the classes found in all the sources, including inner classes
     * and "extra" classes (multiple outer classes defined in the same source file).
     *
     * @return all the classes found in all the sources.
     * @since 1.3
     */
    public JavaClass[] getClasses() {
        Set resultSet = new HashSet();
        JavaSource[] javaSources = getSources();
        for (int i = 0; i < javaSources.length; i++) {
            JavaSource javaSource = javaSources[i];
            addClassesRecursive(javaSource, resultSet);
        }
        JavaClass[] result = (JavaClass[]) resultSet.toArray(new JavaClass[resultSet.size()]);
        return result;
    }

    /**
     * Returns all the packages found in all the sources.
     *
     * @return all the packages found in all the sources.
     * @since 1.9
     */
    public JavaPackage[] getPackages() {
        return oldClassLibrary.getJavaPackages().toArray( new JavaPackage[0] );
    }

    //@todo remove
    private void addClassesRecursive(JavaSource javaSource, Set resultSet) {
        for (JavaClass javaClass : javaSource.getClasses()) {
            addClassesRecursive(javaClass, resultSet);
        }
    }

    //@todo remove
    private void addClassesRecursive(JavaClass javaClass, Set set) {
        // Add the class...
        set.add(javaClass);

        // And recursively all of its inner classes
        List<JavaClass> innerClasses = javaClass.getNestedClasses();
        for (JavaClass innerClass : innerClasses) {
            addClassesRecursive(innerClass, set);
        }
    }

    /**
     * Add all files in a directory (and subdirs, recursively).
     *
     * If a file cannot be read, a RuntimeException shall be thrown.
     */
    public void addSourceTree(File file) {
        FileVisitor errorHandler = new FileVisitor() {
            public void visitFile(File badFile) {
                throw new RuntimeException("Cannot read file : " + badFile.getName());
            }
        };
        addSourceTree(file, errorHandler);
    }

    /**
     * Add all files in a directory (and subdirs, recursively).
     *
     * If a file cannot be read, errorHandler will be notified.
     */
    public void addSourceTree(File file, final FileVisitor errorHandler) {
        DirectoryScanner scanner = new DirectoryScanner(file);
        scanner.addFilter(new SuffixFilter(".java"));
        scanner.scan(new FileVisitor() {
            public void visitFile(File currentFile) {
                try {
                    addSource(currentFile);
                } catch (IOException e) {
					errorHandler.visitFile(currentFile);
                }
            }
        });
    }

    public List<JavaClass> search(Searcher searcher) {
        List<JavaClass> results = new LinkedList<JavaClass>();
        for(int index = 0; index < oldClassLibrary.getJavaClasses().size(); index++ ) {
            JavaClass cls = oldClassLibrary.getJavaClasses().get(index);
            if (searcher.eval(cls)) {
                results.add(cls);
            }
        }
        return results;
    }

    public ClassLibrary getClassLibrary() {
        return oldClassLibrary;
    }

    public void save(File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fos);
        try {
            out.writeObject(this);
        } finally {
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
            builder = (JavaDocBuilder) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new Error("Couldn't load class : " + e.getMessage());
        } finally {
            in.close();
            fis.close();
        }
        return builder;
    }

    public void setEncoding(String encoding) {
        oldClassLibrary.setEncoding( encoding );
    }

    /**
     * Forces QDox to dump tokens returned from lexer to System.err.
     */
    public void setDebugLexer(boolean debugLexer) {
        oldClassLibrary.setDebugLexer( debugLexer );
    }

    /**
     * Forces QDox to dump parser states to System.out.
     */
    public void setDebugParser(boolean debugParser) {
        oldClassLibrary.setDebugParser( debugParser );
    }

    public JavaPackage getPackageByName( String name )
    {
        return oldClassLibrary.getJavaPackage( name );
    }

}
