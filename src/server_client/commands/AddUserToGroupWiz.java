package server_client.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.ServerClientHandler.ArgumentType;

public class AddUserToGroupWiz extends Command {

	@Override
	public String getCommand() {
		return "add-user-to-group-wiz";
	}

	@Override
	public String getDescription() {
		return "Add a user to a group using a wizard.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Easier way of adding a user to a group.\n"
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
		descriptions.add("Type in the name of the group you want to add a user to");
		argument_types.add(ArgumentType.text);
		descriptions.add("Type in username of the user you want to add.");

		List<Object> result = handler.wizard(argument_types, descriptions, intro_message);
		
		try {
			if (RequestHandler.addUserToGroup(handler.getUser(), arguments.get(1), arguments.get(0)))
				return "User successfully created!";
			else
				return "User couldn't be created!";
		} catch (Exception e) {
			return "Username already taken!";
		}
	}
}

