package server_client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import server_client.commands.AddUserToGroup;
import server_client.commands.AnswerInvitation;
import server_client.commands.CreateEntry;
import server_client.commands.CreateGroup;
import server_client.commands.CreateUser;
import server_client.commands.DeleteEntry;
import server_client.commands.EditEntry;
import server_client.commands.EditUser;
import server_client.commands.Help;
import server_client.commands.InviteGroup;
import server_client.commands.InviteUser;
import server_client.commands.KickGroup;
import server_client.commands.KickUser;
import server_client.commands.Login;
import server_client.commands.Logout;
import server_client.commands.MakeAdmin;
import server_client.commands.Manual;
import server_client.commands.RemoveUserFromGroup;
import server_client.commands.RunWizard;
import server_client.commands.ShowCommands;
import server_client.commands.ShowNotifications;
import exceptions.ForcedReturnException;

/**
 * All the commands that a client can use once connected and verified. The classes that extend this interface needs to be
 * added to the ServerClientHandler to become usable by the client.
 */
public abstract class Command {
	
	public static final Command[] commands = {
		
		// Essential
		new Help(),
		new Manual(),
		new ShowCommands(),
		new Login(),
		new Logout(),
		new CreateUser(),
		new RunWizard(),
		
		// Arbitrary
		new AddUserToGroup(),
		new AnswerInvitation(),
		new CreateEntry(),
		new CreateGroup(),
		new DeleteEntry(),
		new EditEntry(),
		new EditUser(),
		new InviteGroup(),
		new InviteUser(),
		new KickGroup(),
		new KickUser(),
		new MakeAdmin(),
		new RemoveUserFromGroup(),
		new ShowNotifications(),
	};
	
	/**
	 * Get the command class from the command name.
	 * @param command
	 * @return
	 */
	public static Command getCommand(String command) {
		for (Command command_type : commands)
			if (command.equals(command_type.getCommand()))
				return command_type;
		return null;
	}
	
	/**
	 * Get the one-word command.
	 */
	public abstract String getCommand();
	
	/**
	 * Get a short, one-line description of the command.
	 */
	public abstract String getDescription();
	
	/**
	 * Get a longer description of the command. Doesn't include examples.
	 */
	public abstract String getManual();
	
	/**
	 * Get an descriptive array of the arguments required by the command
	 */
	public abstract Argument[] getArguments();
	
	/**
	 * Get an array with examples of how the command might be used.
	 */
	public abstract String[] getExamples();
	
	/**
	 * Run the command. Returns true if the command was run successful and false otherwise.
	 */
	public abstract String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException;
}