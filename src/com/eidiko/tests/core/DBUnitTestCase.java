package com.eidiko.tests.core;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public abstract class DBUnitTestCase {

	private DataSource ds;

	private JdbcTemplate jdbcTemplate;
	
	private Logger log;

	public DBUnitTestCase() {
		super();
		log = Logger.getLogger(DBUnitTestCase.class);
	}

	@Before
	public void setUp() throws Exception {
		DatabaseConnection connection = null;
		try {
			SeedFilesMetaData seedFilesMetaData[] = getSeedFilesMetaData();
			log.info("SEED FILES " +seedFilesMetaData.length);
			for (int j = 0; j < seedFilesMetaData.length; j++) {
				connection = getConnection(seedFilesMetaData[j].getSchema());
				log.info("Connection DOne " +connection);
				String seedFiles[] = seedFilesMetaData[j].getSeedFiles();
				for (int i = 0; i < seedFiles.length; i++) {
					URL url = this.getClass().getResource(
							seedFilesMetaData[j].getPath() + File.separatorChar
									+ seedFiles[i]);
					IDataSet dataSet = new XmlDataSet(url.openStream());
					getInsertionMode().execute(connection, dataSet);
				}
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	@After
	public void tearDown() throws Exception {
		DatabaseConnection connection = null;
		try {
			SeedFilesMetaData seedFilesMetaData[] = getSeedFilesMetaData();
			for (int j = 0; j < seedFilesMetaData.length; j++) {
				connection = getConnection(seedFilesMetaData[j].getSchema());
				String seedFiles[] = seedFilesMetaData[j].getSeedFiles();
				for (int i = (seedFiles.length - 1); i >= 0; i--) {
					URL url = this.getClass().getResource(
							seedFilesMetaData[j].getPath() + File.separatorChar
									+ seedFiles[i]);
					IDataSet dataSet = new XmlDataSet(url.openStream());
					getDeleteMode().execute(connection, dataSet);
				}
			}
		} finally {
			connection.close();
		}
	}

	private DatabaseConnection getConnection(String schema)
			throws SQLException, DatabaseUnitException {
		Connection jdbcConn = getDataSource().getConnection();
		DatabaseConnection connection = new DatabaseConnection(jdbcConn, schema);
		return connection;
	}

	DataSource getDataSource() {
		if (ds == null) {
			DBAccess dbAccess = getDBAaccess();
			try {
				Class.forName(dbAccess.getDbDriver());
				ds = new DriverManagerDataSource(dbAccess.getDbUrl(),
						dbAccess.getDbUser(), dbAccess.getDbPassword());
			} catch (ClassNotFoundException cnfe) {
				throw new IllegalStateException(
						"Could not load database driver: "
								+ dbAccess.getDbDriver(), cnfe);
			}
		}
		return ds;
	}

	protected JdbcTemplate getJdbcTemplate() {
		if (jdbcTemplate == null) {
			jdbcTemplate = new JdbcTemplate(getDataSource());
		}
		return jdbcTemplate;
	}

	protected abstract SeedFilesMetaData[] getSeedFilesMetaData();

	protected abstract DBAccess getDBAaccess();

	protected abstract DatabaseOperation getDeleteMode();

	protected abstract DatabaseOperation getInsertionMode();

}