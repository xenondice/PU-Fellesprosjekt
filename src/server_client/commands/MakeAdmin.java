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

public class MakeAdmin extends Command {
	
	@Override
	public String getCommand() {
		return "make-admin";
	}

	@Override
	public String getDescription() {
		return "Make another user admin of a calendar event.";
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
			if (RequestHandler.makeAdmin(handler.getUser(), arguments.get(0), Integer.parseInt(arguments.get(1))))
				return "User " + arguments.get(0) + " now admin!";
			else return "Could not make "  + arguments.get(0) + " admin!";
		} catch (Exception e) {
			return "Could not make user admin!";
		}
		
		
	}
}