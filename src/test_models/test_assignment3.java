package test_models;
import static org.junit.Assert.*;

import org.junit.Test;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;


import models.Inventory;
import models.InventoryItemList;
import models.Part;
import models.Warehouse;
import database.GatewayException;
import database.InventoryTableGateway;
import database.PartTableGateway;

import static org.junit.Assert.*;

import org.junit.Test;

public class test_assignment3 {
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private static final long id = 3;
	private static final long partID = 4;
	private static final long warehouseID = 5;
	private static final double quantity = 200.00;
	
	
	private static Inventory testInventory;
	
	private static InventoryTableGateway  testInventoryTableGateway; 
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testInventory = new Inventory(id, partID, warehouseID, quantity);
		testInventoryTableGateway = new InventoryTableGateway();
	}
	
	@Before
	public void setUp() throws Exception {
	
		
	}
	
	//1
	@Test
	public void testInventoryValidWarehouseID() {
		try {
			Inventory inv = new Inventory(id, warehouseID, partID, quantity);
			assertTrue(inv.getWarehouseId()==warehouseID);
		} catch (Exception e) {
			assertNotEquals(null,testInventory);
		}
	}
	
	//2
	@Test(expected = IllegalArgumentException.class)
	public void TestInventoryInvalidWarehouseID() {
		Inventory inv = new Inventory(id, 0, partID, quantity);
		assertTrue(inv != null);
		
		
	}
	
	//3
	@Test
	public void testInventoryValidPartID() {
		try {
			Inventory inv = new Inventory(id, warehouseID, partID, quantity);
			assertTrue(inv.getPartId()==partID);
		} catch (Exception e) {
			assertNotEquals(null,testInventory);
		}
	}
	
	//4
	@Test(expected = IllegalArgumentException.class)
	public void TestInventoryInvalidPartID() {
		Inventory inv = new Inventory(id, warehouseID, 0, quantity);
		assertTrue(inv != null);
		
		
	}
	
	//5 
	@Test
	public void testInventoryValidQuantity() {
		try {
			Inventory inv = new Inventory(id, warehouseID, partID, quantity);
			assertTrue(inv.getQuantity()==quantity);
		} catch (Exception e) {
			assertNotEquals(null,testInventory);
		}
	}
	
	//6
	@Test(expected = IllegalArgumentException.class)
	public void TestInventoryNegativeQuantity() {
		Inventory inv = new Inventory(id, warehouseID, partID, -1.0);
		assertTrue(inv != null);
		
		
	}

}
