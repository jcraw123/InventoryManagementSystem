package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import database.GatewayException;
import database.InventoryTableGateway;

import java.util.HashMap;

public class InventoryItemList extends Observable implements Observer {
	private List<Inventory> myList;
	
	private HashMap<Long, Inventory> myIdMap;
	private ArrayList<Inventory> newRecords;
	private InventoryTableGateway gateway; 
	
	private boolean dontNotify;
	
	public InventoryItemList() {
		myIdMap = new HashMap<Long, Inventory>();
		myList = new ArrayList<Inventory>();
		newRecords = new ArrayList<Inventory>();
		dontNotify = false; 
	}
	
	public void loadFromGateway() {
		List<Inventory> inventory = null;
		
		try {
			inventory = gateway.fetchInventory();
			
		} catch (GatewayException e) {
			e.printStackTrace();
			return;
		}
		
		dontNotify = true;
		
		for(int i = myList.size() - 1; i >= 0; i--) {
			Inventory n = myList.get(i);
			boolean removeRecord = true;
			
			if(n.getId() == Inventory.INVALID_ID) {
				removeRecord = false; 
			} else { 
				for(Inventory nCheck : inventory) {
					if(nCheck.getId() == n.getId()) {
						removeRecord = false;
						break;
					}
				}
			}
			
			if(removeRecord)
				removeInventoryFromList(n);
		}
			
			for(Inventory n : inventory) {
				if(!myIdMap.containsKey(n.getId())) {
					addInventoryToList(n);
				}
			}
			
			this.notifyObservers();
			dontNotify = false; 
		}
		
		public boolean duplicate(Inventory inv) {
			for(int i = myList.size() - 1; i >= 0; i--) {
				Inventory n = myList.get(i);
				if((n.getWarehouseId() == inv.getWarehouseId()) && n.getPartId() == inv.getPartId() &&
						n.getId() != inv.getId())
					return true;
			}
			return false; 
		}
		
		public boolean existingWarehousePart(Long warehouseId, Long partId) {
			if(warehouseId > 0) {
				for(int i = myList.size() - 1; i >= 0; i--) {
					Inventory n = myList.get(i);
					if(n.getWarehouseId() == warehouseId)
						return true;
				}
			} else {
				for(int i = myList.size() - 1; i >= 0; i--) {
					Inventory n = myList.get(i);
					if(n.getPartId() == partId)
						return true;
				}
			}
			return false;
		}
		
		public Inventory searchById(long id) {
			if(myIdMap.containsKey(new Long(id))) return myIdMap.get(new Long(id));
			return null;
		}
		
		public void addInventoryToList(Inventory n) {
			myList.add(n);
			n.setGateway(this.gateway);
			n.addObserver(this);
			
			myIdMap.put(n.getId(), n);
			this.setChanged();
			if(!dontNotify)
				this.notifyObservers();
		}
		
		public Inventory removeInventoryFromList(Inventory n) {
			if(myList.contains(n)) {
				myList.remove(n);
				myIdMap.remove(n.getId());
				
				this.setChanged();
				if(!dontNotify)
					this.notifyObservers();
				
				return n;
			}
			return null;
		}
		
		public double getTotalWarehouseQuantity(Inventory inv) {
			double totalQuantity = 0.0;
			for(int i = myList.size() - 1; i >= 0; i--) {
				Inventory n = myList.get(i);
				if(n.getWarehouseId() == inv.getWarehouseId() && n.getId() != inv.getId()) {
					totalQuantity = n.getQuantity();
				}
			}
			return totalQuantity; 
		}
		
		public double remainingWarehouseCapacity(double capacity, Inventory inv) {
			return capacity - (getTotalWarehouseQuantity(inv) + 
					inv.getQuantity());
		}
		
		public List<Inventory> getList() {
			return myList;
		}
		
		public void setList(List<Inventory> myList) {
			this.myList = myList;
		}
		
		public InventoryTableGateway getGateway() {
			return gateway;
		}
		
		public void setGateway(InventoryTableGateway gateway) {
			this.gateway = gateway; 
		}
		
		public void addToNewRecords(Inventory n) {
			newRecords.add(n);
		}
		
		public void update(Observable o, Object arg) {
			Inventory n = (Inventory) o;
			if(newRecords.contains(n)) {
				myIdMap.remove(Inventory.INVALID_ID);
				myIdMap.put(n.getId(), n);
				newRecords.remove(n);
			}
			this.setChanged();
			notifyObservers();
		}
}
