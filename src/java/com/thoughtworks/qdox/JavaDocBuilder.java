package com.thoughtworks.qdox;

import com.thoughtworks.qdox.directorywalker.DirectoryScanner;
import com.thoughtworks.qdox.directorywalker.FileVisitor;
import com.thoughtworks.qdox.directorywalker.SuffixFilter;
import com.thoughtworks.qdox.model.ClassLibrary;
import com.thoughtworks.qdox.model.DefaultDocletTagFactory;
import com.thoughtworks.qdox.model.DocletTagFactory;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaClassCache;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.ModelBuilder;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.parser.Lexer;
import com.thoughtworks.qdox.parser.ParseException;
import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.PackageDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;

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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

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

	private final JavaClassContext context;;
	
    private Set packages = new HashSet();
    private List sources = new ArrayList();
    private DocletTagFactory docletTagFactory;
    private String encoding = System.getProperty("file.encoding");
    private boolean debugLexer;
    private boolean debugParser;
    private ErrorHandler errorHandler;
    private Map allPackages = new HashMap();

    public static interface ErrorHandler {
        void handle(ParseException parseException);
    }

    public JavaDocBuilder() {
        this(new DefaultDocletTagFactory());
    }

    public JavaDocBuilder(DocletTagFactory docletTagFactory) {
        this.docletTagFactory = docletTagFactory;
        ClassLibrary classLibrary = new ClassLibrary();
        classLibrary.addDefaultLoader();
        this.context = new JavaClassContext(this);
        this.context.setClassLibrary(classLibrary);
    }

    public JavaDocBuilder(ClassLibrary classLibrary) {
        this(new DefaultDocletTagFactory(), classLibrary);
    }

    public JavaDocBuilder(DocletTagFactory docletTagFactory, ClassLibrary classLibrary) {
        this.docletTagFactory = docletTagFactory;
        this.context = new JavaClassContext(this);
        this.context.setClassLibrary(classLibrary);
    }

    private void addClasses(JavaSource source) {
        Set resultSet = new HashSet();
        addClassesRecursive(source, resultSet);
        JavaClass[] javaClasses = (JavaClass[]) resultSet.toArray(new JavaClass[resultSet.size()]);
        for (int classIndex = 0; classIndex < javaClasses.length; classIndex++) {
            JavaClass cls = javaClasses[classIndex];
            addClass(cls);
        }
    }

    private void addClass(JavaClass cls) {
        context.add(cls);
        cls.setJavaClassContext(context);
    }

    public JavaClass getClassByName(String name) {
        if (name == null) {
            return null;
        }
        return context.getClassByName(name);
    }

    protected JavaClass createUnknownClass(String name) {
        ModelBuilder unknownBuilder = new ModelBuilder(context, docletTagFactory, new HashMap());
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
        Class clazz = context.getClass(name);
        if (clazz == null) {
            return null;
        } else {
            try {
				// Create a new builder and mimic the behaviour of the parser.
				// We're getting all the information we need via reflection instead.
				ModelBuilder binaryBuilder = new ModelBuilder(context, docletTagFactory, new HashMap());

				// Set the package name and class name
				String packageName = getPackageName(name);
				binaryBuilder.addPackage(new PackageDef(packageName));

				ClassDef classDef = new ClassDef();
				classDef.name = getClassName(name);

				// Set the extended class and interfaces.
				Class[] interfaces = clazz.getInterfaces();
				if (clazz.isInterface()) {
				    // It's an interface
				    classDef.type = ClassDef.INTERFACE;
				    for (int i = 0; i < interfaces.length; i++) {
				        Class anInterface = interfaces[i];
				        classDef.extendz.add(new TypeDef(anInterface.getName()));
				    }
				} else {
				    // It's a class
				    for (int i = 0; i < interfaces.length; i++) {
				        Class anInterface = interfaces[i];
				        classDef.implementz.add(new TypeDef(anInterface.getName()));
				    }
				    Class superclass = clazz.getSuperclass();
				    if (superclass != null) {
				        classDef.extendz.add(new TypeDef(superclass.getName()));
				    }
				}

				addModifiers(classDef.modifiers, clazz.getModifiers());

				binaryBuilder.beginClass(classDef);

				// add the constructors
				//
				// This also adds the default constructor if any which is different
				// to the source code as that does not create a default constructor
				// if no constructor exists.
				Constructor[] constructors = clazz.getDeclaredConstructors();
				for (int i = 0; i < constructors.length; i++) {
				    addMethodOrConstructor(constructors[i], binaryBuilder);
				}

				// add the methods
				Method[] methods = clazz.getDeclaredMethods();
				for (int i = 0; i < methods.length; i++) {
				    addMethodOrConstructor(methods[i], binaryBuilder);
				}

				Field[] fields = clazz.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
				    addField(fields[i], binaryBuilder);
				}

				binaryBuilder.endClass();
				JavaSource binarySource = binaryBuilder.getSource();
				// There is always only one class in a "binary" source.
				JavaClass result = binarySource.getClasses()[0];
				return result;
			} catch (NoClassDefFoundError e) {
				return null;
			}
        }
    }

    private void addModifiers(Set set, int modifier) {
        String modifierString = Modifier.toString(modifier);
        for (StringTokenizer stringTokenizer = new StringTokenizer(modifierString); stringTokenizer.hasMoreTokens();) {
            set.add(stringTokenizer.nextToken());
        }
    }

    private void addField(Field field, ModelBuilder binaryBuilder) {
        FieldDef fieldDef = new FieldDef();
        Class fieldType = field.getType();
        fieldDef.name = field.getName();
        fieldDef.type = getTypeDef(fieldType);
        fieldDef.dimensions = getDimension(fieldType);
        addModifiers( fieldDef.modifiers, field.getModifiers());
        binaryBuilder.addField(fieldDef);
    }

    private void addMethodOrConstructor(Member member, ModelBuilder binaryBuilder) {
        MethodDef methodDef = new MethodDef();
        // The name of constructors are qualified. Need to strip it.
        // This will work for regular methods too, since -1 + 1 = 0
        int lastDot = member.getName().lastIndexOf('.');
        methodDef.name = member.getName().substring(lastDot + 1);

        addModifiers(methodDef.modifiers, member.getModifiers());
        Class[] exceptions;
        Class[] parameterTypes;
        if (member instanceof Method) {
            methodDef.constructor = false;

            // For some stupid reason, these methods are not defined in Member,
            // but in both Method and Construcotr.
            exceptions = ((Method) member).getExceptionTypes();
            parameterTypes = ((Method) member).getParameterTypes();

            Class returnType = ((Method) member).getReturnType();
            methodDef.returnType = getTypeDef(returnType);
            methodDef.dimensions = getDimension(returnType);

        } else {
            methodDef.constructor = true;

            exceptions = ((Constructor) member).getExceptionTypes();
            parameterTypes = ((Constructor) member).getParameterTypes();
        }
        for (int j = 0; j < exceptions.length; j++) {
            Class exception = exceptions[j];
            methodDef.exceptions.add(exception.getName());
        }
        binaryBuilder.addMethod(methodDef);
        for (int j = 0; j < parameterTypes.length; j++) {
            FieldDef param = new FieldDef();
            Class parameterType = parameterTypes[j];
            param.name = "p" + j;
            param.type = getTypeDef(parameterType);
            param.dimensions = getDimension(parameterType);
            binaryBuilder.addParameter( param );
        }
    }

    private static final int getDimension(Class c) {
        return c.getName().lastIndexOf('[') + 1;
    }

    private static String getTypeName(Class c) {
        return c.getComponentType() != null ? c.getComponentType().getName() : c.getName();
    }
    
    private static TypeDef getTypeDef(Class c) {
        return new TypeDef(getTypeName(c));
    }
    

    private String getPackageName(String fullClassName) {
        int lastDot = fullClassName.lastIndexOf('.');
        return lastDot == -1 ? "" : fullClassName.substring(0, lastDot);
    }

    private String getClassName(String fullClassName) {
        int lastDot = fullClassName.lastIndexOf('.');
        return lastDot == -1 ? fullClassName : fullClassName.substring(lastDot + 1);
    }

    public JavaSource addSource(Reader reader) {
        return addSource(reader, "UNKNOWN SOURCE");
    }

    public JavaSource addSource(Reader reader, String sourceInfo) {
        ModelBuilder builder = new ModelBuilder(context, docletTagFactory, allPackages);
        Lexer lexer = new JFlexLexer(reader);
        Parser parser = new Parser(lexer, builder);
        parser.setDebugLexer(debugLexer);
        parser.setDebugParser(debugParser);
        try {
            parser.parse();
        } catch (ParseException e) {
            e.setSourceInfo(sourceInfo);
            if (errorHandler == null) {
                throw e;
            } else {
                errorHandler.handle(e);
            }
        }
        JavaSource source = builder.getSource();
        sources.add(source);
        addClasses(source);

        JavaPackage pkg = source.getPackage();
        JavaClass[] classes = source.getClasses();
        if (!packages.contains(pkg)) {
            packages.add(pkg);
        }
        for (int i = 0; i < classes.length; i++) {
            if (pkg != null) {
                pkg.addClass(classes[i]);
            }
        }

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
        for (Iterator iterator = context.getClassLibrary().all().iterator(); iterator.hasNext();) {
            String clsName = (String) iterator.next();
            JavaClass cls = getClassByName(clsName);
            if (searcher.eval(cls)) {
                results.add(cls);
            }
        }
        return results;
    }

    public ClassLibrary getClassLibrary() {
        return context.getClassLibrary();
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

}
