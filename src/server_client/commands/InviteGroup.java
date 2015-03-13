package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import calendar.InvitationBuilder;
import exceptions.EntryDoesNotExistException;
import exceptions.ForcedReturnException;
import exceptions.GroupDoesNotExistException;
import exceptions.HasNotTheRightsException;
import exceptions.InvitationAlreadyExistsException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import user.User;
import user.UserBuilder;

public class InviteGroup extends Command {
	
	@Override
	public String getCommand() {
		return "invite-group";
	}

	@Override
	public String getDescription() {
		return "Invite a group to a calendar entry.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[] getArguments() {
		return new Argument[]{
			new Argument(false, "groupname", ArgumentType.text),
			new Argument(false, "entryID", ArgumentType.long_number),
		};
	}

	@Override
	public String[] getExamples() {
		return new String[]{
				""
		};
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
			try {
				if (RequestHandler.inviteGroupToEntry(handler.getUser(), arguments.get(0), Long.parseLong(arguments.get(1))))
					return "Users in group successfully invited to calendar entry!";
				else
					return "Users in group couldn't be invited!";
			} catch (NumberFormatException e) {
				return "Users in group couldn't be invited - wrong number format!";
			} catch (GroupDoesNotExistException e) {
				return "Users in group couldn't be invited - group does not exist!";
			} catch (EntryDoesNotExistException e) {
				return "Users in group couldn't be invited - entry does not exist!";
			} catch (UserDoesNotExistException e) {
				return "Users in group couldn't be invited - user does not exist!";
			} catch (HasNotTheRightsException e) {
				return "Users in group couldn't be invited - has not the rights!";
			} catch (SessionExpiredException e) {
				return "session expired!";
			} catch (InvitationAlreadyExistsException e) {
				return "Users in group couldn't be invited - invitation allready exists!";
			}
		
		
	}
}
