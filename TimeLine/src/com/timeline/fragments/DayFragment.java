package com.timeline.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.timeline.app.AppContext;
import com.timeline.bean.MeetingInfo;
import com.timeline.bean.MomentBean;
import com.timeline.calendar.CalendarUtils;
import com.timeline.calendar.MomentAdapter;
import com.timeline.common.DateTimeHelper;
import com.timeline.common.DensityUtil;
import com.timeline.common.StringUtils;
import com.timeline.common.UIHelper;
import com.timeline.fragments.WeekFragment.PopupWindowBtnClickListener;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.sqlite.InfoHelper;
import com.timeline.ui.Main;
import com.timeline.webapi.HttpFactory;
import com.timeline.widget.MyDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
	
	//会议参与状态设置
	private VolleyListenerInterface hoinStatusvolleyListener;
	//弹出窗口
	PopupWindow popupWindow;
	
	//弹出窗口关闭按钮
	private Button popupCloseBtn;

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
		
		
		//初始化会议参与监听
		hoinStatusvolleyListener= new VolleyListenerInterface(getActivity()){
			@Override
			public void onMySuccess(String result) {
				// TODO Auto-generated method stub
				try {
					JSONObject myJsonObject = new JSONObject(result);
					String rest = myJsonObject.getString("re_st");
					if (rest.equals("success")) {

					}
					UIHelper.ToastMessage(getActivity(), myJsonObject.getString("re_info"));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			@Override
			public void onMyError(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
			
		};
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
					if (DateTimeHelper.isInDate(date, ele.getStartDate(),
							ele.getEndDate())){
						if (ele.getStartDateStr("").equals(ele.getEndDateStr(""))) {								
							ele.setDaystate(1);
						}else {
							if (ele.getStartDateStr("").equals(date)) {
								ele.setDaystate(2);
							}else if (ele.getEndDateStr("").equals(date)) {
								ele.setDaystate(3);
							}else {
								ele.setDaystate(4);
							}
						}
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
//	private void drawMeeting(MeetingInfo[] meetings) {
//		if (meetings.length <=0) {
//			return;
//		}
//		WindowManager wm = (WindowManager) getActivity()
//                .getSystemService(Context.WINDOW_SERVICE);
//	    int width = wm.getDefaultDisplay().getWidth();
//	    
//	    int layWidth=(int) ((width-dip2px(getActivity(), 70))/2.5);
//	    
//	    List<RectF> zones = new ArrayList<RectF>();
//	    Arrays.sort(meetings);
//	    int no=0; 
//			for (final MeetingInfo info:meetings) {
//						
//						RectF reF = new RectF();
//				
//						int num = 1;
//						int start = Integer.valueOf(info.getStart_time());
//						int end = Integer.valueOf(info.getEnd_time());
//						//添加事件1
//						//开始时间：，结束时间：
//						int[] values = new int[] { start/60,end/60 };
//						System.out.println("values=====: "
//								+ Arrays.toString(values));
//						TextView tv = new TextView(getActivity());
//						tv.setBackgroundColor(getResources().getColor(
//								R.color.tasking));
//						if (info.getAlertbeforetime()!=null) {
//							tv.setBackgroundColor(getResources().getColor(R.color.week_red));
//						}
//						tv.setTextColor(Color.WHITE);
//						tv.setText(info.getSubject());
//						tv.setGravity(Gravity.CENTER); 
//						tv.setTextSize(20);
//						tv.setPadding(24, 0, 24, 0);
//						RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
//								layWidth, dip2px(getActivity(),
//										values[1] - values[0]));
//						p.topMargin = dip2px(getActivity(), values[0]);
//						while (isHasView(zones, 10*num+DensityUtil.px2dip(getActivity(), layWidth)*(num-1) ,
//								values[0])||isHasView(zones, 10*num+DensityUtil.px2dip(getActivity(), layWidth)*(num-1) ,
//										values[1])) {
//							num ++;
//						}
//						p.leftMargin = dip2px(getActivity(), 10*num+DensityUtil.px2dip(getActivity(), layWidth)*(num-1) );
//						tv.setTag(info);
//						tv.setOnClickListener(new OnClickListener() {
//
//							@Override
//							public void onClick(View v) {
//								// TODO Auto-generated
//								// method stub
//									showPopupWindow(v);
//								
//							}
//						});
//						mEventContainer.addView(tv, p);
//						reF.top = values[0];
//						reF.left = 10*num+DensityUtil.px2dip(getActivity(), layWidth)*(num-1);
//						reF.right =  10*num+DensityUtil.px2dip(getActivity(), layWidth)*(num);
//						reF.bottom = values[1];
//						zones.add(reF);
//						no++;
//		}
//	}
	private void drawMeeting(MeetingInfo[] meetings) {
		if (meetings.length <=0) {
			return;
		}
		WindowManager wm = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
	    int width = wm.getDefaultDisplay().getWidth();
	    
	    int layWidth=(int) ((width-dip2px(getActivity(), 70))/2.5);
	    if (meetings.length ==1) {
	    	 layWidth=(int)(width-dip2px(getActivity(), 70));
		}
	    for (MeetingInfo info:meetings) {
			int start = 0;
			int end = 0;
			int num = 1;
			if (info.getDaystate() == 2) {
				start = Integer.valueOf(info.getStart_time());
				end = Integer.valueOf(24*60*60);
			}else if (info.getDaystate() == 3) {
				start = Integer.valueOf(0);
				end = Integer.valueOf(info.getEnd_time());
			}else if (info.getDaystate() == 4) {
				start = Integer.valueOf(0);
				end = Integer.valueOf(24*60*60);
			}else {
				start = Integer.valueOf(info.getStart_time());
				end = Integer.valueOf(info.getEnd_time());
			}
			info.setStart_time(String.valueOf(start));
			info.setEnd_time(String.valueOf(end));
			info.setTimelong(end-start);
		}
	    
	    List<RectF> zones = new ArrayList<RectF>();
	    Arrays.sort(meetings);
	    int no=0; 
			for (final MeetingInfo info:meetings) {
						
						RectF reF = new RectF();
						int start = 0;
						int end = 0;
						int num = 1;
	//					if (info.getDaystate() == 2) {
							start = Integer.valueOf(info.getStart_time());
							end = Integer.valueOf(info.getEnd_time());
//						}else if (info.getDaystate() == 3) {
//							start = Integer.valueOf(0);
//							end = Integer.valueOf(info.getEnd_time());
//						}else if (info.getDaystate() == 4) {
//							start = Integer.valueOf(0);
//							end = Integer.valueOf(24*60*60);
//						}else {
//							start = Integer.valueOf(info.getStart_time());
//							end = Integer.valueOf(info.getEnd_time());
//						}	
						//添加事件1
						//开始时间：，结束时间：

						int[] values = new int[] { start/60,end/60-3 };
						if (values[1] -values[0] <30) {
							values[1] = values[0]+30;
						}
						System.out.println("values=====: "
								+ Arrays.toString(values));
						LinearLayout content = (LinearLayout)LayoutInflater.from(getActivity()).inflate(
								R.layout.item_daycontent, null);
						LinearLayout llLayout=(LinearLayout)content.findViewById(R.id.id_split);
						TextView title = (TextView) content.findViewById(R.id.id_title);
						TextView time = (TextView) content.findViewById(R.id.id_Time);
						TextView sub = (TextView) content.findViewById(R.id.id_sub);
						title.setText(info.getSubject());
						time.setText(DateTimeHelper.int2Time(Integer.valueOf(info.getStart_time())) + " - " 
						+ DateTimeHelper.int2Time(Integer.valueOf(info.getEnd_time())));
						sub.setText(info.getDescribe());
						if (info.getAlertbeforetime()!=null) {
							llLayout.setBackgroundColor(getResources().getColor(R.color.week_red));
							content.setBackgroundColor(getResources().getColor(R.color.day_redtran));
						}
						
						
						RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
								layWidth, dip2px(getActivity(),
										values[1] - values[0]));
						p.topMargin = dip2px(getActivity(), values[0]);
						while (isHasView(zones, 10*num+DensityUtil.px2dip(getActivity(), layWidth)*(num-1) ,
								values[0])||isHasView(zones, 10*num+DensityUtil.px2dip(getActivity(), layWidth)*(num-1) ,
										values[1])) {
							num ++;
						}
						p.leftMargin = dip2px(getActivity(), 10*num+DensityUtil.px2dip(getActivity(), layWidth)*(num-1) );
						content.setTag(info);
						content.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated
								// method stub
									showPopupWindow(v);
								
							}
						});
						mEventContainer.addView(content, p);
						reF.top = values[0];
						reF.left = 10*num+DensityUtil.px2dip(getActivity(), layWidth)*(num-1);
						reF.right =  10*num+DensityUtil.px2dip(getActivity(), layWidth)*(num);
						reF.bottom = values[1];
						zones.add(reF);
						no++;
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
	@SuppressLint("NewApi") 
	private void showPopupWindow(View view) {

		// 一个自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(getActivity()).inflate(
				R.layout.contentpopwindow, null);

		TextView subjectTv = (TextView)contentView.findViewById(R.id.id_topicText);
		TextView detailTv = (TextView)contentView.findViewById(R.id.id_detailText);
		TextView sponsorTv = (TextView)contentView.findViewById(R.id.id_orgnizerText);
		TextView dateTimeTv = (TextView)contentView.findViewById(R.id.id_timeText);
		TextView addressTv = (TextView)contentView.findViewById(R.id.id_addressText);
		
		LinearLayout llenroll = (LinearLayout)contentView.findViewById(R.id.id_enrollLl);
		LinearLayout llundetermined = (LinearLayout)contentView.findViewById(R.id.id_undeterminedLl);
		LinearLayout llview = (LinearLayout)contentView.findViewById(R.id.id_viewLl);
		LinearLayout llrefuse = (LinearLayout)contentView.findViewById(R.id.id_refuseLl);
		LinearLayout lledit = (LinearLayout)contentView.findViewById(R.id.id_editLl);
		LinearLayout lldelete = (LinearLayout)contentView.findViewById(R.id.id_deleteLl);
		
		
		Button enrollBtn = (Button)contentView.findViewById(R.id.id_enrollBtn);
		Button viewBtn = (Button)contentView.findViewById(R.id.id_viewBtn);
		Button undeterminedBtn = (Button)contentView.findViewById(R.id.id_undeterminedBtn);
		Button refuseBtn = (Button)contentView.findViewById(R.id.id_refuseBtn);
		Button editBtn = (Button)contentView.findViewById(R.id.id_editBtn);
		Button deletBtn = (Button)contentView.findViewById(R.id.id_deleteBtn);
		
		ImageButton closeBtn = (ImageButton)contentView.findViewById(R.id.id_close);
		closeBtn.setOnClickListener(new PopupWindowBtnClickListener());
		
		TextView enrollTv = (TextView)contentView.findViewById(R.id.id_enrollTv);//报名
		TextView viewTv = (TextView)contentView.findViewById(R.id.id_viewTv);//查看
		TextView undeterminedTv = (TextView)contentView.findViewById(R.id.id_undeterminedTv);//待定
		TextView refuseTv = (TextView)contentView.findViewById(R.id.id_refuseTv);//拒绝
		TextView deleteTv = (TextView)contentView.findViewById(R.id.id_deleteTv);//删除
		TextView editTv = (TextView)contentView.findViewById(R.id.id_editTv);//编辑
		
		MeetingInfo meetingInfo = (MeetingInfo)view.getTag();
		if(meetingInfo == null){
			return;
		}

		if (meetingInfo.getAlertbeforetime() == null) {
			//隐藏个人操作按钮
			lledit.setVisibility(view.GONE);
			lldelete.setVisibility(View.GONE);
			if (meetingInfo.getJoin_st() !=null) {
				

			if (meetingInfo.getJoin_st().equals("1")) {//已报名
				enrollBtn.setBackground(getResources().getDrawable(R.drawable.icon_meeting_upyes));
				enrollTv.setText("已报名");
				
			}else if (meetingInfo.getJoin_st().equals("2")) {//待定
				undeterminedBtn.setBackground(getResources().getDrawable(R.drawable.icon_meeting_underyes));
				
			}else if (meetingInfo.getJoin_st().equals("3")) {//拒绝
				refuseBtn.setBackground(getResources().getDrawable(R.drawable.icon_meeeting_refyes));
				refuseTv.setText("已拒绝");
			}else if (meetingInfo.getJoin_st().equals("4")) {//无操作
				
			}
			}
		}else {
			llview.setVisibility(View.GONE);
			llenroll.setVisibility(View.GONE);
			llundetermined.setVisibility(View.GONE);
			llrefuse.setVisibility(View.GONE);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DensityUtil.dip2px(getActivity(), 25),DensityUtil.dip2px(getActivity(), 25));  
			//此处相当于布局文件中的Android:layout_gravity属性  
			//此处相当于布局文件中的Android:layout_gravity属性  
			lp.gravity = Gravity.LEFT;  
			lp.leftMargin = 80;
			deletBtn.setLayoutParams(lp);  
			LinearLayout.LayoutParams lpT = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); 
			lpT.gravity = Gravity.LEFT;  
			lpT.leftMargin = 80;
			lpT.topMargin = DensityUtil.dip2px(getActivity(), 3);
			deleteTv.setLayoutParams(lpT);  
			
			LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(DensityUtil.dip2px(getActivity(), 25),DensityUtil.dip2px(getActivity(), 25));  
			lp1.gravity = Gravity.RIGHT;  
			lp1.rightMargin = 80;
			editBtn.setLayoutParams(lp1);  
			LinearLayout.LayoutParams lpT1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); 
			lpT1.gravity = Gravity.RIGHT;  
			lpT1.rightMargin = 80;
			lpT1.topMargin = DensityUtil.dip2px(getActivity(), 3);
			editTv.setLayoutParams(lpT1);
		}

		enrollBtn.setTag(meetingInfo);
		viewBtn.setTag(meetingInfo);
		undeterminedBtn.setTag(meetingInfo);
		refuseBtn.setTag(meetingInfo);
		editBtn.setTag(meetingInfo);
		deletBtn.setTag(meetingInfo);
		 llenroll.setTag(meetingInfo);
		 llundetermined.setTag(meetingInfo);
		 llview.setTag(meetingInfo);
		 llrefuse.setTag(meetingInfo);
		 lledit.setTag(meetingInfo);
		 lldelete.setTag(meetingInfo);
		
		enrollBtn.setOnClickListener(new PopupWindowBtnClickListener());
		viewBtn.setOnClickListener(new PopupWindowBtnClickListener());
		undeterminedBtn.setOnClickListener(new PopupWindowBtnClickListener());
		refuseBtn.setOnClickListener(new PopupWindowBtnClickListener());
		editBtn.setOnClickListener(new PopupWindowBtnClickListener());
		deletBtn.setOnClickListener(new PopupWindowBtnClickListener());
		
		 llenroll.setOnClickListener(new PopupWindowBtnClickListener());
		 llundetermined.setOnClickListener(new PopupWindowBtnClickListener());
		 llview.setOnClickListener(new PopupWindowBtnClickListener());
		 llrefuse.setOnClickListener(new PopupWindowBtnClickListener());
		 lledit.setOnClickListener(new PopupWindowBtnClickListener());
		 lldelete.setOnClickListener(new PopupWindowBtnClickListener());
		
		subjectTv.setText(meetingInfo.getSubject());
		detailTv.setText(meetingInfo.getDescribe());
		if (StringUtils.isEmpty(meetingInfo.getSponsor())) {
			sponsorTv.setText("个人事件");
		}else {
			sponsorTv.setText(meetingInfo.getSponsor());
		}
		
		dateTimeTv.setText(DateTimeHelper.int2Time(Integer.valueOf(meetingInfo.getStart_time())) + " - " + DateTimeHelper.int2Time(Integer.valueOf(meetingInfo.getEnd_time())));
		addressTv.setText(meetingInfo.getAddress());
		
		
		popupWindow = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		 // 设置popWindow的显示和消失动画
		popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
		popupWindow.setTouchable(true);

		popupWindow.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				Log.i("mengdd", "onTouch : ");

				return false;
				// 这里如果返回true的话，touch事件将被拦截
				// 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
			}
		});

		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		// 我觉得这里是API的一个bug
		popupWindow.setBackgroundDrawable(getResources().getDrawable(
				R.color.white));
		backgroundAlpha(0.5f);  
		 //添加pop窗口关闭事件  
		popupWindow.setOnDismissListener(new poponDismissListener());  
		int[] location = new int[2];  
        view.getLocationOnScreen(location);  
          
        popupWindow.showAtLocation(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);  
	}
	/**
	 * 设置添加屏幕的背景透明度
	 * @param bgAlpha
	 */
	public void backgroundAlpha(float bgAlpha)
	{
		WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
       lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
	}
	
	/**
	 * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
	 * @author cg
	 *
	 */
	class poponDismissListener implements PopupWindow.OnDismissListener{

		@Override
		public void onDismiss() {
			// TODO Auto-generated method stub
			//Log.v("List_noteTypeActivity:", "我是关闭事件");
			backgroundAlpha(1f);
		}
		
	}
	public class PopupWindowBtnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			popupWindow.dismiss();
			MeetingInfo mi = (MeetingInfo)v.getTag();
			switch(v.getId()){
				//报名
				case R.id.id_enrollBtn:
					//enrollMeeting(mi);
					break;
				//待定	
				case R.id.id_undeterminedBtn:
					undeterminedMeeting(mi);
					break;
				//查看
				case R.id.id_viewBtn:
					viewMeeting(mi);
					break;
				//拒绝
				case R.id.id_refuseBtn:
					refuseMeeting(mi);
					break;
				case R.id.id_deleteBtn:
					deleteMeeting(mi);
					break;
				case R.id.id_editBtn:
					editMeeting(mi);
					break;
					//报名
				case R.id.id_enrollLl:
					//enrollMeeting(mi);
					break;
				//待定	
				case R.id.id_undeterminedLl:
					undeterminedMeeting(mi);
					break;
				//查看
				case R.id.id_viewLl:
					viewMeeting(mi);
					break;
				//拒绝
				case R.id.id_refuseLl:
					refuseMeeting(mi);
					break;
				case R.id.id_deleteLl:
					deleteMeeting(mi);
					break;
				case R.id.id_editLl:
					editMeeting(mi);
					break;	
				case R.id.id_close:
					if(null != popupWindow && popupWindow.isShowing()){  
						popupWindow.dismiss();  
		                if(null == popupWindow){  
		                    Log.e("WeekFragment","null == popupWindow");  
		                }  
		            }  
		            break;  
			}
		}

		//编辑个人新建的活动
		private void editMeeting(MeetingInfo mi) {
			// TODO Auto-generated method stub
			UIHelper.showNewEvent(getActivity(), mi);
			
		}

		//删除个人新建的活动
		private void deleteMeeting(final MeetingInfo mi) {
			// TODO Auto-generated method stub
			new MyDialog(getActivity(), R.style.MyDialog, "您确定要删除?", "确定", "取消",
					new MyDialog.DialogClickListener() {

						@Override
						public void onRightBtnClick(Dialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}

						@Override
						public void onLeftBtnClick(Dialog dialog) {
							// TODO Auto-generated method stub
							InfoHelper ih = new InfoHelper();
							ih.deleteInfo(getActivity(), mi);
							Main.instance.refreshData(R.id.indicator_day);
							dialog.dismiss();
						}
					}).show();
		}

		// 拒绝会议
		private void refuseMeeting(final MeetingInfo mi) {
			new MyDialog(getActivity(), R.style.MyDialog, "您确定要拒绝?", "确定", "取消",
					new MyDialog.DialogClickListener() {

						@Override
						public void onRightBtnClick(Dialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}

						@Override
						public void onLeftBtnClick(Dialog dialog) {
							// TODO Auto-generated method stub
							HttpFactory.Set_Join_Status(mi.getId(), "3", hoinStatusvolleyListener);
							dialog.dismiss();
						}
					}).show();
			
		}

		//查看会议
		private void viewMeeting(MeetingInfo mi) {
			if (mi.getAlertbeforetime() == null) {
				UIHelper.showMeetingDetail(getActivity(), mi.getId());
			}else {
				UIHelper.showEventDe(getActivity(),mi.getId());
			}
			
		}

		//待定
		private void undeterminedMeeting(final MeetingInfo mi) {
			new MyDialog(getActivity(), R.style.MyDialog, "您确定要待定?", "确定", "取消",
					new MyDialog.DialogClickListener() {

						@Override
						public void onRightBtnClick(Dialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}

						@Override
						public void onLeftBtnClick(Dialog dialog) {
							// TODO Auto-generated method stub
							HttpFactory.Set_Join_Status(mi.getId(), "2", hoinStatusvolleyListener);
							dialog.dismiss();
						}
					}).show();
		}

		//报名会议
		private void enrollMeeting(final MeetingInfo mi) {	
//			Boolean hasConflict = false;
//			for(MeetingInfo m : AppContext.getInstance().getEventmeetingBuffer()){
//				if(m.getAlertbeforetime() == null){
//					continue;
//				}
//				//当前日期（单位：秒）
//				String nowDateStr = DateTimeHelper.getDateNow();
//				Date nowDate = DateTimeHelper.DayStringToDate(nowDateStr);
////				Calendar cal = Calendar.getInstance(); 
////				cal.set(nowDate.getYear(), nowDate.getMonth(), nowDate.getDay(),0,0,0);
//				long nowDateSeconds = nowDate.getTime()/1000L;
//				//当前时间（单位：秒）
//				int nowTimeSeconds = nowDate.getHours() * 24 * 60 * 60 + nowDate.getMinutes() * 60 + nowDate.getSeconds();
//				
//				//个人事件结束日期
//				long personalEventEndDate = Long.parseLong(m.getEnd_date());
//				//个人事件结束时间 
//				int persongalEventEndTime = Integer.parseInt(m.getEnd_time());
//				
//				//会议开始日期
//				long eventStartDate = Long.parseLong(mi.getStart_date());
//				//会议开始时间
//				int eventStartTime = Integer.parseInt(mi.getStart_time());
//				//会议结束日期
//				long eventEndDate = Long.parseLong(mi.getEnd_date());
//				//会议结束时间
//				int eventEndTime = Integer.parseInt(mi.getEnd_time());
//				
//				if(nowDateSeconds <= eventEndDate && personalEventEndDate >= eventStartDate && nowTimeSeconds <= eventEndTime && persongalEventEndTime >= eventStartTime){
//					hasConflict = true;
//					new MyDialog(getActivity(), R.style.MyDialog, "有个人事件与该会议事件重叠，您确定要报名?", "确定", "取消",new MyDialog.DialogClickListener() {
//
//						@Override
//						public void onRightBtnClick(Dialog dialog) {
//							// TODO Auto-generated method stub
//							dialog.dismiss();
//						}
//
//						@Override
//						public void onLeftBtnClick(Dialog dialog) {
//							HttpFactory.Set_Join_Status(mi.getId(), "1", hoinStatusvolleyListener);
//							dialog.dismiss();
//						}
//					}).show();
//				}
//			}
//			if(!hasConflict){
//				HttpFactory.Set_Join_Status(mi.getId(), "1", hoinStatusvolleyListener);
//			}
//		}
			new MyDialog(getActivity(), R.style.MyDialog, "您确定要报名?", "确定", "取消",
					new MyDialog.DialogClickListener() {

						@Override
						public void onRightBtnClick(Dialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}

						@Override
						public void onLeftBtnClick(Dialog dialog) {
							// TODO Auto-generated method stub
							HttpFactory.Set_Join_Status(mi.getId(), "1", hoinStatusvolleyListener);
							dialog.dismiss();
						}
					}).show();
			
		}
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
	
	
    private boolean isHasView( List<RectF> zones,int x ,int y) {

        for (int i = 0; i < zones.size(); i++) {
			int left = (int) zones.get(i).left;
			int top =  (int) zones.get(i).top;
			int right =  (int) zones.get(i).right;
			int bottom =  (int) zones.get(i).bottom;
			if ( y >= top && y <= bottom && x >= left
					&& x <= right) {
				return true;
			}
		}
        return false;
    }
    
	/**
	 * 区分所有时间的时间段，便于绘图计算间隔
	 * @param meetings
	 */
	private boolean CompareEveryTime(MeetingInfo[] meetings,int no,int i){
		int startj = Integer.valueOf(meetings[no].getStart_time());
		int endj = Integer.valueOf(meetings[no].getEnd_time());
			if (i !=no) {
				int starti = Integer.valueOf(meetings[i].getStart_time());
				int endi = Integer.valueOf(meetings[i].getEnd_time());
				if (!(starti > endj || endi < startj)) {
					return true;
				}
			}
		return false;

		
	}

}
