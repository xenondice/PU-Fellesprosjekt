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
import exceptions.ForcedReturnException;

public class ServerClientHandler implements Runnable, Closeable {
	private BufferedReader client_input;
	private BufferedWriter client_output;
	private Socket client;
	private User user;
	private String temp_message;
	
	public static enum ArgumentType {
		number,
		long_number,
		text,
		logic,
	}
	
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
		temp_message = "";
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
				
				char status = (char) client_input.read();
				if (status == RequestHandler.STATUS_DISCONNECTED) {
					throw new InterruptedException();
				}
				
				List<String> formatted = formatRequest(client_input.readLine());
				if (formatted.size() > 0) {
					if (formatted.get(0).equals("cancel")) throw new ForcedReturnException("Cancelled by user!");
					return formatted;
				}
			}
				
			if (time_inactive > RequestHandler.WAIT_BEFORE_TIMOUT) throw new TimeoutException();
			time_inactive += RequestHandler.CHECK_FOR_EXPECTED_INPUT_INTERVAL;
			Thread.sleep(RequestHandler.CHECK_FOR_EXPECTED_INPUT_INTERVAL);
		}
	}
	
	public synchronized void explain(String message) throws IOException {
		temp_message += message;
		temp_message += System.lineSeparator();
	}
	
	public synchronized void space() throws IOException {
		temp_message += System.lineSeparator();
	}
	
	public synchronized void status(String message) throws IOException {
		temp_message += message;
		temp_message += System.lineSeparator();
		temp_message += System.lineSeparator();
	}
	
	public synchronized String getBuffer() {
		String message = temp_message;
		temp_message = "";
		return message;
	}
	
	private synchronized void send(char status, String message) throws IOException {
		client_output.write(status + message);
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
	
	public boolean verifyYesOrNo(String response) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		while (true) {
			if (response.equals("yes") || response.equals("y"))
				return true;
			else if (response.equals("no") || response.equals("n"))
				return false;
			
			response = ask("Please answer with yes[y] or no[n]!", 1).get(0);
		}
	}
	
	public List<String> ask(String question, int number_of_arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		explain(question);
		send(RequestHandler.STATUS_OK, getBuffer());
		
		while (true) {
			List<String> response = expectInput();
			
			if (response.size() == number_of_arguments) return response;
			
			explain("Please provide " + number_of_arguments + " argument(s)!");
			send(RequestHandler.STATUS_OK, getBuffer());
		}
	}

	public List<Object> wizard(List<ArgumentType> argument_types, List<String> description, String intro_message) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		if (argument_types.size() != description.size())
			throw new ForcedReturnException("Internal error!");
		
		if (!intro_message.isEmpty()) status(intro_message);
		
		List<Object> results = new ArrayList<>();
		
		for (int i = 0; i < argument_types.size(); i++) {
			
			String answer = ask(description.get(i) + " (" + (i+1) + "/" + argument_types.size() + ")",1).get(0);
			
			Object result;
			
			switch (argument_types.get(i)) {
				case long_number:
					result = verifyLong(answer);
					break;
				case number:
					result = verifyInt(answer);
					break;
				case text:
					result = answer;
					break;
				case logic:
					result = verifyYesOrNo(answer);
				default:
					result = null;
					break;
			}
			
			results.add(result);
		}
		
		return results;
	}
	
	private void handleRequest(List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		String command = arguments.remove(0);
		
		Command command_type = Command.getCommand(command);
		if (command_type == null) {
			status("Unrecognized command! Type \"commands\" for a list over commands.");
			send(RequestHandler.STATUS_OK, getBuffer());
			return;
		}
		
		if (arguments.size() != command_type.getArguments().length) {
			status("Command syntax is wrong! Type \"help command\" or \"man command\" for information about the command.");
			send(RequestHandler.STATUS_OK, getBuffer());
			return;
		}
		
		status(command_type.run(this, arguments));
		send(RequestHandler.STATUS_OK, getBuffer());
	}
	
	public User getUser() {
		return user;
	}
	
	public synchronized void sendNotification(String message) {
		try {
			send(RequestHandler.STATUS_NOTIFICATION, message);
		} catch (IOException e) {
		}
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public void run() {
		
		try {
			explain("Welcome to the calendar system!");
			explain("You are currently logged in as a guest.");
			explain("This means you only have access to create-user, create-user-wiz and login.");
			explain("You will have to login before you can make any further requests!");
			space();
			send(RequestHandler.STATUS_OK, getBuffer());
		} catch (IOException e) {
		}
			
		while (true) {
			try {
				handleRequest(expectInput());
			} catch (ForcedReturnException e) {
				try {status(e.getMessage()); send(RequestHandler.STATUS_OK, getBuffer());} catch (IOException e1) {e.printStackTrace();};
				continue;
			} catch (IOException | TimeoutException | InterruptedException e) {
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