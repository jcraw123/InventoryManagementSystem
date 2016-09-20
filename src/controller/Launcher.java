package controller;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import models.WarehouseList;
import database.PartTableGateway;
import database.WarehouseTableGateway;
import database.GatewayException;
import database.InventoryTableGateway;
import models.InventoryItemList;
import models.PartList; 


public class Launcher {

	/**
	 * Configures and Launches initial view(s) of the application on the Event Dispatch Thread
	 */
	
	/* By Jaleesa Crawford 
	 * 
	 */
	
	public static void createAndShowGUI() {
			
		
		WarehouseTableGateway wtg = null;
		PartTableGateway ptg = null;
		InventoryTableGateway itg = null;
		
		try {
			ptg = new PartTableGateway();
			wtg = new WarehouseTableGateway();
			itg = new InventoryTableGateway();
			
		} catch (GatewayException e) {
			JOptionPane.showMessageDialog(null, "Database is not responding.", "Database Offline!", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		WarehouseList warehouseList = new WarehouseList();
		warehouseList.setGateway(wtg);
		warehouseList.loadFromGateway();

		PartList partList = new PartList();
		partList.setGateway(ptg);
		partList.loadFromGateway();
		
		InventoryItemList inventoryList  = new InventoryItemList();
		inventoryList.setGateway(itg);
		inventoryList.loadFromGateway();
		
		MDIParent appFrame = new MDIParent("Inventory Management System", warehouseList, partList, inventoryList);

		
		
		appFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		//sets initial size of MDI frame
		appFrame.setSize(840, 680);
		
		appFrame.setVisible(true);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}