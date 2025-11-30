package com.thoughtworks.qdox;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

/**
 * Test for parsing type annotations on wildcard types.
 * See GitHub issue #292 for context about the parsing failure.
 */
public class Issue292Test {

    private JavaProjectBuilder builder;

    public Issue292Test() {
        builder = new JavaProjectBuilder();
    }

    @Test
    public void testTypeAnnotationOnWildcardInImplements() {
        String source = "package org.openapitools.jackson.nullable;\n"
                + "\n"
                + "import java.util.Optional;\n"
                + "import javax.validation.valueextraction.ExtractedValue;\n"
                + "import javax.validation.valueextraction.UnwrapByDefault;\n"
                + "import javax.validation.valueextraction.ValueExtractor;\n"
                + "\n"
                + "@UnwrapByDefault\n"
                + "public class MyOptionalExtractor implements ValueExtractor<Optional<@ExtractedValue ?>> {\n"
                + "\n"
                + "  @Override\n"
                + "  public void extractValues(Optional<?> originalValue, ValueReceiver receiver) {\n"
                + "    receiver.value(null, originalValue.get());\n"
                + "  }\n"
                + "}";

        builder.addSource(new StringReader(source));
    }
}
