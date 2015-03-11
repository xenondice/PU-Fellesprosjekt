package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Command;
import server_client.ServerClientHandler;

public class Help extends Command {

	@Override
	public String getCommand() {
		return "help";
	}

	@Override
	public String getDescription() {
		return "Show a short description of a command.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public String[] getArguments() {
		return new String[]{
			"command"	
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		Command command = Command.getCommand(arguments.get(0));
		if (command == null)
			return "Not a command!";
		
		handler.explain(command.getDescription());
		
		String message = "Syntax: " + command.getCommand();
		for (String argument : command.getArguments())
			message += " " + argument;
		
		return message;
	}

}
