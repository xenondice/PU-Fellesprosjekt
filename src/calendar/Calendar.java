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
import java.util.ArrayList;

import user.User;

public class Calendar {
	private final ArrayList<Entry> entries;
	private final ArrayList<User> users;
	
	public Calendar(ArrayList<Entry> entries, ArrayList<User> users){
		this.entries = entries;
		this.users = users;
	}
	
	
	public ArrayList<Entry> getEntries() {
		ArrayList<Entry> clonedList = new ArrayList<>();
		for(Entry e : entries){
			clonedList.add(e.clone());
		}
		return clonedList;
	}
	
	public ArrayList<User> getUsers() {
		ArrayList<User> clonedList = new ArrayList<>();
		for(User u : users){
			clonedList.add(u.clone());
		}
		return clonedList;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		try {
			String[] creditals = readCreditals();
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(creditals[0],creditals[1],creditals[2]);
			//ResultSet res2 = executeStatement(connection, "select * from Spieler");
			//printResultSet(res2);
			System.out.println("done :D");
		} catch (SQLException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static String[] readCreditals() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("db_id.txt"));
		String address = reader.readLine();
		String username = reader.readLine();
		String password = reader.readLine();
		return new String[]{address,username,password};
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
