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

import com.sourpower.model.FriendRequestConnector;
import com.sourpower.model.UserConnector;

public class FriendRequestResource extends ServerResource {
	FriendRequestConnector friendRequestConnector;
	UserConnector userConnector;
	
	@Get
    public Representation getFriendRequests() {
		 if(friendRequestConnector == null) {
			 friendRequestConnector = new FriendRequestConnector();
		 }
		 if(userConnector == null) {
			 userConnector = new UserConnector();
		 }
		 
		 JSONObject response = new JSONObject();
		 
		 try {
			 int userId = Integer.parseInt(getQuery().getValues("userId"));
			 
			 ResultSet friends = friendRequestConnector.select(userId);
			 
			 response.put("success", true);
			 
			 JSONArray friendRequestArr = new JSONArray();
			 
			 while(friends.next()) {
				 JSONObject friend = new JSONObject();
				 friend.put("id", friends.getInt(FriendRequestConnector.COLUMN_REQUESTER));
				 ResultSet user = userConnector.selectUser(friends.getInt(FriendRequestConnector.COLUMN_REQUESTER));
				 user.next();
				 friend.put("name", user.getString(UserConnector.COLUMN_NAME));
				 friend.put("username", user.getString(UserConnector.COLUMN_USERNAME));
				 friend.put("email", user.getString(UserConnector.COLUMN_EMAIL));
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
	
	@Post("json")
	public Representation sendRequest(JsonRepresentation entity) {
		if(friendRequestConnector == null) {
			 friendRequestConnector = new FriendRequestConnector();
		}
		
		JSONObject data = entity.getJsonObject();
		
		int requester = data.getInt("userId");
		int requestee = data.getInt("requestee");
		
		// TODO: Verify inputs, check if request had been sent before
		
		JSONObject response = new JSONObject();
		try {
			if(friendRequestConnector.create(requester, requestee) == 1) {
				response.put("success", true);
			}
			else {
				response.put("success", false);
				response.put("message", "Unable to submit friend request");
			}
		} catch (SQLException e) {
			response.put("success", false);
			response.put("message", "SQLException occurred. Please check server logs for details.");
			e.printStackTrace();
		}
		
		return new JsonRepresentation(response);
	}
}
