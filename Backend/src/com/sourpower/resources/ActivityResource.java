package com.sourpower.resources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.sourpower.model.ActivityConnector;

public class ActivityResource extends ServerResource {
	ActivityConnector activityConnector;
	
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
				
				activities.next();
				
				JSONObject activity = new JSONObject();
				activity.put(ActivityConnector.COLUMN_ID, activities.getInt(ActivityConnector.COLUMN_ID));
				activity.put(ActivityConnector.COLUMN_ACTIVITYTYPE, activities.getString(ActivityConnector.COLUMN_ACTIVITYTYPE));
				activity.put(ActivityConnector.COLUMN_REMARKS, activities.getString(ActivityConnector.COLUMN_REMARKS));
				activity.put(ActivityConnector.COLUMN_SCORE, activities.getInt(ActivityConnector.COLUMN_SCORE));
				activity.put(ActivityConnector.COLUMN_USERID, activities.getInt(ActivityConnector.COLUMN_USERID));
				
				JSONObject response = new JSONObject();
				response.put("success", true);
				response.put("activity", activity);
				
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
}
