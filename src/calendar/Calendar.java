package calendar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Calendar {
	public static void main(String[] args) {
		try {
			String[] creditals = readCreditals();
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/",creditals[0],creditals[1]);
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
}
