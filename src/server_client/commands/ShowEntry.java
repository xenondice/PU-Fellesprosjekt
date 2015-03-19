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
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;

public class ShowEntry extends Command {

	@Override
	public String get() {
		return "show-entry";
	}

	@Override
	public String getDescription() {
		return "shows the entry with the spezified entryID.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][] {
				{
					new Argument(false, "the entry ID", ArgumentType.long_number)
				}
				
		};
	}

	@Override
	public String[] getExamples() {
		return new String[] {get()+" 7"};
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments,
			int sytax) throws IOException, TimeoutException,
			InterruptedException, ForcedReturnException,
			SessionExpiredException {
		
		String requestor = handler.getUsername();
		long entry_id = (long) arguments.get(0);
		try {
			return RequestHandler.getEntry(requestor, entry_id).toString();
		} catch (EntryDoesNotExistException e) {
			return "This entry does not exist or you can not see it.";
		}
	}

}
