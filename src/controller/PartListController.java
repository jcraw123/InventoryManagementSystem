package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer; 

import javax.swing.AbstractListModel;
import javax.swing.event.ListDataListener;

import models.Part;
import models.PartList;

/*
 * provides the PartLisrt data to be used by PartsListView JLists
 * 
 */
public class PartListController extends AbstractListModel<Part>  implements Observer {
	
	/*
	 * Collection for storing Parts object references
	 */
	
	//private static List<Part> myList;
	private PartList myList;
	
	
	/*
	 * GUI container housing this object's list controller's JList
	 * Allows this controller to tell the view to repaint() if models in list change
	 * 
	 */
	
	private static MDIChild myListView;
	
	public PartListController(PartList pl) { 
		super();
		//myList = pl.getList();
		myList = pl;
		
		/*
		 * register as observer to parts list
		 */
		
		//registerAsObserver();
		pl.addObserver(this);
	}
	
	public int getSize() {
		//return myList.size();
		return myList.getList().size();
	}
	
	public Part getElementAt(int index) {
		if(index >= getSize())
			throw new IndexOutOfBoundsException("Index " + index + " is out of list bounds!");
		//return myList.get(index);
		return myList.getList().get(index);
	}
	
	public MDIChild getMyListView() {
		return myListView;
	}
	
	public void setMyListView(MDIChild myListView) {
		this.myListView = myListView;
		
	}
	
	
	
	public void unregisterAsObserver() {
		//for(Part p: myList)
			//p.deleteObserver(this);
		myList.deleteObserver(this);
			
		
	}
	
	/*
	 * model tells this observer that is has changed so tell JList's view to repaint itself now
	 * 
	 */
	
	public void update(Observable o, Object arg) {
		fireContentsChanged(this, 0, getSize());
		myListView.repaint();
	}
	
	public void addPartToList(Part p) {
		p.addObserver(this);
		fireContentsChanged(this, 0, getSize());
	}
	
	
	
	
	
}
