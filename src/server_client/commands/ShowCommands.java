package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Argument;
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
	public Argument[] getArguments() {
		return new Argument[0];
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		String message = ""
				+ "Valid commands:\n";
		
		for (Command command : Command.commands) {
			message += " * " + command.getCommand();	
			for (Argument argument : command.getArguments())
				message += " " + argument;
			message += "\n";
		}
		
		return message.substring(0, message.length()-1);
	}
}
