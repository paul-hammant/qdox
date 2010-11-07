package com.thoughtworks.qdox.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.thoughtworks.qdox.JavaClassContext;
import com.thoughtworks.qdox.JavaDocBuilder.DefaultErrorHandler;
import com.thoughtworks.qdox.JavaDocBuilder.ErrorHandler;
import com.thoughtworks.qdox.parser.Lexer;
import com.thoughtworks.qdox.parser.ParseException;
import com.thoughtworks.qdox.parser.impl.BinaryClassParser;
import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;
import com.thoughtworks.qdox.parser.structs.ClassDef;

/**
 * <strong>Important!! Be sure to add a classloader with the bootstrap classes.</strong>
 * 
 * <p>
 * Normally you can generate your classLibrary like this:<br/>
 * <code>
 * 	ClassLibrary classLibrary = new ClassLibrary();
 *  classLibrary.addDefaultLoader();
 * </code>
 * </p>
 * 
 * <p>
 * If you want full control over the classLoaders you might want to create your library like:<br/> 
 * <code>
 * ClassLibrary classLibrary = new ClassLibrary( ClassLoader.getSystemClassLoader() )
 * </code>  
 * </p>
 * 
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 * @author Robert Scholte
 */
public class ClassLibrary implements Serializable, com.thoughtworks.qdox.library.ClassLibrary  {
    
    private JavaClassContext context = new JavaClassContext();
    private JavaClassContext sourceContext = new JavaClassContext();

    private ModelBuilderFactory modelBuilderFactory;
    
    private boolean defaultClassLoadersAdded = false;
    private transient List classLoaders = new ArrayList();
    private List sourceFolders = new ArrayList(); //<File>

    private String encoding = System.getProperty("file.encoding");
    private boolean debugLexer;
    private boolean debugParser;
    
    private ErrorHandler errorHandler = new DefaultErrorHandler();

    
    /**
     * Remember to add bootstrap classes
     */
    public ClassLibrary() {}

    /**
     * Remember to add bootstrap classes
     */
    public ClassLibrary(ClassLoader loader) {
    	classLoaders.add(loader);
    }
    
    public void setDebugLexer( boolean debugLexer )
    {
        this.debugLexer = debugLexer;
    }
    
