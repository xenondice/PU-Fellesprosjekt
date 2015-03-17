package server_client.commands;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeoutException;

import calendar.CalendarEntry;
import calendar.MonthCalendar;
import exceptions.EntryDoesNotExistException;
import exceptions.ForcedReturnException;
import exceptions.GroupAlreadyExistsException;
import exceptions.GroupDoesNotExistException;
import exceptions.HasNotTheRightsException;
import exceptions.InvitationDoesNotExistException;
import exceptions.SessionExpiredException;
import exceptions.UserDoesNotExistException;
import exceptions.UserInGroupDoesNotExistsException;
import exceptions.UsernameAlreadyExistsException;
import server_client.Argument;
import server_client.Command;
import server_client.RequestHandler;
import server_client.ServerClientHandler;
import server_client.Argument.ArgumentType;

public class ShowCalendar extends Command {

	@Override
	public String get() {
		return "cal";
	}

	@Override
	public String getDescription() {
		return "Get a ASCII calendar over your current activities. Enter either \"week\", \"day\", \"month\", \"year\" or \"agenda\".";
	}

	@Override
	public String getManual() {
		return getDescription();
	}

	@Override
	public Argument[][] getArguments() {
		return new Argument[][]{
				{
					new Argument(false, "type of calendar", ArgumentType.text),
					new Argument(true, "when the calendar is for", ArgumentType.date)
				},
				
				{
					new Argument(false, "type of calendar", ArgumentType.text),
				},
				
				{
				}
		};
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments, int syntax) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, SessionExpiredException, HasNotTheRightsException, UserDoesNotExistException, GroupDoesNotExistException, EntryDoesNotExistException, GroupAlreadyExistsException, UserInGroupDoesNotExistsException, UsernameAlreadyExistsException, InvitationDoesNotExistException {
		long when;
		String type;
		
		if (syntax == 0) {
			when = arguments.get(1) == null ? System.currentTimeMillis() : (long) arguments.get(1);
			type = (String) arguments.get(0);
		} else if (syntax == 1) {
			when = System.currentTimeMillis();
			type = (String) arguments.get(0);
		} else  {
			when = System.currentTimeMillis();
			type = "month";
		}
		
		HashSet<CalendarEntry> entries = RequestHandler.getAllEntriesForUser(handler.getUsername());
		
		if (type.equals("month")) {
			MonthCalendar month = new MonthCalendar(when);
			for (CalendarEntry entry : entries)
				month.insert(entry.getStartTime(), entry.getEndTime());
			return month.toString();
		} else if (type.equals("week")) {
			return "TODO";
		} else if (type.equals("day")) {
			return "TODO";
		} else if (type.equals("year")) {
			return "TODO";
		} else {
			return "Not a valid spesification!";
		}
	}
}
