package server_client.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import calendar.CalendarEntry;
import calendar.CalendarEntryBuilder;
import exceptions.ForcedReturnException;
import exceptions.UsernameAlreadyExistsException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.ServerClientHandler.ArgumentType;
import user.User;
import user.UserBuilder;

public class CreateEntryWiz extends Command {

	@Override
	public String getCommand() {
		return "create-entry-wiz";
	}

	@Override
	public String getDescription() {
		return "Create a new calendar entry using a wizard.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Easier way of creating a calendar entry.\n"
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
		
		argument_types.add(ArgumentType.text);
		descriptions.add("Type in wanted description.");
		argument_types.add(ArgumentType.text);
		descriptions.add("Type in end time.");
		argument_types.add(ArgumentType.text);
		descriptions.add("Type in location.");
		argument_types.add(ArgumentType.text);
		descriptions.add("Type in roomID.");
		argument_types.add(ArgumentType.text);
		descriptions.add("Type in start time.");
		
		List<Object> result = handler.wizard(argument_types, descriptions, intro_message);
		
		CalendarEntryBuilder entry_builder = new CalendarEntryBuilder();
		entry_builder.setDescription((String) result.get(0));
		entry_builder.setEndTime((Integer) (result.get(1)));
		entry_builder.setLocation((String) result.get(2));
		entry_builder.setRoomID((String) result.get(3));
		entry_builder.setCreator(handler.getUser().getUsername());
		entry_builder.setStartTime((Integer) (result.get(4)));
		CalendarEntry calendarEntry = entry_builder.build();
		
		try {
			if (RequestHandler.createEntry(handler.getUser(), calendarEntry))
				return "Calendar entry successfully created!";
			else
				return "Calendar entry couldn't be created!";
		} catch (Exception e) {
			return "Could not create calendar entry!";
		}
	}
}
