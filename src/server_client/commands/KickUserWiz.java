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

public class KickUserWiz extends Command {

	@Override
	public String getCommand() {
		return "kick-user-wiz";
	}

	@Override
	public String getDescription() {
		return "Kick a user from a calendar entry using a wizard.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Easier way of kicking a user from a calendar entry.\n"
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
		descriptions.add("Type in username of the user you wish to kick.");
		argument_types.add(ArgumentType.number);
		descriptions.add("Type in eventID of the event you wish to kick the user from.");

		
		List<Object> result = handler.wizard(argument_types, descriptions, intro_message);
		
		
		try {
			if (RequestHandler.kickUserFromEntry(handler.getUser(), (String) result.get(0), (Integer) result.get(1)))
				return "User successfully kicked from calendar entry!";
			else
				return "Could not kick user from calendar entry!";
		} catch (Exception e) {
			return "Could not kick user from calendar entry!";
		}
	}
}
