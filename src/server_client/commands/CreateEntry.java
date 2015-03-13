package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import calendar.CalendarEntry;
import calendar.CalendarEntryBuilder;
import exceptions.ForcedReturnException;
import server_client.Argument;
import server_client.Argument.ArgumentType;
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
	public Argument[] getArguments() {
		return new Argument[]{
			new Argument(false, "description", ArgumentType.text),
			new Argument(false, "end time", ArgumentType.date),
			new Argument(false, "location", ArgumentType.text),
			new Argument(false, "roomID", ArgumentType.text),
			new Argument(false, "start time", ArgumentType.date),
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
		
		String roomId = arguments.get(3);
		if(roomId == "" || roomId == "null"){
			roomId = null;
		}
		entry_builder.setRoomID(roomId);
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
