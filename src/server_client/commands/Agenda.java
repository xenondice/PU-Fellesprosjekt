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

public class Agenda extends Command {

	@Override
	public String get() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getManual() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Argument[][] getArguments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getExamples() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments,
			int sytax) throws IOException, TimeoutException,
			InterruptedException, ForcedReturnException,
			SessionExpiredException, HasNotTheRightsException,
			UserDoesNotExistException, GroupDoesNotExistException,
			EntryDoesNotExistException, GroupAlreadyExistsException,
			UserInGroupDoesNotExistsException, UsernameAlreadyExistsException,
			InvitationDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

}
