package com.thoughtworks.qdox.model.expression;

import static org.junit.Assert.*;
import org.junit.Test;


public class ConstantTest
{
    
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
    public void testOctalInteger() {
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
    
    @Test
    public void testBoolean() {
        assertEquals( Boolean.TRUE, Constant.newBooleanLiteral( "true" ).getValue() );
        assertEquals( Boolean.FALSE, Constant.newBooleanLiteral( "false" ).getValue() );
    }
    

    @Test
    public void testDecimalFloatingPoint() {
        assertEquals( Float.valueOf( "0.0" ), Constant.newFloatingPointLiteral( "0.0" ).getValue() );
        assertEquals( Float.valueOf( "0.0" ), Constant.newFloatingPointLiteral( "0.0f" ).getValue() );
        assertEquals( Float.valueOf( "0.0" ), Constant.newFloatingPointLiteral( "0.0F" ).getValue() );
        
        assertEquals( Float.valueOf( "0.0" ), Constant.newFloatingPointLiteral( "000.0" ).getValue() );
        assertEquals( Float.valueOf( "0.0" ), Constant.newFloatingPointLiteral( "000.0f" ).getValue() );
        assertEquals( Float.valueOf( "0.0" ), Constant.newFloatingPointLiteral( "000.0F" ).getValue() );
        
        assertEquals( Float.valueOf( "0.0" ), Constant.newFloatingPointLiteral( "0.000" ).getValue() );
        assertEquals( Float.valueOf( "0.0" ), Constant.newFloatingPointLiteral( "0.000f" ).getValue() );
        assertEquals( Float.valueOf( "0.0" ), Constant.newFloatingPointLiteral( "0.000F" ).getValue() );
        
        assertEquals( Float.valueOf( "0.0" ), Constant.newFloatingPointLiteral( ".0" ).getValue() );
        assertEquals( Float.valueOf( "0.0" ), Constant.newFloatingPointLiteral( ".0f" ).getValue() );
        assertEquals( Float.valueOf( "0.0" ), Constant.newFloatingPointLiteral( ".0F" ).getValue() );

        assertEquals( Float.valueOf( "0.0" ), Constant.newFloatingPointLiteral( ".00" ).getValue() );
        assertEquals( Float.valueOf( "0.0" ), Constant.newFloatingPointLiteral( ".00f" ).getValue() );
        assertEquals( Float.valueOf( "0.0" ), Constant.newFloatingPointLiteral( ".00F" ).getValue() );
        
        assertEquals( Float.valueOf( "0" ), Constant.newFloatingPointLiteral( "0f" ).getValue() );
        assertEquals( Float.valueOf( "0" ), Constant.newFloatingPointLiteral( "0F" ).getValue() );

    }
    
    @Test
    public void testDecimalFloatingPointWithExponent() {
        assertEquals( Float.valueOf( "2.0e1" ), Constant.newFloatingPointLiteral( "2.0e1" ).getValue() );
        assertEquals( Float.valueOf( "2.0e1" ), Constant.newFloatingPointLiteral( "2.0E1" ).getValue() );
        assertEquals( Float.valueOf( "2.0e1" ), Constant.newFloatingPointLiteral( "2.0e1f" ).getValue() );
        assertEquals( Float.valueOf( "2.0e1" ), Constant.newFloatingPointLiteral( "2.0E1f" ).getValue() );
        assertEquals( Float.valueOf( "2.0e1" ), Constant.newFloatingPointLiteral( "2.0e1F" ).getValue() );
        assertEquals( Float.valueOf( "2.0e1" ), Constant.newFloatingPointLiteral( "2.0E1F" ).getValue() );

        assertEquals( Float.valueOf( "2.0e-1" ), Constant.newFloatingPointLiteral( "2.0e-1" ).getValue() );
        assertEquals( Float.valueOf( "2.0e-1" ), Constant.newFloatingPointLiteral( "2.0E-1" ).getValue() );
        assertEquals( Float.valueOf( "2.0e-1" ), Constant.newFloatingPointLiteral( "2.0e-1f" ).getValue() );
        assertEquals( Float.valueOf( "2.0e-1" ), Constant.newFloatingPointLiteral( "2.0E-1f" ).getValue() );
        assertEquals( Float.valueOf( "2.0e-1" ), Constant.newFloatingPointLiteral( "2.0e-1F" ).getValue() );
        assertEquals( Float.valueOf( "2.0e-1" ), Constant.newFloatingPointLiteral( "2.0E-1F" ).getValue() );

        assertEquals( Float.valueOf( "2.0e1" ), Constant.newFloatingPointLiteral( "2.0e+1" ).getValue() );
        assertEquals( Float.valueOf( "2.0e1" ), Constant.newFloatingPointLiteral( "2.0E+1" ).getValue() );
        assertEquals( Float.valueOf( "2.0e1" ), Constant.newFloatingPointLiteral( "2.0e+1f" ).getValue() );
        assertEquals( Float.valueOf( "2.0e1" ), Constant.newFloatingPointLiteral( "2.0E+1f" ).getValue() );
        assertEquals( Float.valueOf( "2.0e1" ), Constant.newFloatingPointLiteral( "2.0e+1F" ).getValue() );
        assertEquals( Float.valueOf( "2.0e1" ), Constant.newFloatingPointLiteral( "2.0E+1F" ).getValue() );

        assertEquals( Float.valueOf( ".2e1" ), Constant.newFloatingPointLiteral( ".2e1" ).getValue() );
        assertEquals( Float.valueOf( ".2e1" ), Constant.newFloatingPointLiteral( ".2E1" ).getValue() );
        assertEquals( Float.valueOf( ".2e1" ), Constant.newFloatingPointLiteral( ".2e1f" ).getValue() );
        assertEquals( Float.valueOf( ".2e1" ), Constant.newFloatingPointLiteral( ".2E1f" ).getValue() );
        assertEquals( Float.valueOf( ".2e1" ), Constant.newFloatingPointLiteral( ".2e1F" ).getValue() );
        assertEquals( Float.valueOf( ".2e1" ), Constant.newFloatingPointLiteral( ".2E1F" ).getValue() );

        assertEquals( Float.valueOf( ".2e-1" ), Constant.newFloatingPointLiteral( ".2e-1" ).getValue() );
        assertEquals( Float.valueOf( ".2e-1" ), Constant.newFloatingPointLiteral( ".2E-1" ).getValue() );
        assertEquals( Float.valueOf( ".2e-1" ), Constant.newFloatingPointLiteral( ".2e-1f" ).getValue() );
        assertEquals( Float.valueOf( ".2e-1" ), Constant.newFloatingPointLiteral( ".2E-1f" ).getValue() );
        assertEquals( Float.valueOf( ".2e-1" ), Constant.newFloatingPointLiteral( ".2e-1F" ).getValue() );
        assertEquals( Float.valueOf( ".2e-1" ), Constant.newFloatingPointLiteral( ".2E-1F" ).getValue() );

        assertEquals( Float.valueOf( ".2e1" ), Constant.newFloatingPointLiteral( ".2e+1" ).getValue() );
        assertEquals( Float.valueOf( ".2e1" ), Constant.newFloatingPointLiteral( ".2E+1" ).getValue() );
        assertEquals( Float.valueOf( ".2e1" ), Constant.newFloatingPointLiteral( ".2e+1f" ).getValue() );
        assertEquals( Float.valueOf( ".2e1" ), Constant.newFloatingPointLiteral( ".2E+1f" ).getValue() );
        assertEquals( Float.valueOf( ".2e1" ), Constant.newFloatingPointLiteral( ".2e+1F" ).getValue() );
        assertEquals( Float.valueOf( ".2e1" ), Constant.newFloatingPointLiteral( ".2E+1F" ).getValue() );
        
        assertEquals( Float.valueOf( "2e1" ), Constant.newFloatingPointLiteral( "2e1" ).getValue() );
        assertEquals( Float.valueOf( "2e1" ), Constant.newFloatingPointLiteral( "2E1" ).getValue() );
        
        assertEquals( Float.valueOf( "2e-1" ), Constant.newFloatingPointLiteral( "2e-1" ).getValue() );
        assertEquals( Float.valueOf( "2e-1" ), Constant.newFloatingPointLiteral( "2E-1" ).getValue() );
        
        assertEquals( Float.valueOf( "2e1" ), Constant.newFloatingPointLiteral( "2e+1" ).getValue() );
        assertEquals( Float.valueOf( "2e1" ), Constant.newFloatingPointLiteral( "2E+1" ).getValue() );
   }
    
    @Test
    public void testHexadecimalFloatingPoint() {
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0x2p1" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0X2p1" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0x2p1f" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0X2p1f" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0x2p1F" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0X2p1F" ).getValue() );

