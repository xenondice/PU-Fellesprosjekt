package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import user.Group;
import exceptions.ForcedReturnException;
import exceptions.GroupDoesNotExistException;
import exceptions.SessionExpiredException;

public class ShowGroup extends Command {

	@Override
	public String get() {
		return "show-group";
	}

	@Override
	public String getDescription() {
		return "shows all members that are in the given group.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][] {
				{
					new Argument(false, "groupname", ArgumentType.text)
				}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[]{get()+" group1"};
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments,
			int sytax) throws IOException, TimeoutException,
			InterruptedException, ForcedReturnException,
			SessionExpiredException{
		String groupname = (String) arguments.get(0);
		try {
			Group g = RequestHandler.getGroup(groupname);
			return g.toString();
		} catch (GroupDoesNotExistException e) {
			return "The group '"+groupname+"' does not exist. So there are no user in it.";
		}
		
	}

}
