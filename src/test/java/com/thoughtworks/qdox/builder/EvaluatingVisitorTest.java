package com.thoughtworks.qdox.builder;

import static org.junit.Assert.*;
import org.junit.Test;


public class EvaluatingVisitorTest
{

    @Test
    public void testUnaryNumericResultType() throws Exception 
    {
        assertEquals( Integer.class, EvaluatingVisitor.unaryNumericResultType( Integer.valueOf( 0 ) ) );
        assertEquals( Long.class, EvaluatingVisitor.unaryNumericResultType( Long.valueOf( 0l ) ) );
    }
}
