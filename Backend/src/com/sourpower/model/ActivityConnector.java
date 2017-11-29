package com.sourpower.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

public class ActivityConnector {
	public static PreparedStatement createStatement = null;
	public static PreparedStatement selectStatement = null;
	public static PreparedStatement selectByTypeStatement = null;
	public static PreparedStatement selectByUserStatement = null;
	
	public static final String TABLE_NAME = "activities";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_ACTIVITYTYPE = "activityType";
	public static final String COLUMN_SCORE = "score";
	public static final String COLUMN_REMARKS = "remarks";
	public static final String COLUMN_USERID = "userId";
	
	public ActivityConnector() {
		if(SQLProvider.connect == null) {
			SQLProvider.connect();
		}
	}
	
	public int create(String activityType, int score, String remarks, int userId) throws SQLException {
		if(createStatement == null || createStatement.isClosed()) {
			createStatement = SQLProvider.connect.prepareStatement("INSERT INTO `" + TABLE_NAME + "`(`" + COLUMN_ACTIVITYTYPE + "`, `" + COLUMN_SCORE + "`, `" + COLUMN_REMARKS + "`, `" + COLUMN_USERID + "`) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}
		
		createStatement.setString(1, activityType);
		createStatement.setInt(2, score);
		createStatement.setString(3, remarks);
		createStatement.setInt(4, userId);
		
		int row = createStatement.executeUpdate();
		if(row == 0) return -1; // Failed to insert
		return 1;
	}
	
	public ResultSet select(int id) throws SQLException {
		if(selectStatement == null || selectStatement.isClosed()) {
			selectStatement = SQLProvider.connect.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE `" + COLUMN_ID + "` = ?");
		}
		
		selectStatement.setInt(1, id);
		
		return selectStatement.executeQuery();
	}
	
	public ResultSet selectByType(String activityType) throws SQLException {
		if(selectByTypeStatement == null || selectByTypeStatement.isClosed()) {
			selectByTypeStatement = SQLProvider.connect.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE `" + COLUMN_ACTIVITYTYPE + "` = ?");
		}
		
		selectByTypeStatement.setString(1, activityType);
		
		return selectByTypeStatement.executeQuery();
	}
	
	public ResultSet selectByUser(int userId) throws SQLException {
		if(selectByUserStatement == null || selectByUserStatement.isClosed()) {
			selectByUserStatement = SQLProvider.connect.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE `" + COLUMN_USERID + "` = ?");
		}
		
		selectByUserStatement.setInt(1, userId);
		
		return selectByUserStatement.executeQuery();
	}
}
