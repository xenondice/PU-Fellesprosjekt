package server_client.commands.old;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import exceptions.UsernameAlreadyExistsException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.ServerClientHandler.ArgumentType;
import user.User;
import user.UserBuilder;

public class CreateUserWiz extends Command {

	@Override
	public String getCommand() {
		return "create-user-wiz";
	}

	@Override
	public String getDescription() {
		return "Create a new user using a wizard.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Easier way of creating a user.\n"
				+ "Walks you through each of the required arguments and asks again if an argument is wrong.";
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		List<ArgumentType> argument_types = new ArrayList<>();
		List<String> descriptions = new ArrayList<>();
		
		String intro_message = "";
		
		argument_types.add(ArgumentType.text);
		descriptions.add("Type in wanted username.");
		argument_types.add(ArgumentType.text);
		descriptions.add("Type in password.");
		argument_types.add(ArgumentType.text);
		descriptions.add("Type in full name in quotes.");
		argument_types.add(ArgumentType.text);
		descriptions.add("Type in email-address.");
		
		List<Object> result = handler.wizard(argument_types, descriptions, intro_message);
		
		UserBuilder user_builder = new UserBuilder();
		user_builder.setUsername((String) result.get(0));
		user_builder.setPassword((String) result.get(1));
		user_builder.setName((String) result.get(2));
		user_builder.setEmail((String) result.get(3));
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
