package com.thoughtworks.qdox.model.expression;

import static org.junit.Assert.*;
import org.junit.Test;


public class ConstantTest
{
    int i = 00;
    
    
    @Test
    public void testBinaryInteger() {
        assertEquals( Integer.valueOf( "0", 2 ), Constant.newIntegerLiteral( "0b0" ).getValue() );
        assertEquals( Integer.valueOf( "0", 2 ), Constant.newIntegerLiteral( "0B0" ).getValue() );
        
        assertEquals( Integer.valueOf( "0", 2 ), Constant.newIntegerLiteral( "0b00" ).getValue() );
        assertEquals( Integer.valueOf( "0", 2 ), Constant.newIntegerLiteral( "0B00" ).getValue() );
        
        assertEquals( Integer.valueOf( "10", 2 ), Constant.newIntegerLiteral( "0b10" ).getValue() );
        assertEquals( Integer.valueOf( "10", 2 ), Constant.newIntegerLiteral( "0B10" ).getValue() );
    }
    
    @Test 
    public void OctalInteger() {
        assertEquals( Integer.valueOf( "0", 8 ), Constant.newIntegerLiteral( "00" ).getValue() );
        assertEquals( Integer.valueOf( "0", 8 ), Constant.newIntegerLiteral( "000" ).getValue() );
        assertEquals( Integer.valueOf( "10", 8 ), Constant.newIntegerLiteral( "010" ).getValue() );
    }
    

    @Test
    public void testDecimalInteger() {
        assertEquals( Integer.valueOf( "0" ), Constant.newIntegerLiteral( "0" ).getValue() );
        assertEquals( Integer.valueOf( "10" ), Constant.newIntegerLiteral( "10" ).getValue() );
    }

    @Test
    public void testHexInteger() { 
        assertEquals( Integer.valueOf( "0", 16 ), Constant.newIntegerLiteral( "0x0" ).getValue() );
        assertEquals( Integer.valueOf( "0", 16 ), Constant.newIntegerLiteral( "0X0" ).getValue() );
        
        assertEquals( Integer.valueOf( "0", 16 ), Constant.newIntegerLiteral( "0x00" ).getValue() );
        assertEquals( Integer.valueOf( "0", 16 ), Constant.newIntegerLiteral( "0X00" ).getValue() );
        
        assertEquals( Integer.valueOf( "10", 16 ), Constant.newIntegerLiteral( "0x10" ).getValue() );
        assertEquals( Integer.valueOf( "10", 16 ), Constant.newIntegerLiteral( "0X10" ).getValue() );
    }
    
    @Test
    public void testBinaryLong() {
        assertEquals( Long.valueOf( "0", 2 ), Constant.newIntegerLiteral( "0b0l" ).getValue() );
        assertEquals( Long.valueOf( "0", 2 ), Constant.newIntegerLiteral( "0b0L" ).getValue() );
        assertEquals( Long.valueOf( "0", 2 ), Constant.newIntegerLiteral( "0B0l" ).getValue() );
        assertEquals( Long.valueOf( "0", 2 ), Constant.newIntegerLiteral( "0B0L" ).getValue() );
        
        assertEquals( Long.valueOf( "0", 2 ), Constant.newIntegerLiteral( "0b00l" ).getValue() );
        assertEquals( Long.valueOf( "0", 2 ), Constant.newIntegerLiteral( "0b00L" ).getValue() );
        assertEquals( Long.valueOf( "0", 2 ), Constant.newIntegerLiteral( "0B00l" ).getValue() );
        assertEquals( Long.valueOf( "0", 2 ), Constant.newIntegerLiteral( "0B00L" ).getValue() );
        
        assertEquals( Long.valueOf( "10", 2 ), Constant.newIntegerLiteral( "0b10l" ).getValue() );
        assertEquals( Long.valueOf( "10", 2 ), Constant.newIntegerLiteral( "0b10L" ).getValue() );
        assertEquals( Long.valueOf( "10", 2 ), Constant.newIntegerLiteral( "0B10l" ).getValue() );
        assertEquals( Long.valueOf( "10", 2 ), Constant.newIntegerLiteral( "0B10L" ).getValue() );
    }

    @Test
    public void testOctalLong() {
        assertEquals( Long.valueOf( "0", 8 ), Constant.newIntegerLiteral( "00l" ).getValue() );
        assertEquals( Long.valueOf( "0", 8 ), Constant.newIntegerLiteral( "00L" ).getValue() );
        
        assertEquals( Long.valueOf( "0", 8 ), Constant.newIntegerLiteral( "000l" ).getValue() );
        assertEquals( Long.valueOf( "0", 8 ), Constant.newIntegerLiteral( "000L" ).getValue() );
        
        assertEquals( Long.valueOf( "10", 8 ), Constant.newIntegerLiteral( "010l" ).getValue() );
        assertEquals( Long.valueOf( "10", 8 ), Constant.newIntegerLiteral( "010L" ).getValue() );

    }

    @Test
    public void testDecimalLong() {
        assertEquals( Long.valueOf( "0" ), Constant.newIntegerLiteral( "0l" ).getValue() );
        assertEquals( Long.valueOf( "0" ), Constant.newIntegerLiteral( "0L" ).getValue() );

        assertEquals( Long.valueOf( "10" ), Constant.newIntegerLiteral( "10l" ).getValue() );
        assertEquals( Long.valueOf( "10" ), Constant.newIntegerLiteral( "10L" ).getValue() );
    }

    @Test
    public void testHexLong() {
        assertEquals( Long.valueOf( "0", 16 ), Constant.newIntegerLiteral( "0x0l" ).getValue() );
        assertEquals( Long.valueOf( "0", 16 ), Constant.newIntegerLiteral( "0X0l" ).getValue() );
        assertEquals( Long.valueOf( "0", 16 ), Constant.newIntegerLiteral( "0x0L" ).getValue() );
        assertEquals( Long.valueOf( "0", 16 ), Constant.newIntegerLiteral( "0X0L" ).getValue() );
        
        assertEquals( Long.valueOf( "0", 16 ), Constant.newIntegerLiteral( "0x00l" ).getValue() );
        assertEquals( Long.valueOf( "0", 16 ), Constant.newIntegerLiteral( "0X00l" ).getValue() );
        assertEquals( Long.valueOf( "0", 16 ), Constant.newIntegerLiteral( "0x00L" ).getValue() );
        assertEquals( Long.valueOf( "0", 16 ), Constant.newIntegerLiteral( "0X00L" ).getValue() );
        
        assertEquals( Long.valueOf( "10", 16 ), Constant.newIntegerLiteral( "0x10l" ).getValue() );
        assertEquals( Long.valueOf( "10", 16 ), Constant.newIntegerLiteral( "0X10l" ).getValue() );
        assertEquals( Long.valueOf( "10", 16 ), Constant.newIntegerLiteral( "0x10L" ).getValue() );
        assertEquals( Long.valueOf( "10", 16 ), Constant.newIntegerLiteral( "0X10L" ).getValue() );
    }
    
    
}
