package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import exceptions.HasNotTheRightsException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import user.User;
import user.UserBuilder;

public class EditUser extends Command {
	
	@Override
	public String getCommand() {
		return "edit-user";
	}

	@Override
	public String getDescription() {
		return "Edit an existing user.";
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
			new Argument(false, "name", ArgumentType.text),
			new Argument(false, "email", ArgumentType.text),
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		UserBuilder user_builder = new UserBuilder();
		user_builder.setUsername(arguments.get(0));
		user_builder.setPassword(arguments.get(1));
		user_builder.setName(arguments.get(2));
		user_builder.setEmail(arguments.get(3));
		User user = user_builder.build();
		
		try {
			if (RequestHandler.editUser(handler.getUser(), user)) {
				return "User successfully edited!";
			} else {
				return "User could not be edited!";
			}
		} catch (UserDoesNotExistException | HasNotTheRightsException e) {
			return "Could not edit user!";
		} catch (SessionExpiredException e) {
			return "Session expired!";
		}
		
	}
}
