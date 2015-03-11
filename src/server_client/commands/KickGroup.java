package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;

public class KickGroup extends Command {
	
	@Override
	public String getCommand() {
		return "kick-group";
	}

	@Override
	public String getDescription() {
		return "Kick users in a group from a calendar entry.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public String[] getArguments() {
		return new String[]{
			"group name",
			"entryID"
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		
		try {
			if (RequestHandler.kickGroupFromEntry(handler.getUser(), arguments.get(0), Integer.parseInt(arguments.get(1))))
				return "Users in group successfully kicked from calendar entry!";
			else
				return "Users in group could not be kicked from calendar entry!";
		} catch (Exception e) {
			return "Could not kick users in group from calendar entry!";
		}
	}
}
