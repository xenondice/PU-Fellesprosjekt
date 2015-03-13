package server_client.commands.old;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import calendar.CalendarEntry;
import calendar.CalendarEntryBuilder;
import exceptions.ForcedReturnException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.ServerClientHandler.ArgumentType;

public class AnswerInvitationWiz extends Command {

	@Override
	public String getCommand() {
		return "answer-invitation-wiz";
	}

	@Override
	public String getDescription() {
		return "Answer an invitation using a wizard.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Easier way of answering an invitation.\n"
				+ "Walks you through each of the required arguments and asks again if an argument is wrong.";
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		List<ArgumentType> argument_types = new ArrayList<>();
		List<String> descriptions = new ArrayList<>();
		
		String intro_message = "";
		
		argument_types.add(ArgumentType.number);
		descriptions.add("Type in entryID of the calendar entry you wish to answer an invitation to.");
		argument_types.add(ArgumentType.logic);
		descriptions.add("Type in answer. 'True' or 'False'");
		
		List<Object> result = handler.wizard(argument_types, descriptions, intro_message);
		
		try {
			if (RequestHandler.invitationAnswer(handler.getUser(), (int) result.get(0), (boolean) result.get(1)))
				return "Invitation successfully answered!";
			else
				return "Invitation couldn't be answered!";
		} catch (Exception e) {
			return "Could not answer invitation!";
		}
	}
}
