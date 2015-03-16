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

import server_client.Argument.ArgumentType;
import user.User;
import exceptions.EntryDoesNotExistException;
import exceptions.ForcedReturnException;
import exceptions.GroupAlreadyExistsException;
import exceptions.GroupDoesNotExistException;
import exceptions.HasNotTheRightsException;
import exceptions.InvitationDoesNotExistException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import exceptions.UserInGroupDoesNotExistsException;
import exceptions.UsernameAlreadyExistsException;

public class ServerClientHandler implements Runnable, Closeable {
	private BufferedReader client_input;
	private BufferedWriter client_output;
	private Socket client;
	private User user;
	
	// Protocol declaration (headers to messages)
	public static final int STATUS_DONE =			0;
	public static final int STATUS_AWAIT_MORE =		1;
	public static final int STATUS_DISCONNECT =		2;
	public static final int STATUS_NOTIFICATION =	3;
	public static final int STATUS_RETURN =			4;
	
	private static final String FORMATTING_RULES = "([^\\s\"']+)|\"([^\"]*)\"|'([^']*)'";
	
	/**
	 * Runs on its own thread and looks for input from a open socket.
	 * If anything is found, the input is formatted and checked against a set of commands.
	 * If a command is recognized, the command is run with a given set of arguments.
	 * @param open socket to a user
	 * @throws IOException
	 */
	public ServerClientHandler(Socket user) throws IOException {
		client_output = new BufferedWriter(new OutputStreamWriter(user.getOutputStream()));
		client_input = new BufferedReader(new InputStreamReader(user.getInputStream()));
		client = user;
	}
	
	/**
	 * Formats a request according to a set RegeX expression.
	 * @param Request string to be formatted.
	 * @return A list over the words in the string divided by spaces, and grouped by quotes and all in lower case.
	 */
	public List<String> formatRequest(String request) {
		List<String> splitted_answer = new ArrayList<>();
		
		Pattern regex_pattern = Pattern.compile(FORMATTING_RULES);
		Matcher matcher = regex_pattern.matcher(request);
		
		while (matcher.find())
			for (int i = 1; i <= 3; i++)
				if (matcher.group(i) != null)
					splitted_answer.add(matcher.group(i).toLowerCase());
		
		return splitted_answer;
	}
	
	/**
	 * Waits for input from a user.
	 * @return What the user answered.
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws InterruptedException if user disconnects.
	 * @throws ForcedReturnException if user cancels operation.
	 */
	private String expectInput() throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		long time_inactive = 0;
		
