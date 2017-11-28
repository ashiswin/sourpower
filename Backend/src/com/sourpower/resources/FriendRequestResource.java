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
import com.sourpower.model.FriendRequestConnector;

public class FriendRequestResource extends ServerResource {
	FriendRequestConnector friendRequestConnector;
	
	@Get
    public Representation getFriendRequests() {
		 if(friendRequestConnector == null) {
			 friendRequestConnector = new FriendRequestConnector();
		 }
		 
		 JSONObject response = new JSONObject();
		 
		 try {
			 int userId = Integer.parseInt(getQuery().getValues("userId"));
			 
			 ResultSet friends = friendRequestConnector.select(userId);
			 
			 response.put("success", true);
			 
			 JSONArray friendRequestArr = new JSONArray();
			 
			 while(friends.next()) {
				 JSONObject friend = new JSONObject();
				 friend.put("id", friends.getInt(FriendConnector.COLUMN_SECOND));
				 friendRequestArr.put(friend);
			 }
			 
			 response.put("friendRequests", friendRequestArr);
			 
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
