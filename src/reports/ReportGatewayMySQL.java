package reports;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import database.GatewayException;
import models.Warehouse;
import models.Part;
import models.Inventory;

public class ReportGatewayMySQL implements ReportGateway {

	private static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * external DB connection
	 */
	private Connection conn = null;

	/**
	 * Constructor: creates database connection
	 * @throws GatewayException
	 */
	public ReportGatewayMySQL() throws GatewayException {
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
	
	
	public List<HashMap<String,String>>fetchWarehouseInventory() throws GatewayException {
		List<HashMap<String,String>> warehousePart = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> record = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT w.warehouse_name as warehouse_name, p.part_number as part_number, p.part_name as part_name, i.quantity as quantity, p.quantity_unit as quantity_unit " 
					+ "FROM item i "
					+ "INNER JOIN warehouse w ON i.warehouse_id = w.id "
					+ "INNER JOIN part p ON i.part_id = p.id "
					+ "WHERE i.quantity > 0 "
					+ "ORDER BY w.warehouse_name, p.part_name ");
					
			rs = st.executeQuery();
			//add each to list of people to return
			while(rs.next()) {
				
				record = new HashMap<String,String>();
				record.put("warehouse_name", rs.getString("warehouse_name"));
				record.put("part_number", rs.getString("part_number"));
				record.put("part_name", rs.getString("part_name"));
				record.put("quantity", rs.getString("quantity"));
				record.put("quantity_unit", rs.getString("quantity_unit"));
				
				warehousePart.add(record);
				
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
		return warehousePart;
	}
	
	/**
	 * create a MySQL datasource with credentials and DB URL in db.properties file
	 * @return
	 * @throws RuntimeException
	 * @throws IOException
	 */
	private DataSource getDataSource() throws RuntimeException, IOException {
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


	@Override
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
