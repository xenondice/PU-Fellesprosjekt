package server_client.commands;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeoutException;

import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import calendar.Notification;
import exceptions.EntryDoesNotExistException;
import exceptions.ForcedReturnException;
import exceptions.NotificationDoesNotExistException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;

public class ShowNotification extends Command {

	@Override
	public String get() {
		return "show-notification";
	}

	@Override
	public String getDescription() {
		return "shows the notification with the spezified id.";
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
		return new String[] {get()+" 7"};
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments,
			int sytax) throws IOException, TimeoutException,
			InterruptedException, ForcedReturnException,
			SessionExpiredException, UserDoesNotExistException {
		
		String requestor = handler.getUsername();
		long notification_id = (long) arguments.get(0);
		HashSet<Notification> notifics;
		
		notifics = RequestHandler.getNotifications(requestor);
		for (Notification n : notifics) {
			if (n.getNotificationID() == notification_id) {
				try {RequestHandler.markNotificationAsViewed(requestor, notification_id);
				} catch (NotificationDoesNotExistException e) {}
				return "("
						+ new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(n.getTime())
						+ ") "+(n.getDescription()==null?"No content!":n.getDescription());
			}
		}
		
		return "Notification with id "+notification_id+" does not exist or you can not see it.";
		
	}
}
