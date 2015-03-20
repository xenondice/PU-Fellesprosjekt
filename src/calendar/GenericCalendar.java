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
	 * Get the earliest time for a specification from Calendar.
	 * @param time
	 * @param spesification
	 * @return
	 */
	private static long getEarliest(long time, int specification) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		calendar.set(specification, calendar.getActualMinimum(specification));
		return calendar.getTimeInMillis();
	}
	
	/**
	 * Get the latest time for a specification from Calendar.
	 * @param time
	 * @param spesification
	 * @return
	 */
	private static long getLatest(long time, int specification) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		calendar.set(specification, calendar.getActualMaximum(specification));
		return calendar.getTimeInMillis();
	}
	
	/**
	 * Get earliest time at a day.
	 * @param time
	 * @return
	 */
	private static long getEarliestInDay(long time) {
		time = getEarliest(time, Calendar.MILLISECOND);
		time = getEarliest(time, Calendar.SECOND);
		time = getEarliest(time, Calendar.MINUTE);
		time = getEarliest(time, Calendar.HOUR_OF_DAY);
		return time;
	}
	
	/**
	 * Get latest time at a day.
	 * @param time
	 * @return
	 */
	private static long getLatestInDay(long time) {
		time = getLatest(time, Calendar.MILLISECOND);
		time = getLatest(time, Calendar.SECOND);
		time = getLatest(time, Calendar.MINUTE);
		time = getLatest(time, Calendar.HOUR_OF_DAY);
		return time;
	}
	
	/**
	 * Get times between to dates with a specified increment.
	 * @param time_start
	 * @param time_stop
	 * @param specification
	 * @return
	 */
	private static long[] getTimesBetween(long time_start, long time_stop, int specification) {
		Calendar calendar_start = new GregorianCalendar();
		calendar_start.setTimeInMillis(time_start);
		Calendar calendar_stop = new GregorianCalendar();
		calendar_stop.setTimeInMillis(time_stop);
		
		int start = calendar_start.get(specification);
		int stop = calendar_stop.get(specification);
		int total = stop - start + 1;
		long[] times = new long[total];
		for (int i = 0; i < total; i++) {
			calendar_stop.set(specification, start + i);
			times[i] = calendar_stop.getTimeInMillis();
		}
		
		return times;
	}
	
	/**
	 * Check if time are correct and within the set bounds.
	 * @param time_start
	 * @param time_stop
	 * @param bound_start
	 * @param bound_stop
	 * @return
	 */
	private static boolean isInBound(long time_start, long time_stop, long bound_start, long bound_stop) {
		if (time_start > time_stop) return false;
		else if (time_stop < bound_start || time_start > bound_stop) return false;
		else return true;
	}
	
	/**
	 * Cap by a lower value.
	 * @param time
	 * @param lower_bound
	 * @return
	 */
	private static long capLowerBound(long time, long lower_bound) {
		return time < lower_bound ? lower_bound : time;
	}
	
	/**
	 * Cap by a higher value.
	 * @param time
	 * @param higher_bound
	 * @return
	 */
	private static long capHigherBound(long time, long higher_bound) {
		return time > higher_bound ? higher_bound : time;
	}
	
	/**
	 * Get times bounded within the maximum and minimum of the given specification based on the given time.
	 * @param time_start
	 * @param time_stop
	 * @param time
	 * @param specification
	 * @return
	 */
	public static long[] getTimesBetweenBounded(long time_start, long time_stop, long time, int specification) {
		long bound_start = getEarliest(getEarliestInDay(time), specification);
		long bound_stop = getLatest(getLatestInDay(time), specification);
		if (!isInBound(time_start, time_stop, bound_start, bound_stop)) return new long[0];
		time_start = capLowerBound(time_start, bound_start);
		time_stop = capHigherBound(time_stop, bound_stop);
		
		return getTimesBetween(time_start, time_stop, specification);
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