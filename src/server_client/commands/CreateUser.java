package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Command;
import server_client.ServerClientHandler;

public class CreateUser implements Command {
	
	@Override
	public String getCommand() {
		return "create-user";
	}

	@Override
	public String getDescription() {
		return "Create a new user";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public String[] getArguments() {
		return new String[]{
			"username",
			"password",
			"name",
			"email"
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public void run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		handler.status("TODO");
	}
}