    public void setDebugParser( boolean debugParser )
    {
        this.debugParser = debugParser;
    }
    
    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }
    
    public void setErrorHandler( ErrorHandler errorHandler )
    {
        this.errorHandler = errorHandler;
    }
    
    public JavaClassContext getContext()
    {
        return sourceContext;
    }
    
    public File getSourceFile( String className )
    {
        for(Iterator iterator = sourceFolders.iterator(); iterator.hasNext();) {
            File sourceFolder = (File) iterator.next();
            String mainClassName = className.split( "\\$" )[0];
            File classFile = new File(sourceFolder, mainClassName.replace( '.', File.separatorChar ) + ".java");
            if ( classFile.exists() && classFile.isFile() ) {
                return classFile;
            }
        }
        return null;
    }

    public Class getClass(String className) {
        Class result = null;
        for (Iterator iterator = classLoaders.iterator(); iterator.hasNext();) {
            ClassLoader classLoader = (ClassLoader) iterator.next();
            if (classLoader == null) {
                continue;
            }
            try {
                result = classLoader.loadClass(className);
                if (result != null) {
                    break;
                }
            } catch (ClassNotFoundException e) {
                // continue
            } catch (NoClassDefFoundError e) {
                // continue
            }
        }
        return result;
    }

    public void addClassLoader(ClassLoader classLoader) {
        classLoaders.add(classLoader);
    }

    public void addDefaultLoader() {
        if (!defaultClassLoadersAdded) {
            classLoaders.add(getClass().getClassLoader());
            classLoaders.add(Thread.currentThread().getContextClassLoader());
        }
        defaultClassLoadersAdded = true;
    }

    public void addSourceFolder( File sourceFolder ) {
        sourceFolders.add( sourceFolder );
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        classLoaders = new ArrayList();
        if (defaultClassLoadersAdded) {
            defaultClassLoadersAdded = false;
            addDefaultLoader();
        }
    }

    public JavaClass getJavaClass(String name) {
        JavaClass result = sourceContext.getClassByName( name );
        if (result == null) {
            result = context.getClassByName( name );
        }
        if(result == null) {
            result = createBinaryClass(name);
            
            if ( result == null ) {
                result = createSourceClass(name);
            }
            if ( result == null ) {
                result = createUnknownClass(name);
            }
        }
        return result;
    }
    
    private JavaClass createSourceClass(String name) {
        File sourceFile = getSourceFile( name );
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
                sourceContext.add( source );
                for(int index = 0; index < source.getClasses().length; index++) {
                    sourceContext.add( source.getClasses()[index]);
                }
                sourceContext.add( source.getPackage() );
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
    
    private JavaClass createBinaryClass(String name) {
        // First see if the class exists at all.
        Class clazz = getClass(name);
        if (clazz == null) {
            return null;
        } else {
            // Create a new builder and mimic the behaviour of the parser.
            // We're getting all the information we need via reflection instead.
            ModelBuilder binaryBuilder = modelBuilderFactory.newInstance();
            BinaryClassParser parser  = new BinaryClassParser( clazz, binaryBuilder );
            parser.parse();
            
            JavaSource binarySource = binaryBuilder.getSource();
            // There is always only one class in a "binary" source.
            JavaClass result = binarySource.getClasses()[0];
            
            context.add( result );
            
            return result;
        }
    }
    
    private JavaClass createUnknownClass(String name) {
        ModelBuilder unknownBuilder = modelBuilderFactory.newInstance();
        ClassDef classDef = new ClassDef();
        classDef.name = name;
        unknownBuilder.beginClass(classDef);
        unknownBuilder.endClass();
        JavaSource unknownSource = unknownBuilder.getSource();
        JavaClass result = unknownSource.getClasses()[0];
        
        context.add( result );
        
        return result;
    }
    
    public JavaClass[] getJavaClasses() {
        return sourceContext.getClasses();
    }
    
    public JavaPackage getJavaPackage( String name) {
        return sourceContext.getPackageByName( name );
    }
    
    public JavaPackage[] getJavaPackages() {
        return sourceContext.getPackages();
    }
    
    public JavaSource[] getJavaSources() {
        return sourceContext.getSources();
    }
    
    public boolean hasClassReference( String name )
    {
        if (sourceContext.getClassByName( name ) != null) {
            return true;
        }
        else if (context.getClassByName( name ) != null) {
            return true;
        }
        else if (getSourceFile(name) != null) {
            return true;
        } else {
            return getClass(name) != null;
        }
    }

    public void setModelBuilderFactory( ModelBuilderFactory builderFactory )
    {
        this.modelBuilderFactory = builderFactory;
    }

    public JavaSource addSource( File file ) throws IOException, FileNotFoundException 
    {
        return addSource(file.toURL());
    }
    
    public JavaSource addSource(URL url) throws IOException, FileNotFoundException {
        JavaSource source = addSource(new InputStreamReader(url.openStream(),encoding), url.toExternalForm());
        if(source instanceof DefaultJavaSource) {
            ((DefaultJavaSource) source).setURL(url);
        } 
        return source;
    }
    
    public JavaSource addSource(Reader reader, String sourceInfo) {
        ModelBuilder builder = modelBuilderFactory.newInstance();
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
        sourceContext.add(source);
        sourceContext.add(source.getPackage());
        {
            Set resultSet = new HashSet();
            addClassesRecursive(source, resultSet);
            JavaClass[] javaClasses = (JavaClass[]) resultSet.toArray(new JavaClass[resultSet.size()]);
            for (int classIndex = 0; classIndex < javaClasses.length; classIndex++) {
                JavaClass cls = javaClasses[classIndex];
                sourceContext.add(cls);
            }
        }
        return source;
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

}
