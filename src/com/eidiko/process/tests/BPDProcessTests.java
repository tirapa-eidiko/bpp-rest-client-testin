package com.eidiko.process.tests;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Assert;

//import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;





import org.dbunit.operation.DatabaseOperation;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.eidiko.tests.core.BPMAccess;
import com.eidiko.tests.core.DBAccess;
import com.eidiko.tests.core.DBUnitTestCase;
import com.eidiko.tests.core.HumanServiceDetails;
import com.eidiko.tests.core.SeedFilesMetaData;
import com.eidiko.tests.core.TestUtils;

import bpm.rest.client.BPMClientImpl;
import bpm.rest.client.authentication.was.WASAuthenticationTokenHandler;

public class BPDProcessTests extends DBUnitTestCase {

	private static BPMClientImpl bpmClient;
	private static TestUtils utils;
	private static Logger log;

	private final String BPD_ID = "25.d39062ce-79ad-4a8f-89fd-b00bab47e0ca";
	private final String PROCESS_APP_ID = "2066.9e9f0a7b-dd7f-4136-b333-2d010f3d6a2d"; 
	private final int waitTime = 15000;

	public BPDProcessTests() {
		super();
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		
		com.eidiko.log.util.Logging.logConfiguration(System.getProperty("user.dir")+""+File.separatorChar+"bpm-logs");
	    log = Logger.getLogger(BPDProcessTests.class);
	
		BPMAccess bpmAccess = new BPMAccess(
				"/com/eidiko/process/tests/BPMProps.txt");
		log.error("User Name : "+bpmAccess.getUser());
		log.info("Password : "+bpmAccess.getPassword());
		
		WASAuthenticationTokenHandler handler = new WASAuthenticationTokenHandler(
				bpmAccess.getUser(), bpmAccess.getPassword());
		
		log.info("Handler created");
		
		bpmClient = new BPMClientImpl(bpmAccess.getHostname(),
				bpmAccess.getPort(), handler);
		log.info("foundAuthenticationToken : "+handler.foundAuthenticationToken());
		log.info("isUsingUserIdentityInContainer : "+handler.isUsingUserIdentityInContainer());
		log.info("bpmClient created");
		utils = new TestUtils(bpmClient);
		log.info("utils created");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		utils = null;
		bpmClient = null;
	}

