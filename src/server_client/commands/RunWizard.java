package server_client.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.ServerClientHandler;
import server_client.Wizard;

public class RunWizard extends Command {

	@Override
	public String getCommand() {
		return "wiz";
	}

	@Override
	public String getDescription() {
		return "Makes it easier to write a command.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[] getArguments() {
		return new Argument[]{
				new Argument(false, "command", ArgumentType.text),
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
		
		Wizard wizard = new Wizard();
		for (Argument argument : command.getArguments()) {
			wizard.add(argument);
		}
		
		List<Object> results = handler.runWizard(wizard);
		List<String> string_results = new ArrayList<>();
		
		for (Object result : results)
			if (result == null)
				string_results.add("");
			else
				string_results.add(result.toString());
		
		return command.run(handler, string_results);
	}
}