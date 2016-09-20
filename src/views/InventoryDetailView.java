package views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;


import controller.MDIChild;
import controller.MDIParent;
import database.GatewayException;
import models.Inventory;
import models.InventoryItemList;
import models.Warehouse;




public class InventoryDetailView extends MDIChild implements Observer   {
	
	private Inventory myInventory;
    private InventoryItemList inventoryList;
    
    
    private JLabel fldId;
    private JTextField fldQuantity;
    private JComboBox comboBoxWarehouseId, comboBoxPartId;
        
    private HashMap< Long, String> warehouseList, partList;
    
    private MDIParent mdiparent;
    
    
    public static final String DEFAULT_VALUE = "Select";
    
    
    public InventoryDetailView(String title, Inventory inv, MDIParent m) {
        super(title, m);
        
        myInventory = inv;
        mdiparent = m;

        inventoryList = m.getInventoryList();
        
        //register as an observer
        myInventory.addObserver(this);
        
        //prep layout and fields
        JPanel panel = new JPanel(); 
        
        panel.setLayout(new GridLayout( 5, 2, 5, 3));
        //init fields to record data
        panel.add(new JLabel("Id"));
        fldId = new JLabel("");
        panel.add(fldId);
        
    // warehouse    
        panel.add(new JLabel("Warehouse"));
        // get warehouse list
        warehouseList = myInventory.getWarehouseList(m).getWarehouseList();
        
        
    // get size of warehouse List
        int size = warehouseList.size();
        String [] warehouseNameList = new String[size+1];
        
        
        int i =0;
        warehouseNameList[0] = DEFAULT_VALUE;
        
        for ( Long key : warehouseList.keySet() ) {
                 warehouseNameList[++i] = warehouseList.get(key);
        }

        comboBoxWarehouseId = new JComboBox(warehouseNameList);

        
       
        panel.add(comboBoxWarehouseId);
        
        panel.add(new JLabel("Part"));    
        partList = myInventory.getPartList(m).getPartList();
        
        // get size of part List
        size = partList.size();
        String [] partNameList = new String[size+1];
        
        i =0;
        partNameList[0] = DEFAULT_VALUE;
        
        for ( Long key : partList.keySet() ) {
                 partNameList[++i] = partList.get(key);
        }
        
        comboBoxPartId = new JComboBox(partNameList);
        if( partList.containsKey(myInventory.getPartId()) ){
            comboBoxPartId.getModel().setSelectedItem(partList.get(myInventory.getPartId()));
        }
        
        panel.add(comboBoxPartId);
    
        
        panel.add(new JLabel("Quantity"));
        fldQuantity = new JTextField("");
        fldQuantity.addKeyListener(new TextfieldChangeListener());
        panel.add(fldQuantity);
        
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

        refreshFields();
        
        
        this.setPreferredSize(new Dimension(660, 410));
    }
    
    /**
     * Reload fields with model data
     * Used when model notifies view of change
     */
    public void refreshFields() {
        fldId.setText("" + myInventory.getId());
        if( myInventory.getId() >0 ){
            
            // for warehouse
            if( warehouseList.containsKey(myInventory.getWarehouseId()) ){
                comboBoxWarehouseId.getModel().setSelectedItem(warehouseList.get(myInventory.getWarehouseId()));
            }
            
            // for part
            if( partList.containsKey(myInventory.getPartId()) ){
                comboBoxPartId.getModel().setSelectedItem(partList.get(myInventory.getPartId()));
            }
        }else{
            comboBoxWarehouseId.getModel().setSelectedItem(DEFAULT_VALUE);
            comboBoxPartId.getModel().setSelectedItem(DEFAULT_VALUE);
        }

        fldQuantity.setText(""+myInventory.getQuantity());
        //update window title
        this.setTitle("Inventory " +myInventory.getId());
        //flag as unchanged
        setChanged(false);
    }

    /**
     * saves changes to the view's Inventory model 
     */
   
    @Override
    public boolean saveModel() {
        //display any error message if field data are invalid
        
        if ( comboBoxWarehouseId.getSelectedItem().equals(DEFAULT_VALUE) ){
            parent.displayChildMessage("Invalid Warehouse Id!");
            refreshFields();
            return false;
        }
        
        if ( comboBoxPartId.getSelectedItem().equals(DEFAULT_VALUE) ){
            parent.displayChildMessage("Invalid Part Id!");
            refreshFields();
            return false;
        }
        
        double quantity = 0.0;
        try {
            quantity = Double.parseDouble(fldQuantity.getText().trim());
            if(!myInventory.validQuantity(quantity)) {
                parent.displayChildMessage("Invalid Quantity!");
                refreshFields();
                return false;
            }
            
            
        } catch(Exception e) {
            parent.displayChildMessage("Invalid Quantity!");
            refreshFields();
            return false;
        }

        //fields are valid so save to model
        try {
            
            for ( Long key : warehouseList.keySet() ) {
                 if( warehouseList.get(key).equals(comboBoxWarehouseId.getSelectedItem()) ){
                     myInventory.setWarehouseId( key );
                     break;
                 }
            }
            
            for ( Long key : partList.keySet() ) {
                 if( partList.get(key).equals(comboBoxPartId.getSelectedItem()) ){
                     myInventory.setPartId( key);
                     break;
                 }
            }
            
            if( inventoryList.duplicate(myInventory)){
                parent.displayChildMessage("Could not have more than one record which has the same Warehouse and Part !");
                refreshFields();
                return false;
            }
    
            
            
            
            Warehouse warehouse = mdiparent.getWarehouseList().searchById(myInventory.getWarehouseId());
            Double totalCapacity = warehouse.getStorageCapacity();
            Double oldQuanlity = myInventory.getQuantity(); 
            myInventory.setQuantity(quantity);
            
            if( inventoryList.remainingWarehouseCapacity( totalCapacity, myInventory) < 0 ){
                parent.displayChildMessage(" Warehouse "+ comboBoxWarehouseId.getSelectedItem() +"'s Remaining Storage Capacity is "+ (totalCapacity-inventoryList.getTotalWarehouseQuantity(myInventory)) );
                myInventory.setQuantity(oldQuanlity);
                refreshFields();
                return false;
            }
            
        } catch(Exception e) {
            parent.displayChildMessage(e.getMessage());
            refreshFields();
            return false;
        }
        
        //tell model that update is done (in case it needs to notify observers
        try {
            myInventory.finishUpdate();
            setChanged(false);
            
        } catch (GatewayException e) {
            //e.printStackTrace();
            //reset fields to db copy of inventory if save fails
            refreshFields();
            parent.displayChildMessage(e.getMessage());
            return false;
        }
        
        parent.displayChildMessage("Changes saved");
        return true;
    }

    
    protected void cleanup() {
        //let superclass do its thing
        super.cleanup();
                
        //unregister from observable
        myInventory.deleteObserver(this);
    }

    /**
     * Called by Observable
     */
    @Override
    public void update(Observable o, Object arg) {
        refreshFields();
    }

    public Inventory getMyInventory() {
        return myInventory;
    }

    public void setMyInventory(Inventory myInventory) {
        this.myInventory = myInventory;
    }
    
    private class TextfieldChangeListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
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
