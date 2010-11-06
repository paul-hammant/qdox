package com.thoughtworks.qdox.junit;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.AbstractBaseJavaEntity;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMember;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * APITestCase is a JUnit extension that will let you compare two sources
 * (typically one kept as a static expected result and a generated one) on the API level.
 * <p/>
 * This class has been ported from XJavaDoc's CodeTestCase, carrying over only the
 * parts that compare on the API level. The original CodeTestCase also has comparison
 * of Java source AST (Abstract Syntax Trees). This will probably be extracted into
 * a ASTTestCase class and hosted as a separate project somewhere else. It should
 * probably be based on JavaCC for ease of porting.
 *
 * @author Aslak Helles&oslash;y
 * @author Laurent Etiemble
 */
public abstract class APITestCase extends TestCase {
    /*
     * Needed to sort JavaClass, JavaField and JavaMethod as they
     * don't implement Comparable
     */
    private static Comparator ENTITY_COMPARATOR = new Comparator() {
        public int compare(Object o1, Object o2) {
            AbstractBaseJavaEntity entity1 = (AbstractBaseJavaEntity) o1;
            AbstractBaseJavaEntity entity2 = (AbstractBaseJavaEntity) o2;
            return entity1.getName().compareTo(entity2.getName());
        }
    };

    public APITestCase() {
        super();
    }

    /**
     * Compares API of both sources in the readers.
     * <p/>
     * <p><b>Note:</b> This method is for backward naming compatiblity
     * with xjavadoc.codeunit.CodeTestCase.</p>
     *
     * @param expected the expected source
     * @param actual   the actual source
     */
    public static void assertApiEquals(URL expected, URL actual) throws IOException {
        JavaDocBuilder builder = new JavaDocBuilder();

        builder.addSource(new InputStreamReader(expected.openStream()), expected.toExternalForm());
        builder.addSource(new InputStreamReader(actual.openStream()), actual.toExternalForm());
        JavaSource expectedSource = builder.getSources()[0];
        JavaSource actualsource = builder.getSources()[1];

        assertApiEquals(expectedSource, actualsource);
    }

    /**
     * Compares API of both JavaSource objects.
     *
     * @param expected the expected source
     * @param actual   the actual source
     */
    private static void assertApiEquals(JavaSource expected,
                                        JavaSource actual) {
        List expectedClasses = Arrays.asList(expected.getClasses());
        Collections.sort(expectedClasses, ENTITY_COMPARATOR);
        List actualClasses = Arrays.asList(actual.getClasses());
        Collections.sort(actualClasses, ENTITY_COMPARATOR);

        assertEquals("Number of classes should be equal",
                expectedClasses.size(),
                actualClasses.size());

        for (int i = 0; i < expectedClasses.size(); i++) {
            assertClassesEqual((JavaClass) expectedClasses.get(i),
                    (JavaClass) actualClasses.get(i));
        }
    }

    /**
     * Compares an actual field to an expected one.
     * <p/>
     * <p>As JavaClass doesn't not implements <code>equals</code> and
     * <code>hashCode</code> methods, the comparison is done by hand.</p>
     *
     * @param expected
     * @param actual
     */
    private static void assertClassesEqual(JavaClass expected,
                                           JavaClass actual) {
        assertEquals("Package names should be equal",
                expected.getPackage(),
                actual.getPackage());
        assertModifiersEquals("Class modifiers should be equal",
                expected,
                actual);
        assertEquals("Class names should be equal",
                expected.getName(),
                actual.getName());

        if ((expected.getSuperJavaClass() != null)
                && (actual.getSuperJavaClass() != null)) {
            assertEquals("Super class should be equal",
                    expected.getSuperJavaClass().getName(),
                    actual.getSuperJavaClass().getName());
        }
        if ((expected.getSuperJavaClass() == null)
                ^ (actual.getSuperJavaClass() == null)) {
            fail("Super class should be equal");
        }

        assertInterfacesEqual(expected, actual);
        assertInnerClassesEquals(expected, actual);
        assertFieldsEqual(expected, actual);
        assertMethodsEqual(expected, actual);
    }

    /**
     * Compares an actual field to an expected one.
     * <p/>
     * <p>As JavaField doesn't not implements <code>equals</code> and
     * <code>hashCode</code> methods, the comparison is done by hand.</p>
     *
     * @param expected
     * @param actual
     */
    private static void assertFieldEquals(JavaField expected,
                                          JavaField actual) {
        StringBuffer message = new StringBuffer("-> assertFieldEquals");
        message.append("\n\tExcepted : ");
        message.append(expected);
        message.append("\n\tActual : ");
        message.append(actual);
        message.append("\n");

        assertEquals(message.toString() + "Field types should be equal",
                expected.getType(),
                actual.getType());
        assertEquals(message.toString() + "Field names should be equal",
                expected.getName(),
                actual.getName());
        assertModifiersEquals(message.toString() + "Field modifiers should be equal",
                expected,
                actual);
    }

    /**
     * Compares fields from an actual class to an expected one.
     * <p/>
     * <p>The fields are sorted by name before comparison to be sure
     * that even if the fields are defined in a different order, the
     * comparison is still right.</p>
     */
    private static void assertFieldsEqual(JavaClass expected,
                                          JavaClass actual) {
        List expectedFields = Arrays.asList(expected.getFields());
        Collections.sort(expectedFields, ENTITY_COMPARATOR);
        List actualFields = Arrays.asList(actual.getFields());
        Collections.sort(actualFields, ENTITY_COMPARATOR);

        StringBuffer message = new StringBuffer("-> assertFieldsEqual");
        message.append("\n\tExcepted : ");
        message.append(expectedFields);
        message.append("\n\tActual : ");
        message.append(actualFields);
        message.append("\n");

        assertEquals(message.toString() + "Number of fields should be equal",
                expectedFields.size(),
                actualFields.size());

        for (int i = 0; i < expectedFields.size(); i++) {
            assertFieldEquals((JavaField) expectedFields.get(i),
                    (JavaField) actualFields.get(i));
        }

    }

