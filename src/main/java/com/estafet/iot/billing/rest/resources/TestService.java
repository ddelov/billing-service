package com.estafet.iot.billing.rest.resources;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

@Path("/")
public class TestService {
	
	private final Logger logger = Logger.getLogger(TestService.class);
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String ping() {
		String result = "No connection";
		Connection connection = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://172.17.0.5:5432/sampledb", "test", "test");
		} catch (Exception e) {
		}

		if (connection != null) {
			logger.info("Connection established!");
			result = "Connection OK!";
		}

		return result;
	}
}
