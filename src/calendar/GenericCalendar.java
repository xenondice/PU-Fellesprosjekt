package calendar;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.Calendar;

/**
 * This class provides some basic calendar informations.
 *
 */
public abstract class GenericCalendar {
	
	/**
	 * Returns a set over which of the given entries are not over yet.
	 * @param entries
	 * @return
	 */
	public static Set<CalendarEntry> getActiveEntries(Set<CalendarEntry> entries) {
		long current_time = System.currentTimeMillis();
		Set<CalendarEntry> active_entries = new HashSet<>();
		
		for (CalendarEntry entry : entries)
			if (entry.getEndTime() >= current_time)
				active_entries.add(entry);
		
		return active_entries;
	}
	
	/**
	 * Returns how many days there is in a given month.
	 * @param time
	 * @return
	 */
	public static int getDaysInMonth(long time) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		return calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
	}

	/**
	 * Return what day the month starts on, 1 = Monday, 2 = Tuesday, ...
	 * @param time
	 * @return
	 */
	public static int getMonthStartDay(long time) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		calendar.set(GregorianCalendar.DAY_OF_MONTH, 1);
		return weekStartAtMonday(calendar.get(GregorianCalendar.DAY_OF_WEEK));
	}
	
	/**
	 * Transform a week starting at Sunday to a week starting at Monday
	 * @param week_day
	 * @return
	 */
	public static int weekStartAtMonday(int week_day) {
		week_day = (week_day - 2) % 7;							// Transform week starting at Sunday to Monday
		week_day = (week_day < 0) ? (week_day + 7) : week_day;	// Stop modulo from allowing negative values
		return week_day + 1;
	}

	/**
	 * Give the name of the day in the week.
	 * @param day
	 * @return
	 */
	public static String getDayName(int day) {
		if (day == 1) {
			return "Monday";
		} else if (day == 2) {
			return "Tuesday";
		} else if (day == 3) {
			return "Wednesday";
		} else if (day == 4) {
			return "Thursday";
		} else if (day == 5) {
			return "Friday";
		} else if (day == 6) {
			return "Saturday";
		} else if (day == 7) {
			return "Sunday";
		} else {
			throw new IllegalArgumentException(day + " is not a valid day!");
		}
	}
}