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

public class InviteGroupWiz extends Command {

	@Override
	public String getCommand() {
		return "invite-group-wiz";
	}

	@Override
	public String getDescription() {
		return "Invite users in a group to a calendar entry using a wizard.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Easier way of inviting users in a group to a calendar entry.\n"
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
		descriptions.add("Type in the groupname of the group you would like to invite.");
		argument_types.add(ArgumentType.number);
		descriptions.add("Type in the entryID of the calendar entry you wish to invite to.");
		
		List<Object> result = handler.wizard(argument_types, descriptions, intro_message);
		
		
		try {
			if (RequestHandler.inviteGroupToEntry(handler.getUser(), (String) result.get(0), (int) result.get(1)))
				return "Users in group successfully invited to calendar entry!";
			else
				return "Users in group couldn't be invited to calendar entry!";
		} catch (Exception e) {
			return "Users in group couldn't be invited to calendar entry!!";
		}
	}
}
