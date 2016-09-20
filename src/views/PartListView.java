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

import controller.PartListController;
import controller.MDIChild;
import controller.MDIParent;
import controller.MenuCommands;
import models.Part;

public class PartListView extends MDIChild {
	/**
	 * GUI instance variables
	 */
	private JList<Part> listParts;
	private PartListController myList;
	//saves reference to last selected model in JList
	//parent asks for this when opening a detail view
	private Part selectedModel;
	
	/**
	 * Constructor
	 * @param title Window title
	 * @param list PartListController contains collection of Part objects
	 * @param mdiParent MasterFrame MDI parent window reference
	 */
	public PartListView(String title, PartListController list, MDIParent m) {
		super(title, m);
		
		//set self to list's view (allows ListModel to tell this view to repaint when models change)
		list.setMyListView(this);
		
		//prep list view
		myList = list;
		listParts = new JList<Part>(myList);
		
		
		//use our custom cell renderer instead of default
		listParts.setCellRenderer(new PartListCellRenderer());
		listParts.setPreferredSize(new Dimension(400, 450));
		
		//add event handler for double click
		listParts.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				//if double-click then get index and open new detail view with record at that index
		        if(evt.getClickCount() == 2) {
		        	int index = listParts.locationToIndex(evt.getPoint());
		        	selectedModel = myList.getElementAt(index);
		        	
		        	//open a new detail view
		        	openDetailView();
		        }
		    }
		});
		
		//add to content pane
		this.add(new JScrollPane(listParts));
		this.setPreferredSize(new Dimension(640, 500));

		
		//add a Delete button to delete selected part
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JButton button = new JButton("Delete Part");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (listParts.isSelectionEmpty())
					parent.displayChildMessage("Please select part first");
				else
				deletePart();
			}
		});
		panel.add(button);
		this.add(panel, BorderLayout.SOUTH);

	}

	/**
	 * Tells MDI parent to delete the selected part. if none selected then ignore
	 */
	private void deletePart() {
		//get the selected model and set as selectedPart instance variable
		//mdi parent will ask for this when handling delete part call
		int idx = listParts.getSelectedIndex();
		if(idx < 0)
			return;
		//idx COULD end up > list size so make sure idx is < list size
		if(idx >= myList.getSize())
			return;
		Part p = myList.getElementAt(idx);
		if(p == null)
			return;
		selectedModel = p;
		
		//ask user to confirm deletion
		String [] options = {"Yes", "No"};
		if(JOptionPane.showOptionDialog(myFrame
				, "Do you really want to delete " + p.getPartName() + " ?"
				, "Confirm Deletion"
				, JOptionPane.YES_NO_OPTION
			    , JOptionPane.QUESTION_MESSAGE
			    , null
			    , options
				, options[1]) == JOptionPane.NO_OPTION) {
			return;
		}

		//tell the controller to do the deletion
		parent.doCommand(MenuCommands.DELETE_PART, this);
		
	}
	
	/**
	 * Opens a PartDetailView with the given part object
	 */
	public void openDetailView() {
		parent.doCommand(MenuCommands.SHOW_DETAIL_PART, this);
	}
	
	/**
	 * returns selected part in list
	 * @return
	 */
	public Part getSelectedPart() {
		return selectedModel;
	}

	/**
	 * Subclass-specific cleanup
	 */
	
	protected void cleanup() {
		//let superclass do its thing
		super.cleanup();
				
		//unregister from observables
		myList.unregisterAsObserver();
	}

	/**
	 * Accessors for PartListController
	 * @return
	 */
	public PartListController getMyList() {
		return myList;
	}

	public void setMyList(PartListController myList) {
		this.myList = myList;
	}

	public JList<Part> getListParts() {
		return listParts;
	}

	
	public Part getSelectedModel() {
		return selectedModel;
	}

	public void setSelectedModel(Part selectedModel) {
		this.selectedModel = selectedModel;
	}
}
