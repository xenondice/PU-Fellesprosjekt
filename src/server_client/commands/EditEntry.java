package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import calendar.CalendarEntryBuilder;
import exceptions.EntryDoesNotExistException;
import exceptions.ForcedReturnException;
import exceptions.HasNotTheRightsException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;

public class EditEntry extends Command {
	
	@Override
	public String get() {
		return "edit-entry";
	}

	@Override
	public String getDescription() {
		return "Edit an existing calendar entry.";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
			{
				new Argument(false, "ID of existing entry", ArgumentType.long_number),
				new Argument(true, "description of event", ArgumentType.text),
				new Argument(true, "time of end", ArgumentType.date),
				new Argument(true, "description of location", ArgumentType.text),
				new Argument(true, "name of room", ArgumentType.text),
				new Argument(true, "time of start", ArgumentType.date),
			}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments, int syntax) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, EntryDoesNotExistException, HasNotTheRightsException, UserDoesNotExistException, SessionExpiredException {
		
		CalendarEntryBuilder entry_builder = new CalendarEntryBuilder();
		entry_builder.setEntryID((Long) arguments.get(0));
		entry_builder.setDescription((String) arguments.get(1));
		entry_builder.setEndTime((Long) arguments.get(2));
		entry_builder.setLocation((String) arguments.get(3));
		entry_builder.setRoomID((String) arguments.get(4));
		entry_builder.setCreator(handler.getUser().getUsername());
		entry_builder.setStartTime((Long) arguments.get(5));
		
		if (RequestHandler.editEntry(handler.getUser(), entry_builder.build()))
			return "Calendar entry successfully edited!";
		else
			return "Calendar entry couldn't be edited!";
	}
}
