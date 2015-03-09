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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.TimeLimitExceededException;

public class ServerClientHandler implements Runnable, Closeable {
	private BufferedReader client_input;
	private BufferedWriter client_output;
	private Socket client;
	private String username;
	private String password;
	
	public static final String[][][] commands = { // Once login is done, store user creditals and change eg. calendar to show your calendar only
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
	};
	
	public ServerClientHandler(Socket user) throws IOException {
		client_output = new BufferedWriter(new OutputStreamWriter(user.getOutputStream()));
		client_input = new BufferedReader(new InputStreamReader(user.getInputStream()));
		client = user;
	}
	
	private List<String> formatRequest(String request) throws IOException {
		List<String> splitted_answer = new ArrayList<>();
		
		Pattern regex_pattern = Pattern.compile("([^\\s\"']+)|\"([^\"]*)\"|'([^']*)'");
		Matcher matcher = regex_pattern.matcher(request);
		
		while (matcher.find())
			for (int i = 1; i <= 3; i++)
				if (matcher.group(i) != null)
					splitted_answer.add(matcher.group(i).toLowerCase());
		
		return splitted_answer;
	}
	
	private List<String> expectInput() throws TimeLimitExceededException, IOException {
		try {
			for (int i = 0; i < RequestHandler.WAIT_BEFORE_TIMOUT; i += RequestHandler.CHECK_FOR_EXPECTED_INPUT_INTERVAL) {
				if (client_input.ready()) {
					String request = "";
					while (client_input.ready())
						request += client_input.readLine();
					return formatRequest(request);
				}
				Thread.sleep(Client.SERVER_LISTENER_CHECK_INTERVAL);
			}
			throw new TimeLimitExceededException();
		} catch (InterruptedException e) {
			return null;
		}
	}
	
	private int verifyInt(String argument) throws IOException, TimeLimitExceededException {
		while (true) {
			try {
				return Integer.parseInt(argument);
			} catch (NumberFormatException e) {
				argument = ask("Argument \"" + argument + "\" is not an integer, try again:", 1).get(0);
			}
		}
	}
	
	private long verifyLong(String argument) throws IOException, TimeLimitExceededException {
		while (true) {
			try {
				return Long.parseLong(argument);
			} catch (NumberFormatException e) {
				argument = ask("Argument \"" + argument + "\" is not a long, please type only this argument again:", 1).get(0);
			}
		}
	}
	
	private void send() throws IOException {
		client_output.flush();
	}
	
	private List<String> ask(String question, int number_of_arguments) throws IOException, TimeLimitExceededException {
		while (true) {
			message(question);
			send();
			
			List<String> response = expectInput();
			
			if (response.size() == number_of_arguments)
				return response;
			
			message("Please provide " + number_of_arguments + " argument(s)!");
		}
	}
	
	private void message(String message) throws IOException {
		client_output.write(message);
		client_output.write(System.lineSeparator());
	}
	
	private boolean askYesOrNo(String question) throws IOException, TimeLimitExceededException {
		while (true) {
			List<String> response = ask(question + " (answer with yes[y] or no[n])", 1);
			
			if (response.get(0).equals("yes") || response.get(0).equals("y"))
				return true;
			else if (response.get(0).equals("no") || response.get(0).equals("n"))
				return false;
			
			message("Invalid response!");
		}
	}
	
	private void askForSequenceOfArguments() {
		//TODO
	}
	
	private String handleRequest(String request) throws IOException {
		
		List<String> arguments = formatRequest(request);
		
		if (arguments.isEmpty())
			return "Invalid request!";
		
		String command = arguments.remove(0);
		
		switch (command) {
			case "user":
				int group_loc = 0;
				if (arguments.isEmpty())
					return commandHelp(group_loc, 0);
				else {
					String sub_command = arguments.remove(0);
					
					switch(sub_command) {
						case "create":
							int command_loc = 1;
							if (arguments.size() != numberOfCommandArguments(group_loc, command_loc))
								return commandHelp(group_loc, command_loc);
							return createUser(arguments.get(0));
						default:
							return commandHelp(0, 0);
					}
				}
			default:
				return "Command not recogniced!";
		}
	}

	private String createUser(String username) {
		return "create user"; //TODO
	}

	private int numberOfCommandArguments(int group, int command) {
		return commands[group][command].length - 2;
	}
	
	private String commandHelp(int group, int command) {
		String output = "Help for command " + commands[group][command][0] + ":\n";
		output +=  commands[group][command][1] + "\n\n";
		
		if (command == 0 && commands[group].length > 1) {
			output += "This command have the following sub-commands:\n";
			for (int i = 1; i < commands[group].length; i++)
				output += "\t" + commands[group][i][0] + "\n";
		} else if (commands[group][command].length > 2) {
			output += "Syntax:\n\t" + commands[group][0][0] + " " + commands[group][command][0];
			for (int i = 2; i < commands[group][command].length; i++)
				output += " " + commands[group][command][i];
		} else {
			output += "No arguments\n";
		}
		
		return output + "\n";
	}
	
	private boolean login() {
		return true; //TODO
	}
	
	public String getUsername() {
		return username;
	}

	@Override
	public void run() {
		login();
		
		long time_inactive = 0;
		while (!Thread.interrupted()) {
			try {
				
				if (time_inactive >= RequestHandler.WAIT_BEFORE_TIMOUT) break;
				
				while (client_input.ready()) {
					time_inactive = 0;
					message(handleRequest(client_input.readLine()));
					send();
				}
				
				time_inactive += RequestHandler.CHECK_FOR_EXPECTED_INPUT_INTERVAL;
				Thread.sleep(RequestHandler.CHECK_FOR_EXPECTED_INPUT_INTERVAL);
			} catch (InterruptedException | IOException e) {
				break;
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