package com.thoughtworks.qdox.builder;

import static org.junit.Assert.*;
import org.junit.Test;


public class EvaluatingVisitorTest
{

    @Test
    public void testUnaryNumericResultType() throws Exception 
    {
        assertEquals( Integer.class, EvaluatingVisitor.unaryNumericResultType( 0 ) );
        assertEquals( Integer.class, EvaluatingVisitor.unaryNumericResultType( ( byte ) 0  ) );
        assertEquals( Integer.class, EvaluatingVisitor.unaryNumericResultType( ( short ) 0  ) );
        assertEquals( Integer.class, EvaluatingVisitor.unaryNumericResultType( ( char ) 0 ) );
        
        assertEquals( Long.class, EvaluatingVisitor.unaryNumericResultType( 0L ) );
        
        assertEquals( void.class, EvaluatingVisitor.unaryNumericResultType( new Object() ) );
        assertEquals( void.class, EvaluatingVisitor.unaryNumericResultType( ( double ) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.unaryNumericResultType( ( float ) 0 ) );
        assertEquals( void.class, EvaluatingVisitor.unaryNumericResultType( null ) );
    }
    
}
