package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import exceptions.GroupAlreadyExistsException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import exceptions.UserInGroupDoesNotExistsException;
import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import user.GroupBuilder;


public class CreateGroup extends Command {
	
	@Override
	public String get() {
		return "create-group";
	}

	@Override
	public String getDescription() {
		return "Create a new group.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
			{
				new Argument(false, "name of group", ArgumentType.text),
			}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments, int syntax) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, UserDoesNotExistException, GroupAlreadyExistsException, UserInGroupDoesNotExistsException, SessionExpiredException {
		
		GroupBuilder group_builder = new GroupBuilder();
		group_builder.setName((String) arguments.get(0));
		
		if (RequestHandler.createGroup(handler.getUsername(), group_builder.build()))
			return "Group successfully created!";
		else
			return "Group couldn't be created!";
	}
}

