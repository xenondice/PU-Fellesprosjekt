package server_client.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import exceptions.GroupAlreadyExistsException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.ServerClientHandler.ArgumentType;
import user.Group;
import user.GroupBuilder;

public class CreateGroupWiz extends Command {

	@Override
	public String getCommand() {
		return "create-group-wiz";
	}

	@Override
	public String getDescription() {
		return "Create a new group using a wizard.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Easier way of creating a group.\n"
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
		descriptions.add("Type in groupname.");
		
		List<Object> result = handler.wizard(argument_types, descriptions, intro_message);
		
		GroupBuilder group_builder = new GroupBuilder();
		group_builder.setName((String) result.get(0));
		Group group = group_builder.build();
		
		try {
			if (RequestHandler.createGroup(handler.getUser(), group))
				return "Group successfully created!";
			else
				return "Group couldn't be created!";
		} catch (Exception e) {
			return "Group couldn't be created!";
		}
	}
}
