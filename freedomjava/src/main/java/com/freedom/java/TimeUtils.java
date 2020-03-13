/**
 * Copyright 2014 Freedom-Loader Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.freedom.java;

import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.format.DateUtils;
import android.sax.*;
import java.text.SimpleDateFormat;
import java.text.*;

public class TimeUtils {
    public static final int CACHE_DURATION_FOREVER = Integer.MAX_VALUE;
    public static final int DURATION_FOREVER = CACHE_DURATION_FOREVER;
	public static int SECOND_IN_MILLS = 1000;
	private static final int CACHE_MILLS = SECOND_IN_MILLS * 60 * 60 * 24;

	//1 minute = 60 seconds
	//1 hour = 60 x 60 = 3600
	// 1 day = 3600 x 24 = 86400
	// 1 week = 86400 x 7 = 604800
	// 1 month = 604800 x 4 = 2419200
	// 1 year = 2419200 x 12 = 29030400
	public static int MINUTES_IN_MILLS = SECOND_IN_MILLS * 60;
	public static int HOURS_IN_MILLS = MINUTES_IN_MILLS * 60;
	public static int DAYS_IN_MILLS = HOURS_IN_MILLS * 24;
	public static int WEEKS_IN_MILLS = DAYS_IN_MILLS * 7;
	public static int MONTHS_IN_MILLS = WEEKS_IN_MILLS * 4;
	public static int YEARS_IN_MILLS = MONTHS_IN_MILLS *12;
	
    private static final String AGO = " ago";
    private static final String JUST_NOW = "just now";
    private static final String A_MINUTE = "a minute";
    private static final String AN_HOUR = "an hour";
    private static final String SECONDS = " seconds";
    private static final String MINUTES = " minutes";
    private static final String HOURS = " hours";
	private static final String DAYS = " days";
	private static final String WEEKS = " hours";
	private static final String MONTHS = " hours";
	private static final String YEARS = " hours";
    private static final String YESTERDAY = "Yesterday";
	
	private Context con;
	private long time;
	private long end;
	private long diff;

	public TimeUtils(long timeValue) {
		this(null, timeValue);
	}

	public TimeUtils(long timeValue, long endValue) {
		this(null, timeValue, endValue);
	}
	
	public TimeUtils(Context con, long timeValue) {
		this(con, timeValue, 0);
	}
	
	public TimeUtils(Context context, long timeValue, long endValue) {
	    con = context;
		time = timeValue;
		end = endValue;
		
		diff = getDiff(time,end);
	}
	
	public static TimeUtils parse(long timeValue) {
		return parse(null, timeValue);
	}
	
	public static TimeUtils parse(long timeValue, long endValue) {
		return parse(null, timeValue, endValue);
	}
	
	public static TimeUtils parse(final Context con, long timeValue) {
		return parse(con, timeValue, 0);
	}
	
	public static TimeUtils parse(final Context con, long timeValue, long endValue) {
		TimeUtils timeEntry = new TimeUtils(con, timeValue, endValue);
		return timeEntry;
	}

	public TimeUtils setEndTime(long endTime) {
		this.end = endTime;
		return this;
	}

	public TimeUtils setStartTime(long startTime) {
		this.time = startTime;
		return this;
	}
	
	public boolean isToday() {
		boolean isToday = checkDay(0);
		return isToday;
	}
	
	public boolean isYesterday() {
		boolean isYesterday = checkDay(1);
		return isYesterday;
	}
	
	public boolean isCurrentWeek() {
		boolean isThisWeek = checkWeek(0);
		return isThisWeek;
	}

	public boolean isLastWeek() {
		boolean isLastWeek = checkWeek(1);
		return isLastWeek;
	}
	
	public boolean isCurrentMonth() {
		boolean isThisMonth = checkMonth(0);
		return isThisMonth;
	}

	public boolean isLastMonth() {
		boolean isLastMonth = checkMonth(1);
		return isLastMonth;
	}

	public boolean isCurrentYear() {
		boolean isThisYear = checkYear(0);
		return isThisYear;
	}

	public boolean isLastYear() {
		boolean isLastYear = checkYear(1);
		return isLastYear;
	}
	
	public String getSeconds() {
		long elapsedSeconds = diff / SECOND_IN_MILLS;
		return elapsedSeconds + SECONDS;
	}
	
	public String getMinutes() {
		long elapsedMinutes = diff / MINUTES_IN_MILLS;
		return elapsedMinutes + MINUTES;
	}
	
	public String getHours() {
		long elapsedHours = diff / HOURS_IN_MILLS;
		return elapsedHours + HOURS;
	}
	
	public String getDays() {
		long elapsedDays = diff / DAYS_IN_MILLS;
		return elapsedDays + DAYS;
	}
	
	public String getMonth() {
		long elapsedMonths = diff / MONTHS_IN_MILLS;
		return elapsedMonths + MONTHS;
	}
	
	public String getYears() {
		long elapsedYears = diff / YEARS_IN_MILLS;
		return elapsedYears + YEARS;
	}
	
	public String getRealTime() {
		return getRealTime(false);
	}
	
	// trying to return the TimeStamp date
	// check for the date and return the date
	// also include time ago
	@SuppressLint("NewApi")
	public String getRealTime(boolean isDays) {
		if (diff ==0) return "";
		
		if (diff < MINUTES_IN_MILLS) {
			return JUST_NOW;
		} else if (diff < MINUTES_IN_MILLS * 2) {
			return A_MINUTE+ " ";
		} else if (diff < MINUTES_IN_MILLS * 50) {
			return diff / MINUTES_IN_MILLS + MINUTES;
		} else if (diff < MINUTES_IN_MILLS * 90) {
			return AN_HOUR;
		} else if (diff < HOURS_IN_MILLS * 24) {
			return diff / HOURS_IN_MILLS + " "+ HOURS ;
		} else if (diff < HOURS_IN_MILLS * 48) {
			return YESTERDAY;
		} else if (diff < HOURS_IN_MILLS * 72) {
			if (isDays) {
				return con == null ? diff / DAYS_IN_MILLS + " days" 
					:DateUtils.formatDateTime(con, time,
						DateUtils.FORMAT_SHOW_TIME
						| DateUtils.FORMAT_SHOW_DATE) + " ";
			} else {
				 return diff / DAYS_IN_MILLS + " days";
			}
		} else {
			if (isDays) {
				String label = con == null ? diff / DAYS_IN_MILLS + " days" 
					:DateUtils.formatDateTime(con, time,
						DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL) + " ";
		      	return label;
			} else {
		        return diff / DAYS_IN_MILLS + " days";
			}
		}
	}

	public boolean checkSecond(int status) {
		return checkDate(T.SECOND,status);
	}

	public boolean checkMinute(int status) {
		return checkDate(T.MINUTE,status);
	}

	public boolean checkHour(int status) {
		return checkDate(T.HOUR,status);
	}

	public boolean checkDay(int status) {
		return checkDate(T.DAY,status);
	}

	public boolean checkWeek(int status) {
		return checkDate(T.WEEK,status);
	}

	public boolean checkMonth(int status) {
		return checkDate(T.MONTH,status);
	}

	public boolean checkYear(int status) {
		return checkDate(T.YEAR,status);
	}

	public boolean checkDate(String t, int status) {
		boolean isCheck = false;
		if (diff == 0) {
			isCheck = false;
		} else {
			if (t.equals(T.SECOND) || t.equals("1")) {
				isCheck = checkDate(60, diff, status, SECOND_IN_MILLS);
			} 
			else if (t.equals(T.MINUTE) || t.equals("2")) {
				isCheck = checkDate(60, diff, status, MINUTES_IN_MILLS);
		    } 
			else if (t.equals(T.HOUR) || t.equals("3")) {
				isCheck = checkDate(24, diff, status, HOURS_IN_MILLS);
			} 
			else if(t.equals(T.DAY) || t.equals("4")) {
				isCheck = checkDate(7, diff, status, DAYS_IN_MILLS);
			}
			else if (t.equals(T.WEEK) || t.equals("5")) {
				isCheck = checkDate(4, diff, status, WEEKS_IN_MILLS);
			}
			else if (t.equals(T.MONTH) || t.equals("6")) {
				isCheck = checkDate(12, diff, status, MONTHS_IN_MILLS);
			} 
			else if (t.equals(T.YEAR) || t.equals("7")) {
				isCheck = checkDate(50, diff, status, YEARS_IN_MILLS);
			}
		}
		return isCheck;
	}
	
	public static long valueTime = 0;
	private static boolean checkDate(int millsLimit, long diff, int status, long mills) {
		boolean isMillsLimit = status <= 100000000;
		boolean isCheck = false;
		if (isMillsLimit) {
			if (diff < mills * millsLimit) {
				long value =  diff / mills;
				valueTime = value;
				if (value == status) {
					isCheck = true;
				}
			}
		}
		return isCheck;
	}
	
	public boolean hasPassSecond(int status) {
		return hasPass(T.SECOND,status);
	}
	
	public boolean hasPassMinute(int status) {
		return hasPass(T.MINUTE,status);
	}

	public boolean hasPassHour(int status) {
		return hasPass(T.HOUR,status);
	}

	public boolean hasPassDay(int status) {
		return hasPass(T.DAY,status);
	}

	public boolean hasPassWeek(int status) {
		return hasPass(T.WEEK,status);
	}

	public boolean hasPassMonth(int status) {
		return hasPass(T.MONTH,status);
	}

	public boolean hasPassYear(int status) {
		return hasPass(T.YEAR,status);
	}
	
	public boolean hasPass(String t,int status) {
		boolean isPass = false;
		if (diff == 0) {
			isPass = false;
		} else {
			if (t.equals(T.SECOND) || t.equals("1")) {
				isPass = hasPass(diff, status, SECOND_IN_MILLS);
			} 
			else if (t.equals(T.MINUTE) || t.equals("2")) {
				isPass = hasPass(diff, status, MINUTES_IN_MILLS);
		    } 
			else if (t.equals(T.HOUR) || t.equals("3")) {
				isPass = hasPass(diff, status, HOURS_IN_MILLS);
			} 
			else if(t.equals(T.DAY) || t.equals("4")) {
				int plusDays = status;
				isPass = hasPass(diff, plusDays, DAYS_IN_MILLS);
			}
			else if (t.equals(T.WEEK) || t.equals("5")) {
				isPass = hasPass(diff, status, WEEKS_IN_MILLS);
			}
			else if (t.equals(T.MONTH) || t.equals("6")) {
				isPass = hasPass(diff, status, MONTHS_IN_MILLS);
			} 
			else if (t.equals(T.YEAR) || t.equals("7")) {
				isPass = hasPass(diff, status, YEARS_IN_MILLS);
			}
		}
		return isPass;
	}
	
	private static boolean hasPass(long diff, int status, long mills) {
		boolean isPass = false;
		boolean isMillsLimit = status <= 100000000;
		if (isMillsLimit) {
			if (diff < mills * status) {
				isPass = false;
			}  else {
				isPass = true;
			}
		}
		return isPass;
	}
	
	private static Long getDiff(long start,long end) {
		long time = start;
		if (time < 1000000000000L) {
			time = time * SECOND_IN_MILLS;
		}
		Date times = getCurrentTime(time);
		long now = times.getTime();
		if (time > now || time <= 0) {
			return Long.valueOf(0);
		}
		final long diff = end > 0 ? end - time : now - time;
		return diff;
	}

	public static Date getCurrentTime(long time) {
		Calendar cal = Calendar.getInstance();
		return cal.getTime();
	}
	
	public static class T {
    	public static final String SECOND = "second";
    	public static final String MINUTE = "minute";
		public static final String HOUR = "hour";
		public static final String DAY = "day";
		public static final String WEEK = "week";
		public static final String MONTH = "month";
		public static final String YEAR = "year";
    }

    public static class SECOND {
    	public static final int ONE_SECOND = SECOND_IN_MILLS * 1;
    	public static final int TWO_SECOND = SECOND_IN_MILLS * 2;
    	public static final int THREE_SECOND = SECOND_IN_MILLS * 3;
    	public static final int FOUR_SECOND = SECOND_IN_MILLS * 4;
    	public static final int FIVE_SECOND = SECOND_IN_MILLS * 5;
    	public static final int TEN_SECOND = SECOND_IN_MILLS * 10;
		public static final int TWENTY_SECOND = SECOND_IN_MILLS * 20;
    	public static final int THIRTY_SECOND = SECOND_IN_MILLS * 30;
    	public static final int FOURTY_SECOND =SECOND_IN_MILLS * 40;
		public static final int FIFTY_SECOND = SECOND_IN_MILLS * 50;	
    }

    public static class MINUTE {
    	public static final int ONE_MINUTE = MINUTES_IN_MILLS * 1;
    	public static final int TWO_MINUTE = MINUTES_IN_MILLS * 2;
    	public static final int THREE_MINUTE = MINUTES_IN_MILLS * 3;
    	public static final int FOUR_MINUTE = MINUTES_IN_MILLS *4;
    	public static final int FIVE_MINUTE = MINUTES_IN_MILLS * 5;
    	public static final int TEN_MINUTE = MINUTES_IN_MILLS * 10;
		public static final int TWENTY_MINUTE = MINUTES_IN_MILLS * 20;
		public static final int THIRTY_MINUTE = MINUTES_IN_MILLS * 30;
		public static final int FOURTY_MINUTE = MINUTES_IN_MILLS * 40;
		public static final int FIFTY_MINUTE = MINUTES_IN_MILLS * 50;
    }

    public static class HOUR {
    	public static final int ONE_HOUR =  MINUTES_IN_MILLS * 50;
		public static final int TWO_HOUR = HOURS_IN_MILLS *2 ;
		public static final int THREE_HOUR = HOURS_IN_MILLS * 3;
		public static final int FOUR_HOUR = HOURS_IN_MILLS * 4;
		public static final int FIVE_HOUR = HOURS_IN_MILLS * 5;
		public static final int TEN_HOUR = HOURS_IN_MILLS * 10;
    }

    public static class DAY {
        public static final int ONE_DAYS = CACHE_MILLS * 1;
        public static final int TWO_DAYS = CACHE_MILLS * 2;
        public static final int THREE_DAYS = CACHE_MILLS * 3;
        public static final int FOUR_DAYS = CACHE_MILLS * 4;
        public static final int FIVE_DAYS = CACHE_MILLS * 5;
        public static final int SIX_DAYS = CACHE_MILLS * 6;
		public static final int SEVEN_DAYS = CACHE_MILLS * 7;
    }
	
    public static class WEEK {
        public static final int ONE_WEEK = DAYS_IN_MILLS * 7;
        public static final int TWO_WEEK = ONE_WEEK * 2;
        public static final int THREE_WEEK = ONE_WEEK * 3;
        public static final int FOUR_WEEK = ONE_WEEK * 4;
    }

    public static class MONTH {
        public static final int ONE_MONTH = MONTHS_IN_MILLS;
		public static final int TWO_MONTH = MONTHS_IN_MILLS * 2;
		public static final int THREE_MONTH = MONTHS_IN_MILLS * 3;
		public static final int FOUR_MONTH = MONTHS_IN_MILLS * 4;
		public static final int FIVE_MONTH = MONTHS_IN_MILLS * 5;
		public static final int SIX_MONTH = MONTHS_IN_MILLS * 6;
		public static final int SEVEN_MONTH = MONTHS_IN_MILLS * 7;
		public static final int EIGHT_MONTH = MONTHS_IN_MILLS * 8;
		public static final int NINE_MONTH = MONTHS_IN_MILLS * 9;
		public static final int TEN_MONTH = MONTHS_IN_MILLS * 10;
		public static final int ELEVEN_MONTH = MONTHS_IN_MILLS * 11;
		public static final int TWIHVE_MONTH = MONTHS_IN_MILLS *12; 
    }
}
