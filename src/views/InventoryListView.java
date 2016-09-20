package views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import controller.InventoryListController;
import controller.MDIChild;
import controller.MDIParent;
import controller.MenuCommands;
import models.Inventory;
import models.TransferableInventory;


public class InventoryListView extends MDIChild {
	
	  private JList<Inventory> listInventory;
	    private InventoryListController myList;
	        private Inventory selectedModel;
	        
	        
	        public InventoryListView(String title, InventoryListController list, MDIParent m) {
	        super(title, m);
	        
	        list.setMyListView(this);
	        myList = list;
	        
	        listInventory = new JList<Inventory>(myList);
	        
	        listInventory.setDragEnabled(true);
	        listInventory.setTransferHandler(new InventoryDragTransferHandler());
	        
	        listInventory.setCellRenderer(new InventoryListCellRenderer());
	        listInventory.setPreferredSize(new Dimension(400, 450));
	        
	        listInventory.addMouseListener(new MouseAdapter() {
	            public void mouseClicked(MouseEvent evt) {
	                //if double-click then get index and open new detail view with record at that index
	                if(evt.getClickCount() == 2) {
	                    int index = listInventory.locationToIndex(evt.getPoint());
	                    selectedModel = myList.getElementAt(index);
	                    
	                    //open a new detail view
	                    openDetailView();
	                }
	            }
	        });
	        
	        //add to content pane
	        this.add(new JScrollPane(listInventory));
	   this.setPreferredSize(new Dimension(640, 500));

	        
	        JPanel panel = new JPanel();
	        panel.setLayout(new FlowLayout());
	        JButton button = new JButton("Delete Inventory Item");
	        button.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                deleteInventory();
	            }
	        });
	        panel.add(button);
	        
	        this.add(panel, BorderLayout.SOUTH);

	    }

	    /**
	     * Tells MDI parent to delete the selected inventoy. if none selected then ignore
	     */
	    private void deleteInventory() {
	        int idx = listInventory.getSelectedIndex();
	        if(idx < 0)
	            return;
	        if(idx >= myList.getSize())
	            return;
	        Inventory n = myList.getElementAt(idx);
	        if(n == null)
	            return;
	        selectedModel = n;
	        
	        //ask user to confirm deletion
	        String [] options = {"Yes", "No"};
	        if(JOptionPane.showOptionDialog(myFrame
	                , "Do you really want to delete " + n.getId() + " ?"
	                , "Confirm Deletion"
	                , JOptionPane.YES_NO_OPTION
	                , JOptionPane.QUESTION_MESSAGE
	                , null
	                , options
	                , options[1]) == JOptionPane.NO_OPTION) {
	            return;
	        }

	        //tell the controller to do the deletion
	        parent.doCommand(MenuCommands.DELETE_INVENTORY, this);
	        
	    }
	    
	    /**
	     * Opens a InventoryDetailView with the given Inventory object
	     */
	    public void openDetailView() {
	        parent.doCommand(MenuCommands.SHOW_DETAIL_INVENTORY, this);
	    }
	        public Inventory getSelectedInventory() {
	        return selectedModel;
	    }

	        protected void cleanup() {
	        //let superclass do its thing
	        super.cleanup();
	                
	        //unregister from observables
	        myList.unregisterAsObserver();
	    }

	    /**
	     * Accessors for InventoryListController
	     * @return
	     */
	    public InventoryListController getMyList() {
	        return myList;
	    }

	    public void setMyList(InventoryListController myList) {
	        this.myList = myList;
	    }

	    public JList<Inventory> getListInventorys() {
	        return listInventory;
	    }

	    public void setListInventorys(JList<Inventory> listWarehouse) {
	        this.listInventory = listWarehouse;
	    }

	    public Inventory getSelectedModel() {
	        return selectedModel;
	    }

	    public void setSelectedModel(Inventory selectedModel) {
	        this.selectedModel = selectedModel;
	    }
	    
	    private class InventoryDragTransferHandler extends TransferHandler {
	        private int index = 0;

	        public int getSourceActions(JComponent comp) {
	            return COPY_OR_MOVE;
	        }
	                
	        public Transferable createTransferable(JComponent comp) {
	            index = listInventory.getSelectedIndex();
	            if (index < 0 || index >= myList.getSize()) {
	                return null;
	            }
	            return new TransferableInventory( (Inventory) listInventory.getSelectedValue());
	        }
	        
	        public void exportDone(JComponent comp, Transferable trans, int action) {
	            if (action != MOVE) {
	                return;
	            }
	        }
	    }

	

}
