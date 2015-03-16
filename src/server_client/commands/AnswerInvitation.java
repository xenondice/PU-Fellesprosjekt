package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.EntryDoesNotExistException;
import exceptions.ForcedReturnException;
import exceptions.HasNotTheRightsException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import server_client.Argument;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.Argument.ArgumentType;

public class AnswerInvitation extends Command {
	
	@Override
	public String get() {
		return "answer";
	}

	@Override
	public String getDescription() {
		return "Answer yes or no to an invitation to an entry.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Once you are invited to an entry, you are automatically set as not attending.\n"
				+ "A notification is then also sent to you, either instantly if you are online,\n"
				+ "or the next time you login. You can then read these invitations by using the\n"
				+ "\"inbox\" command. There you can get the ID of the invitation, which you again\n"
				+ "can put into this command. For answering, you can use yes/y or no/n to answer.";
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
			{
				new Argument(false, "ID number of the entry", ArgumentType.long_number),
				new Argument(false, "wheter you are attending or not", ArgumentType.logic),
			}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[]{
				"To answer an invitation with the id 5, do as following:\n"
				+ "answer 5 y\n"
				+ "or\n"
				+ "answer 5 yes\n"
				+ "or\n"
				+ "wiz answer",
		};
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments, int syntax) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, SessionExpiredException, HasNotTheRightsException, EntryDoesNotExistException, UserDoesNotExistException {
		if (RequestHandler.invitationAnswer(handler.getUser(), (long) arguments.get(0), (boolean) arguments.get(1)))
			return "Your answer is recorded!";
		else
			return "Couldn't answer invitation!";
	}
}
