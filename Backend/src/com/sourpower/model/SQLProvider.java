package com.sourpower.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLProvider {
	public static Connection connect = null;
	
	public static final String DB_NAME = "sourpower";
	public static final String DB_USER = "sourpower";
	public static final String DB_PASSWORD = "sourpower";
	public static void connect() {
		if(connect != null) return;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
	        connect = DriverManager.getConnection("jdbc:mysql://localhost/" + DB_NAME + "?" + "user=" + DB_USER + "&password=" + DB_PASSWORD);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
