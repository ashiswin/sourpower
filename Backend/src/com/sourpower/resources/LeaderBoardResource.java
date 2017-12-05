package com.sourpower.resources;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.sourpower.model.ScoreConnector;

public class LeaderBoardResource extends ServerResource{
	private static ScoreConnector scoreConnector = null;
	
	@Get
	public Representation getTopOverall() {
		if (scoreConnector == null) {
			scoreConnector = new ScoreConnector();
		}
		
		JSONObject response = new JSONObject();
		
		try {
			ResultSet leaderboard = scoreConnector.selectTopOverall();
			if(!leaderboard.first()) {
				response.put("success", false);
				response.put("message", "Select fail. Check ScoreConnector.class and Database");
			}
			else {
				leaderboard.beforeFirst();
				
				JSONArray leaderboardArr = new JSONArray();
				while(leaderboard.next()) {
					JSONObject user = new JSONObject();
					
					user.put(ScoreConnector.COLUMN_ID, leaderboard.getInt(ScoreConnector.COLUMN_ID));
					user.put(ScoreConnector.COLUMN_MENTALWELLNESS, leaderboard.getInt(ScoreConnector.COLUMN_MENTALWELLNESS));
					user.put(ScoreConnector.COLUMN_DIET, leaderboard.getInt(ScoreConnector.COLUMN_DIET));
					user.put(ScoreConnector.COLUMN_FITNESS, leaderboard.getInt(ScoreConnector.COLUMN_FITNESS));
					user.put(ScoreConnector.COLUMN_ACADEMICS, leaderboard.getInt(ScoreConnector.COLUMN_ACADEMICS));
					
					leaderboardArr.put(user);
				}
				response.put("success", true);
				response.put("leaderboard", leaderboardArr);
			}
		} catch(SQLException e) {
			response.put("success", false);
			response.put("message", "SQLException occurred. Please check server logs for details.");
			e.printStackTrace();
		}
		return new JsonRepresentation(response);
	}
}