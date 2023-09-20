package com.thoughtworks.qdox.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParseExceptionTest
{

    @Test
    public void testNullParseException() 
    {
       ParseException pe = new ParseException( null, -1, -1 );
       try {
           pe.getMessage();
           Assertions.fail("Message should never be null");
       }
       catch( NullPointerException npe )
       {
       }
    }
    
    @Test
    public void testEmptyParseException() 
    {
       ParseException pe = new ParseException( "", -1, -1 );
       Assertions.assertEquals("", pe.getMessage());
    }

    @Test
    public void testNegativeColumnParseException() 
    {
       ParseException pe = new ParseException( "Failed to parse:", 5, -50 );
       Assertions.assertEquals("Failed to parse: @[5]", pe.getMessage());
    }

    @Test
    public void testPositiveColumnParseException() 
    {
       ParseException pe = new ParseException( "Failed to parse:", 5, 50 );
       Assertions.assertEquals("Failed to parse: @[5,50]", pe.getMessage());
    }

    @Test
    public void testSurceInfoParseException() 
    {
       ParseException pe = new ParseException( "Failed to parse:", 5, 50 );
       pe.setSourceInfo( "com/foo/Bar.java" );
       Assertions.assertEquals("Failed to parse: @[5,50] in com/foo/Bar.java", pe.getMessage());
    }

}
