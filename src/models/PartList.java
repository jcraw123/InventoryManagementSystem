package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import database.GatewayException;
import database.PartTableGateway;

public class PartList extends Observable implements Observer {
	
	private List<Part>myList; 
	
	private PartTableGateway gateway;
	
	private HashMap<Long, Part> myIdMap;
	
	private ArrayList<Part> newRecords; 
	
	private boolean dontNotify; 
	
	public PartList() {
		myList = new ArrayList<Part>();
		myIdMap = new HashMap<Long, Part>();
		newRecords = new ArrayList<Part>();
		dontNotify = false; 
	}
	
	public void loadFromGateway() {
		//clear the contents from the existing list
		//for(int i = myList.size() - 1; i >= 0; i--) {
			//Part p = myList.get(i);
			//p.deleteObservers();
			//removePartFromList(p);
		//}
		
		List<Part> parts = null;
		try {
			parts = gateway.fetchParts();
		} catch (GatewayException e) {
			//TODO: handle exception here
			return;
		}
		
		dontNotify = true; 
		
		for(int i = myList.size() - 1; i >= 0; i--) {
			Part p = myList.get(i);
			boolean removeRecord = true;
			
			if(p.getId() == Part.INVALID_ID) {
				removeRecord = false;
			} else {
				for(Part pCheck : parts) {
					if(pCheck.getId() == p.getId()) {
						removeRecord = false;
						break;
					}
				}
			}
			
			if(removeRecord)
				removePartFromList(p);
		}
		for(Part p : parts) { 
			if(!myIdMap.containsKey(p.getId())) {
				addPartToList(p);
			}
		}
		this.notifyObservers();
		dontNotify = false;
			
	}
	
	public HashMap< Long, String> getPartList(){
        HashMap< Long, String> partNameList = new HashMap< Long, String>();
        
        for(int i = myList.size() - 1; i >= 0; i--) {
            Part p = myList.get(i);
            partNameList.put(p.getId(), p.getPartNumber());
        }    
        
        return partNameList;
    }
    
    public Part searchById(long id) {
        if(myIdMap.containsKey(new Long(id)))
            return myIdMap.get(new Long(id));
        return null;
    }
	
	
	 //Add a warehouse object to the list's collection
	 
	public void addPartToList(Part p) {
		myList.add(p);
		p.setGateway(this.gateway);
		p.addObserver(this);
		myIdMap.put(p.getId(), p);
		this.setChanged();
		if(!dontNotify)
			this.notifyObservers();
	}
	
	
	 	
	public Part removePartFromList(Part p) {
		if(myList.contains(p)) {
			myList.remove(p);

			this.setChanged();
		//	if(!dontNotify)
				this.notifyObservers();
			return p;
		}
		return null;
	}
	
	public List<Part> getList() {
		return myList;
	}

	public void setList(List<Part> myList) {
		this.myList = myList;
	}
	
	public PartTableGateway getGateway() {
		return gateway;
	}
	
	public void setGateway(PartTableGateway gateway) {
		this.gateway = gateway;
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
