package hu.gambino.data;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import hu.gambino.Start;

public class DatabaseDAO {
	private Connection conn;

	private static Map<Integer, PreparedString> preparedStatements = new HashMap<Integer, PreparedString>();

	public DatabaseDAO() {
		preparedStatements.put(0, new PreparedString("SELECT OrderItemId FROM public.order_item WHERE OrderItemId=?;"));
		preparedStatements.put(1, new PreparedString("SELECT OrderId FROM public.order WHERE OrderId=?;"));
		preparedStatements.put(2, new PreparedString("SELECT OrderTotalValue FROM public.order WHERE OrderId=?;"));
		preparedStatements.put(3, new PreparedString(
				"INSERT INTO public.order (OrderId,BuyerName,BuyerEmail,OrderDate,OrderTotalValue,Address,Postcode) VALUES (?,?,?,?,?,?,?);"));
		preparedStatements.put(4, new PreparedString("UPDATE public.order SET OrderTotalValue=? WHERE OrderId=?;"));
		preparedStatements.put(5, new PreparedString(
				"INSERT INTO public.order_item (OrderItemId,OrderId,SalePrice,ShippingPrice,TotalItemPrice,SKU,Status) VALUES (?,?,?,?,?,?,?::OrderItemStatus);"));

		init();

		for (Map.Entry<Integer, PreparedString> tmpMap : preparedStatements.entrySet()) {
			PreparedString preparedString = preparedStatements.get(tmpMap.getKey());
			String queryString = preparedString.getPreparedStatementString();

			try {
				preparedString.setPreparedStatement(conn.prepareStatement(queryString));
			} catch (SQLException e) {
				System.out.println("An error has occured while trying to pre-compile the PreparedStatement: "
						+ preparedString.getPreparedStatementString() + "!");
			}
		}

	}

	// Initializes the database connection
	private void init() {
		try {
			Class.forName("org.postgresql.Driver");

			Properties props = new Properties();
			props.load(new FileReader(Start.databaseConnectInfoFile));

			conn = DriverManager.getConnection(props.getProperty("link"), props);
			conn.setAutoCommit(true);
		} catch (Exception e) {
			System.out.println("An error occured while trying to load the Database driver or the Database parameters!");
		}
	}

	// Runs the given SQL query
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<List> runQuery(Integer preparedStatementKey) {
		PreparedString preparedString = preparedStatements.get(preparedStatementKey);
		String queryString = preparedString.getPreparedStatementString();

		init();
		List<List> backArray = null;
		
		try {
			ResultSet rs = null;

			if (queryString.toLowerCase().startsWith("update") || queryString.toLowerCase().startsWith("insert")
					|| queryString.toLowerCase().startsWith("delete"))
				preparedString.getPreparedStatement().executeUpdate();
			else
				rs = preparedString.getPreparedStatement().executeQuery();
			
			preparedString.getPreparedStatement().clearParameters();

			if (rs != null) {
				ResultSetMetaData metaData = rs.getMetaData();

				backArray = new ArrayList<>();
				for (int i = 0; i < metaData.getColumnCount(); i++) {
					backArray.add(new ArrayList<ArrayList>());
				}

				while (rs.next()) {
					for (int i = 1; i <= metaData.getColumnCount(); i++) {
						backArray.get(i - 1).add(rs.getObject(i));
					}
				}
				rs.close();
			}
		} catch (SQLException e) {
			System.out.println("Can't connect to Database!");
			Start.getFileDAO()
					.addToResponseFile(new ResponseHeader((long) -1, ResponseStatus.ERROR, "Database error!"));
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				System.out.println("An error occured while trying to close the database connection!");
			}
		}

		return backArray;
	}

	// Checks if a given OrderItemId exist in the database
	public boolean orderItemIdExist(Long orderItemId) {
		Long tmpId = null;
		try {
			PreparedStatement tmpPreparedStatement = preparedStatements.get(0).getPreparedStatement();
			tmpPreparedStatement.setLong(1, orderItemId.longValue());
			tmpId = (Long) runQuery(0).get(0).get(0);
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
			PreparedStatement tmpPreparedStatement = preparedStatements.get(1).getPreparedStatement();
			tmpPreparedStatement.setLong(1, orderId.longValue());
			tmpId = (Long) runQuery(1).get(0).get(0);
		} catch (Exception e) {
			return false;
		}

		if (tmpId.equals(orderId))
			return true;
		else
			return false;
	}

	// Gets the OrderTotalValue from the database
	public Double getOrderTotalValue(Long orderId) {
		Double tmpValue = 0.0;
		try {
			PreparedStatement tmpPreparedStatement = preparedStatements.get(2).getPreparedStatement();
			tmpPreparedStatement.setLong(1, orderId.longValue());
			tmpValue = (Double) runQuery(2).get(0).get(0);
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
			PreparedStatement tmpPreparedStatement = preparedStatements.get(3).getPreparedStatement();
			tmpPreparedStatement.setLong(1, input.getOrderId().longValue());
			tmpPreparedStatement.setString(2, input.getBuyerName());
			tmpPreparedStatement.setString(3, input.getBuyerEmail());
			tmpPreparedStatement.setDate(4, input.getOrderDate());
			tmpPreparedStatement.setDouble(5, tmpTotalItemPrice.doubleValue());
			tmpPreparedStatement.setString(6, input.getAddress());
			tmpPreparedStatement.setInt(7, input.getPostcode());
			runQuery(3);
		} else {
			Double tmpOrderTotalValue = getOrderTotalValue(input.getOrderId()) + tmpTotalItemPrice;

			PreparedStatement tmpPreparedStatement = preparedStatements.get(4).getPreparedStatement();
			tmpPreparedStatement.setDouble(1, tmpOrderTotalValue);
			tmpPreparedStatement.setLong(2, input.getOrderId().longValue());
			runQuery(4);
		}

		PreparedStatement tmpPreparedStatement = preparedStatements.get(5).getPreparedStatement();
		tmpPreparedStatement.setLong(1, input.getOrderItemId().longValue());
		tmpPreparedStatement.setLong(2, input.getOrderId().longValue());
		tmpPreparedStatement.setDouble(3, input.getSalePrice().doubleValue());
		tmpPreparedStatement.setDouble(4, input.getShippingPrice().doubleValue());
		tmpPreparedStatement.setDouble(5, tmpTotalItemPrice.doubleValue());
		tmpPreparedStatement.setString(6, input.getSku());
		tmpPreparedStatement.setString(7, input.getStatus().name());
		runQuery(5);

		Start.getFileDAO().addToResponseFile(new ResponseHeader(input.getLineNumber(), ResponseStatus.OK, ""));
	}

}
