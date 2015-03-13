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

public class EditUserWiz extends Command {

	@Override
	public String getCommand() {
		return "edit-user-wiz";
	}

	@Override
	public String getDescription() {
		return "Edit an existing user using a wizard.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Easier way of editing a user.\n"
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
		descriptions.add("Type in your username.");
		argument_types.add(ArgumentType.text);
		descriptions.add("Type in new password to edit. Else: type in old password");
		argument_types.add(ArgumentType.text);
		descriptions.add("Type in new full name in quotes to edit. Else: type in old name");
		argument_types.add(ArgumentType.text);
		descriptions.add("Type in new email-address to edit. Else: type in old email-address");
		
		List<Object> result = handler.wizard(argument_types, descriptions, intro_message);
		
		UserBuilder user_builder = new UserBuilder();
		user_builder.setUsername((String) result.get(0));
		user_builder.setPassword((String) result.get(1));
		user_builder.setName((String) result.get(2));
		user_builder.setEmail((String) result.get(3));
		user_builder.setSalt("");
		User user = user_builder.build();
		
		try {
			if (RequestHandler.editUser(handler.getUser(), user)) {
				return "User successfully edited!";
			} else {
				return "User could not be edited!";
			}
		} catch (UserDoesNotExistException e) {
			return "User does not exist!";
		} catch (HasNotTheRightsException e) {
			return "You do not have the rights!";
		} catch (SessionExpiredException e) {
			return "Session expired!";
		}
	}
}
