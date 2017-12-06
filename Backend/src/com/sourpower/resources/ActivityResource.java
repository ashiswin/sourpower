package com.sourpower.resources;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.sourpower.model.ActivityConnector;
import com.sourpower.model.ScoreConnector;

public class ActivityResource extends ServerResource{
	ActivityConnector activityConnector;
	ScoreResource scoreResource;
	
	@Get
	public Representation getActivities() {
		if(activityConnector == null) {
			activityConnector = new ActivityConnector();
		}
		
		try {
			Map<String, String> values = getQuery().getValuesMap();
			ResultSet activities;
			if(values.containsKey("userId")) {
				int userId = Integer.parseInt(values.get("userId"));
				
				activities = activityConnector.selectByUser(userId);
				
				JSONArray activityArr = new JSONArray();
				
				while(activities.next()) {
					JSONObject activity = new JSONObject();
					activity.put(ActivityConnector.COLUMN_ID, activities.getInt(ActivityConnector.COLUMN_ID));
					activity.put(ActivityConnector.COLUMN_ACTIVITYTYPE, activities.getString(ActivityConnector.COLUMN_ACTIVITYTYPE));
					activity.put(ActivityConnector.COLUMN_REMARKS, activities.getString(ActivityConnector.COLUMN_REMARKS));
					activity.put(ActivityConnector.COLUMN_SCORE, activities.getInt(ActivityConnector.COLUMN_SCORE));
					activity.put(ActivityConnector.COLUMN_USERID, activities.getInt(ActivityConnector.COLUMN_USERID));
					
					activityArr.put(activity);
				}
				
				JSONObject response = new JSONObject();
				response.put("success", true);
				response.put("activities", activityArr);
				
				return new JsonRepresentation(response);
			}
			else if(values.containsKey("activityId")) {
				int activityId = Integer.parseInt(values.get("activityId"));
				
				activities = activityConnector.select(activityId);
				
				JSONObject response = new JSONObject();
				
				if(activities.next()) {
					JSONObject activity = new JSONObject();
					activity.put(ActivityConnector.COLUMN_ID, activities.getInt(ActivityConnector.COLUMN_ID));
					activity.put(ActivityConnector.COLUMN_ACTIVITYTYPE, activities.getString(ActivityConnector.COLUMN_ACTIVITYTYPE));
					activity.put(ActivityConnector.COLUMN_REMARKS, activities.getString(ActivityConnector.COLUMN_REMARKS));
					activity.put(ActivityConnector.COLUMN_SCORE, activities.getInt(ActivityConnector.COLUMN_SCORE));
					activity.put(ActivityConnector.COLUMN_USERID, activities.getInt(ActivityConnector.COLUMN_USERID));

					response.put("success", true);
					response.put("activity", activity);
				}
				else {
					response.put("success", false);
					response.put("message", "Activity not found");
				}
				
				return new JsonRepresentation(response);
			}
			else {
				JSONObject response = new JSONObject();
				response.put("success", false);
				response.put("message", "Insufficient inputs to function");
				
				return new JsonRepresentation(response);
			}
		} catch(SQLException e) {
			JSONObject response = new JSONObject();
			response.put("success", false);
			response.put("message", "SQLException occurred. Please check server logs for details.");
			e.printStackTrace();
			
			return new JsonRepresentation(response);
		}
	}
	
	@Post("json")
	public Representation addActivity(JsonRepresentation entity) {
		if(activityConnector == null) {
			activityConnector = new ActivityConnector();
		}
		if(scoreResource == null) {
			scoreResource = new ScoreResource();
		}
		
		JSONObject data = entity.getJsonObject();
		
		String activityType = data.getString("activityType");
		String remarks = data.getString("remarks");
		int score = data.getInt("score");
		int userId = data.getInt("userId");
		
		JSONObject response = new JSONObject();
		
		try {
			if(activityConnector.create(activityType, score, remarks, userId) >= 1) {
				response.put("success", true);
				scoreResource.observerUpdate(userId, activityType, score);
			}
			else {
				response.put("success", false);
				response.put("message", "Unable to add activity");
			}
			
		} catch (SQLException e) {
			response.put("success", false);
			response.put("message", "SQLException occurred. Please check server logs for details.");
			e.printStackTrace();
		}
		
		return new JsonRepresentation(response);
	}
}
