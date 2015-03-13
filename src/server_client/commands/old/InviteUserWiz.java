package server_client.commands.old;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.ServerClientHandler.ArgumentType;

public class InviteUserWiz extends Command {

	@Override
	public String getCommand() {
		return "invite-user-wiz";
	}

	@Override
	public String getDescription() {
		return "Invite a user to a calendar entry using a wizard.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Easier way of inviting a user to a calendar entry.\n"
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
		descriptions.add("Type in the username of the user you would like to invite.");
		argument_types.add(ArgumentType.number);
		descriptions.add("Type in the entryID of the calendar entry you wish to invite to.");
		
		List<Object> result = handler.wizard(argument_types, descriptions, intro_message);
		
		
		try {
			if (RequestHandler.inviteUserToEntry(handler.getUser(), (String) result.get(0), (int) result.get(1)))
				return "User successfully invited to calendar entry!";
			else
				return "User couldn't be invited to calendar entry!";
		} catch (Exception e) {
			return "User couldn't be invited to calendar entry!!";
		}
	}
}
