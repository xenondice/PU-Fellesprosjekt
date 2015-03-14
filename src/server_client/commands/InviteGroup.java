package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.EntryDoesNotExistException;
import exceptions.ForcedReturnException;
import exceptions.GroupDoesNotExistException;
import exceptions.HasNotTheRightsException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;

public class InviteGroup extends Command {
	
	@Override
	public String get() {
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
	public Argument[][] getArguments() {
		return new Argument[][]{
			{
				new Argument(false, "name of group", ArgumentType.text),
				new Argument(false, "ID of existing entry", ArgumentType.long_number),
			}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, GroupDoesNotExistException, EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException, SessionExpiredException {
		
		if (RequestHandler.inviteGroupToEntry(handler.getUser(), (String) arguments.get(0), (long) arguments.get(1)))
			return "Users in group successfully invited to calendar entry!";
		else
			return "Users in group couldn't be invited!";
	}
}
