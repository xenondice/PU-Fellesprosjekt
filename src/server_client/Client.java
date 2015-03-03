package server_client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import room_booking.RoomBuilder;
import user.User;
import user.UserBuilder;
import calendar.Calendar;
import calendar.EntryBuilder;
import dbms.DataBaseManager;

	/**
	 * Client interface used to connect to ServerClientHandler, which in turn sends requests to the RequestHandler.
	 * Gets output from ServerClientHandler and formats it so it's easy to read.
	 * Might later be replaced by a browser if the project is to be converted to Play.
	 * 
	 * @author Tormod
	 *
	 */
public class Client {
	public static final String DEFAULT_SERVER_ADDRESS = "127.0.0.1";
	public static final int DEFAULT_SERVER_PORT = 80;
	public static final long SERVER_LISTENER_CHECK_INTERVAL = 1000;
	
	private static Socket server_connection;
	private static BufferedWriter server_output;
	private static BufferedReader server_input;
	
	private static BufferedWriter console_output;
	private static BufferedWriter console_error;
	private static BufferedReader console_input;
	
	private static ClientServerListener server_listener;
	private static Thread server_listener_thread;
	
	private static void setup() throws IOException {
		
		// Setup console connection
		console_output = new BufferedWriter(new OutputStreamWriter(System.out));
		console_input = new BufferedReader(new InputStreamReader(System.in));
		console_error = new BufferedWriter(new OutputStreamWriter(System.err));
		
		String address = DEFAULT_SERVER_ADDRESS;
		int port = DEFAULT_SERVER_PORT;
		
		if (!askYesOrNo("Use default address and port?")) {
			List<String> response = ask("Enter server ip-address and port:", 2);
			address = response.get(0);
			port = verifyInt(response.get(1));
		}
		
		try {
			server_connection = new Socket(address, port);
		} catch (IOException e) {
			error("Couldn't connect to server!", true);
		}
		
		message("Successfully connected!");
		message();
		
		// Setup server connection
		server_output = new BufferedWriter(new OutputStreamWriter(server_connection.getOutputStream()));
		server_input = new BufferedReader(new InputStreamReader(server_connection.getInputStream()));
		
		// Setup server listener
		server_listener = new ClientServerListener(console_output, server_input);
		server_listener_thread = new Thread(server_listener);
		server_listener_thread.start();
	}
	
	private static void error(String message, boolean should_exit) throws IOException {
		console_error.write(message);
		console_error.write(System.lineSeparator());
		console_error.flush();
		if (should_exit) System.exit(-1);
	}
	
	private static void message(String message) throws IOException {
		console_output.write(message);
		console_output.write(System.lineSeparator());
		console_output.flush();
	}
	
	private static void message() throws IOException {
		console_output.write(System.lineSeparator());
		console_output.flush();
	}
	
	private static boolean askYesOrNo(String question) throws IOException {
		while (true) {
			message(question + " (answer with yes[y] or no[n])");
		
			List<String> response = consoleInput();
			
			if (response.size() == 1)
				if (response.get(0).equals("yes") || response.get(0).equals("y"))
					return true;
				else if (response.get(0).equals("no") || response.get(0).equals("n"))
					return false;
			
			message("Invalid response!");
			message();
		}
	}
	
	private static List<String> ask(String question, int number_of_arguments) throws IOException {
		while (true) {
			message(question);
		
			List<String> response = consoleInput();
			
			if (response.size() == number_of_arguments)
				return response;
			
			message("Please provide " + number_of_arguments + " argument(s)!");
			message();
		}
	}
	
	private static List<String> consoleInput() throws IOException {
		console_output.write("> ");
		console_output.flush();
		
		String answer = console_input.readLine();
		
		message();
		
		List<String> splitted_answer = new ArrayList<>();
		
		Pattern regex_pattern = Pattern.compile("([^\\s\"']+)|\"([^\"]*)\"|'([^']*)'");
		Matcher matcher = regex_pattern.matcher(answer);
		
		while (matcher.find())
			for (int i = 1; i <= 3; i++)
				if (matcher.group(i) != null)
					splitted_answer.add(matcher.group(i).toLowerCase());
		
		return splitted_answer;
	}
	
	private static int verifyInt(String argument) throws IOException {
		while (true) {
			try {
				return Integer.parseInt(argument);
			} catch (NumberFormatException e) {
				argument = ask("Argument \"" + argument + "\" is not an integer, try again:", 1).get(0);
			}
		}
	}
	
	private static long verifyLong(String argument) throws IOException {
		while (true) {
			try {
				return Long.parseLong(argument);
			} catch (NumberFormatException e) {
				argument = ask("Argument \"" + argument + "\" is not a long, try again:", 1).get(0);
			}
		}
	}
	
