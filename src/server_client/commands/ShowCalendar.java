package server_client.commands;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.SimpleFormatter;

import calendar.Calendar;
import calendar.CalendarEntry;
import calendar.GenericCalendar;
import calendar.Invitation;
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
				}
		}; //TODO: Add optional arguments for agenda, month, week, year
	}

	@Override
	public String[] getExamples() {
		return new String[0];
	}

	@Override
	public String run(ServerClientHandler handler, List<Object> arguments, int syntax) throws IOException, TimeoutException, InterruptedException, ForcedReturnException, SessionExpiredException, HasNotTheRightsException, UserDoesNotExistException, GroupDoesNotExistException, EntryDoesNotExistException, GroupAlreadyExistsException, UserInGroupDoesNotExistsException, UsernameAlreadyExistsException, InvitationDoesNotExistException {
		//Calendar user_calendar = RequestHandler.createCalendar(handler.getUser());
		HashSet<Invitation> invitations = RequestHandler.getInvitations(handler.getUser());//user_calendar.getEntries();
		HashSet<CalendarEntry> entries = new HashSet<>();
		for (Invitation temp_entry : invitations)
			entries.add(RequestHandler.getEntry(handler.getUser(), temp_entry.getEntry_id()));
		
		GregorianCalendar calendar = new GregorianCalendar();
		
		if (arguments.get(1) != null) calendar.setTimeInMillis((long) arguments.get(1));
		else calendar.setTimeInMillis(System.currentTimeMillis());
		
		if (arguments.get(0).equals("month")) {
			boolean[][] c = new boolean[7][6];
			boolean[][] n = new boolean[7][6];
			
			for (CalendarEntry entry : entries) {
				long time = entry.getStartTime();
				GregorianCalendar temp_cal = new GregorianCalendar();
				temp_cal.setTimeInMillis(time);
				if (temp_cal.get(GregorianCalendar.YEAR) == calendar.get(GregorianCalendar.YEAR))
					if (temp_cal.get(GregorianCalendar.MONTH) == calendar.get(GregorianCalendar.MONTH))
						c[startAtMonday(temp_cal.get(GregorianCalendar.DAY_OF_WEEK))][temp_cal.get(GregorianCalendar.WEEK_OF_MONTH)] = true;
			}
			
			GregorianCalendar temp_cal = new GregorianCalendar();
			temp_cal.setTimeInMillis(System.currentTimeMillis());
			if (temp_cal.get(GregorianCalendar.YEAR) == calendar.get(GregorianCalendar.YEAR))
				if (temp_cal.get(GregorianCalendar.MONTH) == calendar.get(GregorianCalendar.MONTH))
					n[startAtMonday(temp_cal.get(GregorianCalendar.DAY_OF_WEEK))][temp_cal.get(GregorianCalendar.WEEK_OF_MONTH)] = true;
			
			calendar.set(GregorianCalendar.DAY_OF_MONTH, 1);
			System.out.println(calendar.get(GregorianCalendar.DAY_OF_WEEK));
			int start = startAtMonday(calendar.get(GregorianCalendar.DAY_OF_WEEK));
			int number_of_days = calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
			int t = (-1)*start;
			System.out.println(start + "," + number_of_days + "["+GregorianCalendar.MONDAY+","+GregorianCalendar.TUESDAY+","+GregorianCalendar.WEDNESDAY+","+GregorianCalendar.THURSDAY+","+GregorianCalendar.FRIDAY+","+GregorianCalendar.SATURDAY+","+GregorianCalendar.SUNDAY+"]");
			String message = ""
					+ "+----------------------------------+\n"
					+ "|             " + new SimpleDateFormat("MMM yyyy").format(calendar.getTime()).toUpperCase() + "             |\n"
					+ "+----+----+----+----+----+----+----+\n"
					+ "| MO | TU | WE | TU | FR | SA | SU |\n"
					+ "+----+----+----+----+----+----+----+\n";
			
			for (int y = 0; y < 6; y++) {
				for (int x = 0; x < 7; x++) {
					message += "| "+(++t>number_of_days?"  ":t<1?"  ":t<10?" "+t:t)+(c[x][y]?"*":" ");
				}
				message += "|\n";
				for (int x = 0; x < 7; x++) {
					message += "| "+(n[x][y]?"--":"  ")+" ";
				}
				message += "|\n"
						+ "+----+----+----+----+----+----+----+\n";
			}
			
			return message;
		} else if (arguments.get(0).equals("week")) {
			return "TODO";
		} else if (arguments.get(0).equals("day")) {
			return "TODO";
		} else if (arguments.get(0).equals("year")) {
			return "TODO";
		} else if (arguments.get(0).equals("agenda")) {
			if (entries.size() == 0) return "You are not invited to anything! :(";
			String message = "Future plans:\n";
			
			for (CalendarEntry entry : entries) {
				message += "(" + new Date(entry.getStartTime()) + " - " + new Date(entry.getEndTime()) + ") " + entry.getDescription() + "\n";
			}
			
			return message;
		} else {
			return "Not a valid spesification!";
		}
	}
	
	private int startAtMonday(int weekday) {
		weekday = (weekday - 2) % 7; // Transform from week starting with Sunday to Monday
		weekday = (weekday < 0) ? (weekday + 7) : weekday; //Fix weird thing with modulo in java
		return weekday;
	}
}
