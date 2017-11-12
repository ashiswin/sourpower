package com.sourpower.resources;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.sourpower.Util;
import com.sourpower.model.UserConnector;

public class AuthenticationResource extends ServerResource {
	private static UserConnector userConnector = null;
	
	
	
	@Post("json")
	public Representation authenticate(JsonRepresentation entity) {
		JSONObject data = entity.getJsonObject();
		String username = data.getString("username");
		String password = data.getString("password");
		
		if(userConnector == null) {
			userConnector = new UserConnector();
		}
		
		JSONObject response = new JSONObject();
		ResultSet user;
		try {
			user = userConnector.selectUserByUsername(username);
			user.last();
			if(user.getRow() == 0) {
				response.put("success", false);
				response.put("message", "Invalid username or password");
			}
			else {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] encodedhash = digest.digest((password + user.getString(UserConnector.COLUMN_SALT)).getBytes(StandardCharsets.UTF_8));
				String passwordHash = Util.bytesToHex(encodedhash);
				
				if(passwordHash.equals(user.getString(UserConnector.COLUMN_PASSWORD))) {
					response.put("success", true);
					JSONObject userObject = new JSONObject();
					userObject.put("id", user.getInt(UserConnector.COLUMN_ID));
					userObject.put("email", user.getString(UserConnector.COLUMN_EMAIL));
					userObject.put("name", user.getString(UserConnector.COLUMN_NAME));
					response.put("user", userObject);
				}
				else {
					response.put("success", false);
					response.put("message", "Invalid username or password");
				}
			}
		} catch (SQLException e) {
			response.put("success", false);
			response.put("message", "SQLException occurred. Please check server logs for details.");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			response.put("success", false);
			response.put("message", "NoSuchAlgorithm occurred. Please check server logs for details.");
			e.printStackTrace();
		}
		
        Representation result = new JsonRepresentation(response);
        return result;
   }
}
