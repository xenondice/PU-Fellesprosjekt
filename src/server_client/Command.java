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
import server_client.commands.ShowAllReservations;
import server_client.commands.ShowAllRooms;
import server_client.commands.ShowCalendar;
import server_client.commands.ShowEntry;
import server_client.commands.ShowNotifications;
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

/**
 * All the commands that a client can use once connected and verified. The classes that extend this interface needs to be
 * added to the ServerClientHandler to become usable by the client.
 */
public abstract class Command {
	
	public static final Command[] commands = {
		
		// Core
		new Help(),
		new Manual(),
		new Login(),
		new Logout(),
		new CreateUser(),
		new RunWizard(),
		
		// Additional
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
		new ShowCalendar(),
		new ShowNotifications(),
		new ShowAllReservations(),
		new ShowAllRooms(),
		new ShowEntry(),
	};
	
	/**
	 * Get the command class from the command name.
	 * @param command
	 * @return
	 */
	public static Command get(String command) {
		for (Command command_type : commands)
			if (command.equals(command_type.get()))
				return command_type;
		return null;
	}
	
	/**
	 * Get the one-word command.
	 */
	public abstract String get();
	
	/**
	 * Get a short, one-line description of the command.
	 */
	public abstract String getDescription();
	
	/**
	 * Get a longer description of the command. Doesn't include examples.
	 */
	public abstract String getManual();
	
	/**
	 * Get an descriptive array of arrays of the arguments required by the command. Returns an empty array if there are no arguments.
	 * Each array of arrays describes an alternate syntax for the command. The first one is user by Wizard by default, so it is the most complete.
	 */
	public abstract Argument[][] getArguments();
	
	/**
	 * Get an array with examples of how the command might be used. Returns an empty array of there are no examples.
	 */
	public abstract String[] getExamples();
	
	/**
	 * Run the command. Returns true if the command was run successful and false otherwise.
	 * @param The handler the command should use, and a list of commands that can be casted directly to what is specified in getArguments.
	 * Also the 0-indexed syntax to use.
	 */
	public abstract String run(ServerClientHandler handler, List<Object> arguments, int sytax) throws
		IOException,
		TimeoutException,
		InterruptedException,
		ForcedReturnException,
		SessionExpiredException,
		HasNotTheRightsException,
		UserDoesNotExistException,
		GroupDoesNotExistException,
		EntryDoesNotExistException,
		GroupAlreadyExistsException,
		UserInGroupDoesNotExistsException,
		UsernameAlreadyExistsException,
		InvitationDoesNotExistException;
}