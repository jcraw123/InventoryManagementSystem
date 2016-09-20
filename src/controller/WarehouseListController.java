package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractListModel;
import javax.swing.event.ListDataListener;


import models.Warehouse;
import models.WarehouseList;

public class WarehouseListController extends AbstractListModel<Warehouse> implements Observer {

//private static List<Warehouse> myList;
	private WarehouseList myList;
	/**
	 * GUI container housing this object's list controller's JList
	 * Allows this controller to tell the view to repaint() if models in list change
	 */
	//private static MDIChild myListView;
	private MDIChild myListView;
	
	public WarehouseListController(WarehouseList wl) {
		super();
		//myList = wl.getList();
		myList = wl;
		
		//registerAsObserver();
		wl.addObserver(this);
	}
	
	@Override
	public int getSize() {
		//return myList.size();
		return myList.getList().size();
	}

	@Override
	public Warehouse getElementAt(int index) {
		if(index > getSize())
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
	
	public void removeWarehouseFromList(Warehouse w) {
		w.deleteObserver(this);
		fireContentsChanged(this, 0, getSize());
	}

	/**
	 * iterate through model list and register as observer with all observables
	 */
	

	/**
	 * iterate through model list and UNregister as observer with all observables
	 */
	public void unregisterAsObserver() {
		//unregister with the observable models
	//	for(Warehouse w: myList)
			//w.deleteObserver(this);
		myList.deleteObserver(this);
	}

	//model tells this observer that it has changed
	//so tell JList's view to repaint itself now
	@Override
	public void update(Observable o, Object arg) {
		fireContentsChanged(this, 0, getSize());
		myListView.repaint();
	}
	
	public void addWarehouseToList(Warehouse w) {
		w.addObserver(this);
		fireContentsChanged(this, 0, getSize());
	}
	
}
