package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import exceptions.UserDoesNotExistException;
import exceptions.WrongPasswordException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import user.User;

public class Logout extends Command {

	@Override
	public String getCommand() {
		return "logout";
	}

	@Override
	public String getDescription() {
		return "Log out of a session.";
	}

	@Override
	public String getManual() {
		return getDescription();
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
		handler.setUser(null);
		return "Logged out!";
	}
}