	private static void run() throws IOException {
		while (true) {
			
			List<String> response = consoleInput();
			
			if (response.size() > 0 && response.get(0).equals("exit")) return;
			
		}
			
			/*if (response.get(0).equals("calendar")) {
				if (response.size() != 2) {
						System.out.println("Invalid amount of arguments!");
						System.out.println();
						System.out.println("Awating input:");
						break;
					}
					
					User user = dbm.getUser(response.get(1));
					
					if (user == null)
						System.out.println("Couldn't find user!");
					else {
						Calendar cal = dbm.createCalendar(user);
						if (cal.equals(null))
							System.out.println("Failure!");
						else
							System.out.println(cal);
					}
				
				} else if (response.get(0).equals("adduser")) {
					
					if (response.size() != 5) {
						System.out.println("Invalid amount of arguments!");
						System.out.println();
						System.out.println("Awating input:");
						break;
					}
					
					UserBuilder ub = new UserBuilder();
						ub.setUsername(response.get(1));
						ub.setName(response.get(2));
						ub.setPassword(response.get(3));
						ub.setSalt("");
						ub.setEmail(response.get(4));
					
					if (dbm.addUser(ub.build()))
						System.out.println("Success!");
					else
						System.out.println("Failure!");
				
				} else if (response.get(0).equals("addroom")) {
					
					if (response.size() != 3) {
						System.out.println("Invalid amount of arguments!");
						System.out.println();
						System.out.println("Awating input:");
						break;
					}
					
					RoomBuilder rb = new RoomBuilder();
						rb.setRoom_id(response.get(1));
						
					try {
						rb.setSize(Integer.parseInt(response.get(2)));
					} catch (NumberFormatException e) {
						System.out.println("Invalid number!");
						System.out.println();
						System.out.println("Awating input:");
						break;
					}
					
					if (dbm.addRoom(rb.build()))
						System.out.println("Success!");
					else
						System.out.println("Failure!");
				
				} else if (response.get(0).equals("addentry")) {
					
					if (response.size() != 6) {
						System.out.println("Invalid amount of arguments!");
						System.out.println();
						System.out.println("Awating input:");
						break;
					}
					
					EntryBuilder eb = new EntryBuilder();

					try {
						eb.setStartTime(Long.parseLong(response.get(2)));
						eb.setEndTime(Long.parseLong(response.get(3)));
					} catch (NumberFormatException e) {
						System.out.println("Invalid number!");
						System.out.println();
						System.out.println("Awating input:");
						break;
					}
					
						eb.setDescription(response.get(4));
						eb.setLocation(response.get(5));
					
					User user = dbm.getUser(response.get(1));
					
					if (user == null)
						System.out.println("Couldn't find user!");
					else {
						if (dbm.addEntry(eb.build(), user))
							System.out.println("Success!");
						else
							System.out.println("Failure!");
					}
				
				} else if (response.get(0).equals("canedit")) {
					
					if (response.size() != 3) {
						System.out.println("Invalid amount of arguments!");
						System.out.println();
						System.out.println("Awating input:");
						break;
					}
					
					int entryid;
					try {
						entryid = Integer.valueOf(response.get(2));
					} catch (NumberFormatException e) {
						System.out.println("Invalid number!");
						System.out.println();
						System.out.println("Awating input:");
						break;
					}
					
					String username = response.get(1);
					
					if (dbm.getUser(username) == null)
						System.out.println("Couldn't find user!");
					else if (dbm.getEntry(entryid) == null)
						System.out.println("Couldn't find event!");
					else if (dbm.canEdit(username, entryid))
						System.out.println("User can edit entry!");
					else
						System.out.println("User can't edit entry!");
					
				} else if (response.get(0).equals("help")) {
					
					System.out.println("Commands:");
					System.out.println(" * calendar username");
					System.out.println(" * adduser username name (use quotes) password email");
					System.out.println(" * addroom roomid roomsize");
					System.out.println(" * addentry admin_username starttime (milliseconds since 1970 00:00) endtime (same) description location");
					System.out.println(" * canedit username entryid");
					System.out.println(" * exit");
				
				} else if (response.get(0).equals("exit")) {
					
					System.out.println("Exiting...");
					System.exit(0);
				
				} else
					System.out.println("Invalid input, type help for a list over commands");
				
				System.out.println();
				System.out.println("Awating input:");
			}
		}*/
	}
	
	private static void dispose() {
	
	}
	
	public static void main(String[] args) {
		try {
			setup();
			run();
			dispose();
		} catch (IOException e) {
			System.err.println("Critical error!");
			System.exit(-1);
		}
	}
}
