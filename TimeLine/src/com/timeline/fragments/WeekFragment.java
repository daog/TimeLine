package com.timeline.fragments;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.timeline.app.AppContext;
import com.timeline.bean.MeetingInfo;
import com.timeline.calendar.CalendarUtils;
import com.timeline.common.DateTimeHelper;
import com.timeline.common.DensityUtil;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.StringUtils;
import com.timeline.common.UIHelper;
import com.timeline.fragments.MonthFragment.poponDismissListener;
import com.timeline.interf.FragmentCallBack;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.sqlite.InfoHelper;
import com.timeline.ui.Main;
import com.timeline.ui.MeetingDetailAc;
import com.timeline.ui.SettingAc;
import com.timeline.webapi.HttpFactory;
import com.timeline.widget.MyDialog;

import de.greenrobot.dao.internal.FastCursor;

public class WeekFragment extends Fragment  {
	public static final String ARG_PAGE = "page";
	private int mPageNumber;
	private View weekFragment;
	
	// 一周的活动
	private List<View> days = new ArrayList<View>();
	//当前选择的一周，每天的日期字符串列表
	private List<String> currentWeekDateStrs = new ArrayList<String>();

	//一段时期内会议搜索监听
	private VolleyListenerInterface periodvolleyListener;
	//一段时期内的所有会议
	private List<MeetingInfo> periodMeetings = new ArrayList<MeetingInfo>();
	
	private ViewFlipper mFlipper;

	private LinearLayout ll;
	
	//弹出窗口
	PopupWindow popupWindow;
	
	//弹出窗口关闭按钮
	private Button popupCloseBtn;

	//会议参与状态设置
	private VolleyListenerInterface hoinStatusvolleyListener;

	/**
	 * 绘制事件区域
	 * */
	Handler mWeekdrawHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				//登录好后进行请求
				try {
					//currentWeekDateStrs = getCurrentWeekDateStr();
//					List<String> WeekDateStrs = Main.getCurrentWeekDateStrs();
//					currentWeekDateStrs = CalendarUtils.getInstance().getSelectedWeek(mPageNumber,WeekDateStrs);
//					Thread.sleep(100);
					if(currentWeekDateStrs != null && currentWeekDateStrs.size() > 0){
						setAllChildren();
					}
				}catch (Exception e) {
					// TODO: handle exceptionshafangfa
				}
				break;
			case 1:
				
