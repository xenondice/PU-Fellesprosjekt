package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;

public class KickUser extends Command {
	
	@Override
	public String getCommand() {
		return "kick-user";
	}

	@Override
	public String getDescription() {
		return "Kick a user from a calendar entry.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[] getArguments() {
		return new Argument[]{
			new Argument(false, "username", ArgumentType.text),
			new Argument(false, "entryID", ArgumentType.long_number),
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		
		try {
			if (RequestHandler.kickUserFromEntry(handler.getUser(), arguments.get(0), Integer.parseInt(arguments.get(1))))
				return "User successfully kicked from calendar entry!";
			else
				return "User could not be kicked from calendar entry!";
		} catch (Exception e) {
			return "Could not kick user from calendar entry!";
		}
	}
}
