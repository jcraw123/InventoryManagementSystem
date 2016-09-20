package database;

import java.awt.Component;
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
import javax.swing.JOptionPane;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import models.Part;

public class PartTableGateway {
	private static final boolean DEBUG = true;

	/**
	 * external DB connection
	 */
	private Connection conn = null;
	
	/**
	 * Constructor: creates database connection
	 * @throws GatewayException
	 */
	
	public PartTableGateway() throws GatewayException {
		
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
        	conn.setAutoCommit(false);
        	//for pessimistic locking
        	conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		} catch (SQLException e) {
			throw new GatewayException("SQL Error: " + e.getMessage());
		}
	}
		
	

	/**
	 * create a MySQL datasource with credentials and DB URL in db.properties file
	 * @return
	 * @throws RuntimeException
	 * @throws IOException
	 */


	public Part fetchPart(long id) throws GatewayException {
		Part p = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			//fetch part
			
			
			st = conn.prepareStatement("select * from part where id = ? ");
			st.setLong(1, id);
			rs = st.executeQuery();
			//should only be 1
			rs.next();
			p = new Part(rs.getLong("id"), rs.getString("part_number"), rs.getString("part_name")
					, rs.getString("vendor_name"), rs.getString("quantity_unit"),
					rs.getString("vendor_part_number"));
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
		return p;
	}
	
	
	
	
	public boolean partAlreadyExists(long id, String number) throws GatewayException {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("select count(id) as num_records "
					+ " from part where part_number = ? and id <> ? ");
			st.setString(1, number);
			st.setLong(2, id);
			rs = st.executeQuery();
			//should only be 1
			rs.next();
			if(rs.getInt("num_records") > 0)
				return true;
			//give part object a reference to this gateway
			//p.setGateway(this);
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

	
	public void deletePart(long id) throws GatewayException {
		PreparedStatement st = null;
		try {
			//turn off autocommit to start the tx
		
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement("delete from item where part_id = ? ");
			st.setLong(1, id);
			st.executeUpdate();
			
			st.close();
			st = conn.prepareStatement("delete from part where id = ?");
			st.setLong(1,  id);
			st.executeUpdate();
			
			conn.commit();			
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

	public long insertPart(Part p) throws GatewayException {
		//init new id to invalid
		long newId = Part.INVALID_ID;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			
			
			
			st = conn.prepareStatement("insert part (part_number, part_name, vendor_name, quantity_unit, vendor_part_number) "
					+ " values ( ?, ?, ?, ?, ? ) ", PreparedStatement.RETURN_GENERATED_KEYS);
			st.setString(1, p.getPartNumber());
			st.setString(2, p.getPartName());
			st.setString(3, p.getVendorName());
			st.setString(4, p.getQuantityUnit());
			st.setString(5, p.getVendorPartNumber());
			
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

	
	public void savePart(Part p) throws GatewayException {
		//execute the update and throw exception if any problem
		PreparedStatement st = null;
		try {
			
			
			
			st = conn.prepareStatement("update part "
					+ " set part_number = ?, part_name = ?, vendor_name = ?, quantity_unit = ?, vendor_part_number = ? "
					+ " where id = ? ");
			st.setString(1, p.getPartNumber());
			st.setString(2, p.getPartName());
			st.setString(3, p.getVendorName());
			st.setString(4, p.getQuantityUnit());
			st.setString(5, p.getVendorPartNumber());
			st.setLong(6, p.getId());
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
	
	public int lockRow(int part) throws SQLException {
		System.out.println("File locked");
		DataSource ds = null;
		try {
			ds = getDataSource();
		} catch (RuntimeException | IOException e1) {
			throw new SQLException(e1.getMessage());
		}
		if(ds == null) {
        	throw new SQLException("Datasource is null!");
        }

		PreparedStatement st = null;
		try {
			if(conn == null) {
				conn = ds.getConnection();
				System.out.println("Connection establshed");
			}
			String sql = "SELECT * FROM part where id=? FOR UPDATE";
			conn.setAutoCommit(false);
			
			st = (PreparedStatement) conn.prepareStatement(sql);
			st.setQueryTimeout(3);
			st.setInt(1, part);
			st.executeQuery();
		} catch (SQLException e) {
			Component frame = null; 
			JOptionPane.showMessageDialog(frame, "Locked");
			conn.rollback();
			conn.setAutoCommit(true);
			conn.close();
			e.printStackTrace();
			return 1;
		}
		return 0;
	}

	
	public List<Part> fetchParts() throws GatewayException {
		ArrayList<Part> ret = new ArrayList<Part>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			
			//fetch parts
			st = conn.prepareStatement("select * from part");
			rs = st.executeQuery();
			//add each to list of parts to return
			while(rs.next()) {
				Part p = new Part(rs.getLong("id"), rs.getString("part_number"), rs.getString("part_name")
						, rs.getString("vendor_name"), rs.getString("quantity_unit")
						, rs.getString("vendor_part_number"));
				ret.add(p);
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
