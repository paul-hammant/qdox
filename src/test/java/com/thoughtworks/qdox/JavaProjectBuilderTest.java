package com.thoughtworks.qdox;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.qdox.library.ClassLibraryBuilder;
import com.thoughtworks.qdox.library.ErrorHandler;
import com.thoughtworks.qdox.library.OrderedClassLibraryBuilder;
import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaGenericDeclaration;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaParameterizedType;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.JavaTypeVariable;
import com.thoughtworks.qdox.model.util.SerializationUtils;
import com.thoughtworks.qdox.parser.ParseException;
import com.thoughtworks.qdox.testdata.PropertyClass;

import junit.framework.TestCase;

public class JavaProjectBuilderTest extends TestCase
{

    private JavaProjectBuilder builder;
    
    @Override
	protected void setUp() throws Exception {
        super.setUp();
        builder = new JavaProjectBuilder();
        createFile("target/test-source/com/blah/Another.java", "com.blah", "Another");
        createFile("target/test-source/com/blah/Ignore.notjava", "com.blah", "Ignore");
        createFile("target/test-source/com/blah/Thing.java", "com.blah", "Thing");
        createFile("target/test-source/com/blah/subpackage/Cheese.java", "com.blah.subpackage", "Cheese");
    }

    @Override
	protected void tearDown() throws Exception {
        super.tearDown();
        deleteDir("target/test-source");
    }

    public void testParsingMultipleJavaFiles() {
        JavaSource source1 = builder.addSource(new StringReader(createTestClassList()));
        JavaSource source2 = builder.addSource(new StringReader(createTestClass()));
        Collection<JavaSource> sources = builder.getSources();
        assertEquals(2, sources.size());

        JavaClass testClassList = source1.getClasses().get(0);
        assertEquals("TestClassList", testClassList.getName());
        assertEquals("TestClass", testClassList.getSuperClass().getValue());
        assertEquals("com.thoughtworks.util.TestClass", testClassList.getSuperClass().getFullyQualifiedName());

        JavaClass testClass = source2.getClasses().get(0);
        assertEquals("TestClass", testClass.getName());

        JavaClass testClassListByName = builder.getClassByName("com.thoughtworks.qdox.TestClassList");
        assertEquals("TestClassList", testClassListByName.getName());

        JavaClass testClassByName = builder.getClassByName("com.thoughtworks.util.TestClass");
        assertEquals("TestClass", testClassByName.getName());
    }

