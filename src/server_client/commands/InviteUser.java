package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.EntryDoesNotExistException;
import exceptions.ForcedReturnException;
import exceptions.HasNotTheRightsException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;

public class InviteUser extends Command {
	
	@Override
	public String get() {
		return "invite-user";
	}

	@Override
	public String getDescription() {
		return "Invite a user to a calendar entry.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
			{
				new Argument(false, "username", ArgumentType.text),
				new Argument(false, "ID of existing entry", ArgumentType.long_number),
			}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[]{
				""
		};
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments, int syntax) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, EntryDoesNotExistException, UserDoesNotExistException, HasNotTheRightsException, SessionExpiredException {
		
		if (RequestHandler.inviteUserToEntry(handler.getUser(), (String) arguments.get(0), (long) arguments.get(1)))
			return "User successfully invited to calendar entry!";
		else
			return "User couldn't be invited!";
	}
}
