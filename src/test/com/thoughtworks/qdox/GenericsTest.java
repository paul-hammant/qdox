package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.parser.ParseException;
import junit.framework.TestCase;

import java.io.StringReader;

/**
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Aslak Helles&oslash;y
 */
public class GenericsTest extends TestCase {

    private JavaDocBuilder builder = new JavaDocBuilder();

    public void testShouldUnderstandGenericClassDeclarations() {
        try {
            String source = "" +
                    "public interface Collection<T> extends Iterable<T> {}";

            builder.addSource(new StringReader(source));
            JavaClass collection = builder.getClassByName("Collection");
        } catch (ParseException e) {
            System.out.println("col:" + e.getColumn());
            throw e;
        }
    }
}
