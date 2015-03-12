package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;

public class InviteGroup extends Command {
	
	@Override
	public String getCommand() {
		return "invite-group";
	}

	@Override
	public String getDescription() {
		return "Invite a group to a calendar entry.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public String[] getArguments() {
		return new String[]{
			"groupname",
			"entryID",
		};
	}

	@Override
	public String[] getExamples() {
		return new String[]{
				""
		};
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		try {
			if (RequestHandler.inviteGroupToEntry(handler.getUser(), arguments.get(0), Integer.parseInt(arguments.get(1))))
				return "Users in group successfully invited to calendar entry!";
			else
				return "Users in group couldn't be invited!";
		} catch (Exception e) {
			return "Users in group couldn't be invited!";
		}
	}
}
