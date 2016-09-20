package database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import models.Warehouse;

public class WarehouseTableGateway {
	private static final boolean DEBUG = true;

	/**
	 * external DB connection
	 */
	private Connection conn = null;
	
	/**
	 * Constructor: creates database connection
	 * @throws GatewayException
	 */
	public WarehouseTableGateway() throws GatewayException {
		//read the properties file to establish the db connection
		DataSource ds = null;
		try {
			ds = getDataSource();
		} catch (RuntimeException | IOException e1) {
			throw new GatewayException(e1.getMessage());
		}
		if(ds == null) {
        	throw new GatewayException("Datasource is null!");
        }
		try {
        	conn = ds.getConnection();
		} catch (SQLException e) {
			throw new GatewayException("SQL Error: " + e.getMessage());
		}
	}

	
	public Warehouse fetchWarehouse(long id) throws GatewayException {
		Warehouse w = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			//fetch warehouse
			st = conn.prepareStatement("select * from warehouse where id = ? ");
			st.setLong(1, id);
			rs = st.executeQuery();
			//should only be 1
			rs.next();
			w = new Warehouse(rs.getLong("id"), rs.getString("warehouse_name"), rs.getString("address"), rs.getString("city"), rs.getString("state"), rs.getString("zipcode"), rs.getInt("storage_capacity"));
			
		} catch (SQLException e) {
			//e.printStackTrace();
			throw new GatewayException(e.getMessage());
		} finally {
			//clean up
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				throw new GatewayException("SQL Error: " + e.getMessage());
			}
		}
		return w;
	}

	/**
	 * determines if warehouse name already exists 
	 * 
	 * @return true if warehouse exists in database, else false
	 */
	public boolean warehouseAlreadyExists(long id, String name) throws GatewayException {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			//fetch person
			st = conn.prepareStatement("select count(id) as num_records "
					+ " from warehouse where warehouse_name = ? and id <> ? ");
			st.setString(1, name);
			st.setLong(2, id);
			rs = st.executeQuery();
			//should only be 1
			rs.next();
			if(rs.getInt("num_records") > 0)
				return true;
		
		} catch (SQLException e) {
			//e.printStackTrace();
			throw new GatewayException(e.getMessage());
		} finally {
			//clean up
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				throw new GatewayException("SQL Error: " + e.getMessage());
			}
		}
		return false;
	}
	
	/**
	 * Deletes warehouse from the database
	 * @param id Id of the warehouse in the db to fetch
	 */
	public void deleteWarehouse(long id) throws GatewayException {
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);

			st = conn.prepareStatement("delete from item where warehouse_id = ? ");
			st.setLong(1, id);
			st.executeUpdate();
			
			st.close();
			
			st = conn.prepareStatement("delete from warehouse where id = ? ");
			st.setLong(1, id);
			st.executeUpdate();
			
			conn.commit();
			
		} catch (SQLException e) {
			//e.printStackTrace();
			throw new GatewayException(e.getMessage());
		} finally {
			//clean up
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				throw new GatewayException("SQL Error: " + e.getMessage());
			}
		}
	}

	/**
	 * Insert a new warehouse into the database warehouse table
	 * @param w Warehouse to insert into the warehouse table
	 * @return the new Id of the inserted Warehouse
	 * @throws GatewayException
	 */
	public long insertWarehouse(Warehouse w) throws GatewayException {
		//init new id to invalid
		long newId = Warehouse.INVALID_ID;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("insert warehouse (warehouse_name, address, city, state, zipcode, storage_capacity) "
					+ " values ( ?, ?, ?, ?, ?, ? ) ", PreparedStatement.RETURN_GENERATED_KEYS);
			st.setString(1, w.getWarehouseName());
			st.setString(2,  w.getAddress());
			st.setString(3,  w.getCity());
			st.setString(4, w.getState());
			st.setString(5, w.getZipCode());
			st.setDouble(6, w.getStorageCapacity());
			st.executeUpdate();
			//get the generated key
			rs = st.getGeneratedKeys();
			if(rs != null && rs.next()) {
			    newId = rs.getLong(1);
			} else {
				throw new GatewayException("Could not fetch new record Id");
			}
		} catch (SQLException e) {
			//e.printStackTrace();
			throw new GatewayException(e.getMessage());
		} finally {
			//clean up
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				throw new GatewayException("SQL Error: " + e.getMessage());
			}
		}
		return newId;
	}
	
	/**
	 * Saves existing warehose to database.
	 *
	 * @param p
	 * @throws GatewayException
	 */
	public void saveWarehouse(Warehouse w) throws GatewayException {
		//execute the update and throw exception if any problem
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("update warehouse "
					+ " set warehouse_name = ?, address= ?, city = ?, state = ?, zipcode = ?"
					+ "storage_capacity = ? "
					+ " where id = ? ");
			st.setString(1, w.getWarehouseName());
			st.setString(2,  w.getAddress());
			st.setString(3, w.getCity());
			st.setString(4, w.getState());
			st.setString(5,  w.getZipCode());
			st.setDouble(6,  w.getStorageCapacity());
			st.setLong(7, w.getId());	
			st.executeUpdate();
		} catch (SQLException e) {
			throw new GatewayException(e.getMessage());
		} finally {
			//clean up
			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				throw new GatewayException("SQL Error: " + e.getMessage());
			}
		}
	}
	
	
	public List<Warehouse> fetchWarehouses() throws GatewayException {
		ArrayList<Warehouse> ret = new ArrayList<Warehouse>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			//fetch people
			st = conn.prepareStatement("select * from warehouse");
			rs = st.executeQuery();
			//add each to list of people to return
			while(rs.next()) {
				Warehouse w = new Warehouse(rs.getLong("id"), rs.getString("warehouse_name"), rs.getString("address"), rs.getString("city"), rs.getString("state"), rs.getString("zipcode"), rs.getInt("storage_capacity"));
				//give each warehouse object a reference to this gateway
				
				w.setGateway(this);
				ret.add(w);
			}
		} catch (SQLException e) {
			throw new GatewayException(e.getMessage());
		} finally {
			//clean up
			try {
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				throw new GatewayException("SQL Error: " + e.getMessage());
			}
		}
		
		return ret;
	}
	
	public void close() {
		if(DEBUG)
			System.out.println("Closing db connection...");
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * create a MySQL datasource with credentials and DB URL in db.properties file
	 * @return
	 * @throws RuntimeException
	 * @throws IOException
	 */
	public DataSource getDataSource() throws RuntimeException, IOException {
		//read db credentials from properties file
		Properties props = new Properties();
		FileInputStream fis = null;
        fis = new FileInputStream("db.properties");
        props.load(fis);
        fis.close();
        
        //create the datasource
        MysqlDataSource mysqlDS = new MysqlDataSource();
        mysqlDS.setURL(props.getProperty("MYSQL_DB_URL"));
        mysqlDS.setUser(props.getProperty("MYSQL_DB_USERNAME"));
        mysqlDS.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
        return mysqlDS;
	}
}