        assertEquals( Float.valueOf( "0x2p-1" ), Constant.newFloatingPointLiteral( "0x2p-1" ).getValue() );
        assertEquals( Float.valueOf( "0x2p-1" ), Constant.newFloatingPointLiteral( "0X2p-1" ).getValue() );
        assertEquals( Float.valueOf( "0x2p-1" ), Constant.newFloatingPointLiteral( "0x2p-1f" ).getValue() );
        assertEquals( Float.valueOf( "0x2p-1" ), Constant.newFloatingPointLiteral( "0X2p-1f" ).getValue() );
        assertEquals( Float.valueOf( "0x2p-1" ), Constant.newFloatingPointLiteral( "0x2p-1F" ).getValue() );
        assertEquals( Float.valueOf( "0x2p-1" ), Constant.newFloatingPointLiteral( "0X2p-1F" ).getValue() );

        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0x2p+1" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0X2p+1" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0x2p+1f" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0X2p+1f" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0x2p+1F" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0X2p+1F" ).getValue() );

        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0x2.p1" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0X2.p1" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0x2.p1f" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0X2.p1f" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0x2.p1F" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0X2.p1F" ).getValue() );

        assertEquals( Float.valueOf( "0x2p-1" ), Constant.newFloatingPointLiteral( "0x2.p-1" ).getValue() );
        assertEquals( Float.valueOf( "0x2p-1" ), Constant.newFloatingPointLiteral( "0X2.p-1" ).getValue() );
        assertEquals( Float.valueOf( "0x2p-1" ), Constant.newFloatingPointLiteral( "0x2.p-1f" ).getValue() );
        assertEquals( Float.valueOf( "0x2p-1" ), Constant.newFloatingPointLiteral( "0X2.p-1f" ).getValue() );
        assertEquals( Float.valueOf( "0x2p-1" ), Constant.newFloatingPointLiteral( "0x2.p-1F" ).getValue() );
        assertEquals( Float.valueOf( "0x2p-1" ), Constant.newFloatingPointLiteral( "0X2.p-1F" ).getValue() );

        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0x2.p+1" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0X2.p+1" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0x2.p+1f" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0X2.p+1f" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0x2.p+1F" ).getValue() );
        assertEquals( Float.valueOf( "0x2p1" ), Constant.newFloatingPointLiteral( "0X2.p+1F" ).getValue() );

        assertEquals( Float.valueOf( "0x.2p1" ), Constant.newFloatingPointLiteral( "0x.2p1" ).getValue() );
        assertEquals( Float.valueOf( "0x.2p1" ), Constant.newFloatingPointLiteral( "0X.2p1" ).getValue() );
        assertEquals( Float.valueOf( "0x.2p1" ), Constant.newFloatingPointLiteral( "0x.2p1f" ).getValue() );
        assertEquals( Float.valueOf( "0x.2p1" ), Constant.newFloatingPointLiteral( "0X.2p1f" ).getValue() );
        assertEquals( Float.valueOf( "0x.2p1" ), Constant.newFloatingPointLiteral( "0x.2p1F" ).getValue() );
        assertEquals( Float.valueOf( "0x.2p1" ), Constant.newFloatingPointLiteral( "0X.2p1F" ).getValue() );

        assertEquals( Float.valueOf( "0x.2p-1" ), Constant.newFloatingPointLiteral( "0x.2p-1" ).getValue() );
        assertEquals( Float.valueOf( "0x.2p-1" ), Constant.newFloatingPointLiteral( "0X.2p-1" ).getValue() );
        assertEquals( Float.valueOf( "0x.2p-1" ), Constant.newFloatingPointLiteral( "0x.2p-1f" ).getValue() );
        assertEquals( Float.valueOf( "0x.2p-1" ), Constant.newFloatingPointLiteral( "0X.2p-1f" ).getValue() );
        assertEquals( Float.valueOf( "0x.2p-1" ), Constant.newFloatingPointLiteral( "0x.2p-1F" ).getValue() );
        assertEquals( Float.valueOf( "0x.2p-1" ), Constant.newFloatingPointLiteral( "0X.2p-1F" ).getValue() );

        assertEquals( Float.valueOf( "0x.2p1" ), Constant.newFloatingPointLiteral( "0x.2p+1" ).getValue() );
        assertEquals( Float.valueOf( "0x.2p1" ), Constant.newFloatingPointLiteral( "0X.2p+1" ).getValue() );
        assertEquals( Float.valueOf( "0x.2p1" ), Constant.newFloatingPointLiteral( "0x.2p+1f" ).getValue() );
        assertEquals( Float.valueOf( "0x.2p1" ), Constant.newFloatingPointLiteral( "0X.2p+1f" ).getValue() );
        assertEquals( Float.valueOf( "0x.2p1" ), Constant.newFloatingPointLiteral( "0x.2p+1F" ).getValue() );
        assertEquals( Float.valueOf( "0x.2p1" ), Constant.newFloatingPointLiteral( "0X.2p+1F" ).getValue() );

        assertEquals( Float.valueOf( "0x3.2p1" ), Constant.newFloatingPointLiteral( "0x3.2p1" ).getValue() );
        assertEquals( Float.valueOf( "0x3.2p1" ), Constant.newFloatingPointLiteral( "0X3.2p1" ).getValue() );
        assertEquals( Float.valueOf( "0x3.2p1" ), Constant.newFloatingPointLiteral( "0x3.2p1f" ).getValue() );
        assertEquals( Float.valueOf( "0x3.2p1" ), Constant.newFloatingPointLiteral( "0X3.2p1f" ).getValue() );
        assertEquals( Float.valueOf( "0x3.2p1" ), Constant.newFloatingPointLiteral( "0x3.2p1F" ).getValue() );
        assertEquals( Float.valueOf( "0x3.2p1" ), Constant.newFloatingPointLiteral( "0X3.2p1F" ).getValue() );

        assertEquals( Float.valueOf( "0x3.2p-1" ), Constant.newFloatingPointLiteral( "0x3.2p-1" ).getValue() );
        assertEquals( Float.valueOf( "0x3.2p-1" ), Constant.newFloatingPointLiteral( "0X3.2p-1" ).getValue() );
        assertEquals( Float.valueOf( "0x3.2p-1" ), Constant.newFloatingPointLiteral( "0x3.2p-1f" ).getValue() );
        assertEquals( Float.valueOf( "0x3.2p-1" ), Constant.newFloatingPointLiteral( "0X3.2p-1f" ).getValue() );
        assertEquals( Float.valueOf( "0x3.2p-1" ), Constant.newFloatingPointLiteral( "0x3.2p-1F" ).getValue() );
        assertEquals( Float.valueOf( "0x3.2p-1" ), Constant.newFloatingPointLiteral( "0X3.2p-1F" ).getValue() );

        assertEquals( Float.valueOf( "0x3.2p1" ), Constant.newFloatingPointLiteral( "0x3.2p+1" ).getValue() );
        assertEquals( Float.valueOf( "0x3.2p1" ), Constant.newFloatingPointLiteral( "0X3.2p+1" ).getValue() );
        assertEquals( Float.valueOf( "0x3.2p1" ), Constant.newFloatingPointLiteral( "0x3.2p+1f" ).getValue() );
        assertEquals( Float.valueOf( "0x3.2p1" ), Constant.newFloatingPointLiteral( "0X3.2p+1f" ).getValue() );
        assertEquals( Float.valueOf( "0x3.2p1" ), Constant.newFloatingPointLiteral( "0x3.2p+1F" ).getValue() );
        assertEquals( Float.valueOf( "0x3.2p1" ), Constant.newFloatingPointLiteral( "0X3.2p+1F" ).getValue() );
}
}
