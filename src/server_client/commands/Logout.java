package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Argument;
import server_client.Command;
import server_client.ServerClientHandler;

public class Logout extends Command {

	@Override
	public String get() {
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
	public Argument[][] getArguments() {
		return new Argument[0][0];
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		handler.setUser(null);
		return "Logged out!";
	}
}