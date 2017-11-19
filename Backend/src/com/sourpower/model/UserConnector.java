package com.sourpower.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

public class UserConnector {
	public static PreparedStatement createStatement = null;
	public static PreparedStatement selectStatement = null;
	public static PreparedStatement selectAuthStatement = null;
	
	public static final String TABLE_NAME = "users";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_USERNAME = "username";
	public static final String COLUMN_PASSWORD = "password";
	public static final String COLUMN_SALT = "salt";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_EMAIL = "email";
	
	public UserConnector() {
		if(SQLProvider.connect == null) {
			SQLProvider.connect();
		}
	}
	
	public int create(String username, String password, String salt, String name, String email) throws SQLException {
		if(createStatement == null || createStatement.isClosed()) {
			createStatement = SQLProvider.connect.prepareStatement("INSERT INTO `" + TABLE_NAME + "`(`" + COLUMN_USERNAME + "`, `" + COLUMN_PASSWORD + "`, `" + COLUMN_SALT + "`, `" + COLUMN_NAME + "`, `" + COLUMN_EMAIL + "`) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}
		
		createStatement.setString(1, username);
		createStatement.setString(2, password);
		createStatement.setString(3, salt);
		createStatement.setString(4, name);
		createStatement.setString(5, email);
		
		int row = createStatement.executeUpdate();
		if(row == 0) return -1; // Failed to insert
		
		ResultSet keys = createStatement.getGeneratedKeys();    
		keys.next();  
		return keys.getInt(1); // Return last insert id
	}
	
	public ResultSet selectUserByUsername(String username) throws SQLException {
		if(selectAuthStatement == null || selectAuthStatement.isClosed()) {
			selectAuthStatement = SQLProvider.connect.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE `" + COLUMN_USERNAME + "` = ?");
		}
		
		selectAuthStatement.setString(1, username);
		
		return selectAuthStatement.executeQuery();
	}
	
	public ResultSet selectUser(int userId) throws SQLException {
		if(selectStatement == null || selectStatement.isClosed()) {
			selectStatement = SQLProvider.connect.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE `" + COLUMN_ID + "` = ?");
		}
		
		selectStatement.setInt(1, userId);
		
		return selectStatement.executeQuery();
	}
}
