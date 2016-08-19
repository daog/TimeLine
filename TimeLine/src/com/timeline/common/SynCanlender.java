package com.timeline.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.timeline.app.AppContext;
import com.timeline.bean.MeetingInfo;
import com.timeline.sqlite.InfoHelper;

import android.R.integer;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

public class SynCanlender {
    //Android2.2版本以后的URL，之前的就不写了 
    private static String calanderURL = "content://com.android.calendar/calendars";
    private static String calanderEventURL = "content://com.android.calendar/events";
    private static String calanderRemiderURL = "content://com.android.calendar/reminders";
    
    private static int CalID ;
    InfoHelper infohelper = new InfoHelper();
	
    /**
     * 读取事件
     * @param context
     */
	public static void readEvent(Context context) {
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(calanderEventURL), null, null, null, null);
      //  if (eventCursor.getCount() > 0) {
        for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
            //注意：这里与添加事件时的账户相对应，都是向最后一个账户添加
        	
            String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
            String Calid= eventCursor.getString(eventCursor.getColumnIndex(Events.CALENDAR_ID));
            if (Calid.equals(String.valueOf(CalID))) {
            	 MeetingInfo info = new MeetingInfo();
                String mstartStr = eventCursor.getString(eventCursor.getColumnIndex(Events.DTSTART));
                String mendStr = eventCursor.getString(eventCursor.getColumnIndex(Events.DTEND));
                Date sdate = new Date(Long.valueOf(mstartStr));
                Date edate = new Date(Long.valueOf(mendStr));
                //开始时间
                SimpleDateFormat tformat=new SimpleDateFormat("HH:mm:ss");
            	String startSt = DateTimeHelper.DateToString(sdate,"yyyy-MM-dd");
            	String startStr = String.valueOf(DateTimeHelper.DayStringToDate(startSt).getTime()/1000);
            	info.setStart_date(startStr);
            	String time = tformat.format(sdate);
            	String[] times = time.split(":");
            	int Itime = Integer.valueOf(times[0])*3600+Integer.valueOf(times[1])*60+Integer.valueOf(times[2]);
            	info.setStart_time(String.valueOf(Itime));
                //结束时间
            	String endSt = DateTimeHelper.DateToString(edate,"yyyy-MM-dd");
            	String endStr = String.valueOf(DateTimeHelper.DayStringToDate(endSt).getTime()/1000);
            	info.setEnd_date(endStr);
            	String etime = tformat.format(edate);
            	String[] etimes = etime.split(":");
            	int eItime = Integer.valueOf(etimes[0])*3600+Integer.valueOf(etimes[1])*60+Integer.valueOf(etimes[2]);
            	info.setEnd_time(String.valueOf(eItime));
            	
            	info.setSubject(eventTitle);
            	info.setAlertbeforetime("1111");
            	info.setId(eventCursor.getString(eventCursor.getColumnIndex(Events._ID)));
            	
            	InfoHelper infohelper = new InfoHelper();
        		infohelper.insertInfo(context, info);
            	//AppContext.EventmeetingBuffer.add(info);
            	
                //UIHelper.ToastMessage(context, "NAME: " + eventTitle);
                //System.out.print(aString);
			}
        }
      //用rows保存删除的行数，以备有用
       // int rows = context.getContentResolver().delete(Events.CONTENT_URI, Events.DESCRIPTION+"=?", new String[]{"去实验室见研究生导师"});
	}
	/**
	 * 读取账户
	 * @param context
	 */
	public static void readAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(calanderURL), null, null, null, null);
        
        System.out.println("Count: " + userCursor.getCount()); 
        UIHelper.ToastMessage(context, "Count: " + userCursor.getCount()); 
        boolean isAccount = false;
        int count = 0;
        for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
            System.out.println("name: " + userCursor.getString(userCursor.getColumnIndex("ACCOUNT_NAME")));
            count++;
            String userName1 = userCursor.getString(userCursor.getColumnIndex("name"));
            String userName0 = userCursor.getString(userCursor.getColumnIndex("ACCOUNT_NAME"));
            if (userName0.equals("ZY@gmail.com")) {
            	isAccount = true;
            	CalID = count;
			}
            UIHelper.ToastMessage(context, "NAME: " + userName1 + " -- ACCOUNT_NAME: " + userName0);
        }
        if (!isAccount) {
        	initCalendars(context);
        	CalID = count+1;
		}
       // int rownum = context.getContentResolver().delete(Uri.parse(calanderURL), "_id==5", null);  //注意：会全部删除所有账户，新添加的账户一般从id=1开始，
	}
	
	public static void insertEvent(Context context,MeetingInfo info) {
			
		long calID = CalID;// 添加的账户ID
		long startMillis = 0; 
		long endMillis = 0;     
		Calendar beginTime = Calendar.getInstance();
		beginTime.set(2016, 7, 19, 7, 30);	//注意，月份的下标是从0开始的
		startMillis = beginTime.getTimeInMillis();	//插入日历时要取毫秒计时
		startMillis = info.getStartDateTime().getTime();
		
		
		Calendar endTime = Calendar.getInstance();
		endTime.set(2016, 7, 19, 10, 30);
		endMillis = endTime.getTimeInMillis();
		endMillis = info.getEndDateTime().getTime();
				
		ContentValues eValues = new ContentValues();  //插入事件
		ContentValues rValues = new ContentValues();  //插入提醒，与事件配合起来才有效
		TimeZone tz = TimeZone.getDefault();//获取默认时区
				
		//插入日程
		eValues.put(Events.DTSTART, startMillis);
		eValues.put(Events.DTEND, endMillis);
		eValues.put(Events.TITLE, info.getSubject());
		eValues.put(Events.DESCRIPTION, info.getDescribe());
		eValues.put(Events.CALENDAR_ID, calID);
		eValues.put(Events.EVENT_LOCATION, "计算机学院");
		eValues.put(Events.EVENT_TIMEZONE, tz.getID()); 
		Uri uri = context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, eValues);
					
		//插完日程之后必须再插入以下代码段才能实现提醒功能	
		String myEventsId = uri.getLastPathSegment(); // 得到当前表的_id
		rValues.put("event_id", myEventsId);
		rValues.put("minutes", 10);	//提前10分钟提醒
		rValues.put("method", 1);	//如果需要有提醒,必须要有这一行
		context.getContentResolver().insert(Uri.parse(calanderRemiderURL),rValues);
		
		info.setEdit_TX2(myEventsId);
		InfoHelper infohelper = new InfoHelper();
		infohelper.updateInfo(context, info);

	}
	  //删除事件
    public static void deleteCalendars(Context context,MeetingInfo info) {
    	 //用rows保存删除的行数，以备有用
//    	ContentValues updateValues = new ContentValues();
//
//    	Uri deleteUri = null;
//
//    	deleteUri  = ContentUris.withAppendedId(Uri.parse(calanderEventURL),Long.valueOf(info.getEdit_TX2()));
//
//    	context.getContentResolver().delete(deleteUri,null,null);
        int rows = context.getContentResolver().delete(Events.CONTENT_URI, Events.TITLE+"=?", new String[]{info.getSubject()});
    }
	  //添加账户
    public static void initCalendars(Context context) {

        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(Calendars.NAME, "ZY");

        value.put(Calendars.ACCOUNT_NAME, "ZY@gmail.com");
        value.put(Calendars.ACCOUNT_TYPE, "com.android.exchange");
        value.put(Calendars.CALENDAR_DISPLAY_NAME, "ZY");
        value.put(Calendars.VISIBLE, 1);
        value.put(Calendars.CALENDAR_COLOR, -9206951);
        value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        value.put(Calendars.SYNC_EVENTS, 1);
        value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(Calendars.OWNER_ACCOUNT, "ZY@gmail.com");
        value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, "ZY@gmail.com")
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, "com.android.exchange")    
                .build();

        context.getContentResolver().insert(calendarUri, value);
    }
}
