package calendar;

/**
 * This class provides some basic calendar informations.
 *
 */
public class GenericCalendar {

	public static int daysInMonth(int nr, int year) {
		int days;

		if (nr == 1 || nr == 3 || nr == 5 || nr == 7 || nr == 8 || nr == 10
				|| nr == 12) {
			days = 31;
		} else if (nr == 4 || nr == 6 || nr == 9 || nr == 11) {
			days = 30;
		} else if (nr == 2) {
			if (isLeapYear(year)) {
				days = 29;
			} else {
				days = 28;
			}
		} else {
			throw new IllegalArgumentException(nr + " is not a valid day!");
		}
		return days;
	}

	public static int getYearStartDay(int year) {
		int startDay = 0;

		for (int j = 1900; j < year + 1; j++) {
			startDay++;
			if (isLeapYear(j - 1)) {
				startDay++;
			}
			if (startDay > 7) {
				startDay = startDay % 7;
			}
		}
		return startDay;
		
	}

	private static boolean isLeapYear(int year) {
		if (year % 100 == 0 && year % 400 == 0) {
			return true;
		} else if (year % 4 == 0) {
			return true;
		}
		return false;
	}

	public static int getMonthStartDay(int year, int month) {
		if (month > 12 || month < 1) {
			throw new IllegalArgumentException();
		}
		int day = getYearStartDay(year);
		for (int j = 1; j < month; j++) {
			day = +daysInMonth(j, year);
			day = (day) % 7;
		}
		return day;
	}


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
