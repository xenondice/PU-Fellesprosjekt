package server_client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	public static final long SERVER_LISTENER_CHECK_INTERVAL = 100;
	public static final long WAIT_BEFORE_TIMEOUT = 15000;
	public static final String ESCAPE_SENTENCE = "\\c";
	
	// Protocol declaration (headers to messages)
	public static final int STATUS_DONE =			0;
	public static final int STATUS_AWAIT_MORE =		1;
	public static final int STATUS_DISCONNECT =		2;
	public static final int STATUS_NOTIFICATION =	3;
	public static final int STATUS_RETURN =			4;
	
	private static boolean can_write = true;
	private static boolean can_recieve_input = true;
	private static boolean connected = false;
	
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
		
		connected = true;
		message("Successfully connected!");
		message();
		
		// Setup server connection
		server_output = new BufferedWriter(new OutputStreamWriter(server_connection.getOutputStream()));
		server_input = new BufferedReader(new InputStreamReader(server_connection.getInputStream()));
		
		// Setup server listener
		server_listener = new ClientServerListener(console_output, server_input, Thread.currentThread());
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
	
	private static void sendRequest(String request) throws TimeoutException, IOException {
		can_write = false;
		server_output.write(STATUS_DONE + request + System.lineSeparator());
		server_output.flush();
		
		waitForEnd();
	}
	
	private static void waitForEnd() throws TimeoutException {
		while (true) {
			try {
				Thread.sleep(WAIT_BEFORE_TIMEOUT);
				throw new TimeoutException();
			} catch (InterruptedException e) {
				if (can_write) return;
			}
		}
	}
	
	public static void markEnd() {
		can_write = true;
	}
	
	public static void disconnect() throws IOException {
		server_output.write(STATUS_DISCONNECT);
		server_output.flush();
		connected = false;
	}
	
	private static String requestConsoleInput() throws IOException {
		
		can_recieve_input = false;
		
		String request = "";
		
		console_output.write("$ ");
		console_output.flush();
		
		request = console_input.readLine().trim().toLowerCase();
			
		message();
		
		can_recieve_input = true;
		
		return request;
	}
	
	public static boolean ready() {
		return can_recieve_input;
	}
	
	private static void cancel() throws IOException {
		server_output.write(STATUS_RETURN);
		server_output.flush();
	}
	
	private static void run() throws IOException {
		
		try {
			can_write = false;
			waitForEnd();
		} catch (TimeoutException e) {
		} //TODO: Change system.in to console.io to hide passwords and handle messages from server properly
		  //Make a date argument type in wizard to make it easier to write in dates
		  //Make some arguments optional in wizard and accept blank line
		  //Fix session expired to display the same message always, catch it in outer block
		  //Make a input taker other than expect input that takes in a whole line with spaces as one argument for the wizard
		  //Send information about a result if it's in a command or if its at the end, remove all formatting tools and buffer stuff, just have send()
		  //Make the serverhandler not time out, just let the session validation handle it?
		  //make ask take in a list of ints that define the number of arguments it takes, like either 0 or 1 arg, update help and remove commands
		  //fix broken commands, add view user, add view calendar, add notifications to welcome message, add show rooms/available rooms
		  // Make the start new command (expect input) quit if something is wrong and throw forced exit
		  // Make calendar gui. pretty
		
		//Add functions for room boking in RH (get rooms, create room, edit room, check available, reserve, cancel)
		//Notification
		//Get all rooms
		//GUI for calendar
		
		while (connected) {
			
			String request = requestConsoleInput();
			
			if (request.equals("exit")) {
				if (askYesOrNo("Are you sure you want to exit?")) {
					message("Exiting...");
					disconnect();
					break;
				}
				continue;
			} else if (request.equals(ESCAPE_SENTENCE)) {
				cancel();
				can_write = false;
				try {
					waitForEnd();
				} catch (TimeoutException e) {
					error("Server timeout!", false);
					break;
				}
				continue;
			}
			
			try {
				sendRequest(request);
			} catch (TimeoutException e1) {
				error("Server timeout!", false);
				break;
			} catch (IOException e2) {
				error("Something went wrong while sending the request!", false);
				break;
			}
		}
		
		dispose();
	}

	private static void dispose() throws IOException {
		server_connection.close();
		server_listener_thread.interrupt();
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