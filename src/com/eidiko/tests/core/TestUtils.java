package com.eidiko.tests.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.log4j.Logger;

import bpm.rest.client.BPMClient;
import bpm.rest.client.BPMClientException;
import bpm.rest.client.authentication.AuthenticationTokenHandlerException;

public class TestUtils {

	private BPMClient bpmClient;
	private Logger log;

	public TestUtils(BPMClient bpmClient) {
		super();
		this.bpmClient = bpmClient;
		log = Logger.getLogger(TestUtils.class);
	}

	public JSONObject findTask(JSONObject inbox, int processID, String taskName)
			throws JSONException {

		JSONObject task = null;
		JSONArray tasks = inbox.getJSONObject("data").getJSONArray("data");
		for (int i = 0; i < tasks.length(); i++) {
			JSONObject taskItem = tasks.getJSONObject(i);
			String taskItemName = taskItem.getString("taskActivityName");
			int instanceID = taskItem.getInt("instanceId");
			if (processID == instanceID && taskName.equals(taskItemName)) {
				task = taskItem;
				break;
			}
		}
		return task;
	}

	public boolean isProcessCompleted(int processId) throws BPMClientException,
			AuthenticationTokenHandlerException, JSONException {
		JSONObject results = bpmClient.getBPDInstanceDetails(processId);
		return ("completed".equalsIgnoreCase(results.getJSONObject("data")
				.getString("executionState")));
	}

	public HumanServiceDetails parseStartDetails(JSONObject startDetails)
			throws JSONException {

		// BPM returns the inputs in a JSON-formatted string
		// Once we get the string, we can create the JSON object
		String inputs = startDetails.getJSONObject("data")
				.getJSONObject("return").getString("data");
		// Create JSON object from string
		JSONObject jsonVars = new JSONObject(inputs);

		// Get the coach name
		String coachName = startDetails.getJSONObject("data")
				.getJSONObject("return").getString("step");

		String btns[] = null;
		if (!coachName.equals("End")) {
			// Get the button names on the coach
			JSONArray buttons = startDetails.getJSONObject("data")
					.getJSONObject("return").getJSONObject("actions")
					.getJSONArray("str");
			btns = new String[buttons.length()];
			for (int i = 0; i < buttons.length(); i++) {
				String buttonName = buttons.getString(i);
				btns[i] = buttonName;
			}
		}

		return new HumanServiceDetails(coachName, jsonVars, btns);
	}

	public HumanServiceDetails parseResumeDetails(JSONObject resumeDetails)
			throws JSONException {

		// Get the coach name
		// If coach name is "End", then we have
		// completed the execution of the service
		String coachName = resumeDetails.getJSONObject("data")
				.getString("step");
		log.info("coachName: " + coachName);

		JSONObject variables = resumeDetails.getJSONObject("data")
				.getJSONObject("data");

		String btns[] = null;
		if (!coachName.equals("End")) {
			// Get the button names on the coach
			JSONArray buttons = resumeDetails.getJSONObject("data")
					.getJSONArray("actions");
			btns = new String[buttons.length()];
			for (int i = 0; i < buttons.length(); i++) {
				String buttonName = buttons.getString(i);
				btns[i] = buttonName;
			}
		}

		return new HumanServiceDetails(coachName, variables, btns);
	}

}
