package com.sourpower.DesignPatterns;

import org.json.JSONObject;
import org.restlet.representation.Representation;

public interface Observer {
	public JSONObject observerUpdate(int userId, String activityType, int score);
}
