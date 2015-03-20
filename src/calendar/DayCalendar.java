package calendar;

import interfaces.TypeCalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DayCalendar implements TypeCalendar {
	private boolean[][] grid;
	private Calendar calendar;
	
	private final static int HEIGHT =			24;
	private final static int LAYERS =			2;
	
	private final static int LAYER_NOW =		0;
	private final static int LAYER_MARK =		1;
	
	/**
	 * Makes a calendar over a given month
	 * @param time_represented
	 */
	public DayCalendar(long time_represented) {
		grid = new boolean[HEIGHT][LAYERS];
		calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time_represented);
		insert(System.currentTimeMillis(), LAYER_NOW);
	}

	private void insert(long time, int layer) {
		Calendar calendar_insertion = new GregorianCalendar();
		calendar_insertion.setTimeInMillis(time);
		if (calendar_insertion.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && calendar_insertion.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
			int hour = calendar_insertion.get(Calendar.HOUR_OF_DAY);
			grid[hour][layer] = true;
		}
	}
	
	@Override
	public void insert(long time) {
		insert(time, LAYER_MARK);
	}
	
	@Override
	public void insert(long time_start, long time_stop) {
		long[] times = GenericCalendar.getTimesBetweenBounded(time_start, time_stop, calendar.getTimeInMillis(), Calendar.HOUR_OF_DAY);
		for (long time : times)
			insert(time);
	}
	
	private boolean isNow(int hour) {
		return grid[hour][LAYER_NOW];
	}
	
	private boolean isMarked(int hour) {
		return grid[hour][LAYER_MARK];
	}
	
	@Override
	public String toString() {
		String message = ""
				+ "+-----+------------+---+\n"
				+ "|HH:MM| "
				+ new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime())
				+ " |NOW|\n"
				+ "+-----+------------+---+\n";

		for (int hour = 0; hour < HEIGHT; hour++) {
			message += "|"+(hour<10?"0"+(hour+1):(hour+1))+":00| "
					+ (isMarked(hour) ? "**********" : "          ");
			message += " | "+(isNow(hour)?"X":" ")+" |\n";
		}
		
		message += "+-----+------------+---+\n";
		
		return message;
	}
}
