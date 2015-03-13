package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import exceptions.ForcedReturnException;
import server_client.Argument;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.Argument.ArgumentType;

public class AnswerInvitation extends Command {
	
	@Override
	public String getCommand() {
		return "answer-invitation";
	}

	@Override
	public String getDescription() {
		return "Answer an invitation.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[] getArguments() {
		return new Argument[]{
			new Argument(false, "entryID", ArgumentType.text),
			new Argument(false, "answer", ArgumentType.logic),
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		
		try {
			if (RequestHandler.invitationAnswer(handler.getUser(), Integer.parseInt(arguments.get(0)), Boolean.parseBoolean(arguments.get(1))))
				return "Invitation successfully answered!";
			else
				return "Couldn't answer invitation!";
		} catch (Exception e) {
			return "Could not answer invitation!";
		}
	}
}