	@Test
	public void runningVeryImportantClientRequest() throws Exception {
		int requestId = 1;

		// Create input argument to start the business process
		JSONObject bpdArgs = new JSONObject();
		bpdArgs.put("requestId", requestId);
		
		log.info("Gonna Start Process App");

		// Start the business process: Sample BPD
		JSONObject results = bpmClient.runBPD(BPD_ID, PROCESS_APP_ID, bpdArgs);
		log.info("Started Process App");
		int processId = results.getJSONObject("data").getInt("piid");
		Thread.sleep(waitTime);

		// Verify process completed as expected
		Assert.assertTrue(utils.isProcessCompleted(processId));

		// Verify that after executing the business process, there are still
		// three request records in the database
		int count = getJdbcTemplate().queryForInt(
				"select count(*) from request");
		Assert.assertEquals("Wrong number of request records!", 3, count);

		// Verify corresponding request record was updated as expected
		String sql1 = "select approved, system_code, comments from request where request_id="
				+ requestId;
		getJdbcTemplate().query(sql1, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rset) throws SQLException {
				String approved = rset.getString(1);
				String systemCode = rset.getString(2);
				String comments = rset.getString(3);
				log.info(approved+" sdfs "+systemCode+" sdfsd "+comments);
				Assert.assertEquals("System code mismatch!", "ABCD", systemCode);
				Assert.assertEquals("Approval value mismatch!", "Y", approved);
				Assert.assertEquals("Comments mismatch!",
						"System automated approval", comments);
			}
		});
	}

	@Test
	public void runningImportantClientRequest() throws Exception {
		int requestId = 2;

		// Create input argument to start the business process
		JSONObject bpdArgs = new JSONObject();
		bpdArgs.put("requestId", requestId);

		// Start the business process: Sample BPD
		JSONObject results = bpmClient.runBPD(BPD_ID, PROCESS_APP_ID, bpdArgs);
		int processId = results.getJSONObject("data").getInt("piid");
		Thread.sleep(waitTime);

		// Get user's inbox
		results = bpmClient.getInbox();
		log.info("INBOX DATA"+results.toString());

		// Find & execute expected task
		JSONObject checkTask = utils
				.findTask(results, processId, "Full Review");
		Assert.assertNotNull(checkTask);
		int checkTaskId = checkTask.getInt("taskId");
		log.info("Found Task ID IS :: "+ checkTaskId);
		results = bpmClient.startTask(checkTaskId);
		log.info("Started Task Result "+ results.toString());
		HumanServiceDetails hsDetails = utils.parseStartDetails(results);
		log.info(hsDetails.getCoach()+" Human Service is Completed "+hsDetails.isCompleted());
		
		//Assert.assertFalse(hsDetails.isCompleted());
		Assert.assertEquals("Full Review", hsDetails.getCoach());

		// Verify in-flight data
		JSONObject vars = hsDetails.getVariables();
		Assert.assertNotNull(vars.getJSONObject("request"));
		Assert.assertEquals(requestId,
				vars.getJSONObject("request").getInt("requestId"));
		vars.getJSONObject("request").put("approved", true);
		vars.getJSONObject("request").put("comments",
				"Approving request for XYZ account.");
		bpmClient.setData(checkTaskId, vars);
		results = bpmClient.resumeService(checkTaskId, "buttonGrp1_ok");
		hsDetails = utils.parseResumeDetails(results);
		Assert.assertTrue(hsDetails.isCompleted());
		Thread.sleep(waitTime);

		// Verify process completed as expected
		Assert.assertTrue(utils.isProcessCompleted(processId));

		// Verify that after executing the business process, there are still
		// three request records in the database
		int count = getJdbcTemplate().queryForInt(
				"select count(*) from request");
		Assert.assertEquals("Wrong number of request records!", 3, count);

		// Verify corresponding request record was updated as expected
		String sql1 = "select approved, system_code, comments from request where request_id="
				+ requestId;
		getJdbcTemplate().query(sql1, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rset) throws SQLException {
				String approved = rset.getString(1);
				String systemCode = rset.getString(2);
				String comments = rset.getString(3);
				log.info(approved+" testImportantRequest "+systemCode+" testImportantRequest "+comments);
				Assert.assertNull("System code mismatch!", systemCode);
				Assert.assertEquals("Approval value mismatch!", "Y", approved);
				Assert.assertEquals("Comments mismatch!",
						"Approving request for XYZ account.", comments);
			}
		});
	}

	@Test
	public void runningNotSoImportantClientRequest() throws Exception {
		int requestId = 3;

		// Create input argument to start the business process
		JSONObject bpdArgs = new JSONObject();
		bpdArgs.put("requestId", requestId);

		// Start the business process: Sample BPD
		JSONObject results = bpmClient.runBPD(BPD_ID, PROCESS_APP_ID, bpdArgs);
		int processId = results.getJSONObject("data").getInt("piid");
		Thread.sleep(waitTime);

		// Get user's inbox
		results = bpmClient.getInbox();

		// Find & execute expected task -> Quick Review
		JSONObject reviewTask = utils.findTask(results, processId,
				"Quick Review");
		
		Assert.assertNotNull(reviewTask);
		int reviewTaskId = reviewTask.getInt("taskId");
		log.info("reviewTask "+reviewTask.toString());
		log.info("reviewTask ID "+reviewTaskId);
		results = bpmClient.startTask(reviewTaskId);
		log.info("ReviewTask Start Result"+results);
		HumanServiceDetails hsDetails = utils.parseStartDetails(results);
		Assert.assertFalse(hsDetails.isCompleted());
		Assert.assertEquals("Quick Review", hsDetails.getCoach());

		// Verify in-flight data
		JSONObject vars = hsDetails.getVariables();
		Assert.assertNotNull(vars.getJSONObject("request"));
		Assert.assertEquals(requestId,
				vars.getJSONObject("request").getInt("requestId"));
		vars.getJSONObject("request").put("approved", true);
		vars.getJSONObject("request").put("comments",
				"Approving request for CBA account.");
		bpmClient.setData(reviewTaskId, vars);
		results = bpmClient.resumeService(reviewTaskId, "buttonGrp1_ok");
		hsDetails = utils.parseResumeDetails(results);
		Assert.assertTrue(hsDetails.isCompleted());
		Thread.sleep(waitTime);

		// Get user's inbox
		results = bpmClient.getInbox();

		// Find & execute expected task -> Full Review
		JSONObject checkTask = utils
				.findTask(results, processId, "Full Review");
		Assert.assertNotNull(checkTask);
		int checkTaskId = checkTask.getInt("taskId");
		results = bpmClient.startTask(checkTaskId);
		hsDetails = utils.parseStartDetails(results);
		Assert.assertFalse(hsDetails.isCompleted());
		Assert.assertEquals("Full Review", hsDetails.getCoach());

		// Verify in-flight data
		vars = hsDetails.getVariables();
		Assert.assertNotNull(vars.getJSONObject("request"));
		Assert.assertEquals(requestId,
				vars.getJSONObject("request").getInt("requestId"));
		vars.getJSONObject("request").put("approved", false);
		vars.getJSONObject("request").put("comments",
				"Rejecting request for CBA account.");
		bpmClient.setData(checkTaskId, vars);
		results = bpmClient.resumeService(checkTaskId, "buttonGrp1_ok");
		hsDetails = utils.parseResumeDetails(results);
		Assert.assertTrue(hsDetails.isCompleted());
		Thread.sleep(waitTime);

		// Verify process completed as expected
		Assert.assertTrue(utils.isProcessCompleted(processId));

		// Verify that after executing the business process, there are still
		// three request records in the database
		int count = getJdbcTemplate().queryForInt(
				"select count(*) from request");
		Assert.assertEquals("Wrong number of request records!", 3, count);

		// Verify corresponding request record was updated as expected
		String sql1 = "select approved, system_code, comments from request where request_id="
				+ requestId;
		getJdbcTemplate().query(sql1, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rset) throws SQLException {
				String approved = rset.getString(1);
				String systemCode = rset.getString(2);
				String comments = rset.getString(3);
				Assert.assertNull("System code mismatch!", systemCode);
				Assert.assertEquals("Approval value mismatch!", "N", approved);
				Assert.assertEquals("Comments mismatch!",
						"Rejecting request for CBA account.", comments);
			}
		});

	}

	@Override
	protected SeedFilesMetaData[] getSeedFilesMetaData() {
		log.info("Database Initialization");
		String seedFiles[] = { "DBDataSeedFile.xml" };
		return new SeedFilesMetaData[] { new SeedFilesMetaData(
				"/com/eidiko/process/tests", seedFiles, "SYSTEM") };
	}

	@Override
	protected DBAccess getDBAaccess() {
		log.info("Database Initialization : getDBAaccess");
		return new DBAccess("/com/eidiko/process/tests/DBProps.txt");
	}

	@Override
	protected DatabaseOperation getDeleteMode() {
		log.info("Database Initialization : getDeleteMode");
		return DatabaseOperation.DELETE_ALL;
	}

	@Override
	protected DatabaseOperation getInsertionMode() {
		log.info("Database Initialization : getInsertionMode");
		return DatabaseOperation.CLEAN_INSERT;
	}

}
