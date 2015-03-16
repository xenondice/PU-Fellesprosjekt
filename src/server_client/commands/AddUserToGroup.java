package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import exceptions.GroupDoesNotExistException;
import exceptions.HasNotTheRightsException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import server_client.Argument;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.Argument.ArgumentType;

public class AddUserToGroup extends Command {
	
	@Override
	public String get() {
		return "add-user-to-group";
	}

	@Override
	public String getDescription() {
		return "Add a user to a group.";
	}

	@Override
	public String getManual() {
		return ""
				+ "In order to use this command, you must first have created a group.\n"
				+ "You can do so with the create-group function. When a group is created\n"
				+ "it's completly empty, If you want that group to contain yourself,\n"
				+ "you must therefore add yourself to the group manually using this command.";
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
			{
				new Argument(false, "username of user to add", ArgumentType.text),
				new Argument(false, "name of group", ArgumentType.text),
			}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments, int syntax) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, SessionExpiredException, HasNotTheRightsException, UserDoesNotExistException, GroupDoesNotExistException {
		
		if (RequestHandler.addUserToGroup(handler.getUser(),(String) arguments.get(0),(String) arguments.get(1)))
			return "User successfully added to group!";
		else
			return "User couldn't be added!";
	}
}
