package server_client.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeoutException;

import calendar.Notification;
import exceptions.ForcedReturnException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.ServerClientHandler.ArgumentType;

public class ShowNotificationsWiz extends Command {

	@Override
	public String getCommand() {
		return "show-notifications-wiz";
	}

	@Override
	public String getDescription() {
		return "Show notifications using a wizard.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Easier way of showing notifications.\n"
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
		descriptions.add("Type in username of the user you wish to see the notifications for.");
		
		List<Object> result = handler.wizard(argument_types, descriptions, intro_message);
		
		
		try {
			HashSet<Notification> notifications = RequestHandler.getNotifications((String)result.get(0));
			String s = "";
			for (Notification notification : notifications) {
				s += notification.toString() +"\n";
			}
			return s;
		} catch (Exception e) {
			return "Notifications couldn't be found!";
		}
	}
}
