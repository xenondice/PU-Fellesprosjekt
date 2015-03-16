package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.EntryDoesNotExistException;
import exceptions.ForcedReturnException;
import exceptions.GroupAlreadyExistsException;
import exceptions.GroupDoesNotExistException;
import exceptions.HasNotTheRightsException;
import exceptions.InvitationDoesNotExistException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import exceptions.UserInGroupDoesNotExistsException;
import exceptions.UsernameAlreadyExistsException;
import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.ServerClientHandler;
import server_client.Wizard;

public class RunWizard extends Command {

	@Override
	public String get() {
		return "wiz";
	}

	@Override
	public String getDescription() {
		return "Makes it easier to write a command.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Makes a wizard that ensures everyting is written correctly.\n"
				+ "It's recommended to use this if you have to write in a password.\n"
				+ "\"create-user\", \"edit-user\" and \"login\" are examples of commands\n"
				+ "that should always be used with wiz.\n"
				+ "\n"
				+ "For functions with more that one syntax, you can write at the end\n"
				+ "which syntax you want to use. If you want to use the first, type 1.";
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
				{
					new Argument(false, "command", ArgumentType.command),
				},
				{
					new Argument(false, "command", ArgumentType.command),
					new Argument(false, "what syntax to use", ArgumentType.number),
				}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments, int syntax) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, SessionExpiredException, HasNotTheRightsException, UserDoesNotExistException, GroupDoesNotExistException, EntryDoesNotExistException, GroupAlreadyExistsException, UserInGroupDoesNotExistsException, UsernameAlreadyExistsException, InvitationDoesNotExistException {
		
		Command command = (Command) arguments.get(0);
		
		int syntax_choice = 0;
		if (syntax == 1) {
			if ((int) arguments.get(1) > command.getArguments().length || (int) arguments.get(1) < 1)
				return "Invalid syntax number!";
			syntax_choice = ((int) arguments.get(1)) - 1;
		}
			
		Wizard wizard = new Wizard();
		for (Argument argument : command.getArguments()[syntax_choice]) {
			wizard.add(argument);
		}
		
		List<Object> results = handler.runWizard(wizard);

		return command.run(handler, results, 0);
	}
}