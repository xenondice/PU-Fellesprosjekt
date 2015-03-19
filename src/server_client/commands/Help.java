package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Argument;
import server_client.Command;
import server_client.ServerClientHandler;
import server_client.Argument.ArgumentType;

public class Help extends Command {

	@Override
	public String get() {
		return "help";
	}

	@Override
	public String getDescription() {
		return "Show a short description of a command or a list over commands.";
	}

	@Override
	public String getManual() {
		return ""
				+ "If you write this command without any arguments it will return a list\n"
				+ "over valid commands. If you provide a command, it will return a short\n"
				+ "description of the command together with a list over syntaxes. If you\n"
				+ "provide a type of argument, it will explain how it's written.";
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
			{
				new Argument(false, "command", ArgumentType.command),
			},
			
			{
			},
			
			{
				new Argument(false, "type of argument", ArgumentType.argument_type),
			}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments, int syntax) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		if (syntax == 1) {
			String message = ""
					+ "Valid commands (type \"help command\" for the correct syntax):\n";
			
			for (Command command : Command.commands)
				message += " * " + command.get() + "\n";
			
			return message;
		} else if (syntax == 2) {
			return ((ArgumentType) arguments.get(0)).getHelp();
		}
		
		Command command = (Command) arguments.get(0);
		
		String message = command.getDescription() + "\n";
		
		for (Argument[] syntaxes : command.getArguments()) {
			message += "Syntax: " + command.get();
		
			for (Argument argument : syntaxes)
				message += " " + argument;
			
			message += "\n";
		}
		
		return message;
	}

}
