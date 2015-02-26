package calendar;

public class GenericCalendar {

	
	
	public int daysInMonth(int nr, int year){
		int days;
		
		if (nr == 1 || nr == 3|| nr == 5 || nr == 7 || nr == 8|| nr == 10 ||nr == 12){
			days= 31;
		}else if (nr == 4||nr == 6 || nr == 9|| nr == 11){
			days = 30;
		}else if (nr == 2){
			if (isLeapYear(year)){
				days = 29;
			}else{
				days = 28;
			}
		}else{
			throw new IllegalArgumentException();
		}
		return days;
	}
	
	public int getYearStartDay(int year){
		int startDay =0;
		
		for (int j = 1900; j < year+1; j++) {
			startDay++;
			if (isLeapYear(j-1)){
				startDay++;
			}
			if (startDay>7){
				startDay = startDay % 7;
			}
		}
		return startDay;
		
	}
	
	private boolean isLeapYear(int year) {
		if (year % 100 == 0 && year % 400 == 0){
			return true;
		}else if(year % 4 ==0 ){
			return true;
		}
		return false;
	}
	
	
	public int getMonthStartDay(int year, int month){
		if (month > 12|| month <1 ){
			throw new IllegalArgumentException();
		}
	    int day= getYearStartDay(year);
	    for (int j = 1; j < month; j++) {
	        day=+ daysInMonth(j, year);
	        day = day % 7;
	    }
	    return day;
	} 
	
	
	public String getMonthName(int month){
		if (month == 1){
			return "January";
		}else if(month == 2){
			return "March";
		}else if(month == 3){
			return "April";
		}else if(month == 4){
			return "May";
		}else if(month == 5){
			return "June";
		}else if(month == 6){
			return "July";
		}else if(month == 7){
			return "August";
		}else if(month == 8){
			return "February";
		}else if(month == 9){
			return "February";
		}else if(month == 10){
			return "February";
		}else if(month == 11){
			return "February";
		}else(month == 12){
			return "February";
		}
	}




}