		while (true) {
			if (client_input.ready()) {
				
				// Handle header
				int status = client_input.read();
				if (status == STATUS_DISCONNECT) {
					throw new InterruptedException();
				} else if (status == STATUS_RETURN) {
					throw new ForcedReturnException("Cancelled by user!");
				}
		
				return client_input.readLine();
			}
			
			if (time_inactive > RequestHandler.WAIT_BEFORE_TIMOUT) throw new TimeoutException();
			time_inactive += RequestHandler.CHECK_FOR_EXPECTED_INPUT_INTERVAL;
			Thread.sleep(RequestHandler.CHECK_FOR_EXPECTED_INPUT_INTERVAL);
		}
	}
	
	/**
	 * Send a message to the client with a given header
	 * @param status
	 * @param message
	 * @throws IOException
	 */
	private synchronized void send(int status, String message) throws IOException {
		client_output.write(((char) status) + message + '\n');
		client_output.flush();
	}
	
	/**
	 * Asks the user a question.
	 * @param Question to be asked
	 * @return What the user wrote in one complete line in lower case.
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws InterruptedException
	 * @throws ForcedReturnException
	 */
	public String ask(String question) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		send(STATUS_AWAIT_MORE, question);
		return expectInput().toLowerCase();
	}

	/**
	 * Runs through a wizard and asks about the values. Guarantees that the non-optional values are not null.
	 * @param wizard
	 * @return A list of objects that is guaranteed to be the object types specified in the wizard. Needs to be casted!
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws InterruptedException
	 * @throws ForcedReturnException
	 */
	public List<Object> runWizard(Wizard wizard) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		List<Object> results = new ArrayList<>();
		
		for (int i = 0; i < wizard.size(); i++) {
			Argument argument = wizard.get(i);
			
			String answer = ask("Enter " + (argument.optional?"optional ":"") + argument.type.name().replace('_', ' ') + " " + argument.description + " (" + (i+1) + "/" + wizard.size() + ")");
			Object result = null;
			
			boolean repeat = true;
			
			while (repeat) {
				String question;
				
				if (answer.isEmpty() && argument.optional) {
					break;
				} else if (argument.type == ArgumentType.number) {
					try {
						result = Integer.parseInt(answer);
						break;
					} catch (NumberFormatException e) {
						question = "Answer is not an integer! Please try again.";
					}
					question = "";
				} else if (argument.type == ArgumentType.long_number) {
					try {
						result = Long.parseLong(answer);
						break;
					} catch (NumberFormatException e) {
						question = "Answer is not a long! Please try again.";
					}
				} else if (argument.type == ArgumentType.text) {
					result = answer;
					break;
				} else if (argument.type == ArgumentType.logic) {
					if (answer.equals("yes") || answer.equals("y")) {
						result = true;
						break;
					} else if (answer.equals("no") || answer.equals("n")) {
						result = false;
						break;
					}
					question = "Please answer with yes[y] or no[n]!";
				} else if (argument.type == ArgumentType.date) {
					try {
						result = Long.parseLong(answer);
						break;
					} catch (NumberFormatException e) {
						question = "Answer is not a long! Please try again.";
					} //TODO: Make it read the format DD/MM/YYYY and DD/MM/YYYY MM:SS
				} else if (argument.type == ArgumentType.password) {
					result = answer;
					break;
					//TODO: Make it send a header for password and hide input
				} else if (argument.type == ArgumentType.command) {
					Command command = Command.get(answer);
					if (command != null) {
						result = command;
						break;
					}
					question = "Answer is not a valid command! Please try again.";
				} else {
					throw new ForcedReturnException("Internal error!");
				}
				answer = ask(question);
			}	
			results.add(result);
		}
		return results;
	}
	
	/**
	 * Gets a list of arguments in and runs the commands (first argument) if everything is correct
	 * @param arguments
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws InterruptedException
	 * @throws ForcedReturnException
	 * @throws SessionExpiredException 
	 */
	private void handleRequest(List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		if (arguments.size() == 0) {
			send(STATUS_DONE, "");
			return;
		}
		
		String command = arguments.remove(0);
		
		Command command_type = Command.get(command);
		if (command_type == null) {
			send(STATUS_DONE, "Unrecognized command! Type \"commands\" for a list over commands.");
			return;
		}
		
		List<Object> formatted_arguments = formatArguments(command_type, arguments);
		if (formatted_arguments == null) {
			send(STATUS_DONE, "Command syntax is wrong! Type \"help command\" or \"man command\" for information about the command.");
			return;
		}
		
		try {
			send(STATUS_DONE, command_type.run(this, formatted_arguments));
		} catch (SessionExpiredException e) {
			throw new ForcedReturnException("Invalid session, use of command denied! Please login.");
		} catch (HasNotTheRightsException e) {
			throw new ForcedReturnException("You are not authorized to do this!");
		} catch (UserDoesNotExistException e) {
			throw new ForcedReturnException("User doesn't exist!");
		} catch (GroupDoesNotExistException e) {
			throw new ForcedReturnException("Group doesn't exist!");
		} catch (EntryDoesNotExistException e) {
			throw new ForcedReturnException("Entry doesn't exist!");
		} catch (GroupAlreadyExistsException e) {
			throw new ForcedReturnException("Group already exists!");
		} catch (UserInGroupDoesNotExistsException e) {
			throw new ForcedReturnException("User is not in group!");
		} catch (UsernameAlreadyExistsException e) {
			throw new ForcedReturnException("Username already taken!");
		} catch (InvitationDoesNotExistException e) {
			throw new ForcedReturnException("User is not invited to entry!");
		}
	}
	
	/**
	 * Verifies that the arguments given are of the right type and amount. Returns null if not.
	 * @param command
	 * @param arguments
	 * @return
	 */
	private List<Object> formatArguments(Command command, List<String> arguments) {
		Argument[][] command_syntaxes = command.getArguments();
		if (command_syntaxes.length == 0 && arguments.isEmpty()) return new ArrayList<Object>();
		
		for (Argument[] command_arguments : command_syntaxes) {
			boolean syntax_correct = true;
			List<Object> formatted_arguments = new ArrayList<>();
			
			if (arguments.size() != command_arguments.length)
				syntax_correct = false;
			else
				for (int i = 0; i < command_arguments.length; i++) {
					Object argument = null;
					
					if (arguments.get(i) == null) {
						if (!command_arguments[i].optional)
							syntax_correct = false;
					} else if (command_arguments[i].type == ArgumentType.number) {
						try {
							argument = Integer.parseInt(arguments.get(i));
						} catch (NumberFormatException e) {
							syntax_correct = false;
						}
					} else if (command_arguments[i].type == ArgumentType.long_number) {
						try {
							argument = Long.parseLong(arguments.get(i));
						} catch (NumberFormatException e) {
							syntax_correct = false;
						}
					} else if (command_arguments[i].type == ArgumentType.text) {
						argument = arguments.get(i);
					} else if (command_arguments[i].type == ArgumentType.logic) {
						if (arguments.get(i).equals("yes") || arguments.get(i).equals("y"))
							argument = true;
						else if (arguments.get(i).equals("no") || arguments.get(i).equals("n"))
							argument = false;
						else
							syntax_correct = false;
					} else if (command_arguments[i].type == ArgumentType.date) {
						try {
							argument = Long.parseLong(arguments.get(i));
						} catch (NumberFormatException e) {
							syntax_correct = false;
						}
					} else if (command_arguments[i].type == ArgumentType.password) {
						argument = arguments.get(i);
					} else if (command_arguments[i].type == ArgumentType.command) {
						Command temp_command = Command.get(arguments.get(i));
						if (temp_command == null)
							syntax_correct = false;
						else
							argument = temp_command;
					} else {
						syntax_correct = false;
					}
					
					if (syntax_correct) {
						formatted_arguments.add(argument);
					} else
						break;
				}
			
			if (syntax_correct)
				return formatted_arguments;
			else
				formatted_arguments.clear();
		}
		
		return null;
	}
	
	/**
	 * Get the user that this handler is currently listening to.
	 * @return A user if the user is logged in, null if not.
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * Sets who this handler is currently listening to (for verification, etc).
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * Send a instant message to the user this handler is listening to.
	 * @param message
	 */
	public void sendNotification(String message) {
		try {
			send(STATUS_NOTIFICATION, message);
		} catch (IOException e) {
		}
	}

	@Override
	public void run() {
		
		// Send a welcome message
		try {
			String welcome_message = ""
					+ "Welcome to the calendar system!\n"
					+ "You are currently logged in as a guest.\n"
					+ "This means you only have access to create-user, wiz, help, man and login.\n"
					+ "You will have to login before you can make any further requests!";
			
			send(STATUS_DONE, welcome_message);
		} catch (IOException e) {
		}
		
		// Check for input and handle requests
		while (true) {
			try {
				handleRequest(formatRequest(expectInput()));
			} catch (ForcedReturnException e) {
				try {send(STATUS_DONE, e.getMessage());} catch (IOException e1) {e.printStackTrace();};
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