			case 2:
				break;
			default:
				break;
			}
		}
	};
	
	
	public static WeekFragment create(int position) {
		WeekFragment fragment = new WeekFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, position);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//currentWeekDateStrs = getCurrentWeekDateStr();
		mPageNumber = getArguments().getInt(ARG_PAGE);
		List<String> WeekDateStrs = Main.getCurrentWeekDateStrs();
		Log.e("Position", "pos"+mPageNumber);
		currentWeekDateStrs =CalendarUtils.getInstance().getSelectedWeek(mPageNumber,WeekDateStrs);
		
		if(AppContext.getInstance().mWeekHandlers.get(mPageNumber) != null){
			AppContext.getInstance().mWeekHandlers.remove(mPageNumber);
		}
		AppContext.getInstance().mWeekHandlers.put(mPageNumber, mWeekdrawHandler);
		
		
		String i  = "2";
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		weekFragment = (View) inflater.inflate(R.layout.fragment_week, null);

		initData(weekFragment);
		setAllChildren();

		return weekFragment;
	}

	
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		
		if(currentWeekDateStrs != null && currentWeekDateStrs.size() > 0){
			setAllChildren();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		//currentWeekDateStrs = getCurrentWeekDateStr();
	//	setAllChildren();
		
	}



	/**
	 * 初始化数据
	 */
	private void initData(View view) {

		ll = (LinearLayout) view.findViewById(R.id.id_ll);
		ll.setLongClickable(true);
		
		days.add(view.findViewById(R.id.id_weekitem1));
		days.add(view.findViewById(R.id.id_weekitem2));
		days.add(view.findViewById(R.id.id_weekitem3));
		days.add(view.findViewById(R.id.id_weekitem4));
		days.add(view.findViewById(R.id.id_weekitem5));
		days.add(view.findViewById(R.id.id_weekitem6));
		days.add(view.findViewById(R.id.id_weekitem7));
		
		//currentWeekDateStrs = getCurrentWeekDateStr();
		
		//初始化获取一段时间的会议的监听
		periodvolleyListener = new VolleyListenerInterface(getActivity()){
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
					generateDayViews();
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
				generateDayViews();
			}
			
		};
		//初始化会议参与监听
		hoinStatusvolleyListener= new VolleyListenerInterface(getActivity()){
			@Override
			public void onMySuccess(String result) {
				// TODO Auto-generated method stub
				try {
					JSONObject myJsonObject = new JSONObject(result);
					String rest = myJsonObject.getString("re_st");
					if (rest.equals("success")) {
						String startDate = currentWeekDateStrs.get(0);
						String endDate = currentWeekDateStrs.get(currentWeekDateStrs.size()-1);
						HttpFactory.getMeetingjoin_list_period(startDate, endDate, periodvolleyListener);
					}
					UIHelper.ToastMessage(getActivity(), myJsonObject.getString("re_info"));
					
					//刷新加载页数据
					HashMap handlers = AppContext.getInstance().mWeekHandlers;
	    			Iterator iter = handlers.entrySet().iterator();
					while (iter.hasNext()) {
						HashMap.Entry entry = (HashMap.Entry) iter.next();
						//Object key = entry.getKey();
						Handler handler = (Handler)entry.getValue();
					 
					 	Message msgWeek = Message.obtain();
					 	msgWeek.what = 0;
					 	handler.sendMessage(msgWeek);
					}
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

	/*
	 * 获取所有子控件
	 */
	private void setAllChildren() {
		generateDayViews();
		String startDate = currentWeekDateStrs.get(0);
		String endDate = currentWeekDateStrs.get(currentWeekDateStrs.size()-1);
		HttpFactory.getMeetingjoin_list_period(startDate, endDate, periodvolleyListener);
		
		
		
		//测试使用，不测试直接注释掉
		//generateTestData();
	}
	
	/**
	 * 生成一周内每天的会议视图
	 */
	private void generateDayViews() {
		for (int dayViewIndex = 0; dayViewIndex < days.size(); dayViewIndex++) {
			View dayView = days.get(dayViewIndex);
			LinearLayout dayContent = (LinearLayout) dayView
					.findViewById(R.id.id_dayContent);
			dayContent.removeAllViews();
			
			String dateStr = currentWeekDateStrs.get(dayViewIndex);
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date currentdate = null;
			try {
				currentdate = sdf.parse(dateStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			TextView dayTv = (TextView)dayView.findViewById(R.id.day);
			String dayStr = dateStr.split("-")[2];
			dayTv.setText(dayStr);
			
			String weekDayStr = getWeekDayStr(dayViewIndex);
			TextView weekDayTv = (TextView)dayView.findViewById(R.id.weekday);
			weekDayTv.setText(weekDayStr);
			
			//设置日期和星期的颜色
			dayTv.setTextColor(Color.BLACK);
			weekDayTv.setTextColor(Color.BLACK);
			if(weekDayStr.equals("周六")||weekDayStr.equals("周日")){
				dayTv.setTextColor(Color.BLUE);
				weekDayTv.setTextColor(Color.BLUE);
			}
			if(dateStr.equals(DateTimeHelper.getDateNow())){
				dayTv.setTextColor(Color.RED);
				weekDayTv.setTextColor(Color.RED);
			}

			//HorizontalScrollView hsv = new HorizontalScrollView(getActivity());
			HorizontalScrollView hsv = new HorizontalScrollView(AppContext.getInstance());

			hsv.setHorizontalScrollBarEnabled(false); 
			hsv.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					return false;
				}
			});

			// 定义布局管理器的参数
			LinearLayout.LayoutParams hsvParam = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);

			final LinearLayout allContents = new LinearLayout(AppContext.getInstance());
			// 定义布局管理器的参数
			LinearLayout.LayoutParams allContentsParam = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			// 所有组件水平摆放
			allContents.setOrientation(LinearLayout.HORIZONTAL);

			List<MeetingInfo> dayMeetings = new ArrayList<MeetingInfo>();
			for(MeetingInfo mi : periodMeetings){
					if (mi.getEndDate()!=null&&mi.getStartDate()!=null) {
						if (currentdate.equals(mi.getStartDate())
								|| currentdate.equals(mi.getEndDate())
								|| (currentdate.after(mi.getStartDate()) && currentdate
										.before(mi.getEndDate()))) {
							MeetingInfo miClone = new MeetingInfo();
							miClone.setId(mi.getId());
							miClone.setSubject(mi.getSubject());
							miClone.setDescribe(mi.getDescribe());
							miClone.setAddress(mi.getAddress());
							miClone.setStart_time(mi.getStart_time());
							miClone.setEnd_time(mi.getEnd_time());
							miClone.setServey_url(mi.getServey_url());
							miClone.setServey_st(mi.getServey_st());
							miClone.setStart_date(mi.getStart_date());
							miClone.setEnd_date(mi.getEnd_date());
							miClone.setRepeate(mi.getRepeate());
							miClone.setAlertbeforetime(mi.getAlertbeforetime());
							miClone.setSponsor(mi.getSponsor());
							miClone.setJoin_st(mi.getJoin_st());
							miClone.setEdit_TX1(mi.getEdit_TX1());
							miClone.setEdit_TX2(mi.getEdit_TX2());
							
							miClone.setIncludeDayStr(DateTimeHelper
									.DateToString(currentdate, "MM月dd日"));
							dayMeetings.add(miClone);
						}
					}


			}
			
			for (MeetingInfo dayMeeting : dayMeetings) {
				// 一个自定义的布局，作为显示的内容
				RelativeLayout content = (RelativeLayout)LayoutInflater.from(AppContext.getInstance()).inflate(
						R.layout.listitem_weekdaycontent, null);
				LinearLayout content1  = (LinearLayout) content.findViewById(R.id.id_content1);
				WindowManager wm = (WindowManager) AppContext.getInstance()
						.getSystemService(Context.WINDOW_SERVICE);
				LinearLayout llspilt  = (LinearLayout) content.findViewById(R.id.id_split);
				int width = (wm.getDefaultDisplay().getWidth() - 15) / 14 * 12 / 4;
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
						width, ViewGroup.LayoutParams.MATCH_PARENT);
				param.setMargins(8, 0, 0, 0);
				param.gravity = Gravity.CENTER_HORIZONTAL;
				// 所有组件垂直摆放
				content1.setOrientation(LinearLayout.VERTICAL);
				content1.setBackgroundColor(AppContext.getInstance().getResources().getColor(R.color.week_greenno));
				
				TextView subjectTv = (TextView)content.findViewById(R.id.id_topic);
				TextView orginzerTv = (TextView)content.findViewById(R.id.id_orgnizer);
				TextView meetIdTv = (TextView)content.findViewById(R.id.id_meetId);
				ImageView imaSta = (ImageView)content.findViewById(R.id.id_StaImg);
				
				if (dayMeeting.getJoin_st()!=null) {
					if (dayMeeting.getJoin_st().equals("1")) {//已报名
						imaSta.setImageDrawable(getResources().getDrawable(R.drawable.icon_meeting_upyes));
						content1.setBackgroundColor(AppContext.getInstance().getResources().getColor(R.color.day_bluetran));
						
					}else if (dayMeeting.getJoin_st().equals("2")) {//待定
						llspilt.setBackgroundColor(AppContext.getInstance().getResources().getColor(R.color.week_green));
						imaSta.setImageDrawable(getResources().getDrawable(R.drawable.icon_meeting_underyes));
						
					}else if (dayMeeting.getJoin_st().equals("3")) {//拒绝
						llspilt.setBackgroundColor(AppContext.getInstance().getResources().getColor(R.color.week_green));
						imaSta.setImageDrawable(getResources().getDrawable(R.drawable.icon_meeeting_refyes));
					}else if (dayMeeting.getJoin_st().equals("4")) {//无操作
						imaSta.setVisibility(View.GONE);
						llspilt.setBackgroundColor(AppContext.getInstance().getResources().getColor(R.color.week_green));
						content1.setBackgroundColor(getResources().getColor(R.color.week_greenno));
					}
				}else {
					llspilt.setBackgroundColor(AppContext.getInstance().getResources().getColor(R.color.week_red));
					content1.setBackgroundColor(AppContext.getInstance().getResources().getColor(R.color.day_redtran));
					imaSta.setVisibility(View.GONE);
				}

				subjectTv.setText(dayMeeting.getSubject());
				orginzerTv.setText(dayMeeting.getSponsor());
				meetIdTv.setText(dayMeeting.getId());
				if (dayMeeting.getAlertbeforetime()!=null) {
					orginzerTv.setText("个人事件");
				}
				
				content1.setTag(dayMeeting);
				
				content1.setOnClickListener(new ContentClickListener());

				allContents.addView(content,param);

			}
			
			hsv.addView(allContents, allContentsParam);
			dayContent.addView(hsv, hsvParam);
		}
	}

	/**
	 * 获取当前是周几
	 * @return
	 */
	private String getWeekDayStr(int index) {
		switch(index){
		case 0:
			return "周日";
		case 1:
			return "周一";
		case 2:
			return "周二";
		case 3:
			return "周三";
		case 4:
			return "周四";
		case 5:
			return "周五";
		case 6:
			return "周六";
			
		}
		return "";
	}

	/**
	 * 获取一周内所有天的日期字符串
	 * @return 一周内所有天的日期字符串
	 */
	private static List<String> getCurrentWeekDateStr() {
		List<String> dateStrs = new ArrayList<String>();
		
		String dateNow = DateTimeHelper.getDateNow();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
		dateNow = dateFormat.format(new Date());
		
		Calendar calendar = Calendar.getInstance();
        int dayForWeek =  calendar.get(Calendar.DAY_OF_WEEK);

		for(int i =0; i< 7; i++){
			int addDays = i - dayForWeek + 1;
			calendar.setTime(new Date());  
	        calendar.add(Calendar.DAY_OF_YEAR, addDays); 
	        String dateStr = dateFormat.format(calendar.getTime());
	        dateStrs.add(dateStr);
		}
		
		return dateStrs;
	}
	
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	/*
	 * content点击事件
	 */
	public class ContentClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
