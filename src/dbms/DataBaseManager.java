package dbms;

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
		
		
		} catch (SQLException e) { 
			e.printStackTrace();
			connection = null;
		}
		
	}
	
	
	
	/**
	 * prepares and executes an executes a SQL statement.
	 * Example of use: ResultSet result = executeStatement(connection, "INSERT INTO User VALUES (name=?, age=?, gender=?)", "Olav", "12", "male");
	 * @param con -> the connection to be used.
	 * @param statement -> eg "INSERT INTO User VALUES (name=?, age=?, gender=?)"
	 * @param arguments -> the string attributes represented by a ? in the statement string.
	 * @return 
	 */
	public static ResultSet executeStatement(Connection con, String statement, String... arguments){
		try {
			PreparedStatement stmt = con.prepareStatement(statement);
			
			for (int i = 0; i < arguments.length; i++){
				stmt.setObject(i+1, arguments[i]);
			}
			return stmt.executeQuery();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	
	
	public static void main(String[] args) {
		
	}
}
