package com.sourpower.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

public class ScoreConnector {
	public static PreparedStatement createStatement = null;
	public static PreparedStatement selectStatement = null;
	
	public static final String TABLE_NAME = "scores";
	public static final String COLUMN_ID = "userId";
	public static final String COLUMN_MENTALWELLNESS = "mentalWellness";
	public static final String COLUMN_DIET = "diet";
	public static final String COLUMN_FITNESS = "fitness";
	public static final String COLUMN_ACADEMICS = "academics";
	
	public ScoreConnector() {
		if(SQLProvider.connect == null) {
			SQLProvider.connect();
		}
	}
	
	public int create(int userId, int mentalWellness, int diet, int fitness, int academics) throws SQLException {
		if(createStatement == null) {
			createStatement = SQLProvider.connect.prepareStatement("INSERT INTO `" + TABLE_NAME + "`(`" + COLUMN_USERID + "`, `" + COLUMN_MENTALWELLNESS + "`, `" + COLUMN_DIET + "`, `" + COLUMN_FITNESS + "`, `" + COLUMN_ACADEMICS + "`) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}
		
		createStatement.setInt(1, userId);
		createStatement.setInt(2, mentalWellness);
		createStatement.setInt(3, diet);
		createStatement.setInt(4, fitness);
		createStatement.setInt(5, academics);
		
		int row = createStatement.executeUpdate();
		if(row == 0) return -1; // Failed to insert
		return 1;
	}
	
	public ResultSet select(int userId) throws SQLException {
		if(selectStatement == null) {
			selectStatement = SQLProvider.connect.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE `" + COLUMN_ID + "` = ?");
		}
		
		selectStatement.setInt(1, userId);
		
		return selectStatement.executeQuery();
	}
}
