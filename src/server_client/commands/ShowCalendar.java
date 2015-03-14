package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.EntryDoesNotExistException;
import exceptions.ForcedReturnException;
import exceptions.GroupAlreadyExistsException;
import exceptions.GroupDoesNotExistException;
import exceptions.HasNotTheRightsException;
import exceptions.InvitationDoesNotExistException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import exceptions.UserInGroupDoesNotExistsException;
import exceptions.UsernameAlreadyExistsException;
import server_client.Argument;
import server_client.Command;
import server_client.ServerClientHandler;

public class ShowCalendar extends Command {

	@Override
	public String get() {
		return "cal";
	}

	@Override
	public String getDescription() {
		return "Get a ASCII calendar over your current activities.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[0][0]; //TODO: Add optional arguments for agenda, month, week, year
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, SessionExpiredException, HasNotTheRightsException, UserDoesNotExistException, GroupDoesNotExistException, EntryDoesNotExistException, GroupAlreadyExistsException, UserInGroupDoesNotExistsException, UsernameAlreadyExistsException, InvitationDoesNotExistException {
		return "TODO";
	}
}
