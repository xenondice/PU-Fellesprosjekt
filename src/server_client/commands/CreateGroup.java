package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import exceptions.GroupAlreadyExistsException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import user.Group;
import user.GroupBuilder;


public class CreateGroup extends Command {
	
	@Override
	public String getCommand() {
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
	public String[] getArguments() {
		return new String[]{
			"name"
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		GroupBuilder group_builder = new GroupBuilder();
		group_builder.setName(arguments.get(0));
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

