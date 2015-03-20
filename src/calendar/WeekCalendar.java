package calendar;

import interfaces.TypeCalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class WeekCalendar implements TypeCalendar{
	private boolean[][][] grid;
	private Calendar calendar;
	
	private final static int HEIGHT =			1;
	private final static int WIDTH =			7;
	private final static int LAYERS =			2;
	
	private final static int LAYER_NOW =		0;
	private final static int LAYER_MARK =		1;
	
	public WeekCalendar(long time_represented) {
		grid = new boolean[WIDTH][HEIGHT][LAYERS];
		calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time_represented);
		insert(System.currentTimeMillis(), LAYER_NOW);
	}

	private void insert(long time, int layer) {
		Calendar calendar_insertion = new GregorianCalendar();
		calendar_insertion.setTimeInMillis(time);
		if (calendar_insertion.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && calendar_insertion.get(Calendar.WEEK_OF_YEAR) == calendar.get(Calendar.WEEK_OF_YEAR)) {
			int day = GenericCalendar.weekStartAtMonday(calendar_insertion.get(Calendar.DAY_OF_WEEK)) - 1;
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
		long[] times = GenericCalendar.getTimesBetweenBounded(time_start, time_stop, calendar.getTimeInMillis(), Calendar.DAY_OF_WEEK);
		for (long time : times)
			insert(time);
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
				+ "+-----------------------------------------+\n"
				+ "|                 WEEK "
				+ new SimpleDateFormat("ww").format(calendar.getTime())
				+ "                 |\n"
				+ "+-----+-----+-----+-----+-----+-----+-----+\n"
				+ "| MON | TUE | WED | THU | FRI | SAT | SUN |\n"
				+ "+-----+-----+-----+-----+-----+-----+-----+\n";
		
		int day = 0;
		for (int y = 0; y < HEIGHT; y++) {
			
			for (int x = 0; x < WIDTH; x++) {
				day++;
				message += "|  "
						+ day
						+ (isMarked(x,y) ? "*" : " ")
						+ " ";
			}
			message += "|\n";
			
			for (int x = 0; x < WIDTH; x++)
				message += "| "
						+ (isNow(x,y) ? "---" : "   ")
						+ " ";
			
			message += "|\n"
					+ "+-----+-----+-----+-----+-----+-----+-----+\n";
		}
		
		return message;
	}
}
