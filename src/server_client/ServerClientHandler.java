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

public class ServerClientHandler implements Runnable {
	private BufferedReader client_input;
	private BufferedWriter client_output;
	private RequestHandler request_handler;
	
	public ServerClientHandler(Socket user, RequestHandler request_handler) throws IOException {
		client_output = new BufferedWriter(new OutputStreamWriter(user.getOutputStream()));
		client_input = new BufferedReader(new InputStreamReader(user.getInputStream()));
		this.request_handler = request_handler;
	}
	
	private static boolean askYesOrNo(String question) throws IOException {
		while (true) {
			message(question + " (answer with yes[y] or no[n])");
		
			List<String> request = consoleInput();
			
			if (request.size() == 1)
				if (request.get(0).equals("yes") || request.get(0).equals("y"))
					return true;
				else if (request.get(0).equals("no") || request.get(0).equals("n"))
					return false;
			
			message("Invalid request!");
			message();
		}
	}
	
	private static List<String> ask(String question, int number_of_arguments) throws IOException {
		while (true) {
			message(question);
		
			List<String> request = consoleInput();
			
			if (request.size() == number_of_arguments)
				return request;
			
			message("Please provide " + number_of_arguments + " argument(s)!");
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
	
	private String handleRequest(String request) {
		if (request.equals("calendar")) {
		if (request.size() != 2) {
				System.out.println("Invalid amount of arguments!");
				System.out.println();
				System.out.println("Awating input:");
				break;
			}
			
			User user = dbm.getUser(request.get(1));
			
			if (user == null)
				System.out.println("Couldn't find user!");
			else {
				Calendar cal = dbm.createCalendar(user);
				if (cal.equals(null))
					System.out.println("Failure!");
				else
					System.out.println(cal);
			}
		
		} else if (request.get(0).equals("adduser")) {
			
			if (request.size() != 5) {
				System.out.println("Invalid amount of arguments!");
				System.out.println();
				System.out.println("Awating input:");
				break;
			}
			
			UserBuilder ub = new UserBuilder();
				ub.setUsername(request.get(1));
				ub.setName(request.get(2));
				ub.setPassword(request.get(3));
				ub.setSalt("");
				ub.setEmail(request.get(4));
			
			if (dbm.addUser(ub.build()))
				System.out.println("Success!");
			else
				System.out.println("Failure!");
		
		} else if (request.get(0).equals("addroom")) {
			
			if (request.size() != 3) {
				System.out.println("Invalid amount of arguments!");
				System.out.println();
				System.out.println("Awating input:");
				break;
			}
			
			RoomBuilder rb = new RoomBuilder();
				rb.setRoom_id(request.get(1));
				
			try {
				rb.setSize(Integer.parseInt(request.get(2)));
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
		
		} else if (request.get(0).equals("addentry")) {
			
			if (request.size() != 6) {
				System.out.println("Invalid amount of arguments!");
				System.out.println();
				System.out.println("Awating input:");
				break;
			}
			
			EntryBuilder eb = new EntryBuilder();

			try {
				eb.setStartTime(Long.parseLong(request.get(2)));
				eb.setEndTime(Long.parseLong(request.get(3)));
			} catch (NumberFormatException e) {
				System.out.println("Invalid number!");
				System.out.println();
				System.out.println("Awating input:");
				break;
			}
			
				eb.setDescription(request.get(4));
				eb.setLocation(request.get(5));
			
			User user = dbm.getUser(request.get(1));
			
			if (user == null)
				System.out.println("Couldn't find user!");
			else {
				if (dbm.addEntry(eb.build(), user))
					System.out.println("Success!");
				else
					System.out.println("Failure!");
			}
		
		} else if (request.get(0).equals("canedit")) {
			
			if (request.size() != 3) {
				System.out.println("Invalid amount of arguments!");
				System.out.println();
				System.out.println("Awating input:");
				break;
			}
			
			int entryid;
			try {
				entryid = Integer.valueOf(request.get(2));
			} catch (NumberFormatException e) {
				System.out.println("Invalid number!");
				System.out.println();
				System.out.println("Awating input:");
				break;
			}
			
			String username = request.get(1);
			
			if (dbm.getUser(username) == null)
				System.out.println("Couldn't find user!");
			else if (dbm.getEntry(entryid) == null)
				System.out.println("Couldn't find event!");
			else if (dbm.canEdit(username, entryid))
				System.out.println("User can edit entry!");
			else
				System.out.println("User can't edit entry!");
			
		} else if (request.get(0).equals("help")) {
			
			System.out.println("Commands:");
			System.out.println(" * calendar username");
			System.out.println(" * adduser username name (use quotes) password email");
			System.out.println(" * addroom roomid roomsize");
			System.out.println(" * addentry admin_username starttime (milliseconds since 1970 00:00) endtime (same) description location");
			System.out.println(" * canedit username entryid");
			System.out.println(" * exit");
		
		} else if (request.get(0).equals("exit")) {
			
			System.out.println("Exiting...");
			System.exit(0);
		
		} else
			System.out.println("Invalid input, type help for a list over commands");
		
		System.out.println();
		System.out.println("Awating input:");
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				while (client_input.ready()) {
					client_output.write(handleRequest(client_input.readLine()));
					client_output.flush();
				}
				Thread.sleep(Client.SERVER_LISTENER_CHECK_INTERVAL); //TODO
			} catch (InterruptedException | IOException e) {
				return;
			}
		}
	}
}