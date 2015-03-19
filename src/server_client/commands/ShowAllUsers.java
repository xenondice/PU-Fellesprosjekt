package server_client.commands;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeoutException;

import server_client.Argument;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
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

public class ShowAllUsers extends Command {

	@Override
	public String get() {
		return "show-all-usernames";
	}

	@Override
	public String getDescription() {
		return "shows all registered usernames";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[0][0];
	}

	@Override
	public String[] getExamples() {
		return new String[]{get()};
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
		HashSet<String> usernames = RequestHandler.getAllUsernames();
		StringBuilder sb = new StringBuilder();
		
		if(usernames.isEmpty()){
			sb.append("There are no users registered!\n");
		}else{
			sb.append("All users: \n");
		}
		for(String name: usernames){
			sb.append("--> ");
			sb.append(name);
			sb.append("\n");
		}
		return sb.toString();
	}

}
