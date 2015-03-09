package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Command;
import server_client.ServerClientHandler;

public class ShowCommands extends Command {

	@Override
	public String getCommand() {
		return "commands";
	}

	@Override
	public String getDescription() {
		return "Show a list over all the commands.";
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
	public void run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		String message = "Valid commands:";
		message += System.lineSeparator();
		
		for (Command command : Command.commands) {
			message += " * " + command.getCommand();	
			for (String argument : command.getArguments())
				message += " " + argument;
			message += System.lineSeparator();
		}
		
		message += System.lineSeparator();
		message += "Please use one of the commands above";
		handler.status(message);
	}

}
