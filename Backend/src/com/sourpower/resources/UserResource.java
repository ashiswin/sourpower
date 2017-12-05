package com.sourpower.resources;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.sourpower.Util;
import com.sourpower.model.AvatarConnector;
import com.sourpower.model.ScoreConnector;
import com.sourpower.model.UserConnector;

public class UserResource extends ServerResource {
	private static UserConnector userConnector = null;
	private static ScoreConnector scoreConnector = null;
	private static AvatarConnector avatarConnector = null;
	
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	public static boolean validate(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}
	
	@Post("json")
	public Representation register(JsonRepresentation entity) {
		if(userConnector == null) {
			 userConnector = new UserConnector();
		}
		if(scoreConnector == null) {
			scoreConnector = new ScoreConnector();
		}
		if(avatarConnector == null) {
			avatarConnector = new AvatarConnector();
		}
		
		JSONObject data = entity.getJsonObject();
		
		String username = data.getString("username");
		String password = data.getString("password");
		String name = data.getString("name");
		String email = data.getString("email");
		
		String salt = Util.getSaltString();
		
		JSONObject response = new JSONObject();
		
		// Verify inputs
		if(!validate(email)) {
			response.put("success", false);
			response.put("message", "Email is invalid");
				
			return new JsonRepresentation(response);
		}
		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest((password + salt).getBytes(StandardCharsets.UTF_8));
			String passwordHash = Util.bytesToHex(encodedhash);
			
			int userId = userConnector.create(username, passwordHash, salt, name, email);
			scoreConnector.create(userId, 0, 0, 0, 0);
			avatarConnector.create(userId, 0, 0, 0, 0, 0);
			
			if(userId != -1) {
				response.put("success", true);
				response.put("userId", userId);
			}
			else {
				response.put("success", false);
				response.put("message", "Unable to create user");
			}
		} catch (NoSuchAlgorithmException e) {
			response.put("success", false);
			response.put("message", "NoSuchAlgorithm occurred. Please check server logs for details.");
			e.printStackTrace();
		} catch (SQLException e) {
			response.put("success", false);
			response.put("message", "SQLException occurred. Please check server logs for details.");
			e.printStackTrace();
		}
		
		return new JsonRepresentation(response);
	}
	 @Get
    public Representation getUser() {
		 if(userConnector == null) {
			 userConnector = new UserConnector();
		 }
		 
		 JSONObject response = new JSONObject();
		 
		 try {
			 int userId = Integer.parseInt(getQuery().getValues("userId"));
			 
			 ResultSet user = userConnector.selectUser(userId);
			 if(!user.next()) {
				 response.put("success", false);
				 response.put("message", "User does not exist");
			 }
			 else {
				 response.put("success", true);
				 
				 JSONObject userObject = new JSONObject();
				 userObject.put("id", user.getInt(UserConnector.COLUMN_ID));
				 userObject.put("username", user.getString(UserConnector.COLUMN_USERNAME));
				 userObject.put("email", user.getString(UserConnector.COLUMN_EMAIL));
				 userObject.put("name", user.getString(UserConnector.COLUMN_NAME));
				 
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
}
