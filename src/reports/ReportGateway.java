package reports;

import java.util.HashMap;
import java.util.List;

import database.GatewayException;
import models.Inventory;

public interface ReportGateway {

	public abstract List<HashMap<String, String>> fetchWarehouseInventory() throws GatewayException;
	public abstract void close();
}
