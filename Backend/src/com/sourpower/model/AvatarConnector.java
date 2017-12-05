package com.sourpower.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class AvatarConnector {
	private static PreparedStatement createStatement = null;
	private static PreparedStatement selectStatement = null;
	private static PreparedStatement updateStatement = null;
	
	public static final String TABLE_NAME = "avatar";
	public static final String COLUMN_ID = "userId";
	public static final String COLUMN_HAT = "hat";
	public static final String COLUMN_TOP = "top";
	public static final String COLUMN_BOTTOM = "bottom";
	public static final String COLUMN_SHOES = "shoes";
	public static final String COLUMN_MOUNT = "mount";
	
	public AvatarConnector() {
		if(SQLProvider.connect == null) {
			SQLProvider.connect();
		}
	}
	
	public int create(int userId, int hat, int top, int bottom, int shoes, int mount) throws SQLException {
		if(createStatement == null || createStatement.isClosed()) {
			createStatement = SQLProvider.connect.prepareStatement("INSERT INTO `" + TABLE_NAME + "`(`" 
						+ COLUMN_ID + "`, `" + COLUMN_HAT + "`, `" + COLUMN_TOP + "`, `" + COLUMN_BOTTOM 
						+ "`, `" + COLUMN_SHOES + "`, `" + COLUMN_MOUNT + "`) VALUES(?, ?, ?, ?, ?, ?)"
						, Statement.RETURN_GENERATED_KEYS);
		}
		
		createStatement.setInt(1, userId);
		createStatement.setInt(2, hat);
		createStatement.setInt(3, top);
		createStatement.setInt(4, bottom);
		createStatement.setInt(5, shoes);
		createStatement.setInt(6, mount);
		
		int row = createStatement.executeUpdate();
		if(row == 0) return -1; // Failed to insert
		return 1;
	}
	
	public ResultSet select(int userId) throws SQLException {
		if(selectStatement == null || selectStatement.isClosed()) {
			selectStatement = SQLProvider.connect.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE `" + COLUMN_ID + "` = ?");
		}
		
		selectStatement.setInt(1, userId);
		
		return selectStatement.executeQuery();
	}
	
	public int update(int userId, String itemType, int itemId) throws SQLException {
		if(updateStatement == null || updateStatement.isClosed()) {
			updateStatement = SQLProvider.connect.prepareStatement("UPDATE `" + TABLE_NAME + "` SET `" + itemType + "` = ? WHERE `" + COLUMN_ID + "` = ?");
		}
		
		updateStatement.setString(1, itemType);
		updateStatement.setInt(2, itemId);
		
		return updateStatement.executeUpdate();
	}
}
