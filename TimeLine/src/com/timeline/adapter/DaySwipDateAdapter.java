package com.timeline.adapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import com.android.volley.VolleyError;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.webapi.HttpFactory;
import com.timeline.widget.Dot;
import com.timeline.adapter.SigninGuestAdapter.ListItemView;
import com.timeline.app.AppContext;
import com.timeline.bean.MeetingInfo;
import com.timeline.calendar.SpecialCalendar;
import com.timeline.common.DateTimeHelper;
import com.timeline.common.DensityUtil;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.LunarCalendar;
import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class DaySwipDateAdapter extends BaseAdapter {
	private static String TAG = "ZzL";
	private boolean isLeapyear = false; // �Ƿ�Ϊ����
	private int daysOfMonth = 0; // ĳ�µ�����
	private int dayOfWeek = 0; // ����ĳһ�������ڼ�
	private int nextDayOfWeek = 0;
	private int lastDayOfWeek = 0;
	private int lastDaysOfMonth = 0; // ��һ���µ�������
	private int eachDayOfWeek = 0;
	private Context context;
	private SpecialCalendar sc = null;
	private Resources res = null;
	private Drawable drawable = null;
	private String[] dayNumber = new String[7];
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
	private int currentFlag = -1; // ���ڱ�ǵ���
	// ϵͳ��ǰʱ��
	private String sysDate = "";
	private String sys_year = "";
	private String sys_month = "";
	private String sys_day = "";
	private String currentYear = "";
	private String currentMonth = "";
	private String currentWeek = "";
	private String currentDay = "";
	private int weeksOfMonth;
	private int default_postion;
	private int clickTemp = -1;
	private int week_num = 0;
	private int week_c = 0;
	private int month = 0;
	private int jumpWeek = 0;
	private int c_month = 0;
	private int c_day_week = 0;
	private int n_day_week = 0;
	private boolean isStart;
	
	private LunarCalendar lc = null;
	
	//һ��ʱ���ڻ�����������
	private VolleyListenerInterface periodvolleyListener;
	//һ��ʱ���ڵ����л���
	private List<MeetingInfo> periodMeetings = new ArrayList<MeetingInfo>();

	// ��ʶѡ���Item
	public void setSeclection(int position) {
		clickTemp = position;
	}

	public DaySwipDateAdapter() {
		Date date = new Date();
		sysDate = sdf.format(date); // ��������
		sys_year = sysDate.split("-")[0];
		sys_month = sysDate.split("-")[1];
		sys_day = sysDate.split("-")[2];
		month = Integer.parseInt(sys_month);
		lc = new LunarCalendar();
	}

	static class ListItemView { // �Զ���ؼ����ϣ���listitem_signin����һ��
		public TextView tvCalendar;
		public TextView tvCalendarLunar;
		public LinearLayout llcan;
		public Dot doview;
	}

	
	public DaySwipDateAdapter(Context context, Resources rs, int year_c, int month_c,
			int week_c, int week_num, int default_postion, boolean isStart) {
		this();
		this.context = context;
		this.res = rs;
		this.default_postion = default_postion;
		this.week_c = week_c;
		this.isStart = isStart;
		sc = new SpecialCalendar();

		lastDayOfWeek = sc.getWeekDayOfLastMonth(year_c, month_c,
				sc.getDaysOfMonth(sc.isLeapYear(year_c), month_c));
		Log.i(TAG, "week_c:" + week_c);
		currentYear = String.valueOf(year_c);
		; // �õ���ǰ�����
		currentMonth = String.valueOf(month_c); // �õ�����
												// ��jumpMonthΪ�����Ĵ�����ÿ����һ�ξ�����һ�»��һ�£�
		currentDay = String.valueOf(sys_day); // �õ���ǰ����������
		getCalendar(Integer.parseInt(currentYear),
				Integer.parseInt(currentMonth));
		currentWeek = String.valueOf(week_c);
		getWeek(Integer.parseInt(currentYear), Integer.parseInt(currentMonth),
				Integer.parseInt(currentWeek));
		//��ʼ����ȡһ��ʱ��Ļ���ļ���
				periodvolleyListener = new VolleyListenerInterface(context){
					@Override
					public void onMySuccess(String result) {
						// TODO Auto-generated method stub
						try {
							JSONObject myJsonObject = new JSONObject(result);
							String rest = myJsonObject.getString("re_st");
							periodMeetings.clear();
							if (rest.equals("success")) {		
								Message msg = Message.obtain();
								MeetingInfo[] meetings
								= JsonToEntityUtils.jsontoMeetingInfo( myJsonObject.getString("re_info"));
								if(meetings != null){
									for(MeetingInfo mi : meetings){
										periodMeetings.add(mi);
									}
								}
							}
							for (MeetingInfo ele : AppContext.getInstance().getEventmeetingBuffer()) {
								periodMeetings.add(ele);
							}
							notifyDataSetChanged();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}

					@Override
					public void onMyError(VolleyError error) {
						// TODO Auto-generated method stub
						periodMeetings.clear();
						for (MeetingInfo ele : AppContext.getInstance().getEventmeetingBuffer()) {
							periodMeetings.add(ele);
						}
						notifyDataSetChanged();
					}
					
				};

		new Thread() {
			@Override
			public void run() {
				try {
					// ����д�����߳���Ҫ���Ĺ���
					Thread.sleep(1000);
					DecimalFormat df = new DecimalFormat("00");
					String mmonth = df.format(getCurrentMonth(0));
					String mmday = df.format(Integer.parseInt(dayNumber[0]
							.toString()));
					String startDate = String.valueOf(currentYear) + "-"
							+ mmonth + "-" + mmday;
					String endDate = DateTimeHelper.DateToString(DateTimeHelper
							.AddDays(DateTimeHelper.DayStringToDate(startDate),
									6), "yyyy-MM-dd");
					HttpFactory.getMeetingjoin_list_period(startDate, endDate,
							periodvolleyListener);
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		}.start();
			
	}

	public int getTodayPosition() {
		int todayWeek = sc.getWeekDayOfLastMonth(Integer.parseInt(sys_year),
				Integer.parseInt(sys_month), Integer.parseInt(sys_day));
		if (todayWeek == 7) {
			clickTemp = 0;
		} else {
			clickTemp = todayWeek;
		}
		return clickTemp;
	}

	public int getCurrentMonth(int position) {
		int thisDayOfWeek = sc.getWeekdayOfMonth(Integer.parseInt(currentYear),
				Integer.parseInt(currentMonth));
		if (isStart) {
			if (thisDayOfWeek != 7) {
				if (position < thisDayOfWeek) {
					return Integer.parseInt(currentMonth) - 1 == 0 ? 12
							: Integer.parseInt(currentMonth) - 1;
				} else {
					return Integer.parseInt(currentMonth);
				}
			} else {
				return Integer.parseInt(currentMonth);
			}
		} else {
			return Integer.parseInt(currentMonth);
		}

	}

	public int getCurrentYear(int position) {
		int thisDayOfWeek = sc.getWeekdayOfMonth(Integer.parseInt(currentYear),
				Integer.parseInt(currentMonth));
		if (isStart) {
			if (thisDayOfWeek != 7) {
				if (position < thisDayOfWeek) {
					return Integer.parseInt(currentMonth) - 1 == 0 ? Integer
							.parseInt(currentYear) - 1 : Integer
							.parseInt(currentYear);
				} else {
					return Integer.parseInt(currentYear);
				}
			} else {
				return Integer.parseInt(currentYear);
			}
		} else {
			return Integer.parseInt(currentYear);
		}
	}

	public void getCalendar(int year, int month) {
		isLeapyear = sc.isLeapYear(year); // �Ƿ�Ϊ����
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); // ĳ�µ�������
		dayOfWeek = sc.getWeekdayOfMonth(year, month); // ĳ�µ�һ��Ϊ���ڼ�
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month - 1);
		nextDayOfWeek = sc.getDaysOfMonth(isLeapyear, month + 1);
	}

	public void getWeek(int year, int month, int week) {
		for (int i = 0; i < dayNumber.length; i++) {
			if (dayOfWeek == 7) {
				dayNumber[i] = String.valueOf((i + 1) + 7 * (week - 1));
			} else {
				if (week == 1) {
					if (i < dayOfWeek) {
						dayNumber[i] = String.valueOf(lastDaysOfMonth
								- (dayOfWeek - (i + 1)));
					} else {
						dayNumber[i] = String.valueOf(i - dayOfWeek + 1);
					}
				} else {
					dayNumber[i] = String.valueOf((7 - dayOfWeek + 1 + i) + 7
							* (week - 2));
				}
			}

		}
	}

	public String[] getDayNumbers() {
		return dayNumber;
	}

	/**
	 * �õ�ĳ���м���(�����㷨)
	 */
	public int getWeeksOfMonth() {
		// getCalendar(year, month);
		int preMonthRelax = 0;
		if (dayOfWeek != 7) {
			preMonthRelax = dayOfWeek;
		}
		if ((daysOfMonth + preMonthRelax) % 7 == 0) {
			weeksOfMonth = (daysOfMonth + preMonthRelax) / 7;
		} else {
			weeksOfMonth = (daysOfMonth + preMonthRelax) / 7 + 1;
		}
		return weeksOfMonth;
	}

	/**
	 * ĳһ���ڵڼ���
	 */
	public void getDayInWeek(int year, int month) {

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dayNumber.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	ListItemView listItemView = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// �Զ�����ͼ
		listItemView = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_calendar, null);
			listItemView = new ListItemView();
			// ��ȡ�ؼ�����
			listItemView.doview = (Dot)convertView.findViewById(R.id.dot);
			listItemView.tvCalendar = (TextView) convertView
					.findViewById(R.id.tv_calendar);	
			listItemView.tvCalendarLunar = (TextView) convertView
					.findViewById(R.id.tv_calendar_Lunar);
			listItemView.llcan = (LinearLayout)convertView.findViewById(R.id.ll_calendar);
			// ���ÿؼ�����convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView) convertView.getTag();
		}
		listItemView.tvCalendar.setText(dayNumber[position]);
		String scheduleLunarDay = getLunarDay(Integer.parseInt(currentYear),
				Integer.parseInt(currentMonth), Integer.parseInt(dayNumber[position].toString()));
		listItemView.tvCalendarLunar.setText(scheduleLunarDay);
		if (position ==0||position == 6 ) {
			listItemView.tvCalendar.setTextColor(Color.parseColor("#db4043"));
		}
		if (clickTemp == position) {
			listItemView.tvCalendar.setSelected(true);
			listItemView.tvCalendar.setTextColor(Color.WHITE);
			listItemView.tvCalendarLunar.setTextColor(Color.WHITE);
			listItemView.llcan.setBackgroundResource(R.drawable.circle_message);
			//tvCalendar.setBackgroundResource(R.drawable.circle_message);
		} else {
			listItemView.tvCalendar.setSelected(false);
			listItemView.tvCalendar.setTextColor(Color.BLACK);
			listItemView.tvCalendarLunar.setTextColor(Color.BLACK);
			listItemView.llcan.setBackgroundColor(Color.TRANSPARENT);
			//tvCalendar.setBackgroundColor(Color.TRANSPARENT);
		}
		if (position ==0||position == 6 ) {
			listItemView.tvCalendar.setTextColor(Color.parseColor("#db4043"));
			listItemView.tvCalendarLunar.setTextColor(Color.parseColor("#db4043"));
		}
		DecimalFormat df = new DecimalFormat("00");
		String mmonth = df.format(getCurrentMonth(position));
		String mmday = df.format(Integer.parseInt(dayNumber[position].toString()));
		String dateStr =String.valueOf(currentYear)+"-"+mmonth
				+"-"+mmday;
		if (dateStr.equals(DateTimeHelper.getDateNow())) {
			listItemView.tvCalendar.setTextColor(Color.RED);
			listItemView.tvCalendarLunar.setTextColor(Color.RED);
		}
		listItemView.doview.setColor(context.getResources().getColor(R.color.white));
		listItemView.doview.invalidate();
		for (MeetingInfo info:periodMeetings) {
			if (DateTimeHelper.isInDate(dateStr, info.getStartDateStr(""), info.getEndDateStr(""))) {
				if (info.getAlertbeforetime()!=null||info.getJoin_st().equals("1")) {
					listItemView.doview.setColor(context.getResources().getColor(R.color.gray));
					listItemView.doview.invalidate();
				}			
			}
		}
		return convertView;
	}
	/**
	 * �������ڵ������շ�����������
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public String getLunarDay(int year, int month, int day) {
		String lunarDay = lc.getLunarDate(year, month, day, true);
		// {������ȡ��������Ӧ����������ʱ������������ڶ�Ӧ����������Ϊ"��һ"���ͱ����ó����·�(��:���£����¡�������)},�����ڴ˾�Ҫ�жϵõ������������Ƿ�Ϊ�·ݣ�������·ݾ�����Ϊ"��һ"
		if (lunarDay.substring(1, 2).equals("��")) {
			lunarDay = "��һ";
		}
		return lunarDay;
	}
}
