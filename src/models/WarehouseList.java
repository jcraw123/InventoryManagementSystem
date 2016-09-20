package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import database.GatewayException;
import database.WarehouseTableGateway;

public class WarehouseList extends Observable implements Observer {
	
	private List<Warehouse>myList; 
	
	private WarehouseTableGateway gateway;
	
	private HashMap<Long, Warehouse> myIdMap;
	
	private ArrayList<Warehouse> newRecords;
	
	private boolean dontNotify;
	
	
	public WarehouseList() {
		myList = new ArrayList<Warehouse>();
		myIdMap = new HashMap<Long, Warehouse>();
		newRecords = new ArrayList<Warehouse>();
		dontNotify = false;
	}
	
	public void loadFromGateway() {
		
		
		List<Warehouse> warehouses = null;
		try {
			warehouses = gateway.fetchWarehouses();
		} catch (GatewayException e) {
			return;
		}
		
		dontNotify = true; 
		
		for (int i = myList.size() - 1; i >= 0; i--) {
			Warehouse w = myList.get(i);
			boolean removeRecord = true;
			if(w.getId () == Warehouse.INVALID_ID) { 
				removeRecord = false;
			} else {
				for(Warehouse wCheck : warehouses) {
					if(wCheck.getId() == w.getId()) {
						removeRecord = false; 
						break;
					}
				}
			}
			if(removeRecord)
				removeWarehouseFromList(w);
		}
		
		for(Warehouse w : warehouses)
			if(!myIdMap.containsKey(w.getId())) {
			addWarehouseToList(w);
			}
		this.notifyObservers();
		dontNotify = false;
	}
	
	
	public void addToNewRecords (Warehouse w) {
		newRecords.add(w);
	}
	
	
	public HashMap< Long, String> getWarehouseList(){
        HashMap< Long, String> warehouseNameList = new HashMap< Long, String>();
        
        for(int i = myList.size() - 1; i >= 0; i--) {
            Warehouse w = myList.get(i);
            warehouseNameList.put(w.getId(), w.getWarehouseName());
        }    
        
        return warehouseNameList;
    }
    
    public HashMap< Long, Double> getWarehouseCapacity(){
        HashMap< Long, Double> warehouseNameList = new HashMap< Long, Double>();
        
        for(int i = myList.size() - 1; i >= 0; i--) {
            Warehouse w = myList.get(i);
            warehouseNameList.put(w.getId(), w.getStorageCapacity());
        }    
        
        return warehouseNameList;
    }
    
    public Warehouse searchById(long id) {
        //check the identity map
        if(myIdMap.containsKey(new Long(id)))
            return myIdMap.get(new Long(id));
        return null;
    }
        
      
	
	 //Add a warehouse object to the list's collection
	 
	public void addWarehouseToList(Warehouse w) {
		myList.add(w);
		w.setGateway(this.gateway);
		w.addObserver(this);
		myIdMap.put(w.getId(), w);
		this.setChanged();
		if(!dontNotify)
			this.notifyObservers();
	}
	
	
	 //remove a warehouse from the list 
	
	public Warehouse removeWarehouseFromList(Warehouse w) {
		if(myList.contains(w)) {
			myList.remove(w);
			myIdMap.remove(w.getId());
			this.setChanged();
			if(!dontNotify)
				this.notifyObservers();
			return w;
		}
		return null;
	}
	
	public List<Warehouse> getList() {
		return myList;
	}

	public void setList(List<Warehouse> myList) {
		this.myList = myList;
	}
	
	public WarehouseTableGateway getGateway() {
		return gateway;
	}
	
	public void setGateway(WarehouseTableGateway gateway) {
		this.gateway = gateway;
	}
	
	public void update(Observable o, Object arg) {
		Warehouse w = (Warehouse) o;
		if(newRecords.contains(w)) {
			myIdMap.remove(Warehouse.INVALID_ID);
			myIdMap.put(w.getId(), w);
			newRecords.remove(w);
		}
		
		this.setChanged();
		notifyObservers();
		
	}
}
