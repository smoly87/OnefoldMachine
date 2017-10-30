/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andrey
 */
public class DataBinConvertorTest {
    
    public DataBinConvertorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getInstance method, of class DataBinConvertor.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        DataBinConvertor expResult = null;
        DataBinConvertor result = DataBinConvertor.getInstance();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setIntegerToByteList method, of class DataBinConvertor.
     */
    @Test
    public void testSetIntegerToByteList() {
        System.out.println("setIntegerToByteList");
        ArrayList<Byte> lst = null;
        int value = 0;
        int start = 0;
        DataBinConvertor instance = null;
        instance.setIntegerToByteList(lst, value, start);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of integerToByte method, of class DataBinConvertor.
     */
    @Test
    public void testIntegerToByte() {
        System.out.println("integerToByte");
        int value = 0;
        DataBinConvertor instance = null;
        Byte[] expResult = null;
        Byte[] result = instance.toBin(value);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of integerToByteList method, of class DataBinConvertor.
     */
    @Test
    public void testIntegerToByteList() {
        System.out.println("integerToByteList");
        int value = 0;
        DataBinConvertor instance = null;
        ArrayList<Byte> expResult = null;
        ArrayList<Byte> result = instance.integerToByteList(value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of bytesToInt method, of class DataBinConvertor.
     */
    @Test
    public void testBytesToInt_ByteArr_int() {
        System.out.println("bytesToInt");
        Byte[] arr = null;
        int start = 0;
        DataBinConvertor instance = null;
        Integer expResult = null;
        Integer result = instance.bytesToInt(arr, start);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of bytesToInt method, of class DataBinConvertor.
     */
    @Test
    public void testBytesToInt_ArrayList_int() {
        System.out.println("bytesToInt");
        ArrayList<Byte> arr = null;
        int start = 0;
        DataBinConvertor instance = null;
        Integer expResult = null;
        Integer result = instance.bytesToInt(arr, start);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
