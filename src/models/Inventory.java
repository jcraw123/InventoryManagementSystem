package models;

import java.util.Observable;
import database.InventoryTableGateway;
import database.GatewayException; 
import java.util.Observer;

import controller.MDIParent;


public class Inventory extends Observable {
	
	public static final String ERRORMSG_INVALID_ID = "Invalid id!";
	public static final String ERRORMSG_INVALID_WAREHOUSE_ID = "Invalid warehouse id!";
	public static final String ERRORMSG_INVALID_PART_ID = "Invalid part id!";
	public static final String ERRORMSG_INVALID_QUANTITY = "Invalid quantity!";
	public static final int INVALID_ID = 0;
	public static final String DEFAULT_EMPTY_NAME = "Unknown";
	
	
	private long id, part_id, warehouse_id;
	private double quantity; 
	private InventoryTableGateway gateway; 
	
	
	
	public Inventory() {
		id = INVALID_ID;
		part_id = warehouse_id = 0L;
		quantity = 0.0;
		
	}
	
	public Inventory(long id, long warehouse_id, long part_id, double quantity) {
		this();
		
		if(id < 1)
			  throw new IllegalArgumentException(ERRORMSG_INVALID_ID);
		if(!validWarehouseId(warehouse_id))
			throw new IllegalArgumentException(ERRORMSG_INVALID_WAREHOUSE_ID);
		if(!validPartId(part_id))
			throw new IllegalArgumentException(ERRORMSG_INVALID_PART_ID);
		if(!validQuantity(quantity))
			throw new IllegalArgumentException(ERRORMSG_INVALID_QUANTITY);
		
		setId(id);
		warehouse_id = warehouse_id;
		part_id = part_id;
		quantity = quantity; 
	}
	
	
	
	public void finishUpdate() throws GatewayException {
		Inventory orig = null;
		
		try {
			if(this.getId() == 0) {
				this.setId(gateway.insertInventory(this));
			} else {
				orig = gateway.fetchInventory(this.getId());
				gateway.saveInventory(this);
				
			}
			notifyObservers();
			
		} catch (GatewayException e) {
			if (orig != null) {
				this.setQuantity(orig.getQuantity());
			}
			throw new GatewayException("Error trying to save the Inventory object");
			
		}
	}
	
	public void delete() throws GatewayException {
		if(this.getId() == 0)
			return;
		try {
			gateway.deleteInventory(this.getId());
			
		} catch (GatewayException e) {
			throw new GatewayException(e.getMessage());
		}
	}
	
	public WarehouseList getWarehouseList(MDIParent m) {
		return m.getWarehouseList();
	}
	
	public PartList getPartList(MDIParent m) {
		return m.getPartList();
	}
	
	public long getId() {
		return id; 
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public Long getWarehouseId() {
		return this.warehouse_id;
	}
	
	public void setWarehouseId(long warehouseId) {
		this.warehouse_id = warehouseId;
		setChanged();
	}
	
	public boolean validWarehouseId(Long warehouseId) {
		if(warehouseId == null)
			return false;
		return (warehouseId < 1)? false:true;
	}
	
	public Long getPartId() {
		return part_id;
	}
	
	public void setPartId(Long partId) {
		this.part_id = partId;
		setChanged();
	}
	
	public boolean validPartId(Long partId) {
		if(partId == null)
			return false;
		return (partId < 1)? false:true; 
	}
	
	public double getQuantity() {
		return quantity; 
	}
	
	public void setQuantity(double quantity) {
		this.quantity = quantity;
		setChanged();
	}
	
	public boolean validQuantity(double quantity) {
		return (quantity >= 0)? true: false; 
	}
	
	public InventoryTableGateway getGateway() {
		return gateway;
	}
	
	public void setGateway(InventoryTableGateway gateway) {
		this.gateway = gateway;
	}

	
}
