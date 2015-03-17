package calendar;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MonthCalendar {
	private int[][] grid;
	private final static int HEIGHT = 6;
	private final static int WIDTH = 7;
	
	public MonthCalendar() {
		grid = new int[WIDTH][HEIGHT];
	}
	
	/**
	 * Return a correctly sized grid that can be used in creating a calendar (new int[7][6]).
	 * @return
	 */
	public static int[][] getMonthGrid() {
		return new int[7][6];
	}
	
	/**
	 * Inserts a time into a existing grid in the correct location if it's in the month.
	 * @param value
	 * @param time_of_insertion
	 * @param time_of_grid
	 * @param grid
	 * @return
	 */
	public static int[][] insertIntoGrid(int value, long time_of_insertion, long time_of_grid, int[][] grid) {
		Calendar calendar_insertion = new GregorianCalendar();
		calendar_insertion.setTimeInMillis(time_of_insertion);
		
		Calendar calendar_grid = new GregorianCalendar();
		calendar_grid.setTimeInMillis(time_of_grid);
		
		if (calendar_insertion.get(Calendar.YEAR) == calendar_grid.get(Calendar.YEAR) && calendar_insertion.get(Calendar.MONTH) == calendar_grid.get(Calendar.MONTH)) {
			int x = GenericCalendar.weekStartAtMonday(calendar_insertion.get(GregorianCalendar.DAY_OF_WEEK));
			int y = calendar_insertion.get(GregorianCalendar.WEEK_OF_MONTH);
			grid[x][y] = value;
		}
		
		return grid;
	}
}
