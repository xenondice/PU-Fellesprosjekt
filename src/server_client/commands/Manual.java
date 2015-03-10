package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Command;
import server_client.ServerClientHandler;

public class Manual extends Command {

	@Override
	public String getCommand() {
		return "man";
	}

	@Override
	public String getDescription() {
		return "Show the manual of a given command.";
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
		if (command == null) {
			return "Not a command!";
		}
		
		handler.status("Manual for " + command.getCommand() + ":");
		
		handler.status(command.getManual());
		
		String message = "Syntax: " + command.getCommand();
		for (String argument : command.getArguments())
			message += " " + argument;
		
		int i = 0;
		for (String example : command.getExamples()) {
			i++;
			message += System.lineSeparator();
			message += System.lineSeparator();
			message += "Example " + i + ":";
			message += System.lineSeparator();
			message += example;
		}
		
		return message;
	}

}
