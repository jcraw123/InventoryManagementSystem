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

import models.Inventory;

public class InventoryTableGateway {
	private static final boolean DEBUG = true;
	
	private Connection conn = null;
	
	public InventoryTableGateway() throws GatewayException {
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
	
	public Inventory fetchInventory(long id) throws GatewayException {
		Inventory n = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("select * from item where id = ?");
			st.setLong(1, id);
			rs = st.executeQuery();
			rs.next();
			n = new Inventory(rs.getLong("id"), rs.getLong("warehouse_id"), rs.getLong("part_id"), rs.getDouble("quantity"));
			
		} catch (SQLException e) {
			
			throw new GatewayException(e.getMessage());
		} finally {
			
			try { 
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
			} catch (SQLException e) {
				throw new GatewayException("SQL Error: " + e.getMessage());
			}
		}
		
		return n;
	}
	
	public void deleteInventory(long id) throws GatewayException { 
		PreparedStatement st = null;
		try { 
			
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement("delete from item where id = ?");
			st.setLong(1, id);
			st.executeUpdate();
			
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				throw new GatewayException(e1.getMessage());
			}
			throw new GatewayException(e.getMessage());
		} finally {
			try { 
				if(st != null)
					st.close();
				conn.setAutoCommit(true);
				
			} catch (SQLException e) {
				throw new GatewayException(e.getMessage());
			}
		}
	}
	
	public long insertInventory(Inventory n) throws GatewayException {
		
        long newId = Inventory.INVALID_ID;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            
            st = conn.prepareStatement("Insert into item ( "
                    + " warehouse_id, part_id, quantity )"
                    + " VALUES ( ?, ?, ? )", PreparedStatement.RETURN_GENERATED_KEYS);
            
            st.setLong(1, n.getWarehouseId());
            st.setLong(2, n.getPartId());
            st.setDouble(3, n.getQuantity());
            
            st.executeUpdate();
            
            rs = st.getGeneratedKeys();
            if(rs != null && rs.next()) {
                newId = rs.getLong(1);
            } else {
                throw new GatewayException("Could not fetch new record Id");
            }
        } catch (SQLException e) {
        	
            e.printStackTrace();
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

    public void saveInventory(Inventory n) throws GatewayException {
        //execute the update and throw exception if any problem
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("update item "
                    + " set warehouse_id = ?, part_id = ?, quantity = ? "
                    + " where id = ? ");
            st.setLong(1, n.getWarehouseId());
            st.setLong(2, n.getPartId());
            st.setDouble(3, n.getQuantity());
            st.setLong(4, n.getId());    
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

    public List<Inventory> fetchInventory() throws GatewayException {
        ArrayList<Inventory> ret = new ArrayList<Inventory>();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {

            st = conn.prepareStatement("select * from item");
            rs = st.executeQuery();
            while(rs.next()) {
                Inventory n = new Inventory( rs.getLong("id"), rs.getLong("warehouse_id"), rs.getLong("part_id"), rs.getDouble("quantity")  );
                ret.add(n);
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
