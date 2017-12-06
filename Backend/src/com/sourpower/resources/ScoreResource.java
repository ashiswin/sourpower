package com.sourpower.resources;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.sourpower.DesignPatterns.Observer;
import com.sourpower.model.ScoreConnector;

public class ScoreResource extends ServerResource implements Observer {
	private static ScoreConnector scoreConnector = null;
	private static final List<String> activityTypes = Arrays.asList("mentalWellness", "diet", "fitness", "academics");
	
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
				 userObject.put("userId", user.getInt(ScoreConnector.COLUMN_ID));
				 userObject.put("mentalWellness", user.getInt(ScoreConnector.COLUMN_MENTALWELLNESS));
				 userObject.put("diet", user.getInt(ScoreConnector.COLUMN_DIET));
				 userObject.put("fitness", user.getInt(ScoreConnector.COLUMN_FITNESS));
				 userObject.put("academics", user.getInt(ScoreConnector.COLUMN_ACADEMICS));
				 
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

	public JSONObject observerUpdate(int userId, String activityType, int score) {
		if (scoreConnector == null) {
			scoreConnector = new ScoreConnector();
		}
		
		JSONObject response = new JSONObject();
		
		try {
			ResultSet user = scoreConnector.select(userId);
			
			if(user.next()) {
				int newScore = user.getInt(activityType) + ScoreResource.calculateWeightedScore(activityType, score);
				scoreConnector.update(userId, activityType, newScore);
			}
			
			else {
				response.put("success", false);
				response.put("message", "Unable to find user");
			}
			
		} catch (SQLException e) {
			response.put("success", false);
			response.put("message", "SQLException occurred. Please check server logs for details.");
			e.printStackTrace();
		}
		
		return response;
	}
	
	public static int calculateWeightedScore(String activityType, int score) {
		int index = activityTypes.indexOf(activityType);
		switch(index) {
		case 0:										// mentalWellness, comes in form of attendance
			return score*10;
		case 1:										// diet, comes in calories
			double b = -Math.abs(score - 2600);
			return (int) Math.round(10*Math.pow(Math.E, b));
		case 2:										// fitness, comes in step count
			return (int) Math.round(2*Math.log(score));
		case 3:										// academics, comes in GPA
			return (int) Math.round(100/5*score);
		default:
			return 0;
		}
	}
	
}
