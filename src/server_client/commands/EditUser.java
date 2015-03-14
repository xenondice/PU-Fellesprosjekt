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
import user.UserBuilder;

public class EditUser extends Command {
	
	@Override
	public String get() {
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
	public Argument[][] getArguments() {
		return new Argument[][]{
			{
				new Argument(false, "exisiting username", ArgumentType.text),
				new Argument(true, "password", ArgumentType.password),
				new Argument(true, "name", ArgumentType.text),
				new Argument(true, "email", ArgumentType.text),
			}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, UserDoesNotExistException, SessionExpiredException, HasNotTheRightsException {
		
		UserBuilder user_builder = new UserBuilder();
		user_builder.setUsername((String) arguments.get(0));
		user_builder.setPassword((String) arguments.get(1));
		user_builder.setName((String) arguments.get(2));
		user_builder.setEmail((String) arguments.get(3));
		
		if (RequestHandler.editUser(handler.getUser(), user_builder.build()))
			return "User successfully edited!";
		else
			return "User could not be edited!";
	}
}
