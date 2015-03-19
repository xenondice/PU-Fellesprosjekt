package server_client.commands;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeoutException;

import server_client.Argument;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import user.Group;
import user.User;
import exceptions.ForcedReturnException;
import exceptions.SessionExpiredException;

public class ShowAllGroups extends Command {

	@Override
	public String get() {
		return "show-all-groups";
	}

	@Override
	public String getDescription() {
		return "shows all existing groups and the users in this groups";
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
			SessionExpiredException {
		
		HashSet<Group> groups = RequestHandler.getAllGroups();
		StringBuilder sb = new StringBuilder();
		sb.append("All Groups: \n");
		for(Group g : groups){
			sb.append(g.getName());
			sb.append(": ");
			if(g.isEmpty()){
				sb.append("has no Users \n");
			}else{
				for(User u : g.getUsers()){
					sb.append("-->");
					sb.append(u.toString());
					sb.append("\n");
				}
			}		
		}
		return sb.toString();
	}

}
