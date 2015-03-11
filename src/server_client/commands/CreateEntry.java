package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import calendar.CalendarEntry;
import calendar.CalendarEntryBuilder;
import exceptions.ForcedReturnException;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;

public class CreateEntry extends Command {
	
	@Override
	public String getCommand() {
		return "create-entry";
	}

	@Override
	public String getDescription() {
		return "Create a new entry.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public String[] getArguments() {
		return new String[]{
			"description",
			"end time",
			"location",
			"roomID",
			"start time"
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<String> arguments) throws IOException, TimeoutException, InterruptedException, ForcedReturnException {
		
		CalendarEntryBuilder entry_builder = new CalendarEntryBuilder();
		entry_builder.setDescription(arguments.get(0));
		entry_builder.setEndTime(Long.parseLong(arguments.get(1)));
		entry_builder.setLocation(arguments.get(2));
		entry_builder.setRoomID(arguments.get(3));
		entry_builder.setCreator(handler.getUser().getUsername());
		entry_builder.setStartTime(Long.parseLong(arguments.get(4)));
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
