package server_client.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.ServerClientHandler.ArgumentType;

public class DeleteEntryWiz extends Command {

	@Override
	public String getCommand() {
		return "delete-entry-wiz";
	}

	@Override
	public String getDescription() {
		return "Delete an existing calendar entry using a wizard.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Easier way of deleting an existing calendar entry.\n"
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
		descriptions.add("Type in eventID of the event you wish to delete.");
		
		List<Object> result = handler.wizard(argument_types, descriptions, intro_message);
		
		try {
			if (RequestHandler.deleteEntry(handler.getUser(), (Integer) result.get(0)))
				return "Calendar entry successfully deleted!";
			else
				return "Calendar entry couldn't be deleted!";
		} catch (Exception e) {
			return "Could not delete calendar entry!";
		}
	}
}
