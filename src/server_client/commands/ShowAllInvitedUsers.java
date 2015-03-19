package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import user.User;
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

public class ShowAllInvitedUsers extends Command{

	@Override
	public String get() {
		return "show-invited-users";
	}

	@Override
	public String getDescription() {
		return "shows all users invited to the given entry";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
				{
					new Argument(false, "entryID", ArgumentType.long_number)
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
			SessionExpiredException{
		
		StringBuilder sb = new StringBuilder("The invited users are: \n");
		for(User u : RequestHandler.getAllInvitedUsers((long) arguments.get(0))){
			sb.append("-->");
			sb.append(u.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

}