    /**
     * Compares inner classes from an actual class to an expected one.
     * <p/>
     * <p>The inner classes are sorted by name before comparison to be sure
     * that even if the inner classes are defined in a different order, the
     * comparison is still right.</p>
     */
    private static void assertInnerClassesEquals(JavaClass expected,
                                                 JavaClass actual) {
        List expectedInnerClasses = Arrays.asList(expected.getNestedClasses());
        Collections.sort(expectedInnerClasses, ENTITY_COMPARATOR);
        List actualInnerClasses = Arrays.asList(actual.getNestedClasses());
        Collections.sort(actualInnerClasses, ENTITY_COMPARATOR);

        StringBuffer message = new StringBuffer("-> assertInnerClassesEquals");
        message.append("\n\tExcepted : ");
        message.append(expectedInnerClasses);
        message.append("\n\tActual : ");
        message.append(actualInnerClasses);
        message.append("\n");

        assertEquals(message.toString() + "Number of inner classes should be equal",
                expectedInnerClasses.size(),
                actualInnerClasses.size());

        for (int i = 0; i < expectedInnerClasses.size(); i++) {
            assertClassesEqual((JavaClass) expectedInnerClasses.get(i),
                    (JavaClass) actualInnerClasses.get(i));
        }
    }

    /**
     * Compares implemented interfaces from an actual class to an expected one.
     * <p/>
     * <p>The implemented interfaces are sorted by name before comparison to be sure
     * that even if the implemented interfaces are defined in a different order, the
     * comparison is still right.</p>
     */
    private static void assertInterfacesEqual(JavaClass expected,
                                              JavaClass actual) {
        List expectedImplements = Arrays.asList(expected.getImplements());
        Collections.sort(expectedImplements);
        List actualImplements = Arrays.asList(actual.getImplements());
        Collections.sort(actualImplements);

        StringBuffer message = new StringBuffer("-> assertInnerClassesEquals");
        message.append("\n\tExcepted : ");
        message.append(expectedImplements);
        message.append("\n\tActual : ");
        message.append(actualImplements);
        message.append("\n");

        assertEquals(message.toString() + "Number of implemented interface should be equal",
                expectedImplements.size(),
                actualImplements.size());

        for (int i = 0; i < expectedImplements.size(); i++) {
            assertEquals("Implemented interface should be equal",
                    expectedImplements.get(i),
                    actualImplements.get(i));
        }
    }

    /**
     * Compares constructors and methods from an actual class to an expected one.
     * <p/>
     * <p>The constructors and the methods are sorted by name before comparison to be sure
     * that even if the constructors and methods are defined in a different order, the
     * comparison is still right.</p>
     */
    private static void assertMethodsEqual(JavaClass expected,
                                           JavaClass actual) {
        List expectedMethods = Arrays.asList(expected.getMethods());
        Collections.sort(expectedMethods, ENTITY_COMPARATOR);
        List actualMethods = Arrays.asList(actual.getMethods());
        Collections.sort(actualMethods, ENTITY_COMPARATOR);

        StringBuffer message = new StringBuffer("-> assertMethodsEqual");
        message.append("\n\tExcepted : ");
        message.append(expectedMethods);
        message.append("\n\tActual : ");
        message.append(actualMethods);
        message.append("\n");

        assertEquals(message.toString() + "Number of methods should be equal",
                expectedMethods.size(),
                actualMethods.size());

        for (int i = 0; i < expectedMethods.size(); i++) {
            assertEquals("Method should be equal",
                    expectedMethods.get(i),
                    actualMethods.get(i));
        }
    }

    /**
     * Compares modifiers an actual entity.
     * <p/>
     * <p>The modifiers are sorted by name before comparison to be sure
     * that even if the modifiers are defined in a different order, the
     * comparison is still right.</p>
     */
    private static void assertModifiersEquals(String msg,
                                              JavaMember expected,
                                              JavaMember actual) {

        List expectedModifiers = Arrays.asList(expected.getModifiers());
        Collections.sort(expectedModifiers);
        List actualModifiers = Arrays.asList(actual.getModifiers());
        Collections.sort(actualModifiers);

        StringBuffer message = new StringBuffer("-> assertModifiersEquals");
        message.append("\n\tExcepted : ");
        message.append(expectedModifiers);
        message.append("\n\tActual : ");
        message.append(actualModifiers);
        message.append("\n");

        assertEquals(message.toString() + msg + "\nNumber of modifiers should be equal",
                expectedModifiers.size(),
                actualModifiers.size());

        for (int i = 0; i < expectedModifiers.size(); i++) {
            assertEquals(msg + "\n" + message.toString() + "\nModifier should be equal",
                    expectedModifiers.get(i),
                    actualModifiers.get(i));
        }
    }

    private static void assertNotDir(File expected, File actual) {
        if (expected.isDirectory())
            fail(expected.getAbsolutePath() + " - should not have been a directory");
        if (actual.isDirectory())
            fail(actual.getAbsolutePath() + " - should not have been a directory");
    }

    protected File getDir() {
        return (
                new File(getClass()
                .getResource("/" + getClass().getName().replace('.', '/') + ".class")
                .getFile()))
                .getParentFile();
    }

    protected File getRootDir() {
        File dir = getDir();
        StringTokenizer st = new StringTokenizer(getClass().getName(), ".");
        for (int i = 0; i < st.countTokens() - 1; i++) {
            dir = dir.getParentFile();
        }
        return dir;
    }
}
