package org.cabbage.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class DateUtils {
	public static Pattern DATA_PATTERN = Pattern
			.compile("\\d{2,4}\\D\\d{1,2}\\D\\d{1,2}\\D{1,6}\\d{1,2}:\\d{1,2}:\\d{1,2}"
					+ "|\\d{2,4}\\D\\d{1,2}\\D\\d{1,2}\\D{1,6}\\d{1,2}:\\d{1,2}"
					+ "|(\\d{4}|\\d{2})年\\d{1,2}月\\d{1,2}日.{0,6}\\d{1,2}(时|時)\\d{1,2}"
					+ "|(\\d{4}|\\d{2})年\\d{1,2}月\\d{1,2}日"
					+ "|\\d{1,2}月\\d{1,2}日.{0,6}\\d{1,2}(时|時)\\d{1,2}"
					+ "|\\d{2}\\D\\d{2}\\D{1,6}\\d{1,2}:\\d{1,2}"
					+ "|\\d{2,4}\\D\\d{1,2}\\D\\d{1,2}"
					+ "|\\d{1,2}\\.\\d{1,2}\\.\\d{4}\\s*\\d{1,2}:\\d{1,2}"
					+ "|\\d{1,2}/\\d{1,2}/\\d{4}\\s*\\d{1,2}:\\d{1,2}:\\d{1,2}"
					+ "|\\d{1,2}月\\s*\\d{1,2}th,\\s*\\d{4}"
					+ "|\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}"
					+ "|\\w+,\\s*(\\d{1,2}\\s*\\w+\\s*\\d{4})");

	public static Long DAY = 3600l * 1000 * 24;
	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");
	public static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	/**
	 * 将通用抽取匹配到的时间格式转化为标准Date类型
	 * 
	 * @param 通用抽取匹配到的时间格式
	 * @return 转化后的标准Date类型
	 * @throws ParseException
	 */
	public synchronized static Date praseDate(String timeStr)
			throws ParseException {
		Date posttime = null;
		SimpleDateFormat df = null;

		timeStr = timeStr.replaceAll("年|月|\\.|/", "-");
		timeStr = timeStr.replaceAll("日", " ");
		// System.out.println(timeStr);
		if (timeStr
				.matches("\\d{2,4}\\D\\d{1,2}\\D\\d{1,2}\\D{1,6}\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
			df = new SimpleDateFormat("yy-M-d H:m:s");
		} else if (timeStr
				.matches("\\d{2,4}\\D\\d{1,2}\\D\\d{1,2}\\D{1,6}\\d{1,2}:\\d{1,2}")) {
			df = new SimpleDateFormat("yy-M-d H:m");
		} else {
			df = new SimpleDateFormat("yy-M-d");
		}
		if (timeStr
				.matches("\\d{2,4}-\\d{1,2}-\\d{1,2}\\D{1,6}\\d{1,2}:\\d{1,2}|\\d{2,4}-\\d{1,2}-\\d{1,2}|\\d{2,4}-\\d{1,2}-\\d{1,2}\\D{1,6}\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
			posttime = df.parse(timeStr);
		}

		return posttime;
	}

	public synchronized static java.sql.Date getSqlDate(Date date) {
		if (date == null) {
			return null;
		}
		return new java.sql.Date(date.getTime());
	}

	public synchronized static java.sql.Timestamp getSqlTimestamp(Date date) {
		if (date == null) {
			return null;
		}
		return new java.sql.Timestamp(date.getTime());
	}

	public synchronized static Date seasonStart(Date date) {
		Calendar c = Calendar.getInstance();
		List<Date> list = seasonScope(date);
		for (Date d : list) {
			if (date.compareTo(d) >= 0) {
				c.setTime(d);
			}
		}
		return c.getTime();
	}

	public synchronized static Date seasonEnd(Date date) {
		Calendar c = Calendar.getInstance();
		List<Date> list = seasonScope(date);
		for (Date d : list) {
			if (date.compareTo(d) >= 0) {
				c.setTime(d);
			}
		}
		c.setTime(deferMonth(c.getTime(), 3));
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}

	public synchronized static List<Date> seasonScope(Date date) {
		List<Date> seasonList = new ArrayList<Date>();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		for (int i = 0; i < 4; i++) {
			seasonList.add(c.getTime());
			c.setTime(deferMonth(c.getTime(), 3));
		}
		return seasonList;
	}

	public synchronized static List<String> paserDateList(List<Date> dataDates) {
		List<String> dates = new ArrayList<String>();
		for (Date date : dataDates) {
			dates.add((DateUtils.DATE_FORMAT.format(date)));
		}
		return dates;
	}

	/**
	 * 取出lockMonth的下一月，如果传进来2012-2-20，返回2012-3-20
	 * 
	 * @param lockMonth
	 * @return
	 */
	public synchronized static Date nextMonth(Date lockMonth) {
		return deferMonth(lockMonth, 1);
	}

	public synchronized static Date deferMonth(Date date, int deferNum) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, deferNum);
		return c.getTime();
	}

	/**
	 * 取出lockMonth的前一月，如果传进来2012-3-20，返回2012-2-20
	 * 
	 * @param lockMonth
	 * @return
	 */
	public synchronized static Date previousMonth(Date lockMonth) {
		Calendar c = Calendar.getInstance();
		c.setTime(lockMonth);
		c.add(Calendar.MONTH, -1);
		return c.getTime();
	}

	/**
	 * 获取一年中第一个月
	 * 
	 * @param time
	 * @return
	 */
	public synchronized static Date firstMonth(Date time) {
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.set(Calendar.MONTH, 0);
		return c.getTime();
	}

	/**
	 * 获取月的开始日期
	 * 
	 * @param time
	 * @return
	 */
	public synchronized static Date firstDayOfMonth(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	/**
	 * 获取日期月的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public synchronized static Date getLastDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		// 将下个月1号作为日期初始值
		calendar.set(Calendar.DATE, 1);
		// 下个月1号减去一天，即得到当前月最后一天
		calendar.add(Calendar.DATE, -1);
		return calendar.getTime();
	}

	public synchronized static Date nextDay(Date date) {
		return addDay(date, 1);
	}

	public synchronized static Date previousDay(Date date) {
		return addDay(date, -1);
	}

	public synchronized static List<Date> getDateList(Date start, Date end,
			int gap) {
		List<Date> list = new LinkedList<Date>();
		Date date = (Date) start.clone();
		while (date.compareTo(end) <= 0) {
			Date temp = (Date) date.clone();
			list.add(temp);
			date = addDay(date, gap);
		}
		return list;
	}

	public synchronized static List<Date> getEachDate(Date start, Date end) {
		return getDateList(start, end, 1);
	}

	public synchronized static Date addDay(Date date, int day) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, day);
		return cal.getTime();
	}

	public synchronized static Date addMonth(Date date, int month) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.MONTH, month);
		return cal.getTime();
	}

	public synchronized static String getDateString(Date date) {
		return DateUtils.DATE_FORMAT.format(date);
	}

	public synchronized static String getDateTimeString(Date date) {
		return DateUtils.DATE_TIME_FORMAT.format(date);
	}

	public synchronized static Date getDate(String date) throws ParseException {
		return DateUtils.DATE_FORMAT.parse(date);
	}

	public synchronized static Date getDateTime(String dateTime)
			throws ParseException {
		return DateUtils.DATE_TIME_FORMAT.parse(dateTime);
	}

	public synchronized static Date yesterday() {
		long c = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
		c = c / DateUtils.DAY * DateUtils.DAY - DateUtils.DAY;
		cal.setTimeInMillis(c);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		return cal.getTime();
	}
	
	public synchronized static String getCurrDateTime() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		return DATE_TIME_FORMAT.format(cal.getTime());
	}
	
	public synchronized static String getCurrDate() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		return DATE_FORMAT.format(cal.getTime());
	}
}
