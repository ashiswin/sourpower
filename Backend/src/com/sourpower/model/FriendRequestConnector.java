package com.sourpower.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendRequestConnector {
	public static PreparedStatement createStatement = null;
	public static PreparedStatement selectStatement = null;
	public static PreparedStatement deleteStatement = null;
	
	public static final String TABLE_NAME = "friendrequests";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_REQUESTER = "requester";
	public static final String COLUMN_REQUESTEE = "requestee";
	
	public FriendRequestConnector() {
		if(SQLProvider.connect == null) {
			SQLProvider.connect();
		}
	}
	
	public int create(int requester, int requestee) throws SQLException {
		if(createStatement == null || createStatement.isClosed()) {
			createStatement = SQLProvider.connect.prepareStatement("INSERT INTO `" + TABLE_NAME + "`(`" + COLUMN_REQUESTER + "`, `" + COLUMN_REQUESTEE + "`) VALUES(?, ?)");
		}
		
		createStatement.setInt(1, requester);
		createStatement.setInt(2, requestee);
		
		int row = createStatement.executeUpdate();
		if(row == 0) return -1; // Failed to insert
		
		return 1;
	}
	
	public ResultSet select(int userId) throws SQLException {
		if(selectStatement == null || selectStatement.isClosed()) {
			selectStatement = SQLProvider.connect.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE `" + COLUMN_REQUESTEE + "` = ?");
		}
		
		selectStatement.setInt(1, userId);
		
		return selectStatement.executeQuery();
	}
	
	public int delete(int requester, int requestee) throws SQLException {
		if(deleteStatement == null || deleteStatement.isClosed()) {
			deleteStatement = SQLProvider.connect.prepareStatement("DELETE FROM `" + TABLE_NAME + "` WHERE " + COLUMN_REQUESTER + "=? AND `" + COLUMN_REQUESTEE + "`=?");
		}
		
		deleteStatement.setInt(1, requester);
		deleteStatement.setInt(2, requestee);
		
		return deleteStatement.executeUpdate();
	}
}
