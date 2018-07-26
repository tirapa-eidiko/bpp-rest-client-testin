package com.eidiko.tests.core;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class BPMAccess {

	private String hostname;
	private int port;
	private String user;
	private String password;

	public BPMAccess(String hostname, int port, String user, String password) {
		super();
		this.hostname = hostname;
		this.port = port;
		this.user = user;
		this.password = password;
	}

	public BPMAccess(String propsFile) {
		try {
			Properties props = new Properties();
			URL url = this.getClass().getResource(propsFile);
			props.load(url.openStream());
			hostname = props.getProperty("hostname");
			port = Integer.parseInt(props.getProperty("port"));
			user = props.getProperty("user");
			password = props.getProperty("password");
		} catch (IOException ioe) {
			throw new IllegalStateException("Could not load properties file: "
					+ propsFile, ioe);
		} catch (NumberFormatException nfe) {
			throw new IllegalStateException("Could not load properties file: "
					+ propsFile, nfe);
		}
	}

	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

}
