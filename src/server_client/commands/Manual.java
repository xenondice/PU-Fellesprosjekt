package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.ServerClientHandler;

public class Manual extends Command {

	@Override
	public String get() {
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
	public Argument[][] getArguments() {
		return new Argument[][]{
				{
					new Argument(false, "command", ArgumentType.command),
				}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments, int syntax) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		Command command = (Command) arguments.get(0);
		
		String message = ""
				+ "Manual for " + command.get() + ":\n"
				+ "\n";
		message += (command.getManual() == null) ? "No manual!\n" : command.getManual() + "\n";
		
		for (Argument[] syntaxes : command.getArguments()) {
			message += "\n"
					+ "Syntax: " + command.get();
			for (Argument argument : syntaxes)
				message += " " + argument;
		}
		
		int i = 0;
		if (command.getExamples() != null)
			for (String example : command.getExamples()) {
				i++;
				message += ""
						+ "\n"
						+ "\n"
						+ "Example " + i + ":\n"
						+ example;
			}
		
		return message;
	}

}
