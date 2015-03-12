package server_client.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import exceptions.GroupDoesNotExistException;
import exceptions.HasNotTheRightsException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.ServerClientHandler.ArgumentType;

public class RemoveUserFromGroupWiz extends Command {

	@Override
	public String getCommand() {
		return "remove-user-from-group-wiz";
	}

	@Override
	public String getDescription() {
		return "Remove a user from a group using a wizard.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Easier way of removing a user from a group.\n"
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
		descriptions.add("Type in the name of the group you want to remove a user from");
		argument_types.add(ArgumentType.text);
		descriptions.add("Type in username of the user you want to remove");

		List<Object> result = handler.wizard(argument_types, descriptions, intro_message);
		
		try {
			if (RequestHandler.removeUserFromGroup(handler.getUser(), arguments.get(0), arguments.get(1)))
				return "User successfully removed from group!";
			else
				return "User couldn't be removed!";
		} catch (GroupDoesNotExistException e) {
			return "User couldn't be removed - Group does not exist!";
		} catch (UserDoesNotExistException e) {
			return "User couldn't be removed - User does not exist!";
		} catch (HasNotTheRightsException e) {
			return "User couldn't be removed - User does not have the rights to remove!";
		} catch (SessionExpiredException e) {
			return "User couldn't be removed - Session expired!";
		}
	}
}

