package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import exceptions.UsernameAlreadyExistsException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
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
	public String[] getArguments() {
		return new String[]{
			"username",
			"password",
			"name",
			"email"
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
			if (RequestHandler.createUser(user))
				return "User successfully created!";
			else
				return "User couldn't be created!";
		} catch (UsernameAlreadyExistsException e) {
			return "Username already taken!";
		}
	}
}
