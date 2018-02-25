package hu.gambino.data;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import hu.gambino.Start;

public class DatabaseDAO {
	private Connection conn;

	public DatabaseDAO() {
	}

	// Initializes the database connection
	private void init() throws Exception {
		Class.forName("org.postgresql.Driver");

		Properties props = new Properties();
		props.load(new FileReader(Start.databaseConnectInfoFile));

		conn = DriverManager.getConnection(props.getProperty("link"),props);

	}

	// Runs the given SQL query
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList<ArrayList> runQuery(String queryString) throws Exception {
		init();
		ArrayList<ArrayList> backArray = null;

		Statement st = conn.createStatement();

		ResultSet rs = null;
		if (queryString.toLowerCase().startsWith("update") || queryString.toLowerCase().startsWith("insert")
				|| queryString.toLowerCase().startsWith("delete"))
			st.executeUpdate(queryString);
		else
			rs = st.executeQuery(queryString);

		if (rs != null) {
			ResultSetMetaData metaData = rs.getMetaData();

			backArray = new ArrayList<ArrayList>();
			for (int i = 0; i < metaData.getColumnCount(); i++) {
				backArray.add(new ArrayList<>());
			}

			while (rs.next()) {
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					backArray.get(i - 1).add(rs.getObject(i));
				}
			}
			rs.close();
		}

		st.close();
		conn.close();

		return backArray;
	}

	// Checks if a given OrderItemId exist in the database
	public boolean orderItemIdExist(Long orderItemId) {
		Long tmpId = null;
		try {
			tmpId = (Long) runQuery(
					"SELECT OrderItemId FROM public.order_item WHERE OrderItemId='" + orderItemId.longValue() + "';").get(0).get(0);
		} catch (Exception e) {
			return false;
		}

		if (tmpId.equals(orderItemId))
			return true;
		else
			return false;
	}

	// Checks if a given OrderId exist in the database
	public boolean orderIdExist(Long orderId) {
		Long tmpId = null;
		try {
			tmpId = (Long) runQuery("SELECT OrderId FROM public.order WHERE OrderId='" + orderId.longValue() + "';")
					.get(0).get(0);
		} catch (Exception e) {
			return false;
		}

		if (tmpId.equals(orderId))
			return true;
		else
			return false;
	}

	// Gets the OrerTotalValue from the database
	public Double getOrderTotalValue(Long orderId) {
		Double tmpValue = 0.0;
		try {
			tmpValue = (Double) runQuery("SELECT OrderTotalValue FROM public.order WHERE OrderId='" + orderId.longValue() + "';")
					.get(0).get(0);
		} catch (Exception e) {

		}
		if (tmpValue == null)
			tmpValue = 0.0;
		return tmpValue;
	}

	// Adds a new record or records into the database based on certain rules
	public void addRecord(InputHeader input) throws Exception {
		Double tmpTotalItemPrice = input.getSalePrice() + input.getShippingPrice();
		if (!orderIdExist(input.getOrderId())) {
			runQuery(
					"INSERT INTO public.order (OrderId,BuyerName,BuyerEmail,OrderDate,OrderTotalValue,Address,Postcode) VALUES ('"
							+ input.getOrderId() + "','" + input.getBuyerName() + "','" + input.getBuyerEmail() + "','"
							+ input.getOrderDate() + "','" + tmpTotalItemPrice.doubleValue() + "','"
							+ input.getAddress() + "','" + input.getPostcode() + "');");
		} else {
			Double tmpOrderTotalValue = getOrderTotalValue(input.getOrderId()) + tmpTotalItemPrice;
			runQuery("UPDATE public.order SET OrderTotalValue='" + tmpOrderTotalValue + "' WHERE OrderId='"
					+ input.getOrderId() + "';");
		}

		runQuery(
				"INSERT INTO public.order_item (OrderItemId,OrderId,SalePrice,ShippingPrice,TotalItemPrice,SKU,Status) VALUES ('"
						+ input.getOrderItemId() + "','" + input.getOrderId() + "','" + input.getSalePrice() + "','"
						+ input.getShippingPrice() + "','" + tmpTotalItemPrice.doubleValue() + "','" + input.getSku()
						+ "','" + input.getStatus() + "');");
		
		Start.getFileDAO().addToResponseFile(new ResponseHeader(input.getLineNumber(),
				ResponseStatus.OK, ""));
	}

}
