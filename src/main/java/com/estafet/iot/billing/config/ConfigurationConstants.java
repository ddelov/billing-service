package com.estafet.iot.billing.config;

public interface ConfigurationConstants {
	// DB constants
	public static final String COL_ID = "id";
	public static final String TABLE_NAME = "dev_ownership";
	public static final String COL_CUSTOMER_ID = "customer_id";
	public static final String COL_THING_NAME = "thing_name";
	public static final String COL_THING_TYPE = "thing_type";
	public static final String COL_VALID_FROM = "valid_from";
	public static final String COL_VALID_TO = "valid_to";
	public static final String COL_OWN = "own";
	public static final String COL_SN = "sn";

	// REST constants
	public static final String HEADERS = "headers";
	public static final String ROLE = "role";
	public static final String MANAGER_ROLE = "manager";
	public static final String CUSTOMER_ID_HEADER = "customer_id";
}
