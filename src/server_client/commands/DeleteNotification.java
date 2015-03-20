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

public class DeleteNotification extends Command {

	@Override
	public String get() {
		return "delete-notification";
	}

	@Override
	public String getDescription() {
		return "removes the specified notification";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][] {
				{
					new Argument(false, "the notification ID", ArgumentType.long_number)
				}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[]{
				get()+" 23"
		};
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
		if(RequestHandler.deleteNotification((long) arguments.get(0))){
			return "notification was deleted";
		}else{
			return "notification could not be deleted!";
		}
	}

}
