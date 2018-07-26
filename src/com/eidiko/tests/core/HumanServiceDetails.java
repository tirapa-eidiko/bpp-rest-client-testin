package com.eidiko.tests.core;

import org.json.JSONObject;

public class HumanServiceDetails {
	private String coach;
	private JSONObject variables;
	private String[] buttons;
	private boolean serviceCompleted = false;

	public HumanServiceDetails(String coach, JSONObject variables,
			String[] buttons) {
		super();
		this.coach = coach;
		this.variables = variables;
		if (buttons == null || buttons.length == 0) {
			this.buttons = new String[0];
			this.serviceCompleted = true;
		} else {
			this.buttons = buttons;
		}
	}

	public String getCoach() {
		return coach;
	}

	public JSONObject getVariables() {
		return variables;
	}

	public String[] getButtons() {
		return buttons;
	}

	public boolean isCompleted() {
		return serviceCompleted;
	}

	@Override
	public String toString() {
		return "coach: " + coach + "\nvariables: " + variables.toString()
				+ "\ntaskComplete: " + serviceCompleted;
	}
}
