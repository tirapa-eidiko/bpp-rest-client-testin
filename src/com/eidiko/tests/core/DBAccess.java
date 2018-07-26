package com.eidiko.tests.core;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class DBAccess {

	private String dbUrl;
	private String dbUser;
	private String dbPassword;
	private String dbDriver;

	public DBAccess(String dbDriver, String dbUrl, String dbUser,
			String dbPassword) {
		super();
		this.dbUrl = dbUrl;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
		this.dbDriver = dbDriver;
	}

	public DBAccess(String propsFile) {
		try {
			Properties props = new Properties();
			URL url = this.getClass().getResource(propsFile);
			props.load(url.openStream());
			dbUrl = props.getProperty("dbUrl");
			dbDriver = props.getProperty("dbDriver");
			dbUser = props.getProperty("dbUser");
			dbPassword = props.getProperty("dbPassword");
		} catch (IOException ioe) {
			throw new IllegalStateException("Could not load properties file: "
					+ propsFile, ioe);
		}
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public String getDbUser() {
		return dbUser;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public String getDbDriver() {
		return dbDriver;
	}

}
