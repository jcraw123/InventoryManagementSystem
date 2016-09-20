package test_models;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


import models.Warehouse;
import models.WarehouseList;

public class TestWarehouse {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private static final String VALID_NAME = "DWARE";
	private static final String VALID_ADDRESS = "239 Dover St";
	private static final String VALID_CITY = "Austin";
	private static final String VALID_STATE = "Texas";
	private static final String VALID_ZIPCODE = "72232";
	private static final int VALID_STORAGECAPACITY = 1000;
	
	private static Warehouse testWarehouse;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testWarehouse = new Warehouse();
	}
	
	@Before
	public void setUp() throws Exception {
	}

	//1
	@Test
	public void testWarehouseNameInvalidNull() {
		String testName = null;
		assertEquals(false, testWarehouse.validName(testName));
	}
	
	//2
		@Test
		public void testWarehouseNameInvalidTooLarge() {
			String testName = "";
			for(int i = 0; i < 256; i++)
				testName += "a";
			assertEquals(false, testWarehouse.validName(testName));
		}
	
	//3 
	@Test
	public void testWarehouseNameValidLarge() {
		String testName = "";
		for(int i = 0; i < 255; i++)
			testName += "a";
		assertEquals(true, testWarehouse.validName(testName));
	}
	
	//4 
	@Test
	public void testWarehouseNameValidNormal() {
		assertEquals(true, testWarehouse.validName(VALID_NAME));
	}
	
		


}
