package com.timeline.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.style.LeadingMarginSpan;
import android.util.Log;

import com.google.gson.Gson;
import com.timeline.app.AppContext;
import com.timeline.bean.MeetingInfo;
import com.timeline.common.DateTimeHelper;
import com.timeline.common.StringUtils;
import com.timeline.main.R;
import com.timeline.sqlite.InfoHelper;

public class AlertService extends Service{

	 private static final String TAG = "LocalService";   
	 private IBinder binder=new AlertService.LocalBinder();
	 private NotificationManager nm;
	 private Timer alertTimer = new Timer();
	 TimerTask task;
	 // 运算频率
	 int calFrequency = 5;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		uploadLamp();
	}

	private void uploadLamp() {
		task = new TimerTask() {
			@Override
			public void run() {
				new Thread(new Runnable() {
					public void run() {
						Log.d("", "service");
						String date = DateTimeHelper.getDateTimeNow();
						for (MeetingInfo info :AppContext.EventmeetingBuffer) {
							if (info.getAlertbeforetime()!=null) {
								Date beforeDate = DateTimeHelper.StringToDate(info.getAlertbeforetime());
								Date rightDate =DateTimeHelper.StringToDate(date);
							 int sum =	(int) (Math.abs(beforeDate.getTime() - rightDate.getTime())/(1000*60));
								if (sum<1) {
									Notification n = new Notification(
											R.drawable.report, "日程安排 ",
											System.currentTimeMillis());
									n.flags = Notification.FLAG_AUTO_CANCEL;
									n.setLatestEventInfo(getBaseContext(),
											info.getSubject(),
											info.getDescribe(), null);
									n.defaults |= Notification.DEFAULT_VIBRATE; 
									n.defaults |= Notification.DEFAULT_SOUND;
									long[] vibrate = {0,100,200,300}; 
									n.vibrate = vibrate ;
									nm.notify(1, n);
									String repeatStr = info.getRepeate();
									
									Date alertbetime = DateTimeHelper.StringToDate(date);
									if (StringUtils.isEmpty(repeatStr)) {
										return;
									}
									if(repeatStr.equals("不重复")){
										
									}
									else if(repeatStr.equals("每天")){
										Date alertbetimeNew = DateTimeHelper.AddDays(alertbetime, 1);
										info.setAlertbeforetime(DateTimeHelper
												.DateToString(alertbetimeNew));
									}
									else if(repeatStr.equals("每周")){
										alertbetime = DateTimeHelper.AddWeeks(alertbetime, 1);
										info.setAlertbeforetime(DateTimeHelper
												.DateToString(alertbetime));
									}
									else if(repeatStr.equals("每月")){
										alertbetime = DateTimeHelper.AddMonths(alertbetime, 1);
										info.setAlertbeforetime(DateTimeHelper
												.DateToString(alertbetime));
									}
									else if(repeatStr.equals("每年")){
										alertbetime = DateTimeHelper.AddMonths(alertbetime, 12);
										info.setAlertbeforetime(DateTimeHelper
												.DateToString(alertbetime));
									}
									if(!isExpired(alertbetime, info)){
										InfoHelper ih = new InfoHelper();
										ih.updateInfo(AppContext.getInstance(), info);
										AppContext.getInstance().getEventmeetingBuffer();
									}
								
								}
							}
						}
					}
				}).start();
			}
		};
		alertTimer.schedule(task, 1000, 5000);
	}
	
	//提醒时间是否过期
	private boolean isExpired(Date alertbetime, MeetingInfo info) {
		Long endDateLong = Long.valueOf(info.getEnd_date());
		int endTimeInt = Integer.valueOf(info.getEnd_time());
		Date endDate = endDate = new Date((endDateLong + endTimeInt)*1000);
		if(alertbetime.equals(endDate) || alertbetime.before(endDate)){
			return false;
		}
		return true;
	}
	
	/**
	 * 上传本机灯列表
	 * 
	 */
	public static void upload() {

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
   //定义内容类继承Binder   
	public class LocalBinder extends Binder{   
		//返回本地服务     
		public AlertService getService(){           
			return AlertService.this;        
			} 
		}
}
