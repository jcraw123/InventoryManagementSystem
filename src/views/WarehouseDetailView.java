package views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.MDIChild;
import controller.MDIParent;
import models.Warehouse;
import database.GatewayException;


public class WarehouseDetailView extends MDIChild implements Observer {
private Warehouse myWarehouse;
	
	/**
	 * Fields for warehouse data access
	 */
	private JLabel fldId;
	private JTextField fldWarehouseName, fldAddress, fldCity, fldState, fldZipCode;
	private JTextField fldStorageCapacity;
	
	/**
	 * Constructor
	 * @param title
	 */
	public WarehouseDetailView(String title, Warehouse warehouse, MDIParent m) {
		super(title, m);
		
		myWarehouse = warehouse;

		//register as an observer
		myWarehouse.addObserver(this);
		
		//prep layout and fields
		JPanel panel = new JPanel(); 
		panel.setLayout(new GridLayout(7, 2));
		
		//init fields to record data
		panel.add(new JLabel("Id"));
		fldId = new JLabel("");
		panel.add(fldId);
		
		panel.add(new JLabel("Warehouse Name"));
		fldWarehouseName = new JTextField("");
		fldWarehouseName.addKeyListener(new TextfieldChangeListener());
		panel.add(fldWarehouseName);
		
		panel.add(new JLabel("Address"));
		fldAddress = new JTextField("");
		fldAddress.addKeyListener(new TextfieldChangeListener());
		panel.add(fldAddress);

		panel.add(new JLabel("City"));
		fldCity = new JTextField("");
		fldCity.addKeyListener(new TextfieldChangeListener());
		panel.add(fldCity);
		
		panel.add(new JLabel("State"));
		fldState = new JTextField("");
		fldState.addKeyListener(new TextfieldChangeListener());
		panel.add(fldState);
		
		panel.add(new JLabel("Zip Code"));
		fldZipCode = new JTextField("");
		fldZipCode.addKeyListener(new TextfieldChangeListener());
		panel.add(fldZipCode);
		
		panel.add(new JLabel("Storage Capacity"));
		fldStorageCapacity = new JTextField("");
		fldStorageCapacity.addKeyListener(new TextfieldChangeListener());
		panel.add(fldStorageCapacity);
		this.add(panel);
		
		//add a Save button to write field changes back to model data
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JButton button = new JButton("Save Record");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveWarehouse();
			}
		});
		panel.add(button);
		
		this.add(panel, BorderLayout.SOUTH);

		//load fields with model data
		refreshFields();
		
		
		this.setPreferredSize(new Dimension(660, 410));
	}
	
	/**
	 * Reload fields with model data
	 * Used when model notifies view of change
	 */
	public void refreshFields() {
		fldId.setText("" + myWarehouse.getId());
		fldWarehouseName.setText(myWarehouse.getWarehouseName());
		fldAddress.setText(myWarehouse.getAddress());
		fldCity.setText(myWarehouse.getCity());
		fldState.setText(myWarehouse.getState());
		fldZipCode.setText("" + myWarehouse.getZipCode());
		fldStorageCapacity.setText("" + myWarehouse.getStorageCapacity());
		this.setTitle(myWarehouse.getWarehouseName());
		setChanged(false);
	}

	
	//if any of them fail then no fields should be changed
	//and previous values reloaded
	//this is called rollback
	public boolean saveWarehouse() {
		
		
		String testName = fldWarehouseName.getText().trim();
		if(!myWarehouse.validName(testName)) {
			parent.displayChildMessage(Warehouse.ERRORMSG_INVALID_NAME);
			if(myWarehouse.getId() != Warehouse.INVALID_ID)
			refreshFields();
			return false;
		}
		String testAddress = fldAddress.getText().trim();
		if(!myWarehouse.validAddress(testAddress)) {
			parent.displayChildMessage(Warehouse.ERRORMSG_INVALID_ADDRESS);
			if(myWarehouse.getId() != Warehouse.INVALID_ID)
			refreshFields();
			return false; 
		}
		String testCity = fldCity.getText().trim();
		if(!myWarehouse.validCity(testCity)) {
			parent.displayChildMessage(Warehouse.ERRORMSG_INVALID_CITY);
			if(myWarehouse.getId() != Warehouse.INVALID_ID)
			refreshFields();
			return false;
		}
		String testState = fldState.getText().trim();
		if(!myWarehouse.validState(testState)) {
			parent.displayChildMessage(Warehouse.ERRORMSG_INVALID_STATE);
			if(myWarehouse.getId() != Warehouse.INVALID_ID)
			refreshFields();
			return false;
		}
		
		String testZip = fldZipCode.getText().trim();
		if(!myWarehouse.validZipCode(testZip)) {
			parent.displayChildMessage(Warehouse.ERRORMSG_INVALID_ZIPCODE);
			if(myWarehouse.getId() != Warehouse.INVALID_ID)
			refreshFields();
			return false;
		}
		
		
		int testCapacity = 0;
		try {
			testCapacity = Integer.parseInt(fldStorageCapacity.getText());
		} catch(Exception e) {
			parent.displayChildMessage(Warehouse.ERRORMSG_INVALID_CAPACITY);
			if(myWarehouse.getId() != Warehouse.INVALID_ID)
			refreshFields();
			return false; 
		}
		
		try {
			myWarehouse.setWarehouseName(testName);
			myWarehouse.setAddress(testAddress);
			myWarehouse.setZipCode(testZip);
			myWarehouse.setCity(testCity);
			myWarehouse.setState(testState);
			myWarehouse.setStorageCapacity(testCapacity);
		} catch(Exception e) {
			parent.displayChildMessage(e.getMessage());
			refreshFields();
			return false;
		} 
		
		//tell model that update is done (in case it needs to notify observers
		try {
			myWarehouse.finishUpdate();
			setChanged(false);
		} catch (GatewayException e) {
			refreshFields();
			parent.displayChildMessage(e.getMessage());
			return false;
		}
		
		parent.displayChildMessage("Changes saved");
		return true;
	}

	/**
	 * Subclass-specific cleanup
	 */
	protected void cleanup() {
		//let superclass do its thing
		super.cleanup();
				
		//unregister from observable
		myWarehouse.deleteObserver(this);
	}

	/**
	 * Called by Observable
	 */
	public void update(Observable o, Object arg) {
		refreshFields();
	}
	
	public Warehouse getMyWarehouse() {
		return myWarehouse;
	}
	
	public void setMyWarehouse(Warehouse myWarehouse) {
		this.myWarehouse = myWarehouse;
	}
	private class TextfieldChangeListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
			//any typing in a text field flags view as having changed
			setChanged(true);
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
	}
	
}
