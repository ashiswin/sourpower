package com.sourpower.resources;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.sourpower.model.UserConnector;

public class UserSearchResource extends ServerResource {
	UserConnector userConnector;
	
	@Get
	public Representation searchUser() {
		if(userConnector == null) {
			 userConnector = new UserConnector();
		 }
		 
		 JSONObject response = new JSONObject();
		 
		 try {
			 String searchTerm = getQuery().getValues("searchTerm");
			 
			 ResultSet users = userConnector.selectUserSearch(searchTerm);
			 
			 response.put("success", true);
			 
			 JSONArray userArr = new JSONArray();
			 
			 while(users.next()) {
				 JSONObject userObject = new JSONObject();
				 userObject.put("id", users.getInt(UserConnector.COLUMN_ID));
				 userObject.put("username", users.getString(UserConnector.COLUMN_USERNAME));
				 userObject.put("email", users.getString(UserConnector.COLUMN_EMAIL));
				 userObject.put("name", users.getString(UserConnector.COLUMN_NAME));
				 
				 userArr.put(userObject);
			 }
			 
			 response.put("user", userArr);
			 
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
