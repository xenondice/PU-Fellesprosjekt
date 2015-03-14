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
import exceptions.UserInGroupDoesNotExistsException;
import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;

public class KickGroup extends Command {
	
	@Override
	public String get() {
		return "kick-group";
	}

	@Override
	public String getDescription() {
		return "Kick users in a group from a calendar entry.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
			{
				new Argument(false, "name of group to kick", ArgumentType.text),
				new Argument(false, "ID of entry", ArgumentType.long_number),
			}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, GroupDoesNotExistException, UserInGroupDoesNotExistsException, EntryDoesNotExistException, SessionExpiredException, UserDoesNotExistException, HasNotTheRightsException {
		
		if (RequestHandler.kickGroupFromEntry(handler.getUser(), (String) arguments.get(0), (int) arguments.get(1)))
			return "Users in group successfully kicked from calendar entry!";
		else
			return "Users in group could not be kicked from calendar entry!";
	}
}
