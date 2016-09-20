package views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import controller.MDIChild;
import controller.MDIParent;
import controller.MenuCommands;
import controller.WarehouseListController;
import models.Warehouse;

public class WarehouseListView extends MDIChild {
	
	
	private static JList<Warehouse> listWarehouses;
	private WarehouseListController myList;
	//saves reference to last selected model in JList
	//parent asks for this when opening a detail view
	private Warehouse selectedModel;
	
	
	public WarehouseListView(String title, WarehouseListController list, MDIParent m) {
		super(title, m);
		
		//set self to list's view (allows ListModel to tell this view to repaint when models change)
		//WarehouseListController is an observer of the models
		list.setMyListView(this);
		
		//prep list view
		myList = list;
		listWarehouses = new JList<Warehouse>(myList);
		//use our custom cell renderer instead of default 
		listWarehouses.setCellRenderer(new WarehouseListCellRenderer());
		listWarehouses.setPreferredSize(new Dimension(400, 450));
		
		//add event handler for double click
		listWarehouses.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				//if double-click then get index and open new detail view with record at that index
		        if(evt.getClickCount() == 2) {
		        	int index = listWarehouses.locationToIndex(evt.getPoint());
		        	//get the warehouse at that index
		        	selectedModel = myList.getElementAt(index);
		        	
		        	//open a new detail view
		        	openDetailView();
		        }
		        /*//for delete warehouse button
		        if(evt.getClickCount() ==1) {
	        		index = listWarehouse.locationToIndex(evt.getPoint());
	        		selectedModel=myList.getElementAt(index);
		        } */
		        
		    }
		});
		

		
		//add to content pane
		this.add(new JScrollPane(listWarehouses));
		
		this.setPreferredSize(new Dimension(640, 500));
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		/*JButton addButton = new JButton ("Add Warehouse");
		addButton.addActionListener(new ActionListener(){ 
			public void actionPerformed(ActionEvent e) {
				parent.doCommand(MenuCommands.SHOW_ADD_WAREHOUSE, null);
			}
		});
		panel.add(addButton);
		this.add(panel,BorderLayout.NORTH);
		
		panel.setLayout(new FlowLayout()); */
		
		JButton deleteButton = new JButton("Delete Warehouse");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (listWarehouses.isSelectionEmpty())
					parent.displayChildMessage("Please select warehouse first");
				else
					DeleteWarehouse();
			}
		});
		panel.add(deleteButton);
		this.add(panel,BorderLayout.SOUTH);
	
	}
	
	private void DeleteWarehouse() {
		int idx = listWarehouses.getSelectedIndex();
		if(idx < 0)
			return;
		//idx COULD end up > list size so make sure idx is < list size
		if(idx >= myList.getSize())
			return;
		Warehouse w = myList.getElementAt(idx);
		if(w == null)
			return;
		selectedModel = w;
		
		//ask user to confirm deletion
		String [] options = {"Yes", "No"};
		if(JOptionPane.showOptionDialog(myFrame
				, "Do you really want to delete " + w.getWarehouseName() + " ?"
				, "Confirm Deletion"
				, JOptionPane.YES_NO_OPTION
			    , JOptionPane.QUESTION_MESSAGE
			    , null
			    , options
				, options[1]) == JOptionPane.NO_OPTION) {
			return;
		}

		//tell the controller to do the deletion
		parent.doCommand(MenuCommands.DELETE_WAREHOUSE, this);
	
		/*
		selectedModel = getSelectedWarehouse();
		String warehouseName = selectedModel.getWarehouseName();
		WarehouseListController.deleteWarehouseFromList(selectedModel);
		parent.displayChildMessage("Warehouse " + warehouseName + "has been deleted");*/
	}

	
	 //Opens a WarehouseDetailView with the given warehouse object
	 
	public void openDetailView() {
		parent.doCommand(MenuCommands.SHOW_DETAIL_WAREHOUSE, this);
	}
	
	
	 //returns selected warehouse in list
	
	public Warehouse getSelectedWarehouse() {
		return selectedModel;
	}
	

	
	protected void cleanup() {
		super.cleanup();
				
		//unregister from observables
		myList.unregisterAsObserver();
	}
	
	public WarehouseListController getMyList() {
		return myList;
	}
	
	public void setMyList(WarehouseListController myList) {
		this.myList = myList;
	}
	
	public JList<Warehouse> getListWarehouses() {
		return listWarehouses;
	}
	
	public void setListWarehouses(JList<Warehouse> listWarehouses) {
		this.listWarehouses = listWarehouses;
	}

	public Warehouse getSelectedModel() {
		return selectedModel;
	}

	public void setSelectedModel(Warehouse selectedModel) {
		this.selectedModel = selectedModel;
	}
	

}
