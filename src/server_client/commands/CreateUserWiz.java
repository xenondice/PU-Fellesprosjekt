package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Command;
import server_client.ServerClientHandler;

public class CreateUserWiz extends Command {

	@Override
	public String getCommand() {
		return "create-user-wiz";
	}

	@Override
	public String getDescription() {
		return "Create a new user using a wizard.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Easier way of creating a user.\n"
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
		return "TODO";
	}
}
