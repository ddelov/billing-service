package com.estafet.iot.billing.config;

public interface ConfigurationConstants {
	// DynamoDB constants
	public static final String ID = "id";
	public static final String TABLE_NAME = "DeviceOwnership";
	public static final String CUSTOMER_ID = "customerId";
	public static final String THING_NAME = "thingName";
	public static final String THING_TYPE = "thingType";
	public static final String VALID_FROM = "validFrom";
	public static final String VALID_TO = "validTo";

	// REST constants
	public static final String HEADERS = "headers";
	public static final String ROLE = "role";
	public static final String MANAGER_ROLE = "manager";
	public static final String CUSTOMER_ID_HEADER = "customer_id";
}
