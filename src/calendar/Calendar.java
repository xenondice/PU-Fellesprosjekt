package calendar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Calendar {
	public static void main(String[] args) {
		try {
			String[] creditals = readCreditals();
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/",creditals[0],creditals[1]);
			ResultSet res1 = executeStatement(connection, "use staemme");
			ResultSet res2 = executeStatement(connection, "select * from Spieler");
			printResultSet(res2);
			System.out.println("done :D");
		} catch (SQLException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static String[] readCreditals() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("db_id.txt"));
		String username = reader.readLine();
		String password = reader.readLine();
		return new String[]{username,password};
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
				stmt.setString(i, arguments[i]);
			}
			return stmt.executeQuery();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static void printResultSet(ResultSet res){
		ResultSetMetaData rsmd;
		try {
			rsmd = res.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (res.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1)
						System.out.print(",  ");
					String columnValue = res.getString(i);
					System.out.print(columnValue + " " + rsmd.getColumnName(i));
				}
				System.out.println("");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
