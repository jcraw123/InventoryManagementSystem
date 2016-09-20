package reports;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import database.GatewayException;
import models.Part;
import models.Warehouse;
import models.Inventory;

public class WarehouseInventoryReportExcel extends ReportMaster {
	/**
	 * Excel document variable
	 * @param gw
	 */
	private StringBuilder doc;
	private static Logger Log = Logger.getLogger(WarehouseInventoryReportExcel.class);

	
	public WarehouseInventoryReportExcel(ReportGateway gw) {
		super(gw);
		
		//pdfbox uses log4j so we need to run a configurator
		BasicConfigurator.configure();
		
		//init doc
		doc = null;
	}

	@Override
	public void generateReport() throws ReportException {
		
		try {
			PropertyConfigurator.configure(new FileInputStream("log4jExcel.properties"));
		} catch(FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		Log.info("Excel report creation started");
		Log.warn("No inventory records found for Excel report");
		
		//declare data variables
		List <HashMap<String,String>> records = null;
		try {
			records = gateway.fetchWarehouseInventory();
		} catch(GatewayException e) {
			throw new ReportException("Error in report generation: " + e.getMessage());
		}
		
		//prep the report page 1
		doc = new StringBuilder();
		
		doc.append("Warehouse Inventory Summary\n\n");
			
		doc.append("Warehouse Name\t");
		doc.append("Part Number\t");
		doc.append("Part Name\t");
		doc.append("Quantity\t");
		doc.append("Unit of Quantity\n");
		
		int counter = 1;
		int size = records.size();
		Log.debug("Fetched " +size+" inventory records for report");




		for(int i = 0; i < size; i++) { 

			Log.trace("Writing record # " + counter +" to report");

			doc.append(records.get(i).get("warehouse_name") + "\t");
			doc.append(records.get(i).get("part_number") + "\t");
			doc.append(records.get(i).get("part_name") + "\t");
			doc.append(records.get(i).get("quantity") + "\t");
			doc.append(records.get(i).get("quantity_unit") + "\n");
			
			counter++;



		}
	}
	
	/**
	 * write Excel report to file
	 */
	@Override
	public void outputReportToFile(String fileName) throws ReportException {
		//Save the results and ensure that the document is properly closed:
		try(PrintWriter out = new PrintWriter(fileName)){
			out.print(doc.toString());
		} catch (IOException e) {
			throw new ReportException("Error in report save to file: " + e.getMessage());
		}
		Log.info("writing report to file");


	}

	@Override
	public void close() {
		super.close();
	}

}
