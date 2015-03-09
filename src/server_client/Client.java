package server_client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.TimeLimitExceededException;

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
	private static boolean can_write = true;
	private static boolean can_recieve_input = true;
	
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
	
	private static void sendRequest(String request) throws TimeLimitExceededException, IOException {
		can_write = false;
		server_output.write(request + System.lineSeparator());
		server_output.flush();
		
		waitForEnd();
	}
	
	private static void waitForEnd() throws TimeLimitExceededException {
		while (true) {
			try {
				Thread.sleep(WAIT_BEFORE_TIMEOUT);
				throw new TimeLimitExceededException();
			} catch (InterruptedException e) {
				if (can_write) return;
			}
		}
	}
	
	public static void markEnd() {
		can_write = true;
	}
	
	private static String requestConsoleInput() throws IOException {
		
		can_recieve_input = false;
		
		String request = "";
		
		while (true) {
			console_output.write("$ ");
			console_output.flush();
			
			request = console_input.readLine().trim().toLowerCase();
			
			message();
			
			if (request.length() > 0) break;
			
			message("Can't send empty string!");
			message();
		}
		
		can_recieve_input = true;
		
		return request;
	}
	
	public static boolean ready() {
		return can_recieve_input;
	}
	
	private static void run() throws IOException {
		
		while (true) {
			
			String request = requestConsoleInput();
			
			if (request.equals("exit")) {
				if (askYesOrNo("Are you sure you want to exit?")) {
					message("Exiting...");
					break;
				}
				continue;
			}
			
			try {
				sendRequest(request);
			} catch (TimeLimitExceededException e1) {
				error("Server timeout!", false);
				return;
			} catch (IOException e2) {
				error("Something went wrong while sending the request!", false);
				return;
			}
		}
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