package calendar;

import interfaces.TypeCalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MonthCalendar implements TypeCalendar {
	private boolean[][][] grid;
	private Calendar calendar;
	
	private final static int HEIGHT =			6;
	private final static int WIDTH =			7;
	private final static int LAYERS =			2;
	
	private final static int LAYER_NOW =		0;
	private final static int LAYER_MARK =		1;
	
	/**
	 * Makes a calendar over a given month
	 * @param time_represented
	 */
	public MonthCalendar(long time_represented) {
		grid = new boolean[WIDTH][HEIGHT][LAYERS];
		calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time_represented);
		insert(System.currentTimeMillis(), LAYER_NOW);
	}
	
	/**
	 * Inserts a time into the calendar in the correct location if it's in the month.
	 * @param value
	 * @param time_of_insertion
	 * @param time_of_grid
	 * @param grid
	 * @return
	 */
	private void insert(long time, int layer) {
		Calendar calendar_insertion = new GregorianCalendar();
		calendar_insertion.setTimeInMillis(time);
		if (calendar_insertion.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && calendar_insertion.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
			int day = calendar_insertion.get(Calendar.DAY_OF_MONTH) + GenericCalendar.getMonthStartDay(time) - 2;
			int x = day % WIDTH;
			int y = day / WIDTH;
			grid[x][y][layer] = true;
		}
	}
	
	@Override
	public void insert(long time) {
		insert(time, LAYER_MARK);
	}
	
	@Override
	public void insert(long time_start, long time_stop) {
		long[] times = GenericCalendar.getTimesBetweenBounded(time_start, time_stop, calendar.getTimeInMillis(), Calendar.DAY_OF_MONTH);
		for (long time : times)
			insert(time);
	}
	
	private boolean isNow(int week_day, int week_in_month) {
		return grid[week_day][week_in_month][LAYER_NOW];
	}
	
	private boolean isMarked(int week_day, int week_in_month) {
		return grid[week_day][week_in_month][LAYER_MARK];
	}
	
	@Override
	public String toString() {
		int day = (-1)*GenericCalendar.getMonthStartDay(calendar.getTimeInMillis()) + 1; // Add a delay to the days
		int number_of_days = GenericCalendar.getDaysInMonth(calendar.getTimeInMillis());
		
		String message = ""
				+ "+----------------------------------+\n"
				+ "|             "
				+ new SimpleDateFormat("MMM yyyy").format(calendar.getTime()).toUpperCase()
				+ "             |\n"
				+ "+----+----+----+----+----+----+----+\n"
				+ "| MO | TU | WE | TU | FR | SA | SU |\n"
				+ "+----+----+----+----+----+----+----+\n";
		
		for (int y = 0; y < HEIGHT; y++) {
			
			for (int x = 0; x < WIDTH; x++)
				message += "| "
						+ (++day > number_of_days ? "  " : day < 1 ? "  " : day < 10 ? " " + day : day)
						+ (isMarked(x,y) ? "*" : " ");
			
			message += "|\n";
			
			for (int x = 0; x < WIDTH; x++)
				message += "| "
						+ (isNow(x,y) ? "--" : "  ")
						+ " ";
			
			message += "|\n"
					+ "+----+----+----+----+----+----+----+\n";
		}
		
		return message;
	}
}