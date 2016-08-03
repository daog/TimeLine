package com.timeline.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.timeline.app.AppContext;
import com.timeline.bean.MeetingInfo;
import com.timeline.bean.MomentBean;
import com.timeline.calendar.CalendarUtils;
import com.timeline.calendar.MomentAdapter;
import com.timeline.common.DensityUtil;
import com.timeline.main.R;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


public class DayFragment extends Fragment {
	public static final String ARG_PAGE = "page";
	private int mPageNumber;
	private Calendar mCalendar;
	private MomentAdapter mAdapter;
	private List<MomentBean> moments = new ArrayList<MomentBean>();
	private Timer mTimer;
	private FrameLayout mContainer;
	private LinearLayout mDigitalContainer;
	private RelativeLayout mEventContainer;
	private ScrollView mScrollView;
	// BaseGsonService实例
	private List<View> mPointerView = new ArrayList<View>();

	public static Fragment create(int pageNumber) {
		DayFragment fragment = new DayFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber =500;
		//mPageNumber = getArguments().getInt(ARG_PAGE);
//		mCalendar = CalendarUtils.getInstance().getSelectedDayOfMonth(
//				mPageNumber,
//				(Calendar) ((CalendarActivity) getActivity())
//						.getCurrentDayCalendar().clone());
		mCalendar = Calendar.getInstance(Locale.CHINA);
		System.out.println("onCreate-----------" + mPageNumber);
		AppContext.getInstance().mDayTagGetHandler = mCalendarTagGetHandler;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("onCreateView-----------" + mPageNumber);
		mScrollView = (ScrollView) inflater
				.inflate(R.layout.fragment_day, null);
		mContainer = (FrameLayout) mScrollView.findViewById(R.id.frame);

		mDigitalContainer = (LinearLayout) mScrollView
				.findViewById(R.id.digital_container);
		mEventContainer = (RelativeLayout) mScrollView
				.findViewById(R.id.event_container);
		initValues();
		mInitViewHandler.sendEmptyMessage(0);
		return mScrollView;
	}

	private void initValues() {
		// 获得Service实例
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		System.out.println("onDestroyView-----------" + mPageNumber);
		super.onDestroyView();
		if (null != mTimer) {
			mTimer.cancel();
		}
	}

