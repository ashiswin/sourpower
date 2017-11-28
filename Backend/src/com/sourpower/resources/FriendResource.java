package com.sourpower.resources;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.sourpower.model.FriendConnector;
import com.sourpower.model.UserConnector;

public class FriendResource extends ServerResource {
	FriendConnector friendConnector;
	
	@Get
    public Representation getFriends() {
		 if(friendConnector == null) {
			 friendConnector = new FriendConnector();
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
}
