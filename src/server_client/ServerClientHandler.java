package server_client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import user.User;
import user.UserBuilder;
import exceptions.ForcedReturnException;
import exceptions.UserDoesNotExistException;
import exceptions.WrongPasswordException;

public class ServerClientHandler implements Runnable, Closeable {
	private BufferedReader client_input;
	private BufferedWriter client_output;
	private Socket client;
	private User user;
	
	/*public static final String[][][] commands = { // Once login is done, store user creditals and change eg. calendar to show your calendar only
		{
			{"user", "Commands connected to users"},
				{
					"create",
					"Create a new user",
					"username"
				},{
					"edit",
					"Edit an existing user",
					"username"
				},{
					"make-admin",
					"Make an existing user a admin",
					"username"
				}
		},{
			{"entry", "Commands connected to entries"},
				{
					"create",
					"Create a new entry"
				},{
					"edit",
					"Edit an existing entry",
					"entryid"
				},{
					"delete",
					"Delete an existing entry",
					"entryid"
				},{
					"kick-user",
					"Kick an invited user from an entry",
					"username", "entryid"
				},{
					"kick-group",
					"Kick invited users in a group from an entry",
					"groupname", "entryid"
				},{
					"invite-user",
					"Invite an user to an entry",
					"username"
				},{
					"invite-group",
					"Invite a group to an entry",
					"groupname"
				}
		},{
			{"group", "Commands connected to groups"},
				{
					"create",
					"Create a new group",
					"groupname"
				},{
					"add",
					"Add an existing user to a group",
					"username", "groupname"
				},{
					"remove",
					"Remove an existing user from a group",
					"username", "groupname"
				}
		},{
			{
				"calendar",
				"Show the calendar of the given user",
				"username"
			}
		},{
			{
				"help",
				"Show a list over all the commands"
			}
		},{
			{"notification", "Commands connected to notifications"},
				{
					"answer",
					"Answer to a invitation",
					"username", "entryid"
				},{
					"show",
					"Show all active notifications of a given user",
					"username"
				}
		}
	};*/
	
	public ServerClientHandler(Socket user) throws IOException {
		client_output = new BufferedWriter(new OutputStreamWriter(user.getOutputStream()));
		client_input = new BufferedReader(new InputStreamReader(user.getInputStream()));
		client = user;
	}
	
	private List<String> formatRequest(String request) {
		List<String> splitted_answer = new ArrayList<>();
		
		Pattern regex_pattern = Pattern.compile("([^\\s\"']+)|\"([^\"]*)\"|'([^']*)'");
		Matcher matcher = regex_pattern.matcher(request);
		
		while (matcher.find())
			for (int i = 1; i <= 3; i++)
				if (matcher.group(i) != null)
					splitted_answer.add(matcher.group(i).toLowerCase());
		
		return splitted_answer;
	}
	
	public List<String> expectInput() throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		long time_inactive = 0;
		
		while (true) {
			if (client_input.ready()) {
				time_inactive = 0;
				List<String> formatted = formatRequest(client_input.readLine());
				if (formatted.size() > 0) {
					if (formatted.get(0).equals("cancel")) throw new ForcedReturnException();
					return formatted;
				}
			}
				
			if (time_inactive > RequestHandler.WAIT_BEFORE_TIMOUT) throw new TimeoutException();
			time_inactive += RequestHandler.CHECK_FOR_EXPECTED_INPUT_INTERVAL;
			Thread.sleep(RequestHandler.CHECK_FOR_EXPECTED_INPUT_INTERVAL);
		}
	}
	
	public void explain(String message) throws IOException {
		client_output.write(message);
		client_output.write(System.lineSeparator());
	}
	
	public void space() throws IOException {
		client_output.write(System.lineSeparator());
	}
	
	public void status(String message) throws IOException {
		client_output.write(message);
		client_output.write(System.lineSeparator());
		client_output.write(System.lineSeparator());
	}
	
	private void send() throws IOException {
		client_output.flush();
	}
	
	public int verifyInt(String argument) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		while (true) {
			try {
				return Integer.parseInt(argument);
			} catch (NumberFormatException e) {
				argument = ask("\"" + argument + "\" is not an integer, please type only this argument again.", 1).get(0);
			}
		}
	}
	
	public long verifyLong(String argument) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		while (true) {
			try {
				return Long.parseLong(argument);
			} catch (NumberFormatException e) {
				argument = ask("\"" + argument + "\" is not a long, please type only this argument again.", 1).get(0);
			}
		}
	}
	
	public List<String> ask(String question, int number_of_arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		explain(question);
		send();
		
		while (true) {
			List<String> response = expectInput();
			
			if (response.size() == number_of_arguments) return response;
			
			explain("Please provide " + number_of_arguments + " argument(s)!");
			send();
		}
	}
	
	public boolean askYesOrNo(String question) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		List<String> response = ask(question + " (answer with yes[y] or no[n])", 1);
		
		while (true) {
			if (response.get(0).equals("yes") || response.get(0).equals("y"))
				return true;
			else if (response.get(0).equals("no") || response.get(0).equals("n"))
				return false;
			
			response = ask("Please answer with yes[y] or no[n]!", 1);
		}
	}
	
	public List<Object> wizard(List<String> argument_types) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		return null; //TODO: Make wizard and forced methods that return if any argument is wrong
	}
	
	private void handleRequest(List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		String command = arguments.remove(0);
		
		Command command_type = Command.getCommand(command);
		if (command_type == null) {
			status("Unrecognized command! Type \"commands\" for a list over commands.");
			send();
			return;
		}
		
		if (arguments.size() != command_type.getArguments().length) {
			status("Command syntax is wrong! Type \"help command\" or \"man command\" for information about the command.");
			send();
			return;
		}
		
		status(command_type.run(this, arguments));
		send();
	}
	
	private boolean login() throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		String username = ask("Enter username.", 1).get(0);
		String password = ask("Enter password.",1).get(0);
		
		UserBuilder user_builder = new UserBuilder();
		user_builder.setUsername(username);
		user_builder.setPassword(password);
		User user = user_builder.build();
		
		try {
			this.user = RequestHandler.logIn(user);
		} catch (UserDoesNotExistException | WrongPasswordException e) {
			return false;
		}
		
		return true;
	}
	
	public User getUser() {
		return user;
	}

	@Override
	public void run() {
		
		boolean logged_in = false;
		
		try {
			logged_in = login();
			if (logged_in)
				status("Successfully logged in!");
		} catch (IOException | TimeoutException | InterruptedException | ForcedReturnException e2) {
			logged_in = false;
		}
		
		if (logged_in)
			while (true) {
				try {
					handleRequest(expectInput());
				} catch (ForcedReturnException e) {
					try {status(e.getMessage());} catch (IOException e1) {e.printStackTrace();};
					continue;
				} catch (IOException | TimeoutException | InterruptedException e) {
					break;
				}
			}
		else {
			try {
				status("Wrong password or username!");
				send();
			} catch (IOException e) {
			}
		}
		
		try {close();} catch (IOException e) {e.printStackTrace();}
	}
	
	@Override
	public void close() throws IOException {
		RequestHandler.disconnectUser(this);
		client_input.close();
		client_output.close();
		client.close();
	}
}