	/**
	 * 创建24个刻度和指针当前位置
	 * */
	Handler mInitViewHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			getMomentsList();
			for (int i = 0; i < moments.size(); i++) {
				View view = LayoutInflater.from(getActivity()).inflate(
						R.layout.fragment_day_item, null);
				TextView hour = (TextView) view.findViewById(R.id.hour);
				FrameLayout pointerContainer = (FrameLayout) view
						.findViewById(R.id.pointer_container);
				View pointer = view.findViewById(R.id.pointer);

				mPointerView.add(pointer);

				pointer.setVisibility(View.GONE);
				hour.setText(i + "");
				pointerContainer.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "click",
								Toast.LENGTH_SHORT).show();
					}
				});
				if (moments.get(i).minute != -1) {
					pointer.setVisibility(View.VISIBLE);
					System.out.println("i: " + i + " visible:"
							+ moments.get(i).minute);
					LayoutParams params = (LayoutParams) pointer
							.getLayoutParams();

					params.topMargin = dip2px(getActivity(),
							moments.get(i).minute);
				}
				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, dip2px(getActivity(), 60));
				mDigitalContainer.addView(view, params);
			}

			final int position = Calendar.getInstance(Locale.CHINA).get(
					Calendar.HOUR_OF_DAY);
			mScrollView.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mScrollView.scrollTo(0,
							dip2px(getActivity(), (position - 4) * 60));
				}
			});

			//定时器更新时针运行
			if (mPageNumber == 500) {
				mTimer = new Timer();
				mTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mPointerHandler.sendEmptyMessage(0);
					}
				}, (60 - Calendar.getInstance().get(Calendar.SECOND)) * 1000,
						1000 * 60);//从整分开始，每一分钟更新一次指针
			}

			/**
			 * 绘制提前安排的事项
			 * */
		//	initTag();
		};
	};

	/***
	 * 操作指针运行的handler
	 * */
	Handler mPointerHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			getMomentsList();
			for (int i = 0; i < moments.size(); i++) {
				mPointerView.get(i).setVisibility(View.GONE);
				if (moments.get(i).minute != -1) {
					mPointerView.get(i).setVisibility(View.VISIBLE);
					LayoutParams params = (LayoutParams) mPointerView.get(i)
							.getLayoutParams();
					params.topMargin = dip2px(getActivity(),
							moments.get(i).minute-7);
				}
			}
		};
	};

	private void initTag() {
		mCalendarTagGetHandler.sendEmptyMessage(0);
	}

	/**
	 * 绘制事件区域
	 * */
	Handler mCalendarTagGetHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				// 有网的情况下
				try {
					mEventContainer.removeAllViews();
					MeetingInfo[] meetings = (MeetingInfo[]) msg.obj;
					drawMeeting(meetings);

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 1:
				String date = (String) msg.obj;
				List<MeetingInfo> melist = new ArrayList<MeetingInfo>();
				for (MeetingInfo ele : AppContext.getInstance().getEventmeetingBuffer()) {
					if (ele.getStartDateStr("").equals(date)) {
						melist.add(ele);
					}
					
				}
				int size =melist.size();
				MeetingInfo[] meetings = (MeetingInfo[])melist.toArray(new MeetingInfo[size]);//使用了第二种接口，返回值和参数均为结果  
				mEventContainer.removeAllViews();
				drawMeeting(meetings);
			case 2:
				break;
			default:
				break;
			}
		}
	};

	private void drawMeeting(MeetingInfo[] meetings) {
		if (meetings.length <=0) {
			return;
		}
		CompareTime(meetings);
		WindowManager wm = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
	    int width = wm.getDefaultDisplay().getWidth();
	    
	    int layWidth=(int) ((width-dip2px(getActivity(), 70))/2.5);
		
		for (int i = 1; i < meetings.length+1; i++) {
			int num  = 1;
			for (MeetingInfo info:meetings) {
					if (info.getTimeno() == i) {
						int start = Integer.valueOf(info.getStart_time());
						int end = Integer.valueOf(info.getEnd_time());
						//添加事件1
						//开始时间：，结束时间：
						int[] values = new int[] { start/60,end/60 };
						System.out.println("values=====: "
								+ Arrays.toString(values));
						TextView tv = new TextView(getActivity());
						tv.setBackgroundColor(getResources().getColor(
								R.color.tasking));
						if (info.getAlertbeforetime()!=null) {
							tv.setBackgroundColor(getResources().getColor(R.color.week_red));
						}
						tv.setTextColor(Color.WHITE);
						tv.setText(info.getSubject());
						tv.setGravity(Gravity.CENTER); 
						tv.setTextSize(20);
						tv.setPadding(24, 0, 24, 0);
						RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
								layWidth, dip2px(getActivity(),
										values[1] - values[0]));
						p.topMargin = dip2px(getActivity(), values[0]);
						p.leftMargin = dip2px(getActivity(), 10*num+DensityUtil.px2dip(getActivity(), layWidth)*(num-1) );
						tv.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated
								// method stub

							}
						});
						mEventContainer.addView(tv, p);
						num++;
					}
			}
			
		}
	}
	
	
	private void getMomentsList() {
		moments = new ArrayList<MomentBean>();
		Calendar cl = Calendar.getInstance();
		for (int i = 0; i < 24; i++) {
			MomentBean bean = new MomentBean();
			bean.hour = i;
			boolean year = mCalendar.get(Calendar.YEAR) == cl
					.get(Calendar.YEAR);
			boolean month = mCalendar.get(Calendar.MONTH) == cl
					.get(Calendar.MONTH);
			boolean day = mCalendar.get(Calendar.DAY_OF_MONTH) == cl
					.get(Calendar.DAY_OF_MONTH);
			if (year && month && day && i == cl.get(Calendar.HOUR_OF_DAY)) {
				// bean.minute = 0;
				bean.minute = cl.get(Calendar.MINUTE);
				System.out.println("minute: " + bean.minute);
			} else {
				bean.minute = -1;
			}
			moments.add(bean);
		}
	}
	/**
	 * 区分所有时间的时间段，便于绘图计算间隔
	 * @param meetings
	 */
	private void CompareTime(MeetingInfo[] meetings){
		int no = 1;
		meetings[0].setTimeno(no);
		for (int i = 0; i < meetings.length; i++) {
			int starti = Integer.valueOf(meetings[i].getStart_time());
			int endi = Integer.valueOf(meetings[i].getEnd_time());
			if (meetings[i].getTimeno() ==0) {
				no++;
				meetings[i].setTimeno(no);
			}
			for (int j = i+1; j < meetings.length; j++) {

				int startj = Integer.valueOf(meetings[j].getStart_time());
				int endj = Integer.valueOf(meetings[j].getEnd_time());
		 		if (!(starti>endj || endi <startj)) {
		 			if (meetings[i].getTimeno() !=0) {
						meetings[j].setTimeno(meetings[i].getTimeno());
					}
				}
			}
		}
	}
	
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}
