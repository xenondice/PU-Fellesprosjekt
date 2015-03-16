package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import calendar.Invitation;
import calendar.Notification;
import exceptions.ForcedReturnException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import exceptions.WrongPasswordException;
import server_client.Argument;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.Argument.ArgumentType;
import user.User;

public class Login extends Command {

	@Override
	public String get() {
		return "login";
	}

	@Override
	public String getDescription() {
		return "Login with an existing user.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Login with an existing user.\n"
				+ "Preferably use wiz login as this will hide your password when you type it in!\n"
				+ "You will get all unseen notifications once you login.";
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
			{
				new Argument(false, "username", ArgumentType.text),
				new Argument(false, "password", ArgumentType.password),
			}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments, int syntax) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, SessionExpiredException {
		
		try {
			User user = RequestHandler.logIn((String) arguments.get(0), (String) arguments.get(1));
			if (user != null) {
				handler.setUser(user);
				Set<Invitation> invitations = RequestHandler.getInvitations(user);
				Set<Notification> notifications = RequestHandler.getNotifications(user);
				if ((invitations != null && invitations.size() > 0) || (notifications != null && notifications.size() > 0)){
					return "Successfully logged in, and you have something in your inbox! Type \"inbox\" to see.";
				}
				return "Successfully logged in!";
			}else{
				return "could not log in";
			}
		} catch (UserDoesNotExistException | WrongPasswordException e) {
			e.printStackTrace();
			return "Invalid password and/or username!";
		}
	}
}
