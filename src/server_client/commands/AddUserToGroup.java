package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;

public class AddUserToGroup extends Command {
	
	@Override
	public String getCommand() {
		return "add-user-to-group";
	}

	@Override
	public String getDescription() {
		return "Add a user to your group.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public String[] getArguments() {
		return new String[]{
			"name",
			"username"
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		try {
			if (RequestHandler.addUserToGroup(handler.getUser(), arguments.get(1), arguments.get(0)))
				return "User successfully added to group!";
			else
				return "User couldn't be added!";
		} catch (Exception e) {
			return "User couldn't be added to group!";
		}
	}
}
