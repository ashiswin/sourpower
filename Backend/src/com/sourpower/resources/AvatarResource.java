package com.sourpower.resources;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.sourpower.model.AvatarConnector;
import com.sourpower.model.ScoreConnector;

public class AvatarResource extends ServerResource{
	AvatarConnector avatarConnector;
	
	@Get
	public Representation getUserAvatar() {
		if(avatarConnector == null) {
			avatarConnector = new AvatarConnector();
		}
		JSONObject response = new JSONObject();
		
		try {
			 int userId = Integer.parseInt(getQuery().getValues("userId"));
			 
			 ResultSet user = avatarConnector.select(userId);
			 if(!user.next()) {
				 response.put("success", false);
				 response.put("message", "User does not exist");
			 }
			 else {
				 response.put("success", true);
				 
				 JSONObject userObject = new JSONObject();
				 userObject.put("userId", user.getInt(ScoreConnector.COLUMN_ID));
				 userObject.put("hat", user.getInt(AvatarConnector.COLUMN_HAT));
				 userObject.put("top", user.getInt(AvatarConnector.COLUMN_TOP));
				 userObject.put("bottom", user.getInt(AvatarConnector.COLUMN_MOUNT));
				 userObject.put("shoes", user.getInt(AvatarConnector.COLUMN_SHOES));
				 userObject.put("mount", user.getInt(AvatarConnector.COLUMN_MOUNT));
				 
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
	public Representation updateUserAvatar() {
		if(avatarConnector == null) {
			avatarConnector = new AvatarConnector();
		}
		JSONObject response = new JSONObject();
		
		try {
			int userId = Integer.parseInt(getQuery().getValues("userId"));
			String itemType = getQuery().getValues("itemType");
			int itemId = Integer.parseInt(getQuery().getValues("itemId"));
			
			int result = avatarConnector.update(userId, itemType, itemId);
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
