package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import calendar.CalendarEntryBuilder;
import exceptions.EntryDoesNotExistException;
import exceptions.ForcedReturnException;
import exceptions.HasNotTheRightsException;
import exceptions.RoomAlreadyBookedException;
import exceptions.RoomDoesNotExistException;
import exceptions.SessionExpiredException;
import exceptions.StartTimeIsLaterTanEndTimeException;
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
				new Argument(true, "start time", ArgumentType.date),
				new Argument(true, "end time", ArgumentType.date),
				new Argument(true, "description of location", ArgumentType.text),
				new Argument(true, "room name or room id (if you want to remove the room, enter 'null')", ArgumentType.text),
				
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
			
		entry_builder.setEntryID((long) arguments.get(0));
		entry_builder.setDescription((String) arguments.get(1));
		entry_builder.setStartTime(arguments.get(2) == null ? -1 : (long)arguments.get(2));
		entry_builder.setEndTime(arguments.get(3) == null ? -1 : (long)arguments.get(3));
		entry_builder.setLocation((String) arguments.get(4));
		entry_builder.setRoomID((String) arguments.get(5));
		entry_builder.setCreator(null);
		
		
		try {
			if (RequestHandler.editEntry(handler.getUsername(), entry_builder.build())){
				return "Calendar entry successfully edited!";
			}else{
				return "Calendar entry couldn't be edited!";
			}
		} catch (RoomAlreadyBookedException e) {
			e.printStackTrace();
			return "Calendar entry couldn't be edited! The given room is already booked for the given time!";
		} catch (RoomDoesNotExistException e) {
			e.printStackTrace();
			return "Calendar entry couldn't be edited! The given room does not exist!";
		} catch (StartTimeIsLaterTanEndTimeException e) {
			e.printStackTrace();
			return "Calendar entry couldn't be edited! The start time can not be later than the end time.";
		}
	}
}
