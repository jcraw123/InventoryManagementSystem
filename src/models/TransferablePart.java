package models;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TransferablePart implements Transferable {
	

	private Part part;
	public static final DataFlavor PART_FLAVOR = new DataFlavor(Part.class, "Part Object");
	
	protected static DataFlavor [] supportedFlavors = {PART_FLAVOR, DataFlavor.stringFlavor}; 
	
	public TransferablePart(Part p) { 
		part = p;
	}
	
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}
	
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if(flavor.equals(PART_FLAVOR) || flavor.equals(DataFlavor.stringFlavor))
			return true;
		return false;
	}
	
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if(flavor.equals(PART_FLAVOR))
			return part;
		else if(flavor.equals(DataFlavor.stringFlavor))
			return part.toString();
		else
			throw new UnsupportedFlavorException(flavor);
	}


}
