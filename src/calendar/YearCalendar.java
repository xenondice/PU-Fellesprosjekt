package calendar;

import interfaces.TypeCalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class YearCalendar implements TypeCalendar {
	
	private boolean[][][] grid;
	private Calendar calendar;
		
	private final static int HEIGHT =			3;
	private final static int WIDTH =			4;
	private final static int LAYERS =			2;
		
	private final static int LAYER_NOW =		0;
	private final static int LAYER_MARK =		1;
		
	/**
	 * Makes a calendar over a given month
	 * @param time_represented
	 */
	public YearCalendar(long time_represented) {
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
		if (calendar_insertion.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
			
			int month = calendar_insertion.get(Calendar.MONTH);
			System.out.println(month);
			
			int x = month % WIDTH;
			int y = month / WIDTH;
			grid[x][y][layer] = true;
		}
	}
	
	public void insert(long time) {
		insert(time, LAYER_MARK);
	}
	
	public void insert(long time_start, long time_stop) {
		Calendar calendar_temp = new GregorianCalendar();
		
		calendar_temp.setTimeInMillis(calendar.getTimeInMillis());
		calendar_temp.set(Calendar.DAY_OF_YEAR, calendar_temp.getActualMinimum(Calendar.DAY_OF_YEAR));
		calendar_temp.set(Calendar.MILLISECOND, calendar_temp.getActualMinimum(Calendar.MILLISECOND));
		calendar_temp.set(Calendar.SECOND, calendar_temp.getActualMinimum(Calendar.SECOND));
		calendar_temp.set(Calendar.MINUTE, calendar_temp.getActualMinimum(Calendar.MINUTE));
		calendar_temp.set(Calendar.HOUR_OF_DAY, calendar_temp.getActualMinimum(Calendar.HOUR_OF_DAY));
		long first_time = calendar_temp.getTimeInMillis();
		System.out.println(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(first_time));
		
		calendar_temp.setTimeInMillis(calendar.getTimeInMillis());
		calendar_temp.set(Calendar.DAY_OF_YEAR, calendar_temp.getActualMaximum(Calendar.DAY_OF_YEAR));
		calendar_temp.set(Calendar.MILLISECOND, calendar_temp.getActualMaximum(Calendar.MILLISECOND));
		calendar_temp.set(Calendar.SECOND, calendar_temp.getActualMaximum(Calendar.SECOND));
		calendar_temp.set(Calendar.MINUTE, calendar_temp.getActualMaximum(Calendar.MINUTE));
		calendar_temp.set(Calendar.HOUR_OF_DAY, calendar_temp.getActualMaximum(Calendar.HOUR_OF_DAY));
		long last_time = calendar_temp.getTimeInMillis();
		System.out.println(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(last_time));
		
		if (time_start > time_stop) return;
		else if (time_stop < first_time || time_start > last_time) return;
		
		time_start = time_start < first_time ? first_time : time_start;
		time_stop = time_stop > last_time ? last_time : time_stop;

		Calendar calendar_start = new GregorianCalendar();
		calendar_start.setTimeInMillis(time_start);
		Calendar calendar_stop = new GregorianCalendar();
		calendar_stop.setTimeInMillis(time_stop);
		
		calendar_temp.setTimeInMillis(calendar.getTimeInMillis());
		for (int i = calendar_start.get(Calendar.MONTH); i <= calendar_stop.get(Calendar.MONTH); i++) {
			System.out.println("month: " + i);
			calendar_temp.set(Calendar.MONTH, i);
			insert(calendar_temp.getTimeInMillis());
		}
	}
	
	private boolean isNow(int x, int y) {
		return grid[x][y][LAYER_NOW];
	}
	
	private boolean isMarked(int x, int y) {
		return grid[x][y][LAYER_MARK];
	}
	
	@Override
	public String toString() {
		Calendar temp_calendar = new GregorianCalendar();
		temp_calendar.setTimeInMillis(calendar.getTimeInMillis());
		
		String message = ""
				+ "+-----------------------+\n"
				+ "|         "
				+ new SimpleDateFormat("yyyy").format(calendar.getTime())
				+ "          |\n"
				+ "+-----+-----+-----+-----+\n";
		
		int month = -1;
		for (int y = 0; y < HEIGHT; y++) {
			
			for (int x = 0; x < WIDTH; x++) {
				month++;
				temp_calendar.set(Calendar.MONTH, month);
				message += "| "
						+ new SimpleDateFormat("MMM").format(temp_calendar.getTimeInMillis()).toUpperCase()
						+ (isMarked(x,y) ? "*" : " ");
			}
			message += "|\n";
			
			for (int x = 0; x < WIDTH; x++)
				message += "| "
						+ (isNow(x,y) ? "---" : "   ")
						+ " ";
			
			message += "|\n"
					+ "+-----+-----+-----+-----+\n";
		}
		
		return message;
	}
}
