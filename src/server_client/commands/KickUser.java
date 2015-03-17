package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.EntryDoesNotExistException;
import exceptions.ForcedReturnException;
import exceptions.HasNotTheRightsException;
import exceptions.InvitationDoesNotExistException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;

public class KickUser extends Command {
	
	@Override
	public String get() {
		return "kick-user";
	}

	@Override
	public String getDescription() {
		return "Kick a user from a calendar entry.";
	}

	@Override
	public String getManual() {
		return ""
				+ "If you are an admin for an entry, you can make specific users unable to see the entry\n"
				+ "by using this command. They will then also stop getting notifications about updates.";
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
			{
				new Argument(false, "username", ArgumentType.text),
				new Argument(false, "ID of entry", ArgumentType.long_number),
			}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments, int syntax) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException, HasNotTheRightsException, InvitationDoesNotExistException {
		
		if (RequestHandler.kickUserFromEntry(handler.getUsername(), (String) arguments.get(0), (long) arguments.get(1)))
			return "User successfully kicked from calendar entry!";
		else
			return "User could not be kicked from calendar entry!";
	}
}
