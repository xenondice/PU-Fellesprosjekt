package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import calendar.CalendarEntry;
import calendar.Invitation;
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
import server_client.RequestHandler;
import server_client.ServerClientHandler;

public class Agenda extends Command {

	@Override
	public String get() {
		return "agenda";
	}

	@Override
	public String getDescription() {
		return "Get all your future entries that you are going to";
	}

	@Override
	public String getManual() {
		return null;
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
			{
			}
		};
	}

	@Override
	public String[] getExamples() {
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
		Set<CalendarEntry> entries = RequestHandler.getAllEntriesForUser(handler.getUsername());
		Set<Invitation> invitations = RequestHandler.getInvitations(handler.getUsername());
		
		String message = "Your agenda:\n";
		
		for (Invitation invitation : invitations)
			if (invitation.isGoing())
				for (CalendarEntry entry : entries)
					if (entry.getEntryID() == invitation.getEntry_id() && entry.getEndTime() >= System.currentTimeMillis())
						message += " * "
								+ entry
								+ "\n";
		
		return message;
	}

}
