package com.thoughtworks.qdox;

import junit.framework.TestCase;

import java.io.StringReader;

/**
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 */
public class GenericsTest extends TestCase {

    private JavaDocBuilder builder = new JavaDocBuilder();

    public void testShouldUnderstandSingleGenericClassDeclarations() {
        String source = "" +
                "public interface Foo<T> extends Bar<T> {}";

        builder.addSource(new StringReader(source));
        assertEquals("Foo", builder.getClassByName("Foo").getName());
    }

    public void testShouldUnderstandMultipleGenericClassDeclarations() {
        String source = "" +
                "public interface Foo<X,Y> extends Bar<X,Y> {}";

        builder.addSource(new StringReader(source));
        assertEquals("Foo", builder.getClassByName("Foo").getName());
    }

    public void testShouldUnderstandMultipleGenericsInMethodDeclarations() {
        String source = "" +
                "public interface Foo {" +
                "   Bar<X,Y> zap(Zip<R,V> r);" +
                "}";

        builder.addSource(new StringReader(source));
        assertEquals("Foo", builder.getClassByName("Foo").getName());
    }

    public void testShouldUnderstandMultipleGenericsInConstructorDeclarations() {
        String source = "" +
                "public class Bar {" +
                "   public Bar(Zip<R,V> r) {}" +
                "}";

        builder.addSource(new StringReader(source));
        assertEquals("Bar", builder.getClassByName("Bar").getName());
    }

    public void testShouldUnderstandMultipleGenericsInFieldDeclarations() {
        String source = "" +
                "public class Bar {" +
                "   private Foo<R,V> foo;" +
                "}";

        builder.addSource(new StringReader(source));
        assertEquals("Bar", builder.getClassByName("Bar").getName());
    }

    // http://madbean.com/blog/12/
    public void FIXME_OR_DELETEME_MIKE_testShouldUnderstandNestedGenerics() {
        String source = "" +
                "public class Bar {" +
                "   private List < List < String > > list1;" +
                "   private List<List<String>> list2;" +
                "}";

        builder.addSource(new StringReader(source));
        assertEquals("Bar", builder.getClassByName("Bar").getName());
    }


}
