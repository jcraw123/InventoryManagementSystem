package reports;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import models.Warehouse;
import models.Part;
import models.Inventory;

public class WarehouseInventoryReportPDF extends ReportMaster {
	
	/**
	 * PDF document variable
	 * 
	 */
	private PDDocument doc;
	private final String DATE = "MM/dd/yyyy HH:mm:ss";
	private static Logger Log = Logger.getLogger(WarehouseInventoryReportPDF.class);
	
	public WarehouseInventoryReportPDF(ReportGateway gw) {
		super(gw);
		
		//pdfbox uses log4j so we need to run a configurator
		BasicConfigurator.configure();
		
		//init doc
		doc = null;
	}

	public void generateReport() throws ReportException {
		try {
			PropertyConfigurator.configure(new FileInputStream("log4j.properties"));
		} catch(FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		Log.info("PDF report creation started");
		Log.warn("No inventory records found for PDF report");
		
		//declare data variables

		List<HashMap<String,String>> records = null;
		try {
			records = gateway.fetchWarehouseInventory();
		} catch (GatewayException e) { 
			throw new ReportException("Error in report generation: " + e.getMessage());

		}
		
		//get the day/time the report is generated 
				DateFormat date = new SimpleDateFormat(DATE);
				Calendar calendar = Calendar.getInstance();
				String time = "Report generated on: " + (date.format(calendar.getTime())); 
		
		//prep the report page 1
		doc = new PDDocument();
		PDPage page = new PDPage();
		PDRectangle rect = page.getMediaBox();
		doc.addPage(page);
		
		page.setRotation(90);
		
		//get content stream for page 1
		PDPageContentStream content = null;
		
		
		
		
		//prep the fonts
		PDFont fontPlain = PDType1Font.HELVETICA;
		PDFont fontBold = PDType1Font.HELVETICA_BOLD;
		PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
		PDFont fontMono = PDType1Font.COURIER;
		PDFont fontMonoBold = PDType1Font.COURIER_BOLD;
		
		//same margin all around document
		//float margin = 20;
		
		int numRecords = 15;
		
		float pageWidth = rect.getWidth();
		float pageHeight = rect.getHeight();
		
		float marginX = pageWidth - 575;
		float marginY = pageHeight - 350;
		
		int fontSize = 15;
		int pageNum = 0;
		
		try {
			content = new PDPageContentStream(doc, page);
		
			//print header of page 1
			//draw 50 point height grey stripe across top of page
			//for landscape mode
			content.concatenate2CTM(0, 1, -1, 0, pageWidth,0 );
			content.setNonStrokingColor(Color.LIGHT_GRAY);
			content.setStrokingColor(Color.BLACK);
		
			content.addRect(marginX - 10, marginY - 10, pageWidth + 125, 60);
			content.fillAndStroke();

			//reset non-stroking color
			content.setNonStrokingColor(Color.BLACK);

			//print report title
			content.setFont(fontBold,48);
			content.beginText();
			content.newLineAtOffset(marginX, marginY);
			content.showText("Warehouse Inventory Summary");
			content.endText();

			
			//get startingY for data (col header first then data rows)
			content.setFont(fontMonoBold, fontSize);
			float dataY = marginY - 50;
			
			//sketch the layout of the columns in the report
			//warehouse name		part number		part name		quantity		unit of quantity
			
			float colX_0 = marginX + 5; //warehouse name
			float colX_1 = colX_0 + 135; //part number
			float colX_2 = colX_1 + 125; //part name
			float colX_3 = colX_2 + 245; //quantity
			float colX_4 = colX_3 + 95; //unit of quantity
			
			//print column headings
			content.beginText();
			content.newLineAtOffset(colX_0, dataY);
			content.showText("Warehouse Name");
			content.endText();
			content.beginText();
			content.newLineAtOffset(colX_1, dataY);
			content.showText("Part Number");
			content.endText();
			content.beginText();
			content.newLineAtOffset(colX_2, dataY);
			content.showText("Part Name");
			content.endText();
			content.beginText();
			content.newLineAtOffset(colX_3, dataY);
			content.showText("Quantity");
			content.endText();
			content.beginText();
			content.newLineAtOffset(colX_4, dataY);
			content.showText("Unit of Quantity");
			content.endText();
			
			//print report rows
			content.setFont(fontMono, fontSize);
			int counter = 1;
			int size = records.size();
			
			//Log.trace("Writing record # " + counter +" to report");
			Log.info("Record # " + numRecords +" will cause a page break");
			Log.debug("Fetched " +size+" inventory records for report");

			
			int n = 0;
			for(int i = 0; i < size; i++) {
				Log.trace("Writing record # " + counter +" to report");

				String record = records.get(i).get("warehouse_name") + " | " + records.get(i).get("part_number") + " | " + records.get(i).get("part_name") + " | " + records.get(i).get("quantity") + " | " + records.get(i).get("quantity_unit");


				
				int j = counter%numRecords;
				float offset;
				
				if(counter > 10 && counter%numRecords ==0) {
					//day/time 
					content.beginText();
					content.newLineAtOffset(25,25);
					content.showText(time);
					content.endText();
					
					//page#
					content.beginText();
					content.newLineAtOffset(pageWidth + 90,25);
					content.showText("Page " + (++pageNum));
					content.endText();
					
					content.close();
					
					Log.info("Page# "+pageNum+" created");
					
					page = new PDPage();
					doc.addPage(page);
					page.setRotation(90);
					content = new PDPageContentStream(doc,page);
					
					content.concatenate2CTM(0, 1, -1, 0, pageWidth, 0);
					content.setFont(fontMono,  fontSize);
					n = 60;
					
					offset  = dataY + n - (j * (fontMono.getHeight(12) + 20));
					
				} else { 
					offset = dataY + n - (j * (fontMono.getHeight(12) + 20));
				}
				
			
			
				
			
				
				content.beginText();
				content.newLineAtOffset(colX_0, offset);
				content.showText(records.get(i).get("warehouse_name"));
				content.endText();
				content.beginText();
				content.newLineAtOffset(colX_1, offset);
				content.showText(records.get(i).get("part_number"));
				content.endText();
				content.beginText();
				content.newLineAtOffset(colX_2, offset);
				content.showText(records.get(i).get("part_name"));
				content.endText();
				content.beginText();
				content.newLineAtOffset(colX_3, offset);
				content.showText(records.get(i).get("quantity"));
				content.endText();
				content.beginText();
				content.newLineAtOffset(colX_4, offset);
				String unit = null;
				content.showText(records.get(i).get("quantity_unit"));
				content.endText();
				
				//move to next row
				counter++;
			
			}
		} catch (IOException e) {
			throw new ReportException("Error in report generation: " + e.getMessage());
		} finally {
			//close page 1
			try {
				
				content.beginText();
				content.newLineAtOffset(25, 25);
				content.showText(time);
				content.endText();
				
				content.beginText();
				content.newLineAtOffset(pageWidth + 90,25);
				content.showText("Page " + (++pageNum));
				content.endText();
				content.close();
				
				Log.info("Page " +pageNum+" created");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * write PDF document to file
	 */
	public void outputReportToFile(String fileName) throws ReportException {
		//Save the results and ensure that the document is properly closed:
		try {
			doc.save(fileName);
			Log.info("writing report to file");
		} catch (IOException e) {
			throw new ReportException("Error in report save to file: " + e.getMessage());
		}
	}
	
	public void close() {
	
		super.close();
		try {
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
