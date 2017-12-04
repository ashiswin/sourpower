package com.sourpower.resources;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.sourpower.model.ScoreConnector;

public class ScoreResource extends ServerResource {
	private static ScoreConnector scoreConnector = null;
	
	@Get
	public Representation getUserScore() {
		if (scoreConnector == null) {
			scoreConnector = new ScoreConnector();
		}
		
		JSONObject response = new JSONObject();
		
		try {
			 int userId = Integer.parseInt(getQuery().getValues("userId"));
			 
			 ResultSet user = scoreConnector.select(userId);
			 if(!user.next()) {
				 response.put("success", false);
				 response.put("message", "User does not exist");
			 }
			 else {
				 response.put("success", true);
				 
				 JSONObject userObject = new JSONObject();
				 userObject.put("id", user.getInt(ScoreConnector.COLUMN_ID));
				 userObject.put("mentalWellness", user.getString(ScoreConnector.COLUMN_MENTALWELLNESS));
				 userObject.put("diet", user.getString(ScoreConnector.COLUMN_DIET));
				 userObject.put("fitness", user.getString(ScoreConnector.COLUMN_FITNESS));
				 userObject.put("academics", user.getString(ScoreConnector.COLUMN_ACADEMICS));
				 
				 response.put("user", userObject);
			 }
			 
			 System.out.println(response.toString());
		 }
		 catch (NumberFormatException e) {
			 response.put("success", false);
			 response.put("message", "Invalid format for ID provided");
			 e.printStackTrace();
		 } catch (SQLException e) {
			response.put("success", false);
			response.put("message", "SQLException occurred. Please check server logs for details.");
			e.printStackTrace();
		}
		 
        return new JsonRepresentation(response);
	}
	
	@Post
	public Representation updateUserScore( ) {
		if (scoreConnector == null) {
			scoreConnector = new ScoreConnector();
		}
		
		JSONObject response = new JSONObject();
		
		try {
			int userId = Integer.parseInt(getQuery().getValues("userId"));
			String activityType = getQuery().getValues("activityType");
			int score = Integer.parseInt(getQuery().getValues("score"));
			
			int result = scoreConnector.update(userId, activityType, score);
			if(result == 0) {
				response.put("success", false);
				response.put("message", "No rows updated. Check parameters");
			} 
			else if (result == 1) {
				response.put("success", true);
			}
			else {
				response.put("success", false);
				response.put("message", "Multiple rows updated. Check database and parameters");
			}
		} catch (SQLException e) {
			response.put("success", false);
			response.put("message", "SQLException occurred. Please check server logs for details.");
			e.printStackTrace();
		}
		
		return new JsonRepresentation(response);
	}
	
}
