package com.thoughtworks.qdox;

import java.io.*;
import java.util.List;
import java.util.Arrays;

import junit.framework.TestCase;

import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.parser.*;

/**
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 */
public class JavaDocBuilderTest extends TestCase {

    private JavaDocBuilder builder;

    public JavaDocBuilderTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        builder = new JavaDocBuilder();
        createFile("tmp/sourcetest/com/blah/Thing.java", "com.blah", "Thing");
        createFile("tmp/sourcetest/com/blah/Another.java", "com.blah", "Another");
        createFile("tmp/sourcetest/com/blah/subpackage/Cheese.java", "com.blah.subpackage", "Cheese");
        createFile("tmp/sourcetest/com/blah/Ignore.notjava", "com.blah", "Ignore");
    }

    public void testParsingMultipleJavaFiles() {
        builder.addSource(new StringReader(createTestClassList()));
        builder.addSource(new StringReader(createTestClass()));
        JavaSource[] sources = builder.getSources();
        assertEquals(2, sources.length);

        JavaClass testClassList = sources[0].getClasses()[0];
        assertEquals("TestClassList", testClassList.getName());
        assertEquals("com.thoughtworks.util.TestClass", testClassList.getSuperClass().getValue());

        JavaClass testClass = sources[1].getClasses()[0];
        assertEquals("TestClass", testClass.getName());


        JavaClass testClassListByName = builder.getClassByName("com.thoughtworks.qdox.TestClassList");
        assertEquals("TestClassList", testClassListByName.getName());

        JavaClass testClassByName = builder.getClassByName("com.thoughtworks.util.TestClass");
        assertEquals("TestClass", testClassByName.getName());

        assertNull(builder.getClassByName("this.class.should.not.Exist"));
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
        builder.addSource(new StringReader(createOuter()));
        JavaSource[] sources = builder.getSources();
        assertEquals(1, sources.length);

        JavaClass outer = sources[0].getClasses()[0];
        assertEquals("Outer", outer.getName());
        assertEquals("foo.bar.Outer", outer.getFullyQualifiedName());

        assertEquals(1, outer.getFields().length);
        assertEquals("int", outer.getFields()[0].getType().getValue());

        assertEquals(1, outer.getMethods().length);
        assertEquals("outerMethod", outer.getMethods()[0].getName());

        assertEquals(1, outer.getInnerClasses().length);
        JavaClass inner = outer.getInnerClasses()[0];
        assertEquals("Inner", inner.getName());
        assertEquals("foo.bar.Outer$Inner", inner.getFullyQualifiedName());

        assertEquals(1, inner.getMethods().length);
        assertEquals("innerMethod", inner.getMethods()[0].getName());
    }

    public void testGetClasses() {
        builder.addSource(new StringReader(createOuter()));
        JavaClass[] classes = builder.getClasses();
        assertEquals(2, classes.length);
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

    public void testSourceTree() throws Exception {
        builder.addSourceTree(new File("tmp/sourcetest"));

        assertNotNull(builder.getClassByName("com.blah.Thing"));
        assertNotNull(builder.getClassByName("com.blah.Another"));
        assertNotNull(builder.getClassByName("com.blah.subpackage.Cheese"));
        assertNull(builder.getClassByName("com.blah.Ignore"));
    }

    public void testRecordFile() throws Exception {
        builder.addSource(new File("tmp/sourcetest/com/blah/Thing.java"));

        JavaSource[] sources = builder.getSources();
        assertEquals(1, sources.length);
        assertEquals(new File("tmp/sourcetest/com/blah/Thing.java"),
                sources[0].getFile());
    }

    public void testSearcher() throws Exception {
        builder.addSourceTree(new File("tmp/sourcetest"));

        List results = builder.search(new Searcher() {
            public boolean eval(JavaClass cls) {
                return cls.getPackage().equals("com.blah");
            }
        });

        assertEquals(2, results.size());
        assertEquals("Another", ((JavaClass) results.get(0)).getName());
        assertEquals("Thing", ((JavaClass) results.get(1)).getName());
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

    public void testDefaultClassLoader() throws Exception {
        String in = ""
                + "package x;"
                + "import java.util.*;"
                + "import java.awt.*;"
                + "class X extends List {}";
        builder.addSource(new StringReader(in));
        JavaClass cls = builder.getClassByName("x.X");
        assertEquals("java.util.List", cls.getSuperClass().getValue());
    }

    public void testAddMoreClassLoaders() throws Exception {

        builder.getClassLibrary().addClassLoader(new ClassLoader() {
            public Class loadClass(String name) {
                return name.equals("com.thoughtworks.Spoon") ? this.getClass() : null;
            }
        });

        builder.getClassLibrary().addClassLoader(new ClassLoader() {
            public Class loadClass(String name) {
                return name.equals("com.thoughtworks.Fork") ? this.getClass() : null;
            }
        });

        String in = ""
                + "package x;"
                + "import java.util.*;"
                + "import com.thoughtworks.*;"
                + "class X {"
                + " Spoon a();"
                + " Fork b();"
                + " Cabbage c();"
                + "}";
        builder.addSource(new StringReader(in));

        JavaClass cls = builder.getClassByName("x.X");
        assertEquals("com.thoughtworks.Spoon", cls.getMethods()[0].getReturns().getValue());
        assertEquals("com.thoughtworks.Fork", cls.getMethods()[1].getReturns().getValue());
        // unresolved
        assertEquals("Cabbage", cls.getMethods()[2].getReturns().getValue());

    }

    public void testOldfashionedExtraClassesAreSupported() throws Exception {
        String in = ""
                + "package oldfashioned;"
                + "public class Ping {"
                + "}"
                + "class Bar {"
                + "}";
        builder.addSource(new StringReader(in));
        assertEquals(2, builder.getClasses().length);
        assertNotNull(builder.getClassByName("oldfashioned.Ping"));
        assertNotNull(builder.getClassByName("oldfashioned.Bar"));
    }

    public void testBinaryClassesAreFound() throws Exception {

        String in = ""
                + "package x;"
                + "import java.util.*;"
                + "class X {"
                + " ArrayList a();"
                + "}";
        builder.addSource(new StringReader(in));

        JavaClass cls = builder.getClassByName("x.X");
        Type returnType = cls.getMethods()[0].getReturns();
        JavaClass returnClass = builder.getClassByName(returnType.getValue());

        assertEquals("java.util.ArrayList", returnClass.getFullyQualifiedName());

        Type[] returnImplementz = returnClass.getImplements();
        boolean foundList = false;
        for (int i = 0; i < returnImplementz.length; i++) {
            Type type = returnImplementz[i];
            if (type.getValue().equals("java.util.List")) {
                foundList = true;
            }
        }
        assertTrue(foundList);

        // See if interfaces work too.
        JavaClass list = builder.getClassByName("java.util.List");
        assertTrue(list.isInterface());
        assertNull(list.getSuperJavaClass());
        assertEquals("java.util.Collection", list.getImplements()[0].getValue());
    }

    public void testSuperclassOfObjectIsNull() throws Exception {
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

    public void testClassCanBeTestedForNonexistantClasses() throws Exception {
        String in = ""
                + "package food;"
                + "class Sausage extends food.Meat implements food.Proteine {"
                + "}";
        builder.addSource(new StringReader(in));

        JavaClass sausage = builder.getClassByName("food.Sausage");
        assertTrue(sausage.isA("food.Meat"));
        assertTrue(sausage.isA("food.Proteine"));
    }

    public void testClassesCanBeAddedLater() throws Exception {
        testClassCanBeTestedForNonexistantClasses();
        assertEquals(1, builder.getClasses().length);
        JavaClass sausage = builder.getClassByName("food.Sausage");

        assertFalse(sausage.isA("global.Stuff"));
        String in = ""
                + "package food;"
                + "class Meat extends global.Stuff {"
                + "}";
        builder.addSource(new StringReader(in));
        assertEquals(2, builder.getClasses().length);
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

        List derivedClassesOfCollection = Arrays.asList(collection.getDerivedClasses());
        List derivedClassesOfList = Arrays.asList(list.getDerivedClasses());
        List derivedClassesOfAbstractList = Arrays.asList(abstractList.getDerivedClasses());
        List derivedClassesOfArrayList = Arrays.asList(arrayList.getDerivedClasses());

        assertEquals(3, derivedClassesOfCollection.size());
        assertEquals(2, derivedClassesOfList.size());
        assertEquals(1, derivedClassesOfAbstractList.size());
        assertEquals(0, derivedClassesOfArrayList.size());
    }

    public void testSourcePropertyClass() throws FileNotFoundException, UnsupportedEncodingException {
        builder.addSource(new File("src/test/com/thoughtworks/qdox/testdata/PropertyClass.java"));
        // Handy way to assert that behaviour for source and binary classes is the same.
        testPropertyClass();
    }

    public void testPropertyClass() {
        JavaClass propertyClass = builder.getClassByName("com.thoughtworks.qdox.testdata.PropertyClass");
        assertEquals(1, propertyClass.getBeanProperties().length);

        // test ctor, methods and fields
        JavaMethod[] methods = propertyClass.getMethods();
        assertEquals(5, methods.length);

        JavaMethod ctor = propertyClass.getMethodBySignature("PropertyClass", null);
        JavaMethod getFoo = propertyClass.getMethodBySignature("getFoo", null);
        JavaMethod isBar = propertyClass.getMethodBySignature("isBar", null);
        JavaMethod get = propertyClass.getMethodBySignature("get", null);
        JavaMethod set = propertyClass.getMethodBySignature("set", new Type[]{new Type("int")});

        assertTrue(ctor.isConstructor());
        assertFalse(getFoo.isConstructor());
        assertFalse(isBar.isConstructor());
        assertFalse(get.isConstructor());
        assertFalse(set.isConstructor());

        assertTrue(getFoo.isStatic());
        assertFalse(isBar.isStatic());
        assertFalse(get.isStatic());
        assertFalse(set.isStatic());

        assertTrue(get.isFinal());
        assertFalse(set.isStatic());

        JavaField[] fields = propertyClass.getFields();
        assertEquals(1, fields.length);
    }

    public void testSerializable() throws Exception {
        builder.addSource(new StringReader("package test; public class X{}"));
        assertEquals("X", builder.getSources()[0].getClasses()[0].getName());

        // serialize
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(buffer);
        oos.writeObject(builder);
        oos.close();
        builder = null;

        // unserialize
        ByteArrayInputStream input = new ByteArrayInputStream(buffer.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(input);
        JavaDocBuilder newBuilder = (JavaDocBuilder) ois.readObject();

        assertEquals("X", newBuilder.getSources()[0].getClasses()[0].getName());

    }

    public void testSaveAndRestore() throws Exception {
        File file = new File("tmp/sourcetest/cache.obj");
        builder.addSourceTree(new File("tmp/sourcetest"));
        builder.save(file);

        JavaDocBuilder newBuilder = JavaDocBuilder.load(file);
        assertNotNull(newBuilder.getClassByName("com.blah.subpackage.Cheese"));
        assertNull(newBuilder.getClassByName("com.blah.Ignore"));

        newBuilder.addSource(new StringReader("package x; import java.util.*; class Z extends List{}"));
        assertEquals("java.util.List", newBuilder.getClassByName("x.Z").getSuperClass().getValue());

    }

    public void testSuperClassOfAnInterfaceReturnsNull() throws Exception {
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
        JavaClass inner = outer.getInnerClasses()[0];
        assertEquals("foo.Outer$Inner", inner.getFullyQualifiedName());

        JavaField field1 = outer.getFieldByName("field1");
        Type type = field1.getType();
        assertEquals("foo.Outer$Inner", type.getJavaClass().getFullyQualifiedName());
    }

    public void testJiraQdox16() {
        String source = "" +
                "/**Hip hop won*t stop*/" +
                "class x{}";
        builder.addSource(new StringReader(source));
        JavaClass x = builder.getClassByName("x");
        assertEquals("Hip hop won*t stop", x.getComment());
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
        JavaMethod m = x.getMethods()[0];
        DocletTag foo = m.getTagByName("y");
        assertEquals("z", foo.getValue());
    }

    public void testTagInheritance() {
        String X = "" +
                "/** @c x */" +
                "class X {" +
                "  /** " +
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
        DocletTag[] c = y.getTagsByName("c", true);
        assertEquals(2, c.length);
        assertEquals("y", c[0].getValue());
        assertEquals("x", c[1].getValue());

        JavaMethod i = y.getMethodBySignature("i", null);
        DocletTag[] m = i.getTagsByName("m", true);
        assertEquals(2, m.length);
        assertEquals("y", m[0].getValue());
        assertEquals("x", m[1].getValue());

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
        JavaDocBuilder builder = new JavaDocBuilder();
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
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSource(new StringReader(sourceCode));
        JavaClass a = builder.getClassByName("A");
        assertEquals(3, a.getFields().length);
        assertEquals("i", a.getFields()[0].getName());
        assertEquals("int", a.getFields()[0].getType().toString());
        assertEquals("j", a.getFields()[1].getName());
        assertEquals("int", a.getFields()[1].getType().toString());
        assertEquals("k", a.getFields()[2].getName());
        assertEquals("int[]", a.getFields()[2].getType().toString());
    }

    public void testJiraQdox40() {
        String sourceCode = ""
                + "package foo.bar;"
                + "public class Outer {"
                + "  class WrappedInternalContextAdapter implements InternalContextAdapter {"
                + "  }"
                + "}";
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSource(new StringReader(sourceCode));
        JavaClass clazz = builder.getClassByName("foo.bar.Outer");

        assertEquals(1, clazz.getInnerClasses().length);
    }
    
    public void testParseErrorLocationIsAvailable() {
        String badSource = ""
        + "package x;\n"
        + "import java.util.*;\n"
        + "class Bad [\n";
        try {
            builder.addSource(new StringReader(badSource));
            fail("ParseException expected");
        } catch(ParseException e) {
            System.out.println(e.getMessage());
            assertEquals(3, e.getLine());
            assertEquals(11, e.getColumn());
        }
    }
    
}
