package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import exceptions.UsernameAlreadyExistsException;
import server_client.Argument;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.Argument.ArgumentType;
import user.User;
import user.UserBuilder;

public class CreateUser extends Command {
	
	@Override
	public String getCommand() {
		return "create-user";
	}

	@Override
	public String getDescription() {
		return "Create a new user.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[] getArguments() {
		return new Argument[]{
			new Argument(false, "username", ArgumentType.text),
			new Argument(false, "password", ArgumentType.text),
			new Argument(true, "name", ArgumentType.text),
			new Argument(true, "email", ArgumentType.text),
		};
	}

	@Override
	public String[] getExamples() {
		return new String[]{
				"To create a simple user named bernt\nwith password 123 and no extra information, do as following:\n"
				+ "create-user bernt 123 \"\" \"\"",
				"To create a password or name containing spaces use quotes:\n"
				+ "create-user test \"Hello world\" 'Full Name' email@address"
		};
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		UserBuilder user_builder = new UserBuilder();
		user_builder.setUsername(arguments.get(0));
		user_builder.setPassword(arguments.get(1));
		user_builder.setName(arguments.get(2));
		user_builder.setEmail(arguments.get(3));
		user_builder.setSalt("");
		User user = user_builder.build();
		
		try {
			if (RequestHandler.createUser(user))
				return "User successfully created!";
			else
				return "User couldn't be created!";
		} catch (UsernameAlreadyExistsException e) {
			return "Username already taken!";
		}
	}
}
