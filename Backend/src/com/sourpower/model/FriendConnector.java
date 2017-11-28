package com.sourpower.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendConnector {
	public static PreparedStatement createStatement = null;
	public static PreparedStatement selectStatement = null;
	
	public static final String TABLE_NAME = "friends";
	public static final String COLUMN_FIRST = "first";
	public static final String COLUMN_SECOND = "second";
	
	public FriendConnector() {
		if(SQLProvider.connect == null) {
			SQLProvider.connect();
		}
	}
	
	public int create(int first, int second) throws SQLException {
		if(createStatement == null || createStatement.isClosed()) {
			createStatement = SQLProvider.connect.prepareStatement("INSERT INTO `" + TABLE_NAME + "`(`" + COLUMN_FIRST + "`, `" + COLUMN_SECOND + "`) VALUES(?, ?)");
		}
		
		createStatement.setInt(1, first);
		createStatement.setInt(2, second);
		
		int row = createStatement.executeUpdate();
		if(row == 0) return -1; // Failed to insert
		
		return 1;
	}
	
	public ResultSet select(int userId) throws SQLException {
		if(selectStatement == null || selectStatement.isClosed()) {
			selectStatement = SQLProvider.connect.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE `" + COLUMN_FIRST + "` = ?");
		}
		
		selectStatement.setInt(1, userId);
		
		return selectStatement.executeQuery();
	}
}
