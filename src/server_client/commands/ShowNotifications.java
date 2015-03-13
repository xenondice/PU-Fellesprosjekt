package server_client.commands;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeoutException;

import calendar.Notification;
import exceptions.ForcedReturnException;
import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;

public class ShowNotifications extends Command {
	
	@Override
	public String getCommand() {
		return "invite-notifications";
	}

	@Override
	public String getDescription() {
		return "Shows notifications of a given user.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[] getArguments() {
		return new Argument[]{
			new Argument(false, "username", ArgumentType.text),
		};
	}

	@Override
	public String[] getExamples() {
		return new String[]{
				""
		};
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		try {
			HashSet<Notification> notifications = RequestHandler.getNotifications(arguments.get(0));
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
