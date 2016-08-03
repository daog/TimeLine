package com.timeline.common;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;

public class DateTimeHelper {

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static Date GetDateTimeNow() {
		return StringToDate(getDateTimeNow());
	}

	public static Date StringToDate(String str) {
		return StringToDate(str, "yyyy-MM-dd HH:mm:ss");
	}

	public static Date DayStringToDate(String str) {
		return StringToDate(str, "yyyy-MM-dd");
	}

	/**
	 * 字符串转日期，根据输入的格式
	 * 
	 * @param str
	 * @param format
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date StringToDate(String str, String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			date = sdf.parse(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 日期转字符串，默认格�?
	 * 
	 * @param date
	 * @return
	 */
	public static String DateToString(Date date) {
		return DateToString(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 日期转字符串，根据输入的格式
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String DateToString(Date date, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 在指定的日期上加小时
	 * 
	 * @param currentDate
	 * @param n
	 * @return
	 */
	public static Date AddHours(Date currentDate, int n) {
		try {
			Date resultDate = new Date();
			Calendar cd = Calendar.getInstance();
			cd.setTime(currentDate);
			cd.add(Calendar.HOUR, n);
			resultDate = cd.getTime();

			return resultDate;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 在指定的日期上加分钟
	 * 
	 * @param currentDate
	 * @param n
	 * @return
	 */
	public static Date AddMinutes(Date currentDate, int n) {
		try {
			Date resultDate = new Date();
			Calendar cd = Calendar.getInstance();
			cd.setTime(currentDate);
			cd.add(Calendar.MINUTE, n);
			resultDate = cd.getTime();

			return resultDate;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 在指定的日期上加�?
	 * 
	 * @param currentDate
	 * @param n
	 * @return
	 */
	public static Date AddSeconds(Date currentDate, int n) {
		try {
			Date resultDate = new Date();
			Calendar cd = Calendar.getInstance();
			cd.setTime(currentDate);
			cd.add(Calendar.SECOND, n);
			resultDate = cd.getTime();

			return resultDate;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 在指定的日期上加天数
	 * 
	 * @param currentDate
	 * @param n
	 * @return
	 */
	public static Date AddDays(Date currentDate, int n) {
		try {
			Date resultDate = new Date();
			Calendar cd = Calendar.getInstance();
			cd.setTime(currentDate);
			cd.add(Calendar.DATE, n);
			resultDate = cd.getTime();

			return resultDate;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 在指定的日期上加周数
	 * 
	 * @param currentDate
	 * @param n
	 * @return
	 */
	public static Date AddWeeks(Date currentDate, int n) {
		return AddDays(currentDate, n * 7);
	}

	/**
	 * 在指定的日期上加季度�?
	 * 
	 * @param currentDate
	 * @param n
	 * @return
	 */
	public static Date AddQuarter(Date currentDate, int n) {
		return AddMonths(currentDate, n * 3);
	}

	/**
	 * 获取日期当月�?大天�?
	 * 
	 * @param currentDate
	 * @return
	 */
	public static int GetMaxDayofMonth(Date dt) {
		Calendar cd = Calendar.getInstance();
		cd.setTime(dt);
		int maxDay = cd.getActualMaximum(Calendar.DAY_OF_MONTH);
		return maxDay;
	}

	/**
	 * 获取日期当前旬最大天�?
	 * 
	 * @param dt
	 * @return
	 */
	public static int GetMaxDayofXun(Date dt) {
		int maxDayofXun = 10;
		int day = GetDayOfMonth(dt);
		int maxDayofMonth = GetMaxDayofMonth(dt);
		if (day >= 21)
			maxDayofXun = maxDayofMonth - 20;
		return maxDayofXun;
	}

	/**
	 * 计算日期�?在月中的�?,�?1�?始计�?
	 * 
	 * @param dt
	 * @return
	 */
	public static int GetXunOfMonth(Date dt) {
		int xun = 1;
		int day = GetDayOfMonth(dt);
		if (day <= 10)
			xun = 1;
		else if (day >= 11 && day <= 20)
			xun = 2;
		else if (day >= 21)
			xun = 3;
		return xun;
	}

	/**
	 * 获取日期�?在月的第几天
	 * 
	 * @param dt
	 * @return
	 */
	public static int GetDayOfMonth(Date dt) {
		Calendar c = Calendar.getInstance();
		c.setTime(dt);

		int day = c.get(Calendar.DAY_OF_MONTH);
		return day;
	}

	/**
	 * 获取日期在旬的第几天
	 * 
	 * @param dt
	 * @return
	 */
	public static int GetDayOfXun(Date dt) {
		int day = GetDayOfMonth(dt);
		int dayOfXun = 1;
		if (day <= 10)
			dayOfXun = day;
		else if (day <= 20)
			dayOfXun = day - 10;
		else
			dayOfXun = day - 20;
		return dayOfXun;
	}

	/**
	 * 在指定的日期上加旬数，如果加完旬后当前月没有该天就返回当月的�?后一�?
	 * 
	 * @param currentDate
	 * @param n
	 * @return
	 */
	public static Date AddXuns(Date currentDate, int n) {
		try {

			// 基准日期�?在旬的第几天
			int beginDayofXun = GetDayOfXun(currentDate);

			// 离开基准的月�?
			int monthCount = n / 3;
			// 离基准除掉月数后剩余的旬�?
			int xunCountMode = n % 3;
			// 基准日期�?在旬的第�?天对应日�?
			Date beginTempDate = AddDays(currentDate, 0 - beginDayofXun + 1);

			// �?始计算加旬后对应旬第�?天日�?
			Date endTempDate = AddMonths(beginTempDate, monthCount);
			// 每旬�?�?11天，离基准除掉月数后剩余的旬包含�?大天数，dayTemp�?�?22�?
			int dayTemp = xunCountMode * 11;
			// 注意旬第�?天加上dayTemp不会垮月
			endTempDate = AddDays(endTempDate, dayTemp);
			int dayofXunTemp = GetDayOfXun(endTempDate);
			// 结束加旬后对应旬第一天日�?
			endTempDate = AddDays(endTempDate, 0 - dayofXunTemp + 1);
			// 加旬后对应旬的最大天�?
			int endmaxDay = GetMaxDayofXun(endTempDate);

			dayTemp = beginDayofXun - 1;
			if (dayTemp > endmaxDay)
				dayTemp = endmaxDay;

			Date resultDate = AddDays(endTempDate, dayTemp);

			return resultDate;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 在指定的日期上加月数
	 * 
	 * @param currentDate
	 * @param n
	 * @return
	 */
	public static Date AddMonths(Date currentDate, int n) {
		try {
			Date resultDate = new Date();
			Calendar cd = Calendar.getInstance();
			cd.setTime(currentDate);
			cd.add(Calendar.MONTH, n);
			resultDate = cd.getTime();

			return resultDate;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取当前时间（format:yyyy-MM-dd HH:mm:ss�?
	 * 
	 * @return
	 */
	public static String getDateTimeNow() {
		String curDate = DateTimeHelper.DateToString(
				new java.util.Date(System.currentTimeMillis()),
				"yyyy-MM-dd HH:mm:ss");
		return curDate;
	}

	/**
	 * 获取当前时间（format:yyyyMMddHHmmss�?
	 * 
	 * @return
	 */
	public static String getDateTimeNow1() {
		String curDate = DateTimeHelper.DateToString(
				new java.util.Date(System.currentTimeMillis()),
				"yyyyMMddHHmmss");
		return curDate;
	}

	/**
	 * 获取当前时间（format:yyyyMMddHH�?
	 * 
	 * @return
	 */
	public static String getDateTimeNow2() {
		String curDate = DateTimeHelper.DateToString(
				new java.util.Date(System.currentTimeMillis()), "yyyyMMddHH");
		return curDate;
	}

	/**
	 * 获取当前日期（format:yyyy-MM-dd�?
	 * 
	 * @return
	 */
	public static String getDateNow() {
		String curDate = DateTimeHelper.DateToString(
				new java.util.Date(System.currentTimeMillis()), "yyyy-MM-dd");
		return curDate;
	}

	/**
	 * 获取当前日期（format:HH:mm�?
	 * 
	 * @return
	 */
	public static String getTimeNow() {
		String curDate = DateTimeHelper.DateToString(
				new java.util.Date(System.currentTimeMillis()), "HH:mm");
		return curDate;
	}

	/**
	 * 获取当前日期（format:HH:mm:ss�?
	 * 
	 * @return
	 */
	public static String getTimeNow1() {
		String curDate = DateTimeHelper.DateToString(
				new java.util.Date(System.currentTimeMillis()), "HH:mm:ss");
		return curDate;
	}

	/**
	 * 时间字符串格式转�?
	 * 
	 * @param datetimeString
	 *            原时间字符串
	 * @param datetimeformat
	 *            转换完成后的时间格式
	 * @return
	 */
	public static String TransDateTime(String datetimeString,
			String datetimeformat) {
		String curDate = DateTimeHelper.DateToString(
				StringToDate(datetimeString), datetimeformat);
		return curDate;
	}

	/**
	 * 获取年
	 * 
	 * @param date
	 * @return
	 */
	public static Integer getYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		return year;
	}

	/**
	 * 获取月
	 * 
	 * @param date
	 * @return
	 */
	public static Integer getMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month = c.get(Calendar.MONTH);
		return month + 1;
	}

	/**
	 * 获取月的英文
	 * 
	 * @param date
	 * @return
	 */
	public static String getMonthEn(String mon) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		Date date = null;
		try {
			date = sdf.parse(mon);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sdf = new SimpleDateFormat("MMMMM", Locale.US);
		return sdf.format(date);

	}

	/**
	 * 获取天
	 * 
	 * @param date
	 * @return
	 */
	public static Integer getDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		return day;
	}

	/**
	 * 判断时间是否在时间段内
	 * 
	 * @param date
	 *            当前时间 yyyy-MM-dd
	 * @param strDateBegin
	 *            开始时间
	 * @param strDateEnd
	 *            结束时间
	 * @return
	 */
	public static boolean isInDate(String date, String strDateBegin,
			String strDateEnd) {
		Date nowDate = DayStringToDate(date);
		Date staDate = DayStringToDate(strDateBegin);
		Date endDate = DayStringToDate(strDateEnd);
		if (nowDate.after(staDate) && nowDate.before(endDate)) {
			return true;
		} else if (nowDate.equals(staDate) || nowDate.equals(endDate)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断时间是否在时间段内
	 * 
	 * @param date
	 *            当前时间 yyyy-MM-dd
	 * @param strDateBegin
	 *            开始时间
	 * @param strDateEnd
	 *            结束时间
	 * @return
	 */
	public static boolean isInDate(String date, Date DateBegin, Date DateEnd) {
		Date nowDate = DayStringToDate(date);
		;
		if (nowDate.after(DateBegin) && nowDate.before(DateEnd)) {
			return true;
		} else if (nowDate.equals(DateBegin) || nowDate.equals(DateEnd)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取当月第一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getFirstDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		// 设置年份
		cal.set(Calendar.YEAR, year);
		// 设置月份
		cal.set(Calendar.MONTH, month - 1);
		// 设置日历中月份的第1天
		cal.set(Calendar.DAY_OF_MONTH, 1);
		// 格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String firstDayOfMonth = sdf.format(cal.getTime());

		return firstDayOfMonth;
	}

	/**
	 * 获取当月最后一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getLastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		// 设置年份
		cal.set(Calendar.YEAR, year);
		// 设置月份
		cal.set(Calendar.MONTH, month - 1);
		// 设置日历中月份的第1天
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		// 格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String firstDayOfMonth = sdf.format(cal.getTime());

		return firstDayOfMonth;
	}

	/**
	 * 转为date
	 * 
	 * @param datestr
	 * @return
	 */
	public static String Str2Date(String datestr) {
		Date date = new Date(Long.valueOf(datestr) * 1000);
		return DateToString(date, "yyyy-MM-dd");
	}

	/**
	 * 秒转化为时间
	 * 
	 * @param time
	 * @return
	 */
	public static String int2Time(int time) {
		DecimalFormat df = new DecimalFormat("00");
		String hour = df.format((int) time / 3600);
		String min = df.format((int) (time % 3600) / 60);
		String sec = df.format((int) time % 60);

		return String.valueOf(hour) + ":" + String.valueOf(min);

	}

	/**
	 * 秒转化为时间(HH:mm:ss)
	 * 
	 * @param time
	 * @return
	 */
	public static String int2TimeHHmmss(int time) {
		DecimalFormat df = new DecimalFormat("00");
		String hour = df.format((int) time / 3600);
		String min = df.format((int) (time % 3600) / 60);
		String sec = df.format((int) time % 60);

		return hour + ":" + min + ":" + sec;

	}
}
