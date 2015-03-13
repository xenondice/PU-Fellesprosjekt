package server_client.commands.old;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import exceptions.HasNotTheRightsException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.ServerClientHandler.ArgumentType;
import user.User;
import user.UserBuilder;

public class MakeAdminWiz extends Command {

	@Override
	public String getCommand() {
		return "make-admin-wiz";
	}

	@Override
	public String getDescription() {
		return "Make another user admin of a calendar entry using a wizard.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Easier way of making another user admin of a calendar entry.\n"
				+ "Walks you through each of the required arguments and asks again if an argument is wrong.";
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		List<ArgumentType> argument_types = new ArrayList<>();
		List<String> descriptions = new ArrayList<>();
		
		String intro_message = "";
		
		argument_types.add(ArgumentType.text);
		descriptions.add("Type in username of the user you would like to make admin.");
		argument_types.add(ArgumentType.text);
		descriptions.add("Type in entryID of the calendar entry you would like to make him admin for");
		
		List<Object> result = handler.wizard(argument_types, descriptions, intro_message);
		
		try {
			if (RequestHandler.makeAdmin(handler.getUser(), arguments.get(0), Integer.parseInt(arguments.get(1))))
				return "User " + arguments.get(0) + " now admin!";
			else return "Could not make "  + arguments.get(0) + " admin!";
		} catch (Exception e) {
			return "Could not make user admin!";
		}
	}
}
