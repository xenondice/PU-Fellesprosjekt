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

public class DeleteEntry extends Command {
	
	@Override
	public String getCommand() {
		return "delete-entry";
	}

	@Override
	public String getDescription() {
		return "Delete an existing entry.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[] getArguments() {
		return new Argument[]{
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
			if (RequestHandler.deleteEntry(handler.getUser(), Integer.parseInt(arguments.get(0))))
				return "Calendar entry successfully deleted!";
			else
				return "Calendar entry couldn't be deleted!";
		} catch (Exception e) {
			return "Could not delete calendar entry!";
		}
	}
}