//			Toast.makeText(getActivity(), v.getId() + "", Toast.LENGTH_SHORT)
//					.show();
			showPopupWindow(v);
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

		if (meetingInfo.getJoin_st() != null) {
			//隐藏个人操作按钮
			lledit.setVisibility(view.GONE);
			lldelete.setVisibility(View.GONE);
			
			if (meetingInfo.getJoin_st().equals("1")) {//已报名
				enrollBtn.setBackground(getResources().getDrawable(R.drawable.icon_meeting_upyes));
				enrollBtn.setEnabled(false);
				llenroll.setEnabled(false);
				enrollTv.setText("已报名");
				
			}else if (meetingInfo.getJoin_st().equals("2")) {//待定
				undeterminedBtn.setEnabled(false);
				llundetermined.setEnabled(false);
				undeterminedBtn.setBackground(getResources().getDrawable(R.drawable.icon_meeting_underyes));
				
			}else if (meetingInfo.getJoin_st().equals("3")) {//拒绝
				refuseBtn.setBackground(getResources().getDrawable(R.drawable.icon_meeeting_refyes));
				refuseTv.setText("已拒绝");
			}else if (meetingInfo.getJoin_st().equals("4")) {//无操作
				
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
		dateTimeTv.setText(meetingInfo.getIncludeDayStr() + " " + DateTimeHelper.int2Time(Integer.valueOf(meetingInfo.getStart_time())) + " - " + DateTimeHelper.int2Time(Integer.valueOf(meetingInfo.getEnd_time())));
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
				enrollMeeting(mi);
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
					enrollMeeting(mi);
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
							dialog.dismiss();
							setAllChildren();
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
			Boolean hasConflict = false;
			for(MeetingInfo m : AppContext.getInstance().getEventmeetingBuffer()){
				if(m.getAlertbeforetime() == null){
					continue;
				}
				//当前日期（单位：秒）
				String nowDateStr = DateTimeHelper.getDateNow();
				Date nowDate = DateTimeHelper.DayStringToDate(nowDateStr);
//				Calendar cal = Calendar.getInstance(); 
//				cal.set(nowDate.getYear(), nowDate.getMonth(), nowDate.getDay(),0,0,0);
				long nowDateSeconds = nowDate.getTime()/1000L;
				//当前时间（单位：秒）
				int nowTimeSeconds = nowDate.getHours() * 24 * 60 * 60 + nowDate.getMinutes() * 60 + nowDate.getSeconds();
				
				//个人事件结束日期
				long personalEventEndDate = Long.parseLong(m.getEnd_date());
				//个人事件结束时间 
				int persongalEventEndTime = Integer.parseInt(m.getEnd_time());
				
				//会议开始日期
				long eventStartDate = Long.parseLong(mi.getStart_date());
				//会议开始时间
				int eventStartTime = Integer.parseInt(mi.getStart_time());
				//会议结束日期
				long eventEndDate = Long.parseLong(mi.getEnd_date());
				//会议结束时间
				int eventEndTime = Integer.parseInt(mi.getEnd_time());
				
				if(nowDateSeconds <= eventEndDate && personalEventEndDate >= eventStartDate && nowTimeSeconds <= eventEndTime && persongalEventEndTime >= eventStartTime){
					hasConflict = true;
					new MyDialog(getActivity(), R.style.MyDialog, "有个人事件与该会议事件重叠，您确定要报名?", "确定", "取消",new MyDialog.DialogClickListener() {

						@Override
						public void onRightBtnClick(Dialog dialog) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}

						@Override
						public void onLeftBtnClick(Dialog dialog) {
							HttpFactory.Set_Join_Status(mi.getId(), "1", hoinStatusvolleyListener);
							dialog.dismiss();
						}
					}).show();
				}
				if(hasConflict){
					break;
				}
			}
			if(!hasConflict){
				HttpFactory.Set_Join_Status(mi.getId(), "1", hoinStatusvolleyListener);
			}
		
//			new MyDialog(getActivity(), R.style.MyDialog, "您确定要报名?", "确定", "取消",
//					new MyDialog.DialogClickListener() {
//
//						@Override
//						public void onRightBtnClick(Dialog dialog) {
//							// TODO Auto-generated method stub
//							dialog.dismiss();
//						}
//
//						@Override
//						public void onLeftBtnClick(Dialog dialog) {
//							// TODO Auto-generated method stub
//								
//							dialog.dismiss();
//						}
//					}).show();
			
		}
		
	}

}
