package controller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import database.GatewayException;
import models.Inventory;
import models.InventoryItemList;
import models.Part;
import models.PartList;
import views.InventoryDetailView;
import views.InventoryListView;
import views.PartDetailView;
import views.PartListView;
import models.Warehouse;
import models.WarehouseList;
import reports.ReportException;
import reports.ReportGatewayMySQL;
import reports.WarehouseInventoryReportExcel;
import reports.WarehouseInventoryReportPDF;
import security.ABACPolicy;
import security.Authenticator;
import security.SecurityException;
import security.User;
import views.WarehouseDetailView;
import views.WarehouseListView;

public class MDIParent extends JFrame implements WindowListener {
	private static final long serialVersionUID = 1L;
	private JDesktopPane desktop;
	private int newFrameX = 0, newFrameY = 0; //used to cascade or stagger starting x,y of JInternalFrames
	
	//models and model-controllers
	private WarehouseList warehouseList;
	private PartList partList;
	private InventoryItemList inventoryList;
	private PartDetailView partView;
	
	private String name = "guest";

	//keep a list of currently open views
	//useful if the MDIParent needs to act on the open views or see if an instance is already open
	private List<MDIChild> openViews;
	
	public MDIParent(String title, WarehouseList wList, PartList pList, InventoryItemList iList) {
		super(title);
		
		warehouseList = wList;
		partList = pList;
		inventoryList = iList;
		

		//init the view list
		openViews = new LinkedList<MDIChild>();
		
		//create menu for adding inner frames
		MDIMenu menuBar = new MDIMenu(this);
		setJMenuBar(menuBar);
		   
		//create the MDI desktop
		desktop = new JDesktopPane();
		add(desktop);
		
		this.addWindowListener(this); //? 
		
		
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				cleanup();
			}
		});

	}
	
	/**
	 * responds to menu events or action calls from child windows (e.g., opening a detail view)
	 * 
	 * @param caller Calling child window reference in case Command requires more info from caller 
	 */
	public void doCommand(MenuCommands cmd, Container caller) {
		ABACPolicy policy = new ABACPolicy();
		int id = 0;

		Authenticator authenticate = new Authenticator();
		switch(cmd) {
			case APP_QUIT :
				//close all child windows first
				cleanup();
				this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
				break;
			case SHOW_LIST_WAREHOUSE :
				WarehouseListView v1 = new WarehouseListView("Warehouse List", new WarehouseListController(warehouseList), this);
				openMDIChild(v1);
				
				break;
			case SHOW_DETAIL_WAREHOUSE :
				Warehouse w = ((WarehouseListView) caller).getSelectedWarehouse();
		    	WarehouseDetailView v = new WarehouseDetailView(w.getWarehouseName(), w, this);
				openMDIChild(v);
				break;
				
			case SHOW_ADD_WAREHOUSE :
				for(Warehouse wCheck : warehouseList.getList()) {
					if(wCheck.getId() == Warehouse.INVALID_ID || wCheck.getWarehouseName().equalsIgnoreCase(Warehouse.DEFAULT_EMPTY_NAME)) {
						this.displayChildMessage("Please save changes to new Warehouse \"" + wCheck.getWarehouseName() + "\" before trying to add another.");
						return;
					}
				}
				Warehouse wAdd = new Warehouse();	
				warehouseList.addWarehouseToList(wAdd);
				
				for(MDIChild c : openViews) {
					if(c instanceof WarehouseListView) {
						((WarehouseListView) c).getMyList().addWarehouseToList(wAdd);
					}
				}
				
				WarehouseDetailView vAdd = new WarehouseDetailView(wAdd.getWarehouseName(), wAdd, this);
				openMDIChild(vAdd);
				
				break;
				
			case DELETE_WAREHOUSE:
				Warehouse wDelete = ((WarehouseListView) caller).getSelectedWarehouse();
				warehouseList.removeWarehouseFromList(wDelete);
				
				
				//close all details that are based on this object
				for(int i = openViews.size() - 1; i >= 0; i--) {
					MDIChild c = openViews.get(i);
					if(c instanceof WarehouseListView) {
						((WarehouseListView) c).getMyList().removeWarehouseFromList(wDelete);
					} else if(c instanceof WarehouseDetailView) {
						//if detail view is showing the deleted object then close the detail view without asking
						if(((WarehouseDetailView) c).getMyWarehouse() == wDelete)
							c.closeFrame();
					}
				}
				
				// delete the warehouse from the db
				try {
					wDelete.delete();
					this.displayChildMessage("Warehouse deleted.");
				} catch (GatewayException e) {
					System.err.println(e.getMessage());
					this.displayChildMessage("Error trying to delete warehouse.");
				}
				break;
				
				
				
			case SHOW_LIST_PARTS:
				partList.loadFromGateway();
				
				PartListView p1 = new PartListView("Part List", new PartListController(partList), this);
				//v1.setSingleOpenOnly(true);
				openMDIChild(p1);
				break;
				
			case SHOW_DETAIL_PART:
				if(policy.canUserAccessFunction(name, "part.edit") == false) {
					displayChildMessage("You do not have permission to edit a part");
					break;
				}
				Part p = ((PartListView) caller).getSelectedPart();

	
		    	PartDetailView pv = new PartDetailView(p.getPartName(), p, this);
				openMDIChild(pv);
				break;
				
			case SHOW_ADD_PART:
				if(policy.canUserAccessFunction(name, "part.add") == false ) {
					displayChildMessage("You do not have permission to add a part");
					break;
				}
				for(Part pCheck : partList.getList()) {
					if(pCheck.getId() == Part.INVALID_ID || pCheck.getPartName().equalsIgnoreCase(Part.DEFAULT_EMPTY_NAME)) {
						this.displayChildMessage("Please save changes to new Part \"" + pCheck.getPartName() + "\" before trying to add another.");
						return;
					}
				}
				//make a new part instance
				Part pAdd = new Part();	
				partList.addPartToList(pAdd);
				
				for(MDIChild d : openViews) {
					if(d instanceof PartListView) {
						((PartListView) d).getMyList().addPartToList(pAdd);
					}
				}
				
				PartDetailView tAdd = new PartDetailView(pAdd.getPartName(), pAdd, this);
				openMDIChild(tAdd);
				
				break;
				
				case DELETE_PART:
					if(policy.canUserAccessFunction(name, "part.delete") == false ) {
						displayChildMessage("You do not have permission to delete a part");
						break;
					}
					//remove the model from the model list
					Part pDelete = ((PartListView) caller).getSelectedPart();
					partList.removePartFromList(pDelete);
					
					//close all details that are based on this object
					//NOTE: closing the detail view changes the openViews collection so traverse it in reverse
					for(int j = openViews.size() - 1; j >= 0; j--) {
						MDIChild d = openViews.get(j);
						if(d instanceof PartDetailView) {
							//if detail view is showing the deleted object then close the detail view without asking
							if(((PartDetailView) d).getMyPart().getId() == pDelete.getId())
								d.closeFrame();
						}
					}
				
					try {
						pDelete.delete();
						this.displayChildMessage("Part deleted.");
					} catch (GatewayException e) {
						System.err.println(e.getMessage());
						this.displayChildMessage("Error trying to delete part.");
					}
					break;
					
				case SHOW_LIST_INVENTORY :
					inventoryList.loadFromGateway();
					
					InventoryListView nList = new InventoryListView("Inventory List", new InventoryListController(inventoryList), this);
					openMDIChild(nList);
					break;
				
				case SHOW_DETAIL_INVENTORY :
					Inventory nList1 = ((InventoryListView) caller).getSelectedInventory();
					InventoryDetailView dList = new InventoryDetailView("" + nList1.getId(), nList1, this);
					openMDIChild(dList);
					break;
					
				case ADD_INVENTORY:
					for(Inventory iCheck : inventoryList.getList()) {
						if(iCheck.getId() == Inventory.INVALID_ID) {
							this.displayChildMessage("Please save changes to new Inventory item \"" + iCheck.getId() + "\" before trying to add another.");
							return;
						}
					}
					
					Inventory iAdd = new Inventory();
					
					inventoryList.addInventoryToList(iAdd);
					inventoryList.addToNewRecords(iAdd);
					
					InventoryDetailView nInventoryAdd = new InventoryDetailView("" + iAdd.getId(), iAdd, this);
					openMDIChild(nInventoryAdd);
					break;
					
				case DELETE_INVENTORY:
					Inventory iDelete = ((InventoryListView) caller).getSelectedInventory();
					inventoryList.removeInventoryFromList(iDelete);
					
					for(int i = openViews.size() - 1; i >= 0; i--) {
						MDIChild c = openViews.get(i);
						if(c instanceof InventoryDetailView) {
							if(((InventoryDetailView) c).getMyInventory().getId() == iDelete.getId())
								c.closeFrame();
						}
					}
					
					try {
						iDelete.delete();
						this.displayChildMessage("Inventory item deleted");
						
					} catch (GatewayException e) {
						System.err.println(e.getMessage());
						this.displayChildMessage("Error trying to delete inventory item");
					}
					break;
					
				case LOGIN_AS_BOB_ROBERTS :
					User user1 = new User("bob", "abcdef", "BobUser");
					
					try {
						id = authenticate.login(user1.getLogin(), user1.getPasswordHash());
						displayChildMessage("Successfully Logged in as Bob");
						getUserName(user1.getLogin());
					} catch (SecurityException e1) {
						displayChildMessage("Unable to login");
					}
					break;
					
				case LOGIN_AS_SUE_WILLIAMS :
					User user2 = new User("sue","decaef", "SueUser");
					
					try {
						id = authenticate.login(user2.getLogin(), user2.getPasswordHash());
						displayChildMessage("Successfully Logged in as Sue");
						getUserName(user2.getLogin());
					} catch (SecurityException e1) {
						displayChildMessage("Unable to login");
					}
					break;
					
				case LOGIN_AS_RAGNAR_JONES :
					User user3 = new User("ragnar","bcdefd", "RagnarUser");
					
					try {
						id = authenticate.login(user3.getLogin(), user3.getPasswordHash());
						displayChildMessage("Successfully Logged in as Ragnar");
						getUserName(user3.getLogin());
					} catch (SecurityException e1) {
						displayChildMessage("Unable to login");
					}
					break;
					
				case LOGIN_AS_GRACE_KELLY :
					User user4 = new User("grace", "bbbbb","GraceNonUser");
					
					try {
						id = authenticate.login(user4.getLogin(), user4.getPasswordHash());
						displayChildMessage("Successfully Logged in as Grace");
						getUserName(user4.getLogin());
					} catch (SecurityException e1) {
						displayChildMessage("Unable to login");
					}
					break;
					
					
				case WAREHOUSE_INVENTORY_REPORT_PDF :
					try {
						
						String PDFfileName = "WarehouseReport.pdf";
								
						WarehouseInventoryReportPDF reportPDF = new WarehouseInventoryReportPDF(new ReportGatewayMySQL());
						reportPDF.generateReport();
						reportPDF.outputReportToFile(PDFfileName);
						reportPDF.close();
					} catch (GatewayException | ReportException e) {
						this.displayChildMessage(e.getMessage());
						return;
					} 

				case WAREHOUSE_INVENTORY_REPORT_EXCEL :
					try {
						
						String ExcelFileName = "WarehouseReport.xls";
						WarehouseInventoryReportExcel reportExcel = new WarehouseInventoryReportExcel(new ReportGatewayMySQL());
						reportExcel.generateReport();
						reportExcel.outputReportToFile(ExcelFileName);
						reportExcel.close();
					} catch (GatewayException | ReportException e) {
						this.displayChildMessage(e.getMessage());
						return;
					} 
				
		}
					
	}
	
	public WarehouseList getWarehouseList() {
		return warehouseList;
	}
	
	public PartList getPartList() {
		return partList;
	}
	
	public InventoryItemList getInventoryList() {
		return inventoryList;
	}
	
	public void getUserName(String Name) {
		this.name = Name;
	}
	
	
	public void cleanup() {
		//System.out.println("     *** In MDIParent.cleanup");

		//iterate through all child frames
		JInternalFrame [] children = desktop.getAllFrames();
		for(int i = children.length - 1; i >= 0; i--) {
			if(children[i] instanceof MDIChildFrame) {
				MDIChildFrame cf = (MDIChildFrame) children[i];
				//tell child frame to cleanup which then tells its child view to clean up
				cf.cleanup();
			}
		}
		//close any model table gateways
		warehouseList.getGateway().close();
		partList.getGateway().close();
		inventoryList.getGateway().close();
	}
		
	
	
	public JInternalFrame openMDIChild(MDIChild child) {
		//first, if child's class is single open only and already open,
		//then restore and show that child
		if(child.isSingleOpenOnly()) {
			for(MDIChild testChild : openViews) {
				if(testChild.getClass().getSimpleName().equals(child.getClass().getSimpleName())) {
					try {
						testChild.restoreWindowState();
					} catch(PropertyVetoException e) {
						e.printStackTrace();
					}
					JInternalFrame c = (JInternalFrame) testChild.getMDIChildFrame();
					return c;
				}
			}
		}
		
		//create then new frame
		MDIChildFrame frame = new MDIChildFrame(child.getTitle(), true, true, true, true, child);
		
		//pack works but the child panels need to use setPreferredSize to tell pack how much space they need
		//otherwise, MDI children default to a standard size that I find too small
		frame.pack();
		frame.setLocation(newFrameX, newFrameY);
		
		//tile its position
		newFrameX = (newFrameX + 10) % desktop.getWidth(); 
		newFrameY = (newFrameY + 10) % desktop.getHeight(); 
		desktop.add(frame);
		//show it
		frame.setVisible(true);
		
		//add child to openViews
		openViews.add(child);
		
		return frame;
	}
	
	//display a child's message in a dialog centered on MDI frame
	public void displayChildMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}
	
	/**
	 * When MDIChild closes, we need to unregister it from the list of open views
	 * @param child
	 */
	public void removeFromOpenViews(MDIChild child) {
		openViews.remove(child);
	}
	
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * This method is called when we select Quit on menu OR click the close button on the window title bar
	 * Check each MDIChild to see if its changed and we cannot close. If MDIChild says we can't close then abort close. 
	 * @param e
	 */
	
	public void windowClosing(WindowEvent e) {
		//ask each MDIChild if it is ok to close 
		//System.out.println("     *** In MDIParent.windowClosing");

		//iterate through all child frames
		JInternalFrame [] children = desktop.getAllFrames();
		for(int i = children.length - 1; i >= 0; i--) {
			if(children[i] instanceof MDIChildFrame) {
				MDIChildFrame cf = (MDIChildFrame) children[i];
				if(!cf.okToClose())
					return;
			}
		}
		
		//if we get here then ok to close MDI parent (also closes all child frames)
		this.dispose();
	}

	
	public void windowClosed(WindowEvent e) {
	}

	
	public void windowIconified(WindowEvent e) {
	}

	
	public void windowDeiconified(WindowEvent e) {
	}

	
	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}
	
	
	
	
}
