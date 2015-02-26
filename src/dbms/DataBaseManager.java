package DBMS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DataBaseManager {
	private Connection connection;
	
	
	public DataBaseManager(){
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://mysql.stud.ntnu.no/mariessa_pu", "mariessa_pu", "fellesprosjekt");
			JPAQuery query = new JPAQuery(entityManager);
		
		
		
		} catch (SQLException e) { 
			e.printStackTrace();
			connection = null;
		}
		
	}
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
	}
}
