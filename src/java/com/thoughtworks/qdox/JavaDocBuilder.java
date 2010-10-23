package com.thoughtworks.qdox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
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
import com.thoughtworks.qdox.parser.Lexer;
import com.thoughtworks.qdox.parser.ParseException;
import com.thoughtworks.qdox.parser.impl.BinaryClassParser;
import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;
import com.thoughtworks.qdox.parser.structs.ClassDef;

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
	private final JavaClassContext context;
	private final ClassLibrary oldClassLibrary;
	
	//@todo move to JavaClassContext
    private Set packages = new HashSet();
    private List sources = new ArrayList();

    private String encoding = System.getProperty("file.encoding");
    private boolean debugLexer;
    private boolean debugParser;
    
    private ErrorHandler errorHandler = new DefaultErrorHandler();

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
        this.context = new JavaClassContext(this);
        this.oldClassLibrary.setContext( context );
        this.builderFactory = new ModelBuilderFactory()
        {
            public ModelBuilder newInstance()
            {
                return new ModelBuilder( context, oldClassLibrary, docletTagFactory );
            }
            public ModelBuilder newInstance( com.thoughtworks.qdox.library.ClassLibrary library )
            {
                return new ModelBuilder( library, docletTagFactory );
            }
        };
    }

    public JavaDocBuilder(ClassLibrary classLibrary) {
        this(new DefaultDocletTagFactory(), classLibrary);
    }

    public JavaDocBuilder(final DocletTagFactory docletTagFactory, ClassLibrary classLibrary) {
        this.oldClassLibrary = classLibrary;
        this.context = new JavaClassContext(this);
        this.oldClassLibrary.setContext( context );
        this.builderFactory = new ModelBuilderFactory()
        {
            public ModelBuilder newInstance()
            {
                return new ModelBuilder( context, oldClassLibrary, docletTagFactory );
            }
            public ModelBuilder newInstance( com.thoughtworks.qdox.library.ClassLibrary library )
            {
                return new ModelBuilder( library, docletTagFactory );
            }
        };
    }

    public JavaClass getClassByName(String name) {
        if (name == null) {
            return null;
        }
        return oldClassLibrary.getJavaClass(name);
    }
    
    protected JavaClass createSourceClass(String name) {
        File sourceFile = oldClassLibrary.getSourceFile( name );
        if (sourceFile != null) {
            try
            {
                JavaSource source = addSource( sourceFile );
                for (int index = 0; index < source.getClasses().length; index++) {
                    JavaClass clazz = source.getClasses()[index];
                    if (name.equals(clazz.getFullyQualifiedName())) {
                        return clazz;
                    }
                }
                return source.getNestedClassByName( name );
            }
            catch ( FileNotFoundException e )
            {
                //nop
            }
            catch ( IOException e )
            {
                //nop
            }
        }
        return null;
    }

    protected JavaClass createUnknownClass(String name) {
        ModelBuilder unknownBuilder = builderFactory.newInstance();
        ClassDef classDef = new ClassDef();
        classDef.name = name;
        unknownBuilder.beginClass(classDef);
        unknownBuilder.endClass();
        JavaSource unknownSource = unknownBuilder.getSource();
        JavaClass result = unknownSource.getClasses()[0];
        return result;
    }

    protected JavaClass createBinaryClass(String name) {
        // First see if the class exists at all.
        Class clazz = oldClassLibrary.getClass(name);
        if (clazz == null) {
            return null;
        } else {
            // Create a new builder and mimic the behaviour of the parser.
            // We're getting all the information we need via reflection instead.
            ModelBuilder binaryBuilder = builderFactory.newInstance();
            BinaryClassParser parser  = new BinaryClassParser( clazz, binaryBuilder );
            parser.parse();
            
            JavaSource binarySource = binaryBuilder.getSource();
            // There is always only one class in a "binary" source.
            JavaClass result = binarySource.getClasses()[0];
            return result;
        }
    }

    

    public JavaSource addSource(Reader reader) {
        return addSource(reader, "UNKNOWN SOURCE");
    }

    public JavaSource addSource(Reader reader, String sourceInfo) {
        ModelBuilder builder = builderFactory.newInstance();
        Lexer lexer = new JFlexLexer(reader);
        Parser parser = new Parser(lexer, builder);
        parser.setDebugLexer(debugLexer);
        parser.setDebugParser(debugParser);
        try {
            parser.parse();
        } catch (ParseException e) {
            e.setSourceInfo(sourceInfo);
            errorHandler.handle(e);
        }
        finally {
            try
            {
                reader.close();
            }
            catch ( IOException e )
            {
            }
        }
        JavaSource source = builder.getSource();
        sources.add(source);
        
        {
            Set resultSet = new HashSet();
            addClassesRecursive(source, resultSet);
            JavaClass[] javaClasses = (JavaClass[]) resultSet.toArray(new JavaClass[resultSet.size()]);
            for (int classIndex = 0; classIndex < javaClasses.length; classIndex++) {
                JavaClass cls = javaClasses[classIndex];
                context.add(cls);
            }
        }

        JavaPackage pkg = context.getPackageByName( source.getPackageName() );
        if (!packages.contains(pkg)) {
            packages.add(pkg);
        }
//        JavaClass[] classes = source.getClasses();
//        for (int i = 0; i < classes.length; i++) {
//            if (pkg != null) {
//                pkg.addClass(classes[i]);
//            }
//        }

        return source;
    }

    public JavaSource addSource(File file) throws IOException, FileNotFoundException {
        return addSource(file.toURL());
    }

    public JavaSource addSource(URL url) throws IOException, FileNotFoundException {
        JavaSource source = addSource(new InputStreamReader(url.openStream(),encoding), url.toExternalForm());
        source.setURL(url);
        return source;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public JavaSource[] getSources() {
        return (JavaSource[]) sources.toArray(new JavaSource[sources.size()]);
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
        return (JavaPackage[]) packages.toArray(new JavaPackage[packages.size()]);
    }

    private void addClassesRecursive(JavaSource javaSource, Set resultSet) {
        JavaClass[] classes = javaSource.getClasses();
        for (int j = 0; j < classes.length; j++) {
            JavaClass javaClass = classes[j];
            addClassesRecursive(javaClass, resultSet);
        }
    }

    private void addClassesRecursive(JavaClass javaClass, Set set) {
        // Add the class...
        set.add(javaClass);

        // And recursively all of its inner classes
        JavaClass[] innerClasses = javaClass.getNestedClasses();
        for (int i = 0; i < innerClasses.length; i++) {
            JavaClass innerClass = innerClasses[i];
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

    public List search(Searcher searcher) {
        List results = new LinkedList();
        for (Iterator iterator = oldClassLibrary.all().iterator(); iterator.hasNext();) {
            String clsName = (String) iterator.next();
            JavaClass cls = getClassByName(clsName);
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
        this.encoding = encoding;		
    }

    /**
     * Forces QDox to dump tokens returned from lexer to System.err.
     */
    public void setDebugLexer(boolean debugLexer) {
        this.debugLexer = debugLexer;
    }

    /**
     * Forces QDox to dump parser states to System.out.
     */
    public void setDebugParser(boolean debugParser) {
        this.debugParser = debugParser;
    }

    public JavaPackage getPackageByName( String name )
    {
        if(name != null) {
            Iterator iter = packages.iterator();
            while(iter.hasNext()) {
                JavaPackage pkg = (JavaPackage) iter.next();
                if(name.equals( pkg.getName() )) {
                    return pkg;
                }
            }
        }
        return null;
    }

}