    private String createTestClassList() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("package com.thoughtworks.qdox;");
        buffer.append("import com.thoughtworks.util.*;");
        buffer.append("public class TestClassList extends TestClass{");
        buffer.append("private int numberOfTests;");
        buffer.append("public int getNumberOfTests(){return numberOfTests;}");
        buffer.append("public void setNumberOfTests(int numberOfTests){this.numberOfTests = numberOfTests;}");
        buffer.append("}");
        return buffer.toString();
    }

    private String createTestClass() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("package com.thoughtworks.util;");
        buffer.append("public class TestClass{");
        buffer.append("public void test(){}");
        buffer.append("}");
        return buffer.toString();
    }

    public void testParseWithInnerClass() {
        JavaSource source = builder.addSource(new StringReader(createOuter()));
        Collection<JavaSource> sources = builder.getSources();
        assertEquals(1, sources.size());

        JavaClass outer = source.getClasses().get(0);
        assertEquals("Outer", outer.getName());
        assertEquals("foo.bar.Outer", outer.getFullyQualifiedName());

        assertEquals(1, outer.getFields().size());
        assertEquals("int", outer.getFields().get(0).getType().getValue());

        assertEquals(1, outer.getMethods().size());
        assertEquals("outerMethod", outer.getMethods().get(0).getName());

        assertEquals(1, outer.getNestedClasses().size());
        JavaClass inner = outer.getNestedClasses().get(0);
        assertEquals("Inner", inner.getName());
        assertEquals("foo.bar.Outer$Inner", inner.getBinaryName());
        assertEquals("foo.bar.Outer.Inner", inner.getFullyQualifiedName());

        assertEquals(1, inner.getMethods().size());
        assertEquals("innerMethod", inner.getMethods().get(0).getName());
    }

    public void testGetClasses() {
        builder.addSource(new StringReader(createOuter()));
        Collection<JavaClass> classes = builder.getClasses();
        assertEquals(2, classes.size());
    }

    public void testGetPackagesShowsOnePackageAndTwoClasses() {
        builder.addSourceTree(new File("target/test-source"));
        Collection<JavaPackage> packages = builder.getPackages();
        assertEquals(2, packages.size());
        JavaPackage comBlah = builder.getPackageByName( "com.blah" );
        JavaPackage comBlahSubpackage = builder.getPackageByName( "com.blah.subpackage" );
        assertEquals("com.blah.subpackage", comBlahSubpackage.getName());
        assertEquals(1, comBlahSubpackage.getClasses().size());
        assertNotNull("Cheese", comBlahSubpackage.getClassByName( "Cheese" ) );
        assertEquals(2, comBlah.getClasses().size());
        assertNotNull( comBlah.getClassByName( "Another" ));
        assertNotNull( comBlah.getClassByName( "Thing" ));
        assertEquals(comBlah, comBlahSubpackage.getParentPackage());
        assertNull(comBlah.getParentPackage());
        Collection<JavaPackage> comBlahSubpackages = comBlah.getSubPackages();
        assertEquals(1, comBlahSubpackages.size());
        assertEquals(comBlahSubpackage, comBlahSubpackages.iterator().next());
        assertEquals(0, comBlahSubpackage.getSubPackages().size());
    }

    private String createOuter() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("package foo.bar;");
        buffer.append("public class Outer {");
        buffer.append("  private int numberOfTests;");
        buffer.append("  class Inner {");
        buffer.append("    public int innerMethod(){return System.currentTimeMillis();}");
        buffer.append("  }");
        buffer.append("  public void outerMethod(int count){}");
        buffer.append("}");
        return buffer.toString();
    }

    public void testSourceTree() {
        builder.addSourceTree(new File("target/test-source"));

        assertNotNull(builder.getClassByName("com.blah.Thing"));
        assertNotNull(builder.getClassByName("com.blah.Another"));
        assertNotNull(builder.getClassByName("com.blah.subpackage.Cheese"));
    }

    public void testRecordFile() throws Exception {
        JavaSource source = builder.addSource(new File("target/test-source/com/blah/Thing.java"));

        Collection<JavaSource> sources = builder.getSources();
        assertEquals(1, sources.size());
        assertEquals(new File("target/test-source/com/blah/Thing.java").toURL(),
                     source.getURL());
    }

    public void testSearcher()
    {
        builder.addSourceTree( new File( "target/test-source" ) );

        Collection<JavaClass> results = builder.search( new Searcher()
        {
            public boolean eval( JavaClass cls )
            {
                return cls.getPackage().getName().equals( "com.blah" );
            }
        } );

        assertEquals( 2, results.size() );

        List<String> classNames = new ArrayList<String>();
        for ( JavaClass cls : results )
        {
            classNames.add( cls.getName() );
        }
        Collections.sort( classNames );
        assertEquals( "Another", classNames.get( 0 ) );
        assertEquals( "Thing", classNames.get( 1 ) );
    }

    private void createFile(String fileName, String packageName, String className) throws Exception {
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        writer.write("// this file generated by JavaDocBuilderTest - feel free to delete it\n");
        writer.write("package " + packageName + ";\n\n");
        writer.write("public class " + className + " {\n\n  // empty\n\n}\n");
        writer.close();
    }

    private void deleteDir(String path) {
        File dir = new File(path);
        File[] children = dir.listFiles();
        for (int i = 0; i < children.length; i++) {
            File file = children[i];
            if (file.isDirectory()) {
                deleteDir(file.getAbsolutePath());
            } else {
                file.delete();
            }
        }
        dir.delete();
    }
    
    public void testDefaultClassLoader() {
        String in = ""
                + "package x;"
                + "import java.util.*;"
                + "import java.awt.*;"
                + "class X extends List {}";
        builder.addSource(new StringReader(in));
        JavaClass cls = builder.getClassByName("x.X");
        assertEquals("List", cls.getSuperClass().getValue());
        assertEquals("java.util.List", cls.getSuperClass().getFullyQualifiedName());
    }

    public void testAddMoreClassLoaders() {
    	builder = new JavaProjectBuilder(new OrderedClassLibraryBuilder(null));
        builder.addClassLoader(new ClassLoader() {
            @Override
			public Class<?> loadClass(String name) throws ClassNotFoundException {
            	if("com.thoughtworks.qdox.Spoon".equals(name))  {
                    return Spoon.class; //Located inside com.thoughtworks.qdox.TestClasses.java
            	}
            	else {
            		throw new ClassNotFoundException(name);
            	}
            }
        });

        builder.addClassLoader(new ClassLoader() {
            @Override
			public Class<?> loadClass(String name) throws ClassNotFoundException {
            	if("com.thoughtworks.qdox.Fork".equals(name))  {
                    return Fork.class;  //Located inside com.thoughtworks.qdox.TestClasses.java
            	}
            	else {
            		throw new ClassNotFoundException(name);
            	}
            }
        });

        String in = ""
                + "package x;"
                + "import java.util.*;"
                + "import com.thoughtworks.qdox.*;"
                + "class X {"
                + " Spoon a();"
                + " Fork b();"
                + " Cabbage c();"
                + "}";
        builder.addSource(new StringReader(in));

        // be sure no default classloaders have been added
        assertNull(builder.getClassByName(Knife.class.getName()));
        JavaClass cls = builder.getClassByName("x.X");
        assertEquals("Spoon", cls.getMethods().get(0).getReturns().getValue());
        assertEquals("com.thoughtworks.qdox.Spoon", cls.getMethods().get(0).getReturns().getFullyQualifiedName());
        assertEquals("Fork", cls.getMethods().get(1).getReturns().getValue());
        assertEquals("com.thoughtworks.qdox.Fork", cls.getMethods().get(1).getReturns().getFullyQualifiedName());
        // unresolved
        assertEquals("Cabbage", cls.getMethods().get(2).getReturns().getValue());

    }

    public void testOldfashionedExtraClassesAreSupported() {
        String in = ""
                + "package oldfashioned;"
                + "public class Ping {"
                + "}"
                + "class Bar {"
                + "}";
        builder.addSource(new StringReader(in));
        assertEquals(2, builder.getClasses().size());
        assertNotNull(builder.getClassByName("oldfashioned.Ping"));
        assertNotNull(builder.getClassByName("oldfashioned.Bar"));
    }

    public void testBinaryClassesAreFound() {

        String in = ""
                + "package x;"
                + "import java.util.*;"
                + "class X {"
                + " ArrayList a();"
                + "}";
        builder.addSource(new StringReader(in));

        JavaClass cls = builder.getClassByName("x.X");
        JavaType returnType = cls.getMethods().get(0).getReturns();
        JavaClass returnClass = builder.getClassByName(returnType.getFullyQualifiedName());

        assertEquals("java.util.ArrayList", returnClass.getFullyQualifiedName());

        boolean foundList = false;
        for (JavaType type : returnClass.getImplements()) {
            if (type.getValue().equals("java.util.List")) {
                foundList = true;
            }
        }
        assertTrue(foundList);

        // See if interfaces work too.
        JavaClass list = builder.getClassByName("java.util.List");
        assertTrue(list.isInterface());
        assertNull(list.getSuperJavaClass());
        assertEquals("java.util.Collection", list.getImplements().get(0).getValue());
    }

    public void testSuperclassOfObjectIsNull() {
        JavaClass object = builder.getClassByName("java.lang.Object");
        JavaClass objectSuper = object.getSuperJavaClass();
        assertNull(objectSuper);
    }

    /*
    Various test for isA. Tests interface extension, interface implementation and
    class extension. Immediate and chained.

    java.util.Collection
             |  interface extension
    java.util.List
             |  interface implemention
    java.util.AbstractList
             |  class extension
    java.util.ArrayList
    */

    public void testConcreteClassCanBeTestedForImplementedClassesAndInterfaces() {
        JavaClass arrayList = builder.getClassByName("java.util.ArrayList");

        assertTrue("should be Object", arrayList.isA("java.lang.Object"));
        assertTrue("should be Collection", arrayList.isA("java.util.Collection"));
        assertTrue("should be List", arrayList.isA("java.util.List"));
        assertTrue("should be AbstractList", arrayList.isA("java.util.AbstractList"));
        assertTrue("should be ArrayList", arrayList.isA("java.util.ArrayList"));

        assertFalse("should not be Map", arrayList.isA("java.util.Map"));
    }

    public void testAbstractClassCanBeTestedForImplementedClassesAndInterfaces() {
        JavaClass abstractList = builder.getClassByName("java.util.AbstractList");

        assertTrue("should be Object", abstractList.isA("java.lang.Object"));
        assertTrue("should be Collection", abstractList.isA("java.util.Collection"));
        assertTrue("should be List", abstractList.isA("java.util.List"));
        assertTrue("should be AbstractList", abstractList.isA("java.util.AbstractList"));

        assertFalse("should not be ArrayList", abstractList.isA("java.util.ArrayList"));
        assertFalse("should not be Map", abstractList.isA("java.util.Map"));
    }

    public void testInterfaceCanBeTestedForImplementedInterfaces() {
        JavaClass list = builder.getClassByName("java.util.List");

        assertTrue("should be Collection", list.isA("java.util.Collection"));
        assertTrue("should be List", list.isA("java.util.List"));

        assertFalse("should not be ArrayList", list.isA("java.util.ArrayList"));
        assertFalse("should not be Map", list.isA("java.util.Map"));
        assertFalse("should not be Object", list.isA("java.lang.Object")); // I think! ;)
    }

    public void testClassCanBeTestedForNonexistantClasses() {
        String in = ""
                + "package food;"
                + "class Sausage extends food.Meat implements food.Proteine {"
                + "}";
        builder.addSource(new StringReader(in));

        JavaClass sausage = builder.getClassByName("food.Sausage");
        assertTrue(sausage.isA("food.Meat"));
        assertTrue(sausage.isA("food.Proteine"));
    }

    public void testClassesCanBeAddedLater() {
        testClassCanBeTestedForNonexistantClasses();
        assertEquals(1, builder.getClasses().size());
        JavaClass sausage = builder.getClassByName("food.Sausage");

        assertFalse(sausage.isA("global.Stuff"));
        String in = ""
                + "package food;"
                + "class Meat extends global.Stuff {"
                + "}";
        builder.addSource(new StringReader(in));
        assertEquals(2, builder.getClasses().size());
        assertTrue(sausage.isA("global.Stuff"));
    }

    public void testImageIconBeanProperties() {
        JavaClass imageIcon = builder.getClassByName("javax.swing.ImageIcon");

        assertNull(imageIcon.getBeanProperty("class"));

        BeanProperty clazz = imageIcon.getBeanProperty("class", true);
        assertNotNull(clazz.getAccessor());
        assertNull(clazz.getMutator());

        BeanProperty iconHeight = imageIcon.getBeanProperty("iconHeight");
        assertNotNull(iconHeight.getAccessor());
        assertNull(iconHeight.getMutator());

        BeanProperty image = imageIcon.getBeanProperty("image");
        assertNotNull(image.getAccessor());
        assertNotNull(image.getMutator());
    }

    public void testDerivedClassesAreFound() {
        /*
        Collection
         |  interface extension
        List
         |  interface implemention
        AbstractList
         |  class extension
        ArrayList
        */
        builder.addSource(new StringReader("public interface Collection {}"));
        builder.addSource(new StringReader("public interface List extends Collection {}"));

        builder.addSource(new StringReader("public class AbstractList implements List {}"));
        builder.addSource(new StringReader("public class ArrayList extends AbstractList {}"));

        JavaClass collection = builder.getClassByName("Collection");
        JavaClass list = builder.getClassByName("List");
        JavaClass abstractList = builder.getClassByName("AbstractList");
        JavaClass arrayList = builder.getClassByName("ArrayList");

        List<JavaClass> derivedClassesOfCollection = collection.getDerivedClasses();
        List<JavaClass> derivedClassesOfList = list.getDerivedClasses();
        List<JavaClass> derivedClassesOfAbstractList = abstractList.getDerivedClasses();
        List<JavaClass> derivedClassesOfArrayList = arrayList.getDerivedClasses();

        assertEquals(3, derivedClassesOfCollection.size());
        assertEquals(2, derivedClassesOfList.size());
        assertEquals(1, derivedClassesOfAbstractList.size());
        assertEquals(0, derivedClassesOfArrayList.size());
    }

    public void testSourcePropertyClass() throws IOException {
        builder.addSource(new File("src/test/java/com/thoughtworks/qdox/testdata/PropertyClass.java"));

        JavaClass propertyClass = builder.getClassByName("com.thoughtworks.qdox.testdata.PropertyClass");
        assertEquals(1, propertyClass.getBeanProperties().size());

        // test ctor, methods and fields
        assertEquals(6, propertyClass.getMethods().size());
        assertEquals(2, propertyClass.getConstructors().size());

        JavaConstructor ctor = propertyClass.getConstructor(null);
        JavaConstructor ctor2 = propertyClass.getConstructor(Collections.singletonList((JavaType) propertyClass));
        JavaMethod getFoo = propertyClass.getMethodBySignature("getFoo", null);
        JavaMethod isBar = propertyClass.getMethodBySignature("isBar", null);
        JavaMethod get = propertyClass.getMethodBySignature("get", null);
        JavaType intType = mock( JavaType.class );
        when( intType.getFullyQualifiedName() ).thenReturn( "int" );
        JavaMethod set = propertyClass.getMethodBySignature( "set", Collections.singletonList( (JavaType) intType ) );

        JavaMethod protectedMethod = propertyClass.getMethodBySignature("protectedMethod", null);
        JavaMethod privateMethod = propertyClass.getMethodBySignature("privateMethod", null);
        JavaMethod shouldntBeInherited = propertyClass.getMethodBySignature("getShouldntBeInherited", null);

        assertNotNull(ctor);
        assertNotNull(ctor2);
        assertNotNull(getFoo);
        assertNotNull(isBar);
        assertNotNull(get);
        assertNotNull(set);
        assertNotNull(protectedMethod);
        assertNotNull(privateMethod);
        assertNull(shouldntBeInherited);

        assertTrue(getFoo.isStatic());
        assertFalse(isBar.isStatic());
        assertFalse(get.isStatic());
        assertFalse(set.isStatic());
        assertFalse(protectedMethod.isStatic());
        assertFalse(privateMethod.isStatic());

        assertTrue(get.isFinal());
        assertFalse(set.isFinal());

        assertTrue(ctor2.isProtected());
        assertTrue(protectedMethod.isProtected());
        assertTrue(privateMethod.isPrivate());

        List<JavaField> fields = propertyClass.getFields();
        assertEquals(3, fields.size());
    }

    public void testSourceDefaultCtor() throws Exception {
        builder.addSource(new File("src/test/resources/com/thoughtworks/qdox/testdata/DefaultCtor.java"));
        JavaClass javaClass = builder.getClassByName("com.thoughtworks.qdox.testdata.DefaultCtor");

        JavaMethod ctor = javaClass.getMethodBySignature("DefaultCtor", null);
        assertNull(ctor);
    }

    public void testBinaryDefaultCtor() {
        JavaClass javaClass = builder.getClassByName("com.thoughtworks.qdox.testdata.DefaultCtor");

        JavaMethod ctor = javaClass.getMethodBySignature("DefaultCtor", null);
        assertNull(ctor);
    }

    /*
     * The JavaDocBuilder has to be serializable, With JavaProjectBuilder, we only need to serialize the ClassLibraryBuilder
     */
    public void testSerializable() {
        
        JavaSource source = builder.addSource(new StringReader("package test; public class X{}"));
        assertEquals("X", source.getClasses().get(0).getName());
        try {
            SerializationUtils.serializedCopy(builder);

            fail("JavaProjectBuilder should not serializable");
        }
        catch(RuntimeException ex) {
            if ( !(ex.getCause() instanceof NotSerializableException)) {
                fail("Unexpected RuntimeException caught: " + ex.getMessage());
            }
        }
    }

    public void testSaveAndRestore() throws Exception {
        File file = new File("target/test-source/cache.obj");
        builder.addSourceTree(new File("target/test-source"));
        builder.save(file);

        JavaProjectBuilder newBuilder = JavaProjectBuilder.load(file);
        assertNotNull(newBuilder.getClassByName("com.blah.subpackage.Cheese"));

        newBuilder.addSource(new StringReader("package x; import java.util.*; class Z extends List{}"));
        assertEquals("List", newBuilder.getClassByName("x.Z").getSuperClass().getValue());
        assertEquals("java.util.List", newBuilder.getClassByName("x.Z").getSuperClass().getFullyQualifiedName());
    }
    
    public void testSaveAndRestoreWithoutDefaultClassloaders() throws Exception {
        builder = new JavaProjectBuilder( new OrderedClassLibraryBuilder() );
        File file = new File("target/test-source/cache.obj");
        builder.addSourceTree(new File("target/test-source"));
        builder.save(file);

        JavaProjectBuilder newBuilder = JavaProjectBuilder.load(file);
        assertNotNull(newBuilder.getClassByName("com.blah.subpackage.Cheese"));

        newBuilder.addSource(new StringReader("package x; import java.util.*; class Z extends List{}"));
        //Here it's just List, since there we didn't use the defaultClassLoaders
        assertEquals("List", newBuilder.getClassByName("x.Z").getSuperClass().getValue());

    }

    public void testSuperClassOfAnInterfaceReturnsNull() {
        String in = "package x; interface I {}";
        builder.addSource(new StringReader(in));
        JavaClass cls = builder.getClassByName("x.I");
        assertNull("Should probably return null", cls.getSuperJavaClass());
    }

    public void testMethodsFromSuperclassesCanBeRetrieved() {
        String goodListSource = ""
                + "package x;"
                + "import java.util.*;"
                + "/**"
                + " * @foo bar"
                + " */"
                + "class GoodList extends ArrayList {"
                + "  public void good() {}"
                + "  private void myown() {}"
                + "}";
        builder.addSource(new StringReader(goodListSource));

        String betterListSource = ""
                + "package x;"
                + "/**"
                + " * @foo zap"
                + " */"
                + "class BetterList extends GoodList {"
                + "  public void better() {}"
                + "}";
        builder.addSource(new StringReader(betterListSource));

        JavaClass betterList = builder.getClassByName("x.BetterList");
        assertNull(betterList.getMethodBySignature("good", null));
        assertNotNull(betterList.getMethodBySignature("good", null, true));
        assertNotNull(betterList.getMethodBySignature("size", null, true));
        assertNull("Shouldn't be able to get private methods", betterList.getMethodBySignature("myown", null, true));
    }

    public void testTagLineNumbersAndSourceInTags() {
        String jallaSource = ""
                + "package x;\n"
                + "import java.util.*;\n"
                + "/**\n"
                + " * @line4 foo\n"
                + " * @line5 overflows onto\n"
                + " *        line6\n"
                + " */\n"
                + "class Jalla extends ArrayList {\n"
                + "}\n";
        builder.addSource(new StringReader(jallaSource));
        JavaClass jalla = builder.getClassByName("x.Jalla");
        DocletTag line4 = jalla.getTagByName("line4");
        assertEquals(4, line4.getLineNumber());
        assertSame(line4.getContext(), jalla);
        DocletTag line5 = jalla.getTagByName("line5");
        assertEquals(5, line5.getLineNumber());
    }

    public void testGetLineNumberForAbstractEntities() {
        String src = ""
            + "package x;\n"
            + "import java.util.*;\n"
            + "\n"
            + "class Foo {\n"
            + "  int i;\n"
            + "  int getI() { return i; }\n"
            + "}\n";
        builder.addSource(new StringReader(src));
        JavaClass fooClass = builder.getClassByName("x.Foo");
        assertEquals(4, fooClass.getLineNumber());
        JavaField iField = fooClass.getFieldByName("i");
        assertEquals(5, iField.getLineNumber());
        JavaMethod getIMethod = 
            fooClass.getMethodBySignature("getI", Collections.EMPTY_LIST);
        assertEquals(6, getIMethod.getLineNumber());
    }
    
    public void testJiraQdox14() {
        String source = "" +
                "package foo; \n" +
                "class Outer { \n" +
                "  Inner field1; \n" +
                "  class Inner { \n" +
                "    Outer.Inner field2; \n" +
                "  } \n" +
                "} \n" +
                "";
        builder.addSource(new StringReader(source));
        JavaClass outer = builder.getClassByName("foo.Outer");
        JavaClass inner = outer.getNestedClasses().get(0);
        assertEquals("foo.Outer$Inner", inner.getBinaryName());
        assertEquals("foo.Outer.Inner", inner.getFullyQualifiedName());

        JavaField field1 = outer.getFieldByName("field1");
        JavaClass type = field1.getType();
        assertEquals("foo.Outer$Inner", type.getBinaryName());
        assertEquals("foo.Outer.Inner", type.getFullyQualifiedName());
    }

    public void testJiraQdox16() {
        String source = "" +
                "/**Hip hop won*t stop*/" +
                "class x{}";
        builder.addSource(new StringReader(source));
        JavaClass x = builder.getClassByName("x");
        assertEquals("Hip hop won*t stop", x.getComment());
    }

    public void testCommentsCanHaveNewlines() {
        String source = "" +
                "/** Hello\n"
                + "* world!"
                + "*/" +
                "class x{}";
        builder.addSource(new StringReader(source));
        JavaClass x = builder.getClassByName("x");
        assertEquals("Hello\nworld!", x.getComment());
    }

    public void testTagValuesCanSpanMultipleLines() {
        String source = "" +
                "/**\n" +
                " * @bar.baz foo=\"this is\\\n" + 
                " *       multilined\"\n" +
                " */\n" +
                "class x{}";
        builder.addSource(new StringReader(source));
        JavaClass x = builder.getClassByName("x");
        DocletTag tag = x.getTagByName("bar.baz");
        assertEquals("foo=\"this is\\\n      multilined\"", tag.getValue());
        assertEquals("this is\n      multilined", tag.getNamedParameter("foo"));
    }

    public void testJiraQdox19() {
        String source = "" +
                "class x { \n" +
                "    /**\n" +
                "     * @y z\n" +
                "     * \n" +
                "     */\n" +
                "    String m();\n" +
                "}\n" +
                "";

        builder.addSource(new StringReader(source));
        JavaClass x = builder.getClassByName("x");
        JavaMethod m = x.getMethods().get(0);
        DocletTag foo = m.getTagByName("y");
        assertEquals("z", foo.getValue());
    }

    public void testTagInheritance() {
        String X = "" +
                "/** @c x */" +
                "class X {" +
                "  /** \n" +
                "   * @m x \n" +
                "   * @s f\n" +
                "   */" +
                "  void i(){}" +
                "}";
        String Y = "" +
                "/** @c y */" +
                "class Y extends X {" +
                "  /** @m y */" +
                "  void i(){}" +
                "}";
        builder.addSource(new StringReader(X));
        builder.addSource(new StringReader(Y));

        JavaClass y = builder.getClassByName("Y");
        List<DocletTag> c = y.getTagsByName("c", true);
        assertEquals(2, c.size());
        assertEquals("y", c.get(0).getValue());
        assertEquals("x", c.get(1).getValue());

        JavaMethod i = y.getMethodBySignature("i", null);
        List<DocletTag> m = i.getTagsByName("m", true);
        assertEquals(2, m.size());
        assertEquals("y", m.get(0).getValue());
        assertEquals("x", m.get(1).getValue());

        DocletTag s = i.getTagByName("s", true);
        assertEquals("f", s.getValue());
    }

    public void testJiraQdox27() {
        String sourceCode = ""
                + "package com.acme.thing;\n"
                + "\n"
                + "/**"
                + " * This class does something."
                + " **/"
                + "public class AClassName {\n"
                + "}";
        builder.addSource(new StringReader(sourceCode));
        JavaClass aClass =
                builder.getClassByName("com.acme.thing.AClassName");
        assertNotNull(aClass);
    }

    public void testJiraQdox39() {
        String sourceCode = ""
                + "public class A {\n"
                + " int i,j=2,k[];"
                + "}";
        builder.addSource(new StringReader(sourceCode));
        JavaClass a = builder.getClassByName("A");
        assertEquals(3, a.getFields().size());
        assertEquals("i", a.getFields().get(0).getName());
        assertEquals("int", a.getFields().get(0).getType().toString());
        assertEquals("j", a.getFields().get(1).getName());
        assertEquals("int", a.getFields().get(1).getType().toString());
        assertEquals("k", a.getFields().get(2).getName());
        assertEquals("int[]", a.getFields().get(2).getType().toString());
    }

    public void testJiraQdox40() {
        String sourceCode = ""
                + "package foo.bar;"
                + "public class Outer {"
                + "  class WrappedInternalContextAdapter implements InternalContextAdapter {"
                + "  }"
                + "}";
        builder.addSource(new StringReader(sourceCode));
        JavaClass clazz = builder.getClassByName("foo.bar.Outer");

        assertEquals(1, clazz.getNestedClasses().size());
    }

    public void testParseErrorLocationShouldBeAvailable() {
        String badSource = ""
                + "package x;\n"
                + "import java.util.*;\n"
                + "class Bad [\n";
        try {
            builder.addSource(new StringReader(badSource));
            fail("ParseException expected");
        } catch (ParseException e) {
            assertEquals(3, e.getLine());
            assertEquals(11, e.getColumn());
        }
    }

    public void testJiraQdox35() {
        String sourceCode = "package pack; public class Foo extends Bar implements Zap {}";
        builder.addSource(new StringReader(sourceCode));
        JavaClass clazz = builder.getClassByName("pack.Foo");
        assertEquals(1, clazz.getInterfaces().size());
        // Ideally the fully qualified names should be the pack.Zap and pack.Bar,
        // but this will do for now to fix the NPE bug.
        assertEquals("Zap", clazz.getInterfaces().get(0).getFullyQualifiedName());
        assertEquals("Bar", clazz.getSuperJavaClass().getFullyQualifiedName());
    }

    /**
     * @french.english
     *      cheese="fromage"
     *      fish="poisson"
     *
     * @band.singer
     *      doors=morrison
     *      cure=smith
     */
    public void testShouldShouldReportTagParameterKeys() {
        String sourceCode = "" +
                "    /**\n" +
                "     * @french.english\r\n" +
                "     *      cheese=\"fromage\"\n\r" +
                "     *      fish=\"poisson\"\r" +
                "     */\n" +
                "     class MultiLine{}";
        JavaClass multiline = builder.addSource(new StringReader(sourceCode)).getClasses().get(0);
        DocletTag frenchEnglish = multiline.getTagByName("french.english");

        Set<String> expected = new HashSet<String>();
        expected.add("cheese");
        expected.add("fish");
        assertEquals(expected,frenchEnglish.getNamedParameterMap().keySet());
    }
    
    public void testJiraQdox58() {
        builder.addSource(new StringReader(
            "class Y implements SomeInterface { }")
        );

        Collection<JavaClass> results = builder.search(new Searcher() {
            public boolean eval(JavaClass javaClass) {
                return javaClass.isA("SomeInterface");
            }
        });

        assertEquals(1, results.size());
    } 
    
    public void testJiraQdox63() {
        builder.addSource(
            new StringReader(
                "package p1;\n" +
                "class A {\n" +
                "  static class Inner {}\n" +
                "}"
            )
        );
        builder.addSource(
            new StringReader(
                "package p2;\n" +
                "import p1.A;\n" +
                "class B {\n" +
                "  A.Inner innerField;\n" +
                "}"
            )
        );

        JavaClass innerClass = 
            builder.getClassByName("p1.A").getNestedClassByName("Inner");
        JavaField innerField = 
            builder.getClassByName("p2.B").getFieldByName("innerField");
        assertEquals(innerClass, innerField.getType() );
        assertEquals("p1.A$Inner", innerField.getType().getBinaryName());
        assertEquals("p1.A.Inner", innerField.getType().getFullyQualifiedName());
    }

    public void testJiraQdox71() {
        String sourceCode = ""
                + "package foo;"
                + "public class C {"
                + "  boolean flag = (X < Y);"
                + "}";
        builder.addSource(new StringReader(sourceCode));
    }

    public void testReadMultipleMethodAnnotations() {
        String sourceCode = ""
                + "public class C {"
                + "  @Annotation\n"
                + "  @AnotherAnnotation\n"
                + "  public void aMethod() {}\n"
                + "}";
        JavaSource javaSource = builder.addSource(new StringReader(sourceCode));
        JavaMethod javaMethod = javaSource.getClasses().get(0).getMethods().get(0);
        assertEquals("aMethod", javaMethod.getName());
    }

    public void testReadMultipleClassAnnotations() {
        String sourceCode = ""
                + "@Annotation\n"
                + "@AnotherAnnotation\n"
                + "public class C {"
                + "  public void aMethod() {}\n"
                + "}";
        JavaSource javaSource = builder.addSource(new StringReader(sourceCode));
        JavaClass javaClass = javaSource.getClasses().get(0);
        assertEquals("C", javaClass.getName());
    }

    public void testMethodBody() {
        String sourceCode = "" +
                "public class X {\n" +
                "  public void doStuff() {\n" +
                "    System.out.println(\"hi\"); // comment\n" +
                "    Foo<X> x = new Cheese().get()[4]; /*x*/\n" +
                "  } // not this \n" +
                "}";
        JavaSource javaSource = builder.addSource(new StringReader(sourceCode));
        JavaClass javaClass = javaSource.getClasses().get(0);
        JavaMethod javaMethod = javaClass.getMethods().get(0);
        String expected = "" +
                "    System.out.println(\"hi\"); // comment\n" +
                "    Foo<X> x = new Cheese().get()[4]; /*x*/";
        assertEquals(expected.trim(), javaMethod.getSourceCode().trim());
    }

    public void testMethodBodyWithConfusingCurlies() {
        String sourceCode = "" +
                "public class X {\n" +
                "  public void doStuff() {\n" +
                "    System.out.println(\"}}} \\\"\"); // }\n" +
                "    Foo<X> x = new Cheese().get()[4]; /*}}*/ /etc\n" +
                "  } // not this \n" +
                "}";
        JavaSource javaSource = builder.addSource(new StringReader(sourceCode));
        JavaClass javaClass = javaSource.getClasses().get(0);
        JavaMethod javaMethod = javaClass.getMethods().get(0);
        String expected = "" +
                "    System.out.println(\"}}} \\\"\"); // }\n" +
                "    Foo<X> x = new Cheese().get()[4]; /*}}*/ /etc\n";
        assertEquals(expected.trim(), javaMethod.getSourceCode().trim());
    }

    public void testMethodBodyWithPrecedingStaticBlock() {
        String sourceCode = "" +
                "public class X {\n" +
                "  static {\n" +
                "    System.out.println(\"static\");\n" +
                "  }\n" +
                "  public void doStuff() {\n" +
                "    System.out.println(\"hi\"); // comment\n" +
                "    Foo<X> x = new Cheese().get()[4]; /*x*/\n" +
                "  } // not this \n" +
                "}";
        JavaSource javaSource = builder.addSource(new StringReader(sourceCode));
        JavaClass javaClass = javaSource.getClasses().get(0);
        JavaMethod javaMethod = javaClass.getMethods().get(0);
        String expected = "" +
                "    System.out.println(\"hi\"); // comment\n" +
                "    Foo<X> x = new Cheese().get()[4]; /*x*/";
        assertEquals(expected.trim(), javaMethod.getSourceCode().trim());
    }

    public void testFieldDefinition() {
        String sourceCode = "" +
                "public class X {\n" +
                "  int x = new FlubberFactory<Y>(\"}\"){}.doCheese(spam/*c*/)\n" +
                "    [9] /*comment*/ //more\n; /*somethingelse*/" +
                "}";
        JavaSource javaSource = builder.addSource(new StringReader(sourceCode));
        JavaClass javaClass = javaSource.getClasses().get(0);
        JavaField javaField = javaClass.getFields().get(0);
        String expected = "" +
                "new FlubberFactory<Y>(\"}\"){}.doCheese(spam/*c*/)\n" +
                "    [9] /*comment*/ //more";
        assertEquals(expected.trim(), javaField.getInitializationExpression().trim());
    }

    public void testNewlessArrays() {
        String source = "" +
                "public class Thing {\n" +
                " long[] bad = {1,2,3};\n" +  // as opposed to bad = new long[] {1,2,3}.
                "}";
        JavaSource javaSource = builder.addSource(new StringReader(source));

        JavaField field = javaSource.getClasses().get(0).getFieldByName("bad");
        assertEquals("{1,2,3}", field.getInitializationExpression().trim());
    }

    public void testDefaultsToThrowingExceptionWhenNotParseable() throws Exception {
        createFile("target/test-source/com/blah/Bad.java", "com.blah", "@%! BAD {}}}}");

        try {
            builder.addSourceTree(new File("target/test-source"));
            fail("Expected exception");
        } catch (ParseException expectedException) {
            // Good!
        }
    }

    public void testContinuesProcessingAfterBadFileIfCustomHandlerPermits() throws Exception {
        createFile("target/test-source/com/blah/Bad.java", "com.blah", "@%! BAD {}}}}");

        ErrorHandler mockErrorHandler = mock(ErrorHandler.class);

        builder.setErrorHandler( mockErrorHandler );
        builder.addSourceTree(new File("target/test-source"));

        assertNotNull(builder.getClassByName("com.blah.Thing"));
        
        verify( mockErrorHandler ).handle( any( ParseException.class ) );
    }

    public void testBinaryClassFieldModifiers() {
        JavaClass javaClass = builder.getClassByName(PropertyClass.class.getName());
        assertEquals("Class", PropertyClass.class.getName(), javaClass.getFullyQualifiedName());
        JavaField javaField = javaClass.getFieldByName("aField");
        assertNotNull("Field", javaField);
        Set<String> modifiers = new HashSet<String>(javaField.getModifiers());
        assertEquals("Modifier count", 2, javaField.getModifiers().size());
        assertTrue("Static", modifiers.contains("static"));
        assertTrue("Public", modifiers.contains("public"));
    }

    public void testMultipleFieldsWithJavaDoc() {
        String sourceCode = "class Thing {\n" +
                " /** some doc */\n" +
                " int a = 1,\n" +
                " /** more doc */\n" +
                " b = 2,\n" +
                " /** etc */\n" +
                " c = 3; }";
        JavaClass javaClass = builder.addSource(new StringReader(sourceCode)).getClasses().get(0);
        JavaField fieldA = javaClass.getFieldByName("a");
        assertEquals("some doc", fieldA.getComment());
        JavaField fieldB = javaClass.getFields().get(1);
        assertEquals("more doc", fieldB.getComment());
        JavaField fieldC = javaClass.getFields().get(2);
        assertEquals("etc", fieldC.getComment());
    }

    public void testValueRemainsIntact() {
        String in = ""
                + "package x;\n"
                + "/**\n"
                + " * @tag aa count(*) bbb * ccc dd=e f='g' i = \"xx\"\n"
                + " */\n"
                + "class X {}";

        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSource(new StringReader(in));
        DocletTag tag = builder.getClassByName("x.X").getTagByName("tag");

        assertEquals("aa count(*) bbb * ccc dd=e f='g' i = \"xx\"", tag.getValue());
    }
    
    public void testJiraQdox117() {
        String sourceCode = "" +
                "public class foo {\n" +
                "{ dosomething(); }\n" +
                "@Test (description=\"test blah blah\")\n" +
                "public void myTest() {}\n" +
                "}";
        JavaSource javaSource = builder.addSource(new StringReader(sourceCode));
        JavaClass javaClass = javaSource.getClasses().get(0);
        JavaMethod javaMethod = javaClass.getMethods().get(0);
        assertEquals("\"test blah blah\"", javaMethod.getAnnotations().get(0).getNamedParameter("description").toString());
    }
    
    public void testJiraQdox131() {
        String sourceCode = "package com.acme.qdox;\n" + 
               "\n" + 
               "public class QDoxBugClass {\n" + 
               "    final public static String C1 = \"C1\", C2 = \"C2\";\n" + 
               "    final public static String[] ALL = { C1, C2 };    \n" + 
               "    /*\n" + 
               "    Comment\n" + 
               "    */\n" + 
               "    public void method() {\n" + 
               "        System.out.println(\"This will contain the comment\");\n" + 
               "    }\n" + 
               "}\n" + 
               "";
       builder.addSource(new StringReader(sourceCode));
       JavaClass aClass = builder.getClassByName("com.acme.qdox.QDoxBugClass");
       assertEquals("\n        System.out.println(\"This will contain the comment\");\n    ", 
               aClass.getMethods().get(0).getSourceCode());
   }
    
    public void testJiraQdox134() {
        String sourceCode = "/**\n" + 
                "*\n" + 
                "@myTag name=TestClass attrs=Something1,Something2,Something3\n" + 
                "*/\n" + 
                "public class TestClassImpl {\r\n" + 
                "}";
        JavaClass jClass = builder.addSource(new StringReader(sourceCode)).getClasses().get(0);
        assertEquals( Arrays.asList( new String[] {"name=TestClass","attrs=Something1,Something2,Something3"}), jClass.getTags().get(0).getParameters());
        //assertTrue( Arrays.equals( new String[] {"name=TestClass","attrs=Something1,Something2,Something3"}, jClass.getTags()[0].getParameters() ));
    }
    
    //for qdox-146
    public void testWhitespaceCanBeRetainedInJavadoc() {
        String sourceCode = ""
                + "package com.acme.thing;\n"
                + "\n"
                + "/**\n"
                + " * This class does something.\n"
                + " *     chalala\n"
                + " *         cha  **  lala\n"
                + " **/\n"
                + "public class AClassName {\n"
                + "}";
        builder.addSource(new StringReader(sourceCode));
        JavaClass aClass = builder.getClassByName("com.acme.thing.AClassName");
        assertEquals("This class does something.\n"
                + "    chalala\n"
                + "        cha  **  lala", aClass.getComment());
    }

    //for qdox-152
    public void testExtendedClass() {
        String source = "import javax.faces.component.UIOutput;" +
                "public abstract class AbstractSayHello extends UIOutput {\n" +
                "}";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses().get(0);
        assertEquals("UIOutput", javaClass.getSuperClass().getValue());
        assertEquals("javax.faces.component.UIOutput", javaClass.getSuperClass().getFullyQualifiedName());
    }
    
    //for QDox-154
    public void testImplicitJavadocCommentOrder() {
        String source = "" +
                "public class Foo {\n" +
                "    /**\n" + 
                "     * A Javadoc sample.\n" + 
                "     *\n" + 
                "     * @return The size.\n" + 
                "     */\n" + 
                "    public long getSize()\n" + 
                "    {\n" + 
                "        return 0;\n" + 
                "    }\n" + 
                "\n" + 
                "    /**\n" + 
                "     * @return The size.\n" +
                "     *\n" + 
                "     * A Javadoc sample.\n" + 
                "     */\n" + 
                "    public long getSize2()\n" + 
                "    {\n" + 
                "        return 0;\n" + 
                "    }\n" +
                "}";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses().get(0);
        JavaMethod method1 = javaClass.getMethods().get(0);
        assertEquals( "A Javadoc sample.", method1.getComment());
        assertEquals( "The size.", method1.getTagByName( "return" ).getValue());
        JavaMethod method2 = javaClass.getMethods().get(1);
        assertEquals( "The size.\n\nA Javadoc sample.", method2.getTagByName( "return" ).getValue());
    }

    //for qdox-155
    public void testCharField() {
        String source = "public class Foo {\n" +
                "public static final char SEPARATOR = ',';" +
                "}";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses().get(0);
        assertEquals(javaClass.getFieldByName( "SEPARATOR" ).getInitializationExpression(), "','");
    }
    
  //for qdox-157
    public void testCommentBetweenFields() {
        String source = "public class Foo {\n" +
                "public static final String TEST1 = \"test1\";\n" +
                "// TODO: blabla\n" +
                "public static final String TEST2 = \"test2\";\n" +
                "}";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses().get(0);
        assertEquals(javaClass.getFieldByName( "TEST2" ).getInitializationExpression(), "\"test2\"");
    }
    
    public void testAnnotationWithComment() {
        String source = "@OneToMany(cascade = {/* CascadeType.PERSIST */}, fetch = FetchType.LAZY)\n" +
                "public class Foo{}"; 
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses().get(0);
        assertNotNull(javaClass.getAnnotations().get(0).getNamedParameter("cascade"));
    } 
    
    /**
     * According to sun specs: Starting with Javadoc 1.4, the leading asterisks are optional
     * @throws Exception
     */
    public void testJavadocWithoutStartingAsterisks() {
        String source = "    /**\n" + 
                "     Some text\n" +
                "more text\n" +
                "\t and even more\n" +
                "     \n" + 
                "     @throws Exception\n" +
                "@deprecated" + 
                "     */\n" + 
                "public class Foo{}";
        JavaSource javaSource = builder.addSource(new StringReader(source));
        JavaClass javaClass = javaSource.getClasses().get(0);
        assertEquals("Some text\nmore text\nand even more", javaClass.getComment());
        assertEquals("throws", javaClass.getTags().get(0).getName());
        assertEquals("Exception", javaClass.getTags().get(0).getValue());
        assertEquals("deprecated", javaClass.getTags().get(1).getName());
    }
    
    // for QDOX-189
    public void testFinalAnnotationParam() {
        String source = "public final class WSEndpointReference {\n" +
            "    public void writeTo(final @NotNull String localName, @NotNull XMLStreamWriter w) throws XMLStreamException {\n" +
            "    }\n" +
            "}";
        builder.addSource(new StringReader(source));
    }
    
    // for QDOX-190
    public void testTwoCommentsBeforeEnumValue() {
        String source = 
            "public enum Source {\n" +
            "    /** comment 1 */    \n" +
            "    /** comment 2 */\n" +
            "    JDK1_2(\"1.2\");\n" +
            "}\n";  
        JavaSource src = builder.addSource(new StringReader(source));
        assertEquals( "comment 2", src.getClasses().get(0).getFieldByName( "JDK1_2" ).getComment() ); 
    }

    //for QDOX-191
    public void testLeftShift() {
        String source = 
            "private static class Flags {\n" +
            "   static final Flags LEFT_JUSTIFY = new Flags(1 << 0);\n" +
            "}\n";
        builder.addSource(new StringReader(source));
    }
    
    public void testGenericConstructor() {
        String source = 
            "public class MXBeanSupport {\n" +
            "  public <T> MXBeanSupport(T resource, Class<T> mxbeanInterface)\n" +
            "    throws NotCompliantMBeanException {\n" +
            " } \n" +
            "}";        
        builder.addSource(new StringReader(source));
    }
    
    // for QDOX-195
    public void testSharedPackageJavaClasses() {
        String source1 = "@javax.xml.bind.annotation.XmlSchema(namespace = \"http://docs.oasis-open.org/wsn/br-2\")\n" +
                "package com.foo;\n" +
                "public class Bar1 {}";
        String source2 = "package com.foo;\n" +
                "public class Bar2{}";
        JavaSource javaSource1 = builder.addSource(new StringReader(source1));
        JavaSource javaSource2 = builder.addSource(new StringReader(source2));
        JavaPackage jPackage = builder.getPackageByName("com.foo");
        assertEquals( 2, jPackage.getClasses().size() );
        assertEquals( 2, javaSource1.getPackage().getClasses().size() );
        assertEquals( 2, javaSource2.getPackage().getClasses().size() );
        assertNotSame( javaSource1.getPackage(), javaSource2.getPackage() );
        assertEquals( 1, javaSource1.getPackage().getAnnotations().size() );
        assertEquals( 0, javaSource2.getPackage().getAnnotations().size() );
        assertEquals( 2, javaSource1.getPackage().getLineNumber() );
        assertEquals( 1, javaSource2.getPackage().getLineNumber() );
    }
    
    public void testSourceFolder() {
        builder.addSourceFolder( new File("target/test-source") );
        String source = "package com.foo;\n" +
                "import com.blah.*;\n" +
                "public abstract class Me {\n" +
                " public abstract Thing getThing(); " +
                "}";
        builder.addSource( new StringReader( source ) );
        JavaClass clazz = builder.addSource( new StringReader( source ) ).getClasses().get(0);
        JavaClass thing = clazz.getMethods().get(0).getReturns();
        assertEquals("com.blah.Thing", thing.getFullyQualifiedName());
        assertNotNull(thing.getSource());
    }
    
    // for QDOX-208
    public void testMethodLineNumber() {
        String source = "package com.foo;\n" +
                "public class Bar {\n" +
                "  public void method1() \n" +
                "  {}\n" +
                "\n" +
                "  /**\n" +
                "   * Method with javadoc\n" +
                "   */\n" +
                "   public void method1() { \n" +
                "   }\n" +
                "}";
        JavaClass clazz = builder.addSource( new StringReader( source ) ).getClasses().get(0);
        assertEquals( 2, clazz.getLineNumber() );
        assertEquals( 3, clazz.getMethods().get(0).getLineNumber() );
        assertEquals( 9, clazz.getMethods().get(1).getLineNumber() );
    }
    
    public void testConstructorLineNumber() {
        String source = "package fix.test;\r\n" + 
            "public class ClassWithJavadoc\r\n" + 
            "    implements InterfaceWithJavadoc\r\n" + 
            "{\r\n" + 
            "    public ClassWithJavadoc()\r\n" + 
            "    {\r\n" + 
            "    }"
            + "}";
        
        builder.addSource(new StringReader( source ));
        
        assertEquals( 5, builder.getClassByName( "fix.test.ClassWithJavadoc" ).getConstructors().get( 0 ).getLineNumber() );
    }
    
    // for QDOX-209
    public void testAnnotationMap() {
        String source = "import javax.persistence.JoinColumn;\n" + 
                "public class Instruction {\n" + 
                "    private static final int something = 40;\n" + 
                "    //-----------------------------------------------------------------------\r\n" + 
                "    @JoinColumn(name=\"test\",bla=\"hi\")\n" + 
                "    int testfield;\r\n" + 
                "}";
        builder.addSource(new StringReader( source ));
        JavaClass classByName = builder.getClassByName("Instruction");
        JavaField fieldByName = classByName.getFieldByName("testfield");
        List<JavaAnnotation> annotations = fieldByName.getAnnotations();
        
        // Now we do have the annotation "JoinColumn" in annotations[0]
        Map<String, Object> propertyMap = annotations.get(0).getNamedParameterMap();
        // This one works
        assertEquals("\"hi\"", propertyMap.get("bla"));
        String string = (String) propertyMap.get("name");
        // This one does not work
        assertEquals("\"test\"", string);
    }
    
    
    public void testEnumConstantArguments()
    {
        String source = "public enum AssignmentOperators {" +
        		" EQ    ( a = b )," +
        		" TMSEQ ( a *= b )," +
        		" DVDEQ ( a /= b )," +
        		" MODEQ ( a %= b )," +
        		" PLEQ  ( a += b )," +
        		" MNEQ  ( a -= b )," +
        		" LT2EQ ( a <<= b )," +
                " GT2EQ ( a >>= b )," +
                " GT3EQ ( a >>>= b )," +
                " ANDEQ ( a &= b )," +
                " XOREQ ( a ^= b )," +
                " OREQ  ( a |= b )" +
        		" }";
        JavaClass cls = builder.addSource(new StringReader( source )).getClassByName( "AssignmentOperators" );
        JavaField xoreq = cls.getFieldByName( "XOREQ" );
        assertEquals( 1, xoreq.getEnumConstantArguments().size() );
        assertEquals( "a ^= b", xoreq.getEnumConstantArguments().get(0).getParameterValue() );
    }
    
    public void testIncrementAndDecrement()
    {
        String source = "public enum Expression {" +
                " POSTINC ( a++ )," +
                " POSTDEC ( a-- )," +
                " PREINC  ( ++a )," +
                " PREDEC  ( --a )" +
                " }";
        JavaClass cls = builder.addSource(new StringReader( source )).getClassByName( "Expression" );
        JavaField postInc = cls.getFieldByName( "POSTINC" );
        assertEquals( 1, postInc.getEnumConstantArguments().size() );
        assertEquals( "a++", postInc.getEnumConstantArguments().get( 0 ).getParameterValue() );
    }
    
    // for QDOX-230
    public void testInterfaceAnnotations() {
        String source = "@RemoteServiceRelativePath(\"greetings\")\r\n" + 
        		"public interface GreetingService extends RemoteService {\r\n" + 
        		"    String greetServer(String name) throws IllegalArgumentException;\r\n" + 
        		"}";
        builder.addSource(new StringReader( source ));
        JavaClass cls = builder.getClassByName( "GreetingService" );
        assertEquals( 1, cls.getAnnotations().size() );
    }
    
    // for QDOX-243
    public void testReadsGenericsInGenericType()
    {
        final String sourceCode =
            ""
                + "package foo;\n"
                + "public static class DummyOne {\n"
                + "  public static java.util.list<java.util.Map<? extends java.util.Set<Long>, String>> crazyType() { return null; }\n"
                + "}\n";

        builder.addSource( new java.io.StringReader( sourceCode ) );
        JavaClass qDoxClass = builder.getClassByName( "foo.DummyOne" );
        JavaMethod qDoxMethod = qDoxClass.getMethodBySignature( "crazyType", null );

        JavaParameterizedType returnType = (JavaParameterizedType) qDoxMethod.getReturnType();
        assertEquals( "java.util.Map<? extends java.util.Set<java.lang.Long>,java.lang.String>",
                      returnType.getActualTypeArguments().get( 0 ).getGenericFullyQualifiedName() );
        assertEquals( "java.util.Map<? extends java.util.Set<java.lang.Long>,java.lang.String>",
                      returnType.getActualTypeArguments().get( 0 ).getGenericCanonicalName() );
    }
    
    // for QDOX-244
    public void testReadsGenericTypeParameters()
    {
        final String sourceCode =
            "" + "package foo;\n" + "public static class DummyOne {\n"
                + "  public static <T extends Number & Iterable<Integer>> T genericTypeParam(T x) { return null; }\n"
                + "}\n";

        builder.addSource( new java.io.StringReader( sourceCode ) );
        JavaClass qDoxClass = builder.getClassByName( "foo.DummyOne" );
        JavaMethod qDoxMethod = qDoxClass.getMethods().get(0);

        JavaTypeVariable<JavaGenericDeclaration> result = qDoxMethod.getTypeParameters().get( 0 );
        assertEquals( "<T extends java.lang.Number & java.lang.Iterable<java.lang.Integer>>", result.getGenericFullyQualifiedName() );
        assertEquals( "<T extends java.lang.Number & java.lang.Iterable<java.lang.Integer>>", result.getGenericCanonicalName() );
    }

    // for QDOX-245
    public void
    testReadsGenerifiedParameterTypes() {
        final String sourceCode = "" +
                "package foo;\n" +
                "public static class DummyOne {\n" +
                "  public static String withGenerifiedParam(java.util.Collection<? extends Comparable<String>> things) { return null; }\n" +
                "}\n";
        
        builder.addSource(new java.io.StringReader(sourceCode));
        JavaClass qDoxClass = builder.getClassByName("foo.DummyOne");
        JavaMethod qDoxMethod = qDoxClass.getMethods().get(0);
        
        JavaType result = qDoxMethod.getParameterTypes(true).get( 0 );
        assertEquals("java.util.Collection<? extends java.lang.Comparable<java.lang.String>>", result.getGenericFullyQualifiedName());
        assertEquals("java.util.Collection<? extends java.lang.Comparable<java.lang.String>>", result.getGenericCanonicalName());
    }

    // for QDOX-253
    public void testConstructorHasAnnotation()
    {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        String source =
            "public class Foo { private String apiPath; public Foo(@Value(\"${api.path}\") String apiPath) {this.apiPath = apiPath}}";
        JavaClass qDoxClass = builder.addSource( new StringReader( source ) ).getClassByName( "Foo" );
        assertEquals( 1, qDoxClass.getConstructors().size() );
        JavaConstructor qDoxConstructor = qDoxClass.getConstructors().get( 0 );
        assertEquals( 1, qDoxConstructor.getParameters().size() );
        JavaParameter qDoxParameter = qDoxConstructor.getParameters().get( 0 );
        assertEquals( "apiPath", qDoxParameter.getName() );
        assertEquals( qDoxConstructor, qDoxParameter.getExecutable() );
    }
    
    // for QDOX-255
    public void testPackageAnnotation()
    {
        String source = "@Anot\r\n" + 
            "package net.jcs.jboilerdowntest;\r\n" + 
            "import net.jcs.annotation.Anot;";
        
        JavaProjectBuilder builder = new JavaProjectBuilder();
        JavaPackage pckg = builder.addSource( new StringReader( source) ).getPackage();
        assertEquals( "net.jcs.jboilerdowntest", pckg.getName() );
        assertEquals( 1, pckg.getAnnotations().size() );
    }
    
    public void testCanonicalName()
    {
        String source =
            "package com.foo;\b" + "public class Outer {\n" + "public class Inner {\n" + "public class Core {}\n"
                + "}\n" + "}\n";
        JavaClass cls = builder.addSource( new StringReader( source ) ).getClasses().get( 0 );
        assertEquals( "com.foo.Outer", cls.getFullyQualifiedName() );
        assertEquals( "com.foo.Outer", cls.getCanonicalName() );
        cls = cls.getNestedClassByName( "Inner" );
        assertEquals( "com.foo.Outer$Inner", cls.getBinaryName() );
        assertEquals( "com.foo.Outer.Inner", cls.getFullyQualifiedName() );
        assertEquals( "com.foo.Outer.Inner", cls.getCanonicalName() );
        cls = cls.getNestedClassByName( "Core" );
        assertEquals( "com.foo.Outer$Inner$Core", cls.getBinaryName() );
        assertEquals( "com.foo.Outer.Inner.Core", cls.getFullyQualifiedName() );
        assertEquals( "com.foo.Outer.Inner.Core", cls.getCanonicalName() );
    }
    
    public void testEnumConstantMethods() {
        String source = "public enum MethodLocationOfEnumMethod\n" + 
        		"{\n" + 
        		"  A()\n" + 
        		"  {\n" + 
        		"    @Override\n" + 
        		"    private void method()\n" + 
        		"    {\n" + 
        		"    };\n" + 
        		"  };\n" + 
        		"  public abstract void method();\n" + 
        		"  private void test()\n" + 
        		"  {\n" + 
        		"  };\n" + 
        		"  \n" + 
        		"  String name = \"x\";" + 
        		"}";
        builder.addSource( new StringReader( source ) );
        JavaClass cls = builder.getClassByName( "MethodLocationOfEnumMethod" );
        assertEquals( 2, cls.getMethods().size() );
        assertEquals( "method", cls.getMethods().get( 0 ).getName() );
        assertEquals( true, cls.getMethods().get( 0 ).isAbstract() );
        assertEquals( "test", cls.getMethods().get( 1 ).getName() );
    }
    
    // QDOX-240
    public void testComplexEnum()
    {
        String source = "import java.util.HashMap;\r\n" + 
            "\r\n" + 
            "public enum MyEnum {\r\n" + 
            "    MAP(new HashMap<String, Object>()); // Parser throws java.util.EmptyStackException\r\n" + 
            "    \r\n" + 
            "    public final Object defaultValue;\r\n" + 
            "    \r\n" + 
            "    private MyEnum(Object defaultValue) {\r\n" + 
            "        this.defaultValue = defaultValue;\r\n" + 
            "    }\r\n" + 
            "}";
        builder.addSource( new StringReader( source ) ); 
    }
    
    public void testFQNField()
    {
        String source = "import java.text.SimpleDateFormat;\r\n" + 
            "public class Car {\r\n" + 
            "    private java.text.SimpleDateFormat format;\r\n" + 
            "}";
        
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSource(new StringReader( source ));
        JavaField field = builder.getClassByName("Car").getFieldByName("format");
        assertEquals( "java.text.SimpleDateFormat", field.getType().getFullyQualifiedName());
    }
    
    // Github #12
    public void testSamePackage() {
        String source = "package com.fg.rtdoc.test;\n" + 
            "public class Test extends Extend implements Iface {}";
        
        String iface = "package com.fg.rtdoc.test;\n" + 
                        "public interface Iface {}";

        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSource(new StringReader( iface ));
        JavaClass clz = builder.addSource(new StringReader( source )).getClassByName( "Test" );
        
        assertEquals( "com.fg.rtdoc.test.Iface", clz.getInterfaces().get(0).getFullyQualifiedName() );
    }

    // Github #16
    public void testBeanProperties()
    {
        String zothParent = "public class ZothParent {\n" + 
            " private String name;\n" + 
            " public String getName() {\n" + 
            "    return name;\n" + 
            " }\n" + 
            " public void setName(String name) {\n" + 
            "    this.name = name;\n" + 
            " }\n" + 
            "}";
        
        String zothChild = "public class ZothChild // extends ZothParent\n" + 
            "{\n" + 
            " private int age;\n" + 
            " public int getAge() {\n" + 
            "    return age;\n" + 
            " }\n" + 
            " public void setAge(int age) {\n" + 
            "    this.age = age;\n" + 
            " }\n" + 
            "}";
        
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSource(new StringReader( zothParent ));
        JavaClass zothClass = builder.addSource(new StringReader( zothChild )).getClassByName( "ZothChild" );
        assertEquals( "java.lang.Object", zothClass.getSuperClass().getBinaryName());
        assertEquals( 2, zothClass.getBeanProperties( true ).size() );
    }

    // Github #31
    public void testParseEnumWithConstructor() throws Exception
    {
        String source = "public enum SomeEnum {\n" + 
            " VALUE1(\"hello\", 1, new String[]{\"hello\", \"world\"});\n" + 
            " SomeEnum(String string, int integer, String[] stringArray) {\n" + 
            " }\r\n" + 
            "}";
        builder.addSource( new StringReader( source ) ); 
    }
    
    // Github #39
    public void testInnerClassInterfaces()
        throws Exception
    {
        String sourceA = "public class A implements Itf1 {\n" + "     class B implements Itf2 {}\n" + "}";
        String sourceItf1 = "interface Itf1 {}";
        String sourceItf2 = "interface Itf2 {}";

        builder.addSource( new StringReader( sourceItf1 ) );
        builder.addSource( new StringReader( sourceItf2 ) );
        builder.addSource( new StringReader( sourceA ) );
        JavaClass classA = builder.getClassByName( "A$B" );

        assertTrue( classA.getImplements().equals( Arrays.asList( builder.getClassByName( "Itf2" ) ) ) );
    }
    
    public void testGenericEnumMethod() throws Exception {
        String source = "package java.time.temporal;\r\n" + 
            "public final class IsoFields {\r\n" + 
            "    private static enum Field implements TemporalField {\r\n" + 
            "        DAY_OF_QUARTER {\r\n" + 
            "            public <R extends Temporal> R adjustInto(R temporal, long newValue) {\r\n" + 
            "                return null;\r\n" + 
            "            }\r\n" + 
            "        }\r\n" + 
            "    }\r\n" + 
            "}";
        
        builder.addSource( new StringReader( source ) );
    }
    
    public void testDeclarationSignatureWithGenerics() {
        String source = "import java.util.List;"
            + "public interface Test {"
            + "  public List<com.amaral.model.EntityModel> findById(java.lang.Long id);"
            + "}";
        
        String model = "package com.amaral.model;"
            + "public class EntityModel {}";
        
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSource(new StringReader( source ));
        builder.addSource(new StringReader( model ));
        
        JavaMethod findById = builder.getClassByName( "Test" ).getMethods().get(0);
        assertEquals( "public java.util.List<com.amaral.model.EntityModel> findById(java.lang.Long id)", findById.getDeclarationSignature( true ) );
    }
    
    public void testOneLineJavadoc()
    {
        String source = "package foo.bar;\n" + 
            "/***/\n" + 
            "public class MyClass{\n" + 
            "} ";
        builder.addSource( new StringReader( source ) ); 
    }
    
    public void testMethodReferences()
    {
        String source = "public enum AmazonClients {\n" + 
            "    ECS(AmazonECSClient::new),\n" + 
            "    ECR(AmazonECRClient::new);\n"
            + "}";

        builder.addSource( new StringReader( source ) ); 
    }
    
    public void testAnonymousEnumConstructor()
    {
        String source = "package parent;\n" + 
            "public enum MyEnum {\n" + 
            "    VALUE(new Runnable() {\n" + 
            "        @Override\n" + 
            "        public void run() { }\n" + 
            "    });\n" + 
            "    MyEnum(Runnable function) { }\n" + 
            "}";
        
        builder.addSource( new StringReader( source ) ); 
    }

    public void testModuleWithComment()
    {
        String source = "/**\r\n" + 
            " * TODO javadoc\r\n" + 
            " */\r\n" + 
            "module maven.moduleinfo.comments {\r\n" + 
            "    // this comment fails the build\r\n" + 
            "    exports maven.reproducer.moduleinfo;\r\n" + 
            "}\r\n";
        
        builder.addSource( new StringReader( source ) );
    }
    
    public void testSimpleModule()
    {
        String source = "module bar {\r\n" + 
            "  requires foo.foo;\r\n" + 
            "}";

        builder.addSource( new StringReader( source ) );

    }
    
    public void testLineNumbers() {
        String source = "package foo.bar;\n" + 
                        "/** some javadoc */\n" + 
                        "public \n" + 
                        "class MyClass {\n" +
                        "public static final String CONSTANT1 = \"default value\";\n" +
                        "public static final String CONSTANT2 = \"default value\";}\n" +
                        "interface \n" + 
                        "MyInterface {\n" + 
                        "public void method1( String aString );\n" + 
                        "@Override\n" +
                        "void method2( String aString );}\n" + 
                        "public \n" + 
                        "enum MyEnum{}\n" +
                        "@interface \n" + 
                        "MyAnnoInterface{}\n" +
                        "interface NewLineInterface \n" +
                        "{}";
        JavaSource jSource = builder.addSource( new StringReader( source ) );
        assertEquals( 3, jSource.getClassByName( "MyClass" ).getLineNumber() );
        assertEquals( 5, jSource.getClassByName( "MyClass" ).getFieldByName( "CONSTANT1" ).getLineNumber() );
        assertEquals( 6, jSource.getClassByName( "MyClass" ).getFieldByName( "CONSTANT2" ).getLineNumber() );
        assertEquals( 7, jSource.getClassByName( "MyInterface" ).getLineNumber() );
        assertEquals( 9, jSource.getClassByName( "MyInterface" ).getMethods().get( 0 ).getLineNumber() );
        assertEquals( 10, jSource.getClassByName( "MyInterface" ).getMethods().get( 1 ).getAnnotations().get( 0 ).getLineNumber() );
        assertEquals( 11, jSource.getClassByName( "MyInterface" ).getMethods().get( 1 ).getLineNumber() );
        assertEquals( 12, jSource.getClassByName( "MyEnum" ).getLineNumber() );
        assertEquals( 14, jSource.getClassByName( "MyAnnoInterface" ).getLineNumber() );
        assertEquals( 16, jSource.getClassByName( "NewLineInterface" ).getLineNumber() );
    }

    public void testSetDebugLexer()
    {
        ClassLibraryBuilder classLibraryBuilder = mock( ClassLibraryBuilder.class );
        boolean debugLexer = true;

        builder = new JavaProjectBuilder( classLibraryBuilder );
        JavaProjectBuilder projectBuilder = builder.setDebugLexer( debugLexer );

        verify( classLibraryBuilder ).setDebugLexer( debugLexer );
        assertSame( builder, projectBuilder );
    }

    public void testSetDebugParser()
    {
        ClassLibraryBuilder classLibraryBuilder = mock( ClassLibraryBuilder.class );
        boolean debugParser = true;

        builder = new JavaProjectBuilder( classLibraryBuilder );
        JavaProjectBuilder projectBuilder = builder.setDebugParser( debugParser );
        
        verify( classLibraryBuilder ).setDebugParser( debugParser );
        assertSame( builder, projectBuilder );
    }

    public void testSetEncoding()
    {
        ClassLibraryBuilder classLibraryBuilder = mock( ClassLibraryBuilder.class );
        String encoding = "UTF-8";

        builder = new JavaProjectBuilder( classLibraryBuilder );
        JavaProjectBuilder projectBuilder = builder.setEncoding( encoding );

        verify( classLibraryBuilder ).setEncoding( same( encoding ) );
        assertSame( builder, projectBuilder );
    }
    
    public void testSetErrorHandler()
    {
        ClassLibraryBuilder classLibraryBuilder = mock( ClassLibraryBuilder.class );
        ErrorHandler errorHandler = mock( ErrorHandler.class );

        builder = new JavaProjectBuilder( classLibraryBuilder );
        JavaProjectBuilder projectBuilder = builder.setErrorHandler( errorHandler );
        
        verify( classLibraryBuilder ).setErrorHander( same( errorHandler ) );
        assertSame( builder, projectBuilder );
    }
    
    public void testAddClassLoader()
    {
        ClassLibraryBuilder classLibraryBuilder = mock( ClassLibraryBuilder.class );
        ClassLoader classLoader = mock( ClassLoader.class );

        builder = new JavaProjectBuilder( classLibraryBuilder );
        builder.addClassLoader( classLoader );
        
        verify( classLibraryBuilder ).appendClassLoader( same( classLoader ) );
    }
    
    public void testAddFileSource() throws Exception
    {
        ClassLibraryBuilder classLibraryBuilder = mock( ClassLibraryBuilder.class );
        File file = mock( File.class );
        JavaSource source  = mock( JavaSource.class );
        
        when( classLibraryBuilder.addSource( file ) ).thenReturn( source );

        builder = new JavaProjectBuilder( classLibraryBuilder );
        JavaSource addedSource = builder.addSource( file );
        
        verify( classLibraryBuilder ).addSource( same( file ) );
        assertSame( addedSource, source );
    }
    
    public void testAddReaderSource()
    {
        ClassLibraryBuilder classLibraryBuilder = mock( ClassLibraryBuilder.class );
        Reader reader = mock( Reader.class );
        JavaSource source  = mock( JavaSource.class );
        
        when( classLibraryBuilder.addSource( reader ) ).thenReturn( source );

        builder = new JavaProjectBuilder( classLibraryBuilder );
        JavaSource addedSource = builder.addSource( reader );
        
        verify( classLibraryBuilder ).addSource( same( reader ) );
        assertSame( addedSource, source );
    }
    
    public void testAddURLSource() throws Exception
    {
        ClassLibraryBuilder classLibraryBuilder = mock( ClassLibraryBuilder.class );
        URL url = new URL( "http://localhost" );
        JavaSource source  = mock( JavaSource.class );
        
        when( classLibraryBuilder.addSource( url ) ).thenReturn( source );

        builder = new JavaProjectBuilder( classLibraryBuilder );
        JavaSource addedSource = builder.addSource( url );
        
        verify( classLibraryBuilder ).addSource( same( url ) );
        assertSame( addedSource, source );
    }
    
    public void testAddSourceFolder() 
    {
        ClassLibraryBuilder classLibraryBuilder = mock( ClassLibraryBuilder.class );
        File file = mock( File.class );
        
        builder = new JavaProjectBuilder( classLibraryBuilder );
        builder.addSourceFolder( file );
        
        verify( classLibraryBuilder ).addSourceFolder( same( file ) );
    }
}