package test_models;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


import models.Part;
import models.PartList;
import models.Warehouse;
import database.GatewayException;
import database.PartTableGateway;

public class TestParts {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	int id = 1;
	private static final String partNumber = "A32";
	private static final String partName = "2 inch stainless steel hex bolt";
	private static final String vendorName = "Cytron";
	private static final String quantityUnit = "Linear ft.";
	private static final String quantityUnit2 = "Pieces";
	private static final String quantityUnitBlank = "";
	private static final String vendorPartNumber = "B345";
	private static final String vendorNameNull = null;
	private static final String partNumberNull = null;
	private static final String partNumberBlank = "";
	
	private static Part testPart;
	private static PartTableGateway  testPartTableGateway; 
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testPart = new Part();
		testPartTableGateway = new PartTableGateway();
	}
	
	@Before
	public void setUp() throws Exception {
	
		
	}
	
	/* Test 1
	 * Tests for valid Vendor
	 */
	@Test
	public void testPartValidVendor() {
		try {
			Part pnv = new Part(id, partNumber, partName, vendorName, quantityUnit, vendorPartNumber);
			assertTrue(pnv.getPartNumber()==partNumber);
			assertTrue(pnv.getPartName()==partName);
			assertTrue(pnv.getVendorName()==vendorName);
			assertTrue(pnv.getQuantityUnit()==quantityUnit);
			assertTrue(pnv.getVendorPartNumber()==vendorPartNumber);
		} catch (Exception e) {
			assertNotEquals(null,testPart);
		}
	}
	
	/*
	 * Test 2
	 * Tests for null vendor
	 */
	@Test
	public void testPartNullVendor() {
		try {
			Part pnv = new Part(id, partNumber, partName, vendorNameNull, quantityUnit, vendorPartNumber);
			assertTrue(pnv.getPartNumber()==partNumber);
			assertTrue(pnv.getPartName()==partName);
			assertTrue(pnv.getVendorName()==vendorNameNull);
			assertTrue(pnv.getQuantityUnit()==quantityUnit);
			assertTrue(pnv.getVendorPartNumber()==vendorPartNumber);
		} catch (Exception e) {
			assertEquals(null,testPart);
		}	
				
	}
	
	/*
	 * Test 3
	 * Test for valid quantity unit of Linear Ft.
	 * 
	 */
	@Test 
	public void testPartQuantityUnitFt() {
		try {
			Part pnv = new Part(id, partNumber, partName, vendorName, quantityUnit, vendorPartNumber);
			assertTrue(pnv.getPartNumber()==partNumber);
			assertTrue(pnv.getPartName()==partName);
			assertTrue(pnv.getVendorName()==vendorName);
			assertTrue(pnv.getQuantityUnit()==quantityUnit);
			assertTrue(pnv.getVendorPartNumber()==vendorPartNumber);
		} catch (Exception e) {
			assertNotEquals(null,testPart);
		}	
	}
	
	/*
	 * Test 4
	 * Test for valid quantity unit of Pieces 
	 * 
	 */
	@Test 
	public void testPartQuantityUnitPieces() {
		try {
			Part pnv = new Part(id, partNumber, partName, vendorName, quantityUnit2, vendorPartNumber);
			assertTrue(pnv.getPartNumber()==partNumber);
			assertTrue(pnv.getPartName()==partName);
			assertTrue(pnv.getVendorName()==vendorName);
			assertTrue(pnv.getQuantityUnit()==quantityUnit2);
			assertTrue(pnv.getVendorPartNumber()==vendorPartNumber);
		} catch (Exception e) {
			assertNotEquals(null,testPart);
		}	
	}
	
	/*
	 * Test 5
	 * Tests for null part number
	 */
	@Test
	public void testPartNullPartNumber() {
		try {
			Part pnv = new Part(id, partNumberNull, partName, vendorName, quantityUnit, vendorPartNumber);
			assertTrue(pnv.getPartNumber()==partNumberNull);
			assertTrue(pnv.getPartName()==partName);
			assertTrue(pnv.getVendorName()==vendorName);
			assertTrue(pnv.getQuantityUnit()==quantityUnit);
			assertTrue(pnv.getVendorPartNumber()==vendorPartNumber);
		} catch (Exception e) {
			assertEquals(null,testPart);
		}	
				
	}
	
	/*
	 * Test 6
	 * test for valid part number for adding to database
	 */
	@Test
	public void testValidPartNumber() {
		try {
			Part pnv = new Part(id, partNumber, partName, vendorName, quantityUnit, vendorPartNumber);
			assertTrue(pnv.getPartNumber()==partNumber);
			assertTrue(pnv.getPartName()==partName);
			assertTrue(pnv.getVendorName()==vendorName);
			assertTrue(pnv.getQuantityUnit()==quantityUnit);
			assertTrue(pnv.getVendorPartNumber()==vendorPartNumber);
		} catch (Exception e) {
			assertEquals(null,testPartTableGateway);
		}	
	
	}
	
	/*
	 * Test 7 
	 * test for blank part number for adding to database
	 */
	@Test
	public void testPartBlankPartNumber() {
		try {
			Part pnv = new Part(id, partNumberBlank, partName, vendorName, quantityUnit, vendorPartNumber);
			assertTrue(pnv.getPartNumber()==partNumberBlank);
			assertTrue(pnv.getPartName()==partName);
			assertTrue(pnv.getVendorName()==vendorName);
			assertTrue(pnv.getQuantityUnit()==quantityUnit);
			assertTrue(pnv.getVendorPartNumber()==vendorPartNumber);
		} catch (Exception e) {
			assertEquals(null,testPartTableGateway);
		}	
				
	}
	
	/*
	 * Test 8
	 * 
	 */
	
	@Test
	public void testPartBlankQuantityUnit() {
		try {
			Part pnv = new Part(id, partNumber, partName, vendorName, quantityUnitBlank, vendorPartNumber);
			assertTrue(pnv.getPartNumber()==partNumber);
			assertTrue(pnv.getPartName()==partName);
			assertTrue(pnv.getVendorName()==vendorName);
			assertTrue(pnv.getQuantityUnit()==quantityUnitBlank);
			assertTrue(pnv.getVendorPartNumber()==vendorPartNumber);
		} catch (Exception e) {
			assertEquals(null,testPart);
		}	
				
	}
	

}
