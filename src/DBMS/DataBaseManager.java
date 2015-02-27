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
import java.sql.Statement;

public class DataBaseManager {
	private static Connection connection;
	
	public DataBaseManager(){
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
	}
	
	public static void createDatabase() {
		try {
			Statement statement = connection.createStatement();
			
			statement.execute(""
					+ "CREATE TABLE User ("
					+ "	username"
					+ "		VARCHAR(10)"
					+ "		NOT NULL"
					+ "		PRIMARY KEY,"
					+ "	name"
					+ "		VARCHAR(30)"
					+ "		NOT NULL,"
					+ "	password"
					+ "		VARCHAR(20)"
					+ "		NOT NULL,"
					+ "	salt"
					+ "		VARCHAR(30)"
					+ "		NOT NULL,"
					+ "	email"
					+ "		VARCHAR(50)"
					+ "		NOT NULL"
					+ ");");
			
			statement.execute(""
					+ "CREATE TABLE Room ("
					+ "	roomID"
					+ "		VARCHAR(10)"
					+ "		NOT NULL"
					+ "		PRIMARY KEY,"
					+ "	size"
					+ "		INT"
					+ "		NOT NULL"
					+ ");");
			
			statement.execute(""
					+ "CREATE TABLE Entry ("
					+ "	entryID"
					+ "		INT"
					+ "		NOT NULL"
					+ "		PRIMARY KEY"
					+ "		AUTO_INCREMENT,"
					+ "	startTime"
					+ "		TIMESTAMP"
					+ "		NOT NULL,"
					+ "	endTime"
					+ "		TIMESTAMP"
					+ "		NOT NULL,"
					+ "	location"
					+ "		VARCHAR(20),"
					+ "	description"
					+ "		VARCHAR(100)"
					+ "		NOT NULL,"
					+ "	isActive"
					+ "		BOOLEAN"
					+ "		DEFAULT TRUE,"
					+ "	roomID"
					+ "		VARCHAR(10)"
					+ "		REFERENCES Room"
					+ ");");
			
			statement.execute(""
					+ "CREATE TABLE Status ("
					+ "	isGoing"
					+ "		BOOLEAN,"
					+ "	isShowing"
					+ "		BOOLEAN"
					+ "		DEFAULT TRUE,"
					+ "	username"
					+ "		VARCHAR(10)"
					+ "		NOT NULL"
					+ "		REFERENCES User"
					+ "		ON DELETE CASCADE"
					+ "		ON UPDATE CASCADE,"
					+ "	entryID"
					+ "		INT"
					+ "		NOT NULL"
					+ "		REFERENCES Entry"
					+ "		ON DELETE CASCADE"
					+ "		ON UPDATE CASCADE,"
					+ "	PRIMARY KEY (username, entryID)"
					+ ");");
			
			statement.execute(""
					+ "CREATE TABLE Notification ("
					+ "	description"
					+ "		VARCHAR(100)"
					+ "		NOT NULL,"
					+ "	isOpened"
					+ "		BOOLEAN"
					+ "		DEFAULT FALSE,"
					+ "	time"
					+ "		TIMESTAMP"
					+ "		NOT NULL,"
					+ "	username"
					+ "		VARCHAR(10)"
					+ "		NOT NULL"
					+ "		REFERENCES User"
					+ "		ON DELETE CASCADE"
					+ "		ON UPDATE CASCADE,"
					+ "	entryID"
					+ "		INT"
					+ "		NOT NULL"
					+ "		REFERENCES Entry"
					+ "		ON UPDATE CASCADE"
					+ "		ON DELETE CASCADE,"
					+ "	PRIMARY KEY (username, entryID)"
					+ ");");
			
			statement.execute(""
					+ "CREATE TABLE IsAdmin ("
					+ "	entryID"
					+ "		VARCHAR(10)"
					+ "		NOT NULL"
					+ "		REFERENCES Entry"
					+ "		ON UPDATE CASCADE"
					+ "		ON DELETE CASCADE,"
					+ "	username"
					+ "		VARCHAR(10)"
					+ "		NOT NULL"
					+ "		REFERENCES User"
					+ "		ON DELETE CASCADE"
					+ "		ON UPDATE CASCADE,"
					+ "	PRIMARY KEY (username, entryID)"
					+ ");");
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
