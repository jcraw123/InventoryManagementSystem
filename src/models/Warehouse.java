package models;

import java.util.Observable;
import java.util.UUID;

import database.GatewayException;
import database.WarehouseTableGateway;

	
	public class Warehouse extends Observable {
		
		/*
		 * Validation error messages
		 * 
		 */
		public static final String ERRORMSG_INVALID_ID = "Invalid id!";
		public static final String ERRORMSG_INVALID_NAME = "Invalid name!";
		public static final String ERRORMSG_INVALID_ADDRESS = "Invalid Address!";
		public static final String ERRORMSG_INVALID_CITY = "Invalid city!";
		public static final String ERRORMSG_INVALID_STATE = "Invalid state!";
		public static final String ERRORMSG_INVALID_ZIPCODE = "Invalid zipcode!";
		public static final String ERRORMSG_INVALID_CAPACITY = "Invalid Capcity";
		public static final String ERRORMSG_WAREHOUSE_ALREADY_EXISTS = "A warehouse with that name already exists!";
		public static final String DEFAULT_EMPTY_NAME = "Unknown";
		
		public static final int INVALID_ID = 0;
		public static final double INVALID_CAPACITY = 0.0;
		
		
		
		
		
		//private static long nextId = 1;
		
		
		 //Unique id Warehouse record, automatically generated 
		
		private long id;
		
		
		private String warehouseName, warehouseStreetAddress, warehouseCity, warehouseState, warehouseZipCode;
		
		
		private double wStorageCapacity;
		
		private WarehouseTableGateway gateway; 
		
		
		
		public Warehouse() {
			
			id = INVALID_ID;
			warehouseName = "";
			warehouseStreetAddress = "";
			warehouseCity = "";
			warehouseState = "";
			warehouseZipCode = "";
			wStorageCapacity = 0.0;
		}
		
		
		public Warehouse (Long id, String name, String address, String city, String state, String zipCode, double storageCapacity) {
			this();
			if(!validName(name))
				throw new IllegalArgumentException(ERRORMSG_INVALID_NAME);
			if(!validAddress(address))
				throw new IllegalArgumentException(ERRORMSG_INVALID_ADDRESS);
			if(!validCity(city))
				throw new IllegalArgumentException(ERRORMSG_INVALID_CITY);
			if(!validState(state))
				throw new IllegalArgumentException(ERRORMSG_INVALID_STATE);
			if(!validZipCode(zipCode))
				throw new IllegalArgumentException(ERRORMSG_INVALID_ZIPCODE);
			if(!validCapacity(storageCapacity))
				throw new IllegalArgumentException(ERRORMSG_INVALID_CAPACITY);
			
			if(id < 1)
				throw new IllegalArgumentException(ERRORMSG_INVALID_ID);
			setId(id);
			
			warehouseName = name;
			warehouseStreetAddress = address;
			warehouseCity = city;
			warehouseState = state;
			warehouseZipCode = zipCode;
			wStorageCapacity = storageCapacity;
			
			
		}
		
	
	
		/**
		 * Returns warehouse's unique id
		 * @return
		 */
		public long getId() {
			return id;
		}
		
		public void setId(long id) {
			this.id = id;
		}
		
		

		
		public String getWarehouseName() {
			return warehouseName;
		}

		
		public static boolean validName(String name) {
			if(name == null)
				return false;
			if(name.length() > 255)
				return false;
			return true;
		}
		
		
		public void setWarehouseName(String warehouseName) {
			if(!validName(warehouseName))
				throw new IllegalArgumentException(ERRORMSG_INVALID_NAME);
			this.warehouseName = warehouseName;
			//get ready to notify observers (notify is called in finishUpdate())
			setChanged();
		}

		
		public String getAddress() {
			return warehouseStreetAddress;
		}
		
		
		public static boolean validAddress(String address) {
			if(address == null)
				return false;
			if(address.length() > 255)
				return false;
			return true;
		}

		
		public void setAddress(String warehouseStreetAddress) {
			if(!validAddress(warehouseStreetAddress))
				throw new IllegalArgumentException(ERRORMSG_INVALID_ADDRESS);
			this.warehouseStreetAddress = warehouseStreetAddress;
			//get ready to notify observers (notify is called in finishUpdate())
			setChanged();
		}

		
		public String getZipCode() {
			return warehouseZipCode;
		}

		public static boolean validZipCode(String zipCode) {
			if(zipCode.length() == 5)
				return true;
			return false; 
		}

		public void setZipCode(String warehouseZipCode) {
			if(!validZipCode(warehouseZipCode))
				throw new IllegalArgumentException(ERRORMSG_INVALID_ZIPCODE);
			this.warehouseZipCode = warehouseZipCode;
			//get ready to notify observers (notify is called in finishUpdate())
			setChanged();
		}
		
		public String getCity() {
			return warehouseCity;
		}

		
		public static boolean validCity(String city) {
			if(city.length() > 100)
				return false;
			return true;
		
		}

		public void setCity(String warehouseCity) {
			if(!validCity(warehouseCity))
				throw new IllegalArgumentException(ERRORMSG_INVALID_CITY);
			this.warehouseCity = warehouseCity;
			setChanged();
			
		}
		
		public String getState() {
			return warehouseState;
		}

		
		public static boolean validState(String state) {
			if(state.length() > 50)
				return false;
			return true;
		
		}

		public void setState(String warehouseState) {
			if(!validState(warehouseState))
				throw new IllegalArgumentException(ERRORMSG_INVALID_STATE);
			this.warehouseState = warehouseState;
			setChanged();
			
		}
		
		
		public double getStorageCapacity() {
			return wStorageCapacity;
		}

		public boolean validCapacity(double storageCapacity) {
			if(storageCapacity < 0)
				return false;
			return true;
		}

		public void setStorageCapacity(double WStorageCapacity) {
			if(!validCapacity(wStorageCapacity))
				throw new IllegalArgumentException(ERRORMSG_INVALID_CAPACITY);
			this.wStorageCapacity = wStorageCapacity;
			setChanged();
		}
		
		
		public boolean warehouseAlreadyExists(long id, String name) {
	
			try {
				return gateway.warehouseAlreadyExists(id, name);
			} catch (GatewayException e) {
				return true;
			}
		}
		
	//	public void updatePart(WarehousePart wp) throws GatewayException {
		//	gateway.S
		//}
	
		
		/**
		 * Tells the model that update has finished so it can finish the update
		 * E.g., notify observers
		 */
		public void finishUpdate() throws GatewayException {
			Warehouse orig = null;
			
			if(this.getId() == 0) {
				if(gateway.warehouseAlreadyExists(0, this.getWarehouseName()))
					throw new GatewayException(this.getWarehouseName() + " is already in the database");
			}
			try {
				//if id is 0 then this is a new Warehouse to insert, else its an update
				if(this.getId() == 0) {
					//set id to the long returned by insertWarehouse
					this.setId(gateway.insertWarehouse(this));
					
				} else {
					//fetch person from db table in case this fails
				
					orig = gateway.fetchWarehouse(this.getId());
			
					//try to save to the database
					gateway.saveWarehouse(this);

				}
				//if gateway ok then notify observers
				notifyObservers();
				
			} catch(GatewayException e) {
				//if fails then try to refetch model fields from the database
				if(orig != null) {
					this.setWarehouseName(orig.getWarehouseName());
					this.setAddress(orig.getAddress());
					this.setCity(orig.getCity());
					this.setState(orig.getState());
					this.setZipCode(orig.getZipCode());
					this.setStorageCapacity(orig.getStorageCapacity());
					
				}
				throw new GatewayException("Error trying to save the Warehouse object!");
			}
		}
		
		/**
		 * delete this object through the gateway (i.e., db)
		 */
		public void delete() throws GatewayException {
			//if id is 0 then nothing to do in the gateway (record has not been saved yet
			if(this.getId() == 0) 
				return;
				try {
					gateway.deleteWarehouse(this.getId());
				} catch (GatewayException e) {
					throw new GatewayException(e.getMessage());
				}
		}
		
	
		
		/**
		 * Accessors for gateway
		 * @return
		 */
		public WarehouseTableGateway getGateway() {
			return gateway;
		}

		public void setGateway(WarehouseTableGateway gateway) {
			this.gateway = gateway;
		}
		
		
}
