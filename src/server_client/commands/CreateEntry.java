package server_client.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import calendar.CalendarEntryBuilder;
import exceptions.ForcedReturnException;
import exceptions.HasNotTheRightsException;
import exceptions.RoomDoesNotExistException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import server_client.Argument;
import server_client.Argument.ArgumentType;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;

public class CreateEntry extends Command {
	
	
	@Override
	public String get() {
		return "create-entry";
	}

	@Override
	public String getDescription() {
		return "Create a new entry.";
	}

	@Override
	public String getManual() {
		return ""
				+ "Create a happening at a certain time which you can invite other users to.\n"
				+ "You have to create the entry first, then you can use \"invite-user\"\n"
				+ "or \"invite-group\" to invite the others. You can also use \"make-admin\"\n"
				+ "to make other users able to edit your entry and invite users of their own.";
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
			{
				new Argument(true, "description of entry", ArgumentType.text),
				new Argument(true, "description of location", ArgumentType.text),
				new Argument(false, "time of start", ArgumentType.date),
				new Argument(true, "time of end", ArgumentType.date),
				new Argument(true, "name or ID of room", ArgumentType.text)
			}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments, int syntax) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, SessionExpiredException, HasNotTheRightsException, UserDoesNotExistException {
		try {
			CalendarEntryBuilder entry_builder = new CalendarEntryBuilder();
			entry_builder.setDescription(arguments.get(0)==null?"":(String) arguments.get(0));
			entry_builder.setLocation(arguments.get(1)==null?"":(String) arguments.get(1));
			entry_builder.setStartTime((long) arguments.get(2));
			entry_builder.setEndTime(arguments.get(3)==null?(long) arguments.get(2):(long) arguments.get(3));
			entry_builder.setRoomID((String) arguments.get(4));
			entry_builder.setCreator(handler.getUser().getUsername());
		
		
			if (RequestHandler.createEntry(handler.getUser(), entry_builder.build())){
				return "Calendar entry successfully created!";
			}else{
				return "Calendar entry couldn't be created!";
			}
		} catch (RoomDoesNotExistException e) {
			return "Calendar entry couldn't be created because this room does not exist!";
		}
	}
}
