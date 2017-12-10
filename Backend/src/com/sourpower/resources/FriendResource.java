package com.sourpower.resources;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.sourpower.model.FriendConnector;
import com.sourpower.model.FriendRequestConnector;
import com.sourpower.model.ScoreConnector;
import com.sourpower.model.UserConnector;

public class FriendResource extends ServerResource {
	FriendConnector friendConnector;
	UserConnector userConnector;
	ScoreConnector scoreConnector;
	FriendRequestConnector friendRequestConnector;
	
	@Get
    public Representation getFriends() {
		 if(friendConnector == null) {
			 friendConnector = new FriendConnector();
		 }
		 if(userConnector == null) {
			 userConnector = new UserConnector();
		 }
		 if(scoreConnector == null) {
			 scoreConnector = new ScoreConnector();
		 }
		 
		 JSONObject response = new JSONObject();
		 
		 try {
			 int userId = Integer.parseInt(getQuery().getValues("userId"));
			 
			 ResultSet friends = friendConnector.select(userId);
			 
			 response.put("success", true);
			 
			 JSONArray friendArr = new JSONArray();
			 
			 while(friends.next()) {
				 JSONObject friend = new JSONObject();
				 friend.put("id", friends.getInt(FriendConnector.COLUMN_SECOND));
				 ResultSet user = userConnector.selectUser(friends.getInt(FriendConnector.COLUMN_SECOND));
				 ResultSet score = scoreConnector.selectSum(friends.getInt(FriendConnector.COLUMN_SECOND));
				 user.next();
				 score.next();
				 friend.put("name", user.getString(UserConnector.COLUMN_NAME));
				 friend.put("username", user.getString(UserConnector.COLUMN_USERNAME));
				 friend.put("email", user.getString(UserConnector.COLUMN_EMAIL));
				 friend.put("score", score.getInt("TOTAL"));
				 
				 friendArr.put(friend);
			 }
			 
			 response.put("friends", friendArr);
			 
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
	
	@Post("json")
	public Representation acceptFriendRequest(JsonRepresentation entity) {
		if(friendConnector == null) {
			 friendConnector = new FriendConnector();
		 }
		 if(userConnector == null) {
			 userConnector = new UserConnector();
		 }
		 if(friendRequestConnector == null) {
			 friendRequestConnector = new FriendRequestConnector();
		 }
		 
		 JSONObject data = entity.getJsonObject();
		 int requestee = data.getInt("requestee");
		 int requester = data.getInt("requester");
		 
		 JSONObject response = new JSONObject();
		 
		 try {
			ResultSet result = friendConnector.selectEdge(requester, requestee);
			if (result.next()) {
				response.put("success", false);
				response.put("message", "Two users are already friends");
			} else {
				friendRequestConnector.delete(requester, requestee);
				friendConnector.create(requester, requestee);
				friendConnector.create(requestee, requester);
				
				response.put("success", true);
			}
		} catch (SQLException e) {
			response.put("success", false);
			response.put("message", "SQLException occurred. Please check server logs for details.");
			e.printStackTrace();
		}
		 
		return new JsonRepresentation(response);
	}
}
