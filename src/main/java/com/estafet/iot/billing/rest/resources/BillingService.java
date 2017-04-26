package com.estafet.iot.billing.rest.resources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.estafet.iot.billing.config.ConfigurationConstants;
import com.estafet.iot.billing.error.DBException;
import com.estafet.iot.billing.models.DeviceBilling;
import com.estafet.iot.billing.models.Item;

@Path("/")
public class BillingService {

	private final Logger logger = Logger.getLogger(BillingService.class);

	@GET
	@Path("/getBilling")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBilling(@HeaderParam("customer_id") String customerId, @HeaderParam("role") String role) {

		if (role == null || role.isEmpty()) {
			return Response.status(400).entity("The role input header is not present.").build();
		}

		if (!role.equals(ConfigurationConstants.MANAGER_ROLE)) {
			return Response.status(400)
					.entity("The role header does not have the specific rights to get a billing report.").build();
		}

		if (customerId != null && !customerId.isEmpty()) {
			Map<String, DeviceBilling> devices = new HashMap<String, DeviceBilling>();
			String query = "SELECT * FROM openshift.dev_ownership d WHERE d.customer_id LIKE " + customerId + ";";
			List<Item> scanOutcome = new ArrayList<Item>();
			try {
				scanOutcome = loadItems(query);
			} catch (DBException e) {
				return Response.status(400).entity(e.getMessage()).build();
			}

			for (Item item : scanOutcome) {
				DeviceBilling device = new DeviceBilling();
				int costs = calculateBillingForThePeriod(item);
				int days = costs / getDevicePrice(item.getThingType());

				String deviceName = (String) item.getThingName();
				device.setDeviceName(deviceName);
				if (devices.containsKey(deviceName)) {
					device = devices.remove(deviceName);
					int previousCosts = device.getCosts();
					int previousDays = device.getDays();
					device.setCosts(costs + previousCosts);
					device.setDays(days + previousDays);
					devices.put(deviceName, device);
				} else {
					device.setCosts(costs);
					device.setDays(days);
					devices.put(deviceName, device);
				}
			}

			if (scanOutcome.isEmpty()) {
				return Response.status(200).entity("No devices are found for the specific user.").build();
			}

			List<DeviceBilling> billings = new ArrayList<DeviceBilling>();
			for (String key : devices.keySet()) {
				billings.add(devices.get(key));
			}

			return Response.status(200).entity(billings).build();
		} else {
			Map<String, Integer> billings = new HashMap<String, Integer>();
			String query = "SELECT * FROM dev_ownership;";
			List<Item> scanOutcome = new ArrayList<Item>();
			try {
				scanOutcome = loadItems(query);
			} catch (DBException e) {
				return Response.status(400).entity(e.getMessage()).build();
			}

			for (Item item : scanOutcome) {
				if (!billings.containsKey(customerId)) {
					int billing = calculateBillingForThePeriod(item);
					billings.put(customerId, billing);
				} else {
					int billingResult = billings.get(customerId);
					billingResult += calculateBillingForThePeriod(item);
					billings.put(customerId, billingResult);
				}
			}

			if (scanOutcome.isEmpty()) {
				return Response.status(200).entity(billings).build();
			}

			return Response.status(200).entity(billings).build();
		}

	}

	private Date transformDate(String dateArg) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = null;
		try {
			date = dateFormat.parse(dateArg);
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}
		return date;
	}

	@SuppressWarnings("deprecation")
	private int calculateBillingForThePeriod(Item item) {
		int result = 0;
		int ratio = getDevicePrice((String) item.getThingType());
		String validFromString = (String) item.getValidFrom();
		String validToString = (item.getValidTo() != null) ? ((String) item.getValidTo()) : null;
		Date validFrom = transformDate(validFromString);
		Date currentDate = new Date();

		if (validToString != null && transformDate(validToString).getMonth() < currentDate.getMonth()) {
			return 0;
		}

		if (validFrom.getMonth() != currentDate.getMonth()) {
			validFrom.setMonth(currentDate.getMonth());
			validFrom.setDate(1);
		}

		if (validToString == null) {
			int days = currentDate.getDate() - validFrom.getDate() + 1;
			result = days * ratio;
		} else {
			Date validTo = transformDate(validToString);
			int days = validTo.getDate() - validFrom.getDate() + 1;
			result = days * ratio;
		}

		return result;
	}

	private int getDevicePrice(String deviceType) {
		if (deviceType.equals("Camera")) {
			return 2;
		} else {
			return 1;
		}
	}

	public List<Item> loadItems(String query) throws DBException {
		List<Item> items = new ArrayList<Item>();
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(
					"jdbc:postgresql://" + System.getenv("DB_HOST") + "/" + System.getenv("POSTGRESQL_DATABASE"),
					System.getenv("POSTGRESQL_USER"), System.getenv("POSTGRESQL_PASSWORD"));
			if (connection != null) {
				connection.setAutoCommit(false);
				logger.debug("Opened database to select items successfully");

				statement = connection.createStatement();
				resultSet = statement.executeQuery(query);
				while (resultSet.next()) {
					Item item = new Item();
					item.setId(resultSet.getString(ConfigurationConstants.COL_ID));
					item.setCustomerId(resultSet.getString(ConfigurationConstants.COL_CUSTOMER_ID));
					item.setThingName(resultSet.getString(ConfigurationConstants.COL_THING_NAME));
					item.setThingType(resultSet.getString(ConfigurationConstants.COL_THING_TYPE));
					item.setSn(resultSet.getString(ConfigurationConstants.COL_SN));
					item.setOwn(resultSet.getBoolean(ConfigurationConstants.COL_OWN));
					item.setValidFrom(resultSet.getString(ConfigurationConstants.COL_VALID_FROM));
					item.setValidTo(resultSet.getString(ConfigurationConstants.COL_VALID_TO));
					items.add(item);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DBException(e.getMessage());
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.commit();
					connection.close();
				}
			} catch (SQLException sqle) {
				logger.error(sqle.getMessage(), sqle);
			}
		}
		return items;
	}
}
