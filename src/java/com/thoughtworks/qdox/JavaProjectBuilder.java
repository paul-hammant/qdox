package com.thoughtworks.qdox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.qdox.directorywalker.DirectoryScanner;
import com.thoughtworks.qdox.directorywalker.FileVisitor;
import com.thoughtworks.qdox.directorywalker.SuffixFilter;
import com.thoughtworks.qdox.library.ClassLibraryBuilder;
import com.thoughtworks.qdox.library.SortedClassLibraryBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;



public class JavaProjectBuilder implements Serializable
{
    private final ClassLibraryBuilder classLibraryBuilder;
    
    public ClassLibraryBuilder setDebugLexer( boolean debugLexer )
    {
        return classLibraryBuilder.setDebugLexer( debugLexer );
    }

    public ClassLibraryBuilder setDebugParser( boolean debugParser )
    {
        return classLibraryBuilder.setDebugParser( debugParser );
    }

    public ClassLibraryBuilder setEncoding( String encoding )
    {
        return classLibraryBuilder.setEncoding( encoding );
    }

    public JavaProjectBuilder()
    {        
        this.classLibraryBuilder = new SortedClassLibraryBuilder();
    }

    public JavaProjectBuilder(ClassLibraryBuilder classLibraryBuilder)
    {        
        this.classLibraryBuilder = classLibraryBuilder;
    }

    public JavaSource addSource(File file) throws IOException, FileNotFoundException {
        return classLibraryBuilder.addSource( file );
    }
    
    public JavaSource addSource(Reader reader) {
        return classLibraryBuilder.addSource( reader );
    }

    public JavaClass getClassByName( String name )
    {
        return classLibraryBuilder.getClassLibrary().getJavaClass( name );
    }
    
    public JavaSource[] getSources() {
        return classLibraryBuilder.getClassLibrary().getJavaSources();
    }

    public JavaClass[] getClasses()
    {
        return classLibraryBuilder.getClassLibrary().getJavaClasses();
    }

    public JavaPackage[] getPackages()
    {
        return classLibraryBuilder.getClassLibrary().getJavaPackages();
    }

    public void addSourceTree( File file )
    {
        FileVisitor errorHandler = new FileVisitor() {
            public void visitFile(File badFile) {
                throw new RuntimeException("Cannot read file : " + badFile.getName());
            }
        };
        addSourceTree(file, errorHandler);        
    }

    public void addSourceTree( File file, final FileVisitor errorHandler )
    {
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

    public JavaClass[] search( Searcher searcher )
    {
        List results = new LinkedList();
        JavaClass[] classArray = classLibraryBuilder.getClassLibrary().getJavaClasses();
        for (int classIndex = 0;classIndex < classArray.length; classIndex++) {
            JavaClass clazz = classArray[classIndex];
            if (searcher.eval(clazz)) {
                results.add(clazz);
            }
        }
        return (JavaClass[]) results.toArray( new JavaClass[0] );
    }

    public void addClassLoader( ClassLoader classLoader )
    {
        classLibraryBuilder.appendClassLoader( classLoader );
    }

    public void save( File file ) throws IOException
    {
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
    public static JavaProjectBuilder load(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream in = new ObjectInputStream(fis);
        JavaProjectBuilder builder = null;
        try {
            builder = (JavaProjectBuilder) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new Error("Couldn't load class : " + e.getMessage());
        } finally {
            in.close();
            fis.close();
        }
        return builder;
    }

    public void addSourceFolder( File sourceFolder )
    {
        classLibraryBuilder.appendSourceFolder( sourceFolder );
    }

    public JavaPackage getPackageByName( String name )
    {
        return classLibraryBuilder.getClassLibrary().getJavaPackage( name );
    }
    
    

}
