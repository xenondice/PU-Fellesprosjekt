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

public class MakeAdmin extends Command {
	
	@Override
	public String get() {
		return "make-admin";
	}

	@Override
	public String getDescription() {
		return "Make another user admin of a calendar event.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Make someone else than you admin over an entry you already am an admin over.\n"
				+ "As an admin you can invite/kick users to, edit and delete the entry.";
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
	public String run(ServerClientHandler handler, List<Object> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, HasNotTheRightsException, EntryDoesNotExistException, UserDoesNotExistException, SessionExpiredException {
		
		if (RequestHandler.makeAdmin(handler.getUser(), (String) arguments.get(0), (long) arguments.get(1)))
			return "User " + arguments.get(0) + " now admin!";
		else
			return "Could not make "  + arguments.get(0) + " admin!";
	}
}