package views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.MDIChild;
import controller.MDIParent;
import database.GatewayException;
import database.PartTableGateway;
import models.Part;
import views.PartDetailView.TextfieldChangeListener;


public class PartDetailView extends MDIChild implements Observer {
	
	/**
	 * Part object shown in view instance
	 */
	private Part myPart;
	
	/**
	 * Fields for Part data access
	 */
	private JLabel fldId;
	private JTextField fldPartNumber, fldPartName;
	private JTextField fldVendorName, fldQuantityUnit, fldVendorPartNumber;
	private PartTableGateway gateway;
	


		
	/**
	 * Constructor
	 * @param title
	 */
	public PartDetailView(String title, Part p, MDIParent m) {
		super(title, m);
		
		myPart = p;

		//register as an observer
		myPart.addObserver(this);
		
		//prep layout and fields
		JPanel panel = new JPanel(); 
		panel.setLayout(new GridLayout(6, 2));
		
		//init fields to record data
		panel.add(new JLabel("Id"));
		fldId = new JLabel("");
		panel.add(fldId);
		
		panel.add(new JLabel("Part Number"));
		fldPartNumber = new JTextField("");
		fldPartNumber.addKeyListener(new TextfieldChangeListener());
		panel.add(fldPartNumber);
		
		panel.add(new JLabel("Part Name"));
		fldPartName = new JTextField("");
		fldPartName.addKeyListener(new TextfieldChangeListener());
		panel.add(fldPartName);
		
		panel.add(new JLabel("Vendor Name"));
		fldVendorName = new JTextField("");
		fldVendorName.addKeyListener(new TextfieldChangeListener());
		panel.add(fldVendorName);
		
		panel.add(new JLabel("Unit of Quantity"));
		fldQuantityUnit = new JTextField("");
		fldQuantityUnit.addKeyListener(new TextfieldChangeListener());
		panel.add(fldQuantityUnit);
		
		panel.add(new JLabel("Vendor Part Number"));
		fldVendorPartNumber = new JTextField("");
		fldVendorPartNumber.addKeyListener(new TextfieldChangeListener());
		panel.add(fldVendorPartNumber);
		
		

		this.add(panel, BorderLayout.CENTER);
		
		//add a Save button to write field changes back to model data
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JButton button = new JButton("Save Record");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveModel();
			}
		});
		panel.add(button);
		
		this.add(panel, BorderLayout.SOUTH);

		//load fields with model data
		refreshFields();
		
		//can't call this on JPanel
		//this.pack();
		this.setPreferredSize(new Dimension(660, 410));
	}
	
	/**
	 * Reload fields with model data
	 * Used when model notifies view of change
	 */
	public void refreshFields() {
		fldId.setText("" + myPart.getId());
		fldPartNumber.setText(myPart.getPartNumber());
		fldPartName.setText(myPart.getPartName());
		fldVendorName.setText(myPart.getVendorName());
		fldQuantityUnit.setText(myPart.getQuantityUnit());
		fldVendorPartNumber.setText(myPart.getVendorPartNumber());
		
		//update window title
		this.setTitle(myPart.getPartName());
		//flag as unchanged
		setChanged(false);
	}
	
	public int lockRecord(int part) {
		try {
			return gateway.lockRow(part);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return part;
	}
	


	/**
	 * saves changes to the view's Part model 
	 */
	//if any of them fail then no fields should be changed
	//and previous values reloaded
	//this is called rollback
	@Override
	public boolean saveModel() {
		//display any error message if field data are invalid
		String testName = fldPartName.getText().trim();
		String testNumber = fldPartNumber.getText().trim();
		String testVendorName = fldVendorName.getText().trim();
		String testQuantityUnit = fldQuantityUnit.getText().trim();
		String testVendorNumber = fldVendorPartNumber.getText().trim();
		
		if(!myPart.validPartName(testName)) {
			parent.displayChildMessage(Part.ERRORMSG_INVALID_PARTNAME);
			//when inserting new record, don't clear the fields
			if(myPart.getId() != Part.INVALID_ID)
				refreshFields();
			return false;
		}
		
		if(!myPart.validPartNumber(testNumber)) {
			parent.displayChildMessage(Part.ERRORMSG_INVALID_PARTNUMBER);
			if(myPart.getId() != Part.INVALID_ID)
				refreshFields();
			return false;
		}
		
		if(!myPart.validVendor(testVendorName)) {
			parent.displayChildMessage(Part.ERRORMSG_INVALID_VENDORNAME);
			if(myPart.getId() != Part.INVALID_ID)
				refreshFields();
			return false;
		}
		
		if(!myPart.validPartUnit(testQuantityUnit)) {
			parent.displayChildMessage(Part.ERRORMSG_INVALID_QUANTIYUNITY);
			if(myPart.getId() != Part.INVALID_ID)
				refreshFields();
			return false;
		}
		
		
		
		if(!myPart.validVendorPart(testVendorNumber)) {
			parent.displayChildMessage(Part.ERRORMSG_INVALID_VENDORPARTUMBER);
			if(myPart.getId() != Part.INVALID_ID)
				refreshFields();
			return false;
		}
		
		
	
		//fields are valid so save to model
		try {
			myPart.setPartNumber(testNumber);
			myPart.setPartName(testName);
			myPart.setVendorName(testVendorName);
			myPart.setQuantityUnit(testQuantityUnit);
			myPart.setVendorPartNumber(testVendorNumber);
			
		} catch(Exception e) {
			parent.displayChildMessage(e.getMessage());
			refreshFields();
			return false;
		}
		
		//tell model that update is done (in case it needs to notify observers
		try {
			myPart.finishUpdate();
			setChanged(false);
			
		} catch (GatewayException e) {
			//e.printStackTrace();
			//reset fields to db copy of part if save fails
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
	@Override
	protected void cleanup() {
		super.cleanup();
				
		//unregister from observable
		myPart.deleteObserver(this);
	}

	/**
	 * Called by Observable
	 */
	@Override
	public void update(Observable o, Object arg) {
		refreshFields();
	}

	public Part getMyPart() {
		return myPart;
	}

	public void setMyPart(Part myPart) {
		this.myPart = myPart;
	}
	
	public class TextfieldChangeListener implements KeyListener {
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
