package server_client.commands;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeoutException;

import calendar.Invitation;
import calendar.Notification;
import exceptions.ForcedReturnException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import server_client.Argument;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;

public class ShowNotifications extends Command {
	
	@Override
	public String get() {
		return "inbox";
	}

	@Override
	public String getDescription() {
		return "Shows your pending notifications and invitations. Use \"answer\" to accept or decline.";
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
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, UserDoesNotExistException, SessionExpiredException {
		
		HashSet<Notification> notifications = RequestHandler.getNotifications(handler.getUser());
		HashSet<Invitation> invitations = RequestHandler.getInvitations(handler.getUser());
		
		String message = "Notifications:\n";
		for (Notification notification : notifications) {
			message += " * " + notification +"\n";
		}
		
		message += "\n"
				+ "Invitations:\n";
		for (Invitation invitation : invitations) {
			message += " * " + invitation +"\n";
		}
		
		return message;
	}
}
