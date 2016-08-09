package com.timeline.ui;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.timeline.interf.FragmentCallBack;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.service.AlertService;
import com.timeline.slidedatetimepicker.SlideDateTimeListener;
import com.timeline.slidedatetimepicker.SlideDateTimePicker;
import com.timeline.webapi.HttpFactory;
import com.timeline.app.AppContext;
import com.timeline.app.AppManager;
import com.timeline.bean.JobBase;
import com.timeline.bean.MeetingDescribe;
import com.timeline.bean.MeetingInfo;
import com.timeline.bean.ReturnInfo;
import com.timeline.bean.User;
import com.timeline.common.DateTimeHelper;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.Numeric2ChineseStr;
import com.timeline.common.UIHelper;
import com.timeline.controls.ChangeColorIconWithText;

import android.R.integer;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract.Contacts.Data;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class Main extends BaseActivity implements FragmentCallBack{
	
	public static Main instance = null;
	private ViewPager mViewPager;
	private View viewDay,viewWeek,viewMonth,viewMeeting;
	private ArrayList<View> mTabs = new ArrayList<View>();
	
	private int currIndex = 0;// 当前页卡编号
	private PagerAdapter mAdapter;
	ChangeColorIconWithText day;
	ChangeColorIconWithText week;
	ChangeColorIconWithText month;
	ChangeColorIconWithText meeting;
	
	private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();
	private ImageButton btnSeach;
	private ImageButton btnMyInfo;
	private ImageButton btnadd;
	private RelativeLayout rlhead;
	
	private VolleyListenerInterface loginvolleyListener;
	private VolleyListenerInterface baseInfovolleyListener;
	private TextView headView;
	private DoubleClickExitHelper mDoubleClickExitHelper = new DoubleClickExitHelper(this);
	//day控件
	String daydate;
	private VolleyListenerInterface dayvolleyListener;//当前日期会议搜索监听
	
	String dayhead;
	//week控件
	String weekhead;
	/**
	 * 当前选择的一周，每天的日期字符串列表
	 */
	private static List<String> currentWeekDateStrs = new ArrayList<String>();
	
	//month控件
	
	
	public static List<String> getCurrentWeekDateStrs() {
		return currentWeekDateStrs;
	}

	public static void setCurrentWeekDateStrs(List<String> currentWeekDateStrs) {
		Main.currentWeekDateStrs = currentWeekDateStrs;
	}
	//meeting控件
	private LinearLayout llbottom;
	private TextView dateView;
	private TextView monthView;
	private TextView subView;
	private TextView sponView;
	private TextView msgView;
	private TextView zhuView;
	
	private String meetid;
	private VolleyListenerInterface meetingvolleyListener;//当前日期会议搜索监听
	private MeetingInfo nowDescribe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currIndex = AppContext.getInstance().getPage();
      //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        initView();
        initData();
        mViewPager.setAdapter(mAdapter);
        initAllPagerData();
        if (AppContext.getInstance().getIslogin()) {
        	HttpFactory.Login(AppContext.getInstance().getAccount(),AppContext.getInstance().getPsw(), loginvolleyListener);
		}
        baseInfovolleyListener = new VolleyListenerInterface(Main.this) {
			@Override
			public void onMySuccess(String result) {
				// TODO Auto-generated method stub
				try {
					JSONObject myJsonObject = new JSONObject(result);
					String rest = myJsonObject.getString("re_st");
					if (rest.equals("success")) {
						String jobsult =  myJsonObject.getString("re_info");
						JobBase bean = JsonToEntityUtils.jsontoJobBase(jobsult);
						AppContext.getInstance().jonbase = bean;
						if (AppContext.getUser().getStatus().equals("4")) {
							UIHelper.showMyInfo(Main.this, "", "");
						}
						
					}else {
						ReturnInfo info = JsonToEntityUtils.jsontoReinfo(result);
						UIHelper.ToastMessage(Main.this,info.getRe_info() );
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			@Override
			public void onMyError(VolleyError error) {
				// TODO Auto-generated method stub

			}

		};
		instance = this;	
        BinderService();
        week.performClick();
//        if (currIndex == 1) {
//        	week.performClick();
//		}else if (currIndex == 2) {
//			month.performClick();
//		}else if (currIndex == 3) {
//			meeting.performClick();
//		}
    }
    
	/*
	 * 初始化各栏目数据项
	 */
	private void initAllPagerData() {
		headView = (TextView)findViewById(R.id.home_head_title);
		btnSeach = (ImageButton)findViewById(R.id.home_head_morebutton);
		btnMyInfo =(ImageButton)findViewById(R.id.img_head);
		btnadd = (ImageButton)findViewById(R.id.home_add);
		rlhead = (RelativeLayout)findViewById(R.id.home_head);
		btnSeach.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//UIHelper.showMeetingDetail(Main.this);
				UIHelper.showSearch(Main.this);
			}
		});
		btnMyInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (AppContext.getInstance().getIslogin()) {
					if (AppContext.getInstance().jonbase!=null) {
						UIHelper.showMySign(Main.this);
					}
					
				}else {
					UIHelper.showMySign(Main.this);
				}
				
			}
		});
		btnadd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UIHelper.showNewEvent(Main.this, null);
			}
		});
		loginvolleyListener = new VolleyListenerInterface(Main.this) {

			@Override
			public void onMySuccess(String result) {
				// TODO Auto-generated method stub
				try {
					JSONObject myJsonObject = new JSONObject(result);
					String rest = myJsonObject.getString("re_st");
					refreshData(R.id.indicator_week);
					refreshData(R.id.indicator_month);
					refreshData(R.id.indicator_meeting);
					if (rest.equals("success")) {
						User us = JsonToEntityUtils.jsontoUser( myJsonObject.getString("re_info"));
						AppContext.setUser(us);
						HttpFactory.getAvatar(us.getAvatar());
						HttpFactory.BASE_Dictionary(baseInfovolleyListener);
						HttpFactory.getMeetingjoin_list(daydate, dayvolleyListener);
					}else if (rest.equals("verify")) {//当前账号处于待审核状态
						User us = JsonToEntityUtils.jsontoUser( myJsonObject.getString("re_info"));
						AppContext.setUser(us);
						HttpFactory.getAvatar(us.getAvatar());
						HttpFactory.BASE_Dictionary(baseInfovolleyListener);
						HttpFactory.getMeetingjoin_list(daydate, dayvolleyListener);
					}else if (rest.equals("consummate")) {//请完善个人信息
						User us = JsonToEntityUtils.jsontoUser( myJsonObject.getString("re_info"));
						AppContext.setUser(us);
						HttpFactory.getAvatar(us.getAvatar());
						HttpFactory.BASE_Dictionary(baseInfovolleyListener);
						HttpFactory.getMeetingjoin_list(daydate, dayvolleyListener);
						
					}else {
						ReturnInfo info = JsonToEntityUtils.jsontoReinfo(result);
						UIHelper.ToastMessage(Main.this,info.getRe_info() );
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			@Override
			public void onMyError(VolleyError error) {
				// TODO Auto-generated method stub
				Log.e("", "error");
			}

		};
		//day
		daydate = DateTimeHelper.getDateNow();
		String[] str = daydate.split("-");
		dayhead = Numeric2ChineseStr.foematInteger(Integer.valueOf(str[1].toString()))+"月";
		headView.setText(dayhead);
		dayvolleyListener = new VolleyListenerInterface(Main.this){
			@Override
			public void onMySuccess(String result) {
				// TODO Auto-generated method stub
				List<MeetingInfo> melist = new ArrayList<MeetingInfo>();
				try {
					JSONObject myJsonObject = new JSONObject(result);
					String rest = myJsonObject.getString("re_st");
					if (rest.equals("success")) {
						MeetingInfo[] meetings
						= JsonToEntityUtils.jsontoMeetingInfo( myJsonObject.getString("re_info"));
						meetid = meetings[0].getId();
						Collections.addAll(melist, meetings);

					}
					for (MeetingInfo ele : AppContext.getInstance().getEventmeetingBuffer()) {
						if (ele.getStartDateStr("").equals(daydate)) {
							melist.add(ele);
						}
						
					}
					MeetingInfo[] meets = new MeetingInfo[melist.size()];
					for (int i = 0; i < meets.length; i++) {
						meets[i] = melist.get(i);
					}
					Message msg = Message.obtain();
					msg.what = 0;
					msg.obj = meets;
					AppContext.getInstance().mDayTagGetHandler.sendMessage(msg);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			@Override
			public void onMyError(VolleyError error) {
				// TODO Auto-generated method stub
    			Message msg = Message.obtain();
				msg.what = 1;
				msg.obj = daydate;
				AppContext.getInstance().mDayTagGetHandler.sendMessage(msg);
			}
			
		};
		//week
		
		currentWeekDateStrs = getCurrentWeekDateStr();
		String Sweek = currentWeekDateStrs.get(0)+"-"+currentWeekDateStrs.get(6);
		String[] Wstr = Sweek.split("-");
		weekhead = Wstr[0]+"."+Wstr[1]+"."+Wstr[2]+"-"+Wstr[4]+"."+Wstr[5];
		
		//month
		
		
		//meeting
		dateView = (TextView)viewMeeting.findViewById(R.id.meeting_date);
		monthView = (TextView)viewMeeting.findViewById(R.id.meeting_month);
		subView = (TextView)viewMeeting.findViewById(R.id.meeting_title);
		sponView = (TextView)viewMeeting.findViewById(R.id.meeting_host);
		msgView = (TextView)viewMeeting.findViewById(R.id.meeting_msg);
		zhuView = (TextView)viewMeeting.findViewById(R.id.meeting_zhu);
		llbottom = (LinearLayout)viewMeeting.findViewById(R.id.meting_bottom);
		meetingvolleyListener = new VolleyListenerInterface(Main.this){
			@Override
			public void onMySuccess(String result) {
				// TODO Auto-generated method stub
				try {
					JSONObject myJsonObject = new JSONObject(result);
					String res = myJsonObject.getString("re_st");
					if (res.equals("success")) {
						String rest = myJsonObject.getString("re_info");
						JSONObject meetingObject = new JSONObject(rest);
						MeetingInfo[] meetingdes = JsonToEntityUtils
								.jsontoMeetingInfo(meetingObject
										.getString("meeting_info"));
						nowDescribe = meetingdes[0];
						if (nowDescribe != null) {
							String datestr = nowDescribe.getStart_date();
							Date date = new Date(Long.valueOf(datestr));
							String month = DateTimeHelper.getMonthEn(String.valueOf(DateTimeHelper.getMonth(date)));
							String year = String.valueOf(DateTimeHelper.getYear(date));
							dateView.setText(DateTimeHelper.getDay(date));
							monthView.setText(month+"."+year);
							subView.setText(nowDescribe.getSubject());
							sponView.setText(nowDescribe.getSponsor());
							dateView.setVisibility(View.VISIBLE);
							monthView.setVisibility(View.VISIBLE);
							subView.setVisibility(View.VISIBLE);
							sponView.setVisibility(View.VISIBLE);
							zhuView.setVisibility(View.VISIBLE);
							llbottom.setVisibility(View.VISIBLE);
						    msgView.setVisibility(View.GONE);
						}
				}else {
					dateView.setVisibility(View.GONE);
					monthView.setVisibility(View.GONE);
					subView.setVisibility(View.GONE);
					sponView.setVisibility(View.GONE);
					zhuView.setVisibility(View.GONE);
					llbottom.setVisibility(View.GONE);
				    msgView.setVisibility(View.VISIBLE);
				}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}

			@Override
			public void onMyError(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
	
	  public void Interaction_Click(View v){
		  UIHelper.showInterac(this);
	  }
	  public void Signin_Click(View v){
		  UIHelper.showGuSign(meetid,this);
	  }
	  
	  public void search_Click(View v){
		  UIHelper.showSearch(this);
	  }
	  public void info_Click(View v){
			if (AppContext.getInstance().getIslogin()) {
				if (AppContext.getInstance().jonbase!=null) {
					UIHelper.showMySign(Main.this);
				}
				
			}else {
				UIHelper.showMySign(Main.this);
			}
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
	protected void onStop() {
		super.onStop();
		AppContext.getInstance().savePage(currIndex);
	}  
		
		
    @Override
	  protected void onResume() {
		super.onResume();
    	if (AppContext.getInstance().getIslogin()) {
    			HttpFactory.getMeetingjoin_list(daydate, dayvolleyListener);
    	}else {
//			Message msg = Message.obtain();
//			msg.what = 1;
//			AppContext.getInstance().mDayTagGetHandler.sendMessage(msg);
			Message msg = Message.obtain();
			msg.what = 1;
			msg.obj = daydate;
			AppContext.getInstance().mDayTagGetHandler.sendMessage(msg);
		}
	}
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    			// 是否退出应用
    			return mDoubleClickExitHelper.onKeyDown(keyCode, event);
    	}
    	return false;
    }
    private void initData() {
		// TODO Auto-generated method stub
		LayoutInflater mLi = LayoutInflater.from(this);
		viewDay = mLi.inflate(R.layout.frame_day,null);
		viewWeek = mLi.inflate(R.layout.frame_week,null);
		viewMonth = mLi.inflate(R.layout.frame_month,null);
		viewMeeting = mLi.inflate(R.layout.frame_meeting,null);
		
		ViewWeekHelper.InitViewWeek(viewWeek, this);
		
		
		mTabs.add(viewDay);
		mTabs.add(viewWeek);
		mTabs.add(viewMonth);
		mTabs.add(viewMeeting);
		
		mAdapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return mTabs.size();
			}

			@Override  
            public int getItemPosition(Object object) {  
                return super.getItemPosition(object);  
            }  			
			
			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(mTabs.get(position));
			}
			
			//@Override
			//public CharSequence getPageTitle(int position) {
				//return titles.get(position);
			//}
			
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(mTabs.get(position));
				return mTabs.get(position);
			}
		};
		
		mViewPager.setAdapter(mAdapter);		
		mViewPager.setCurrentItem(currIndex,false);

    }

	private void initView() {
		// TODO Auto-generated method stub
		mViewPager = (ViewPager)findViewById(R.id.id_viewpager);
		
		 day = (ChangeColorIconWithText)findViewById(R.id.indicator_day);
		mTabIndicators.add(day);
		 week = (ChangeColorIconWithText)findViewById(R.id.indicator_week);
		mTabIndicators.add(week);
		 month = (ChangeColorIconWithText)findViewById(R.id.indicator_month);
		mTabIndicators.add(month);
		 meeting = (ChangeColorIconWithText)findViewById(R.id.indicator_meeting);
		mTabIndicators.add(meeting);
		
		day.setOnClickListener(new IndicatorClickListener());
		week.setOnClickListener(new IndicatorClickListener());
		month.setOnClickListener(new IndicatorClickListener());
		meeting.setOnClickListener(new IndicatorClickListener());
		
		day.SetIconAlpha(1.0f);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
    
    public class IndicatorClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			resetOtherTabs();
			
			switch(v.getId())
			{
			case R.id.indicator_day:
				mTabIndicators.get(0).SetIconAlpha(1.0f);
				mViewPager.setCurrentItem(0,false);
				rlhead.setVisibility(View.VISIBLE);
				headView.setText(dayhead);
				refreshData(v.getId());
				currIndex = 0;
				AppContext.getInstance().savePage(currIndex);
				break;
			case R.id.indicator_week:
				mTabIndicators.get(1).SetIconAlpha(1.0f);
				mViewPager.setCurrentItem(1,false);
				rlhead.setVisibility(View.VISIBLE);
				headView.setText(weekhead);
				refreshData(v.getId());
				currIndex = 1;
				AppContext.getInstance().savePage(currIndex);
				break;
			case R.id.indicator_month:
				mTabIndicators.get(2).SetIconAlpha(1.0f);
				mViewPager.setCurrentItem(2,false);
				rlhead.setVisibility(View.GONE);
				refreshData(v.getId());
				currIndex = 2;
				AppContext.getInstance().savePage(currIndex);
				break;
			case R.id.indicator_meeting:
				mTabIndicators.get(3).SetIconAlpha(1.0f);
				mViewPager.setCurrentItem(3,false);
				rlhead.setVisibility(View.GONE);
				refreshData(v.getId());
				currIndex = 3;
				AppContext.getInstance().savePage(currIndex);
				break;	
			}
		}
    }
    /**
     *数据实时刷新
     * @param Id
     */
    public void refreshData(int Id)
    {
    	if (AppContext.getInstance().getIslogin()) {
    		switch(Id)
    		{
    		case R.id.indicator_day:	
    			HttpFactory.getMeetingjoin_list(daydate, dayvolleyListener);
    			break;
    		case R.id.indicator_week:
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

    			break;
    		case R.id.indicator_month:
    			Message msg = Message.obtain();
				msg.what = 0;
				AppContext.getInstance().mMontHandler.sendMessage(msg);
    			break;
    		case R.id.indicator_meeting:
    			HttpFactory.MeetingNowMyJoin(meetingvolleyListener);
    			break;
    		}
		}else {
    		switch(Id)
    		{
    		case R.id.indicator_day:	
    			Message msg = Message.obtain();
				msg.what = 1;
				msg.obj = daydate;
				AppContext.getInstance().mDayTagGetHandler.sendMessage(msg);
    			break;
    		case R.id.indicator_week:
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
//    			for(int i = 0; i < AppContext.getInstance().mWeekHandlers.size(); i++){
//    				Message msgWeek = Message.obtain();
//    				msgWeek.what = 0;
//    				AppContext.getInstance().mWeekHandlers.get(i).sendMessage(msgWeek);
//    			}
    			break;
    		case R.id.indicator_month:
    			Message msgMon = Message.obtain();
    			msgMon.what = 1;
				AppContext.getInstance().mMontHandler.sendMessage(msgMon);
    			break;
    		case R.id.indicator_meeting:
    			HttpFactory.MeetingNowMyJoin(meetingvolleyListener);
    			break;
    		}
		}

    }
    
    private void resetOtherTabs() {
		// TODO Auto-generated method stub
		for(int i =0; i< mTabIndicators.size(); i++)
		{
			mTabIndicators.get(i).SetIconAlpha(0);
		}
	}

	@Override
	public void callbackFun1(String date) {
		// TODO Auto-generated method stub
		String[] str = date.split("-");
		DecimalFormat df = new DecimalFormat("00");
		String month =  df.format(Integer.valueOf(str[1]));
		String day =  df.format(Integer.valueOf(str[2]));
		date = str[0]+"-"+month+"-"+day;

		daydate = date;
		dayhead = (String) Numeric2ChineseStr.getmSelMonthText(Integer.valueOf(str[1].toString()));
		headView.setText(dayhead);
		HttpFactory.getMeetingjoin_list(date, dayvolleyListener);
	}

	@Override
	public void callbackFun2(int arg) {
		// TODO Auto-generated method stub
		headView.setText(Numeric2ChineseStr.getmSelMonthText(Integer.valueOf(arg)));
	}
	
	@Override
	public void callbackFun3(String re) {
		// TODO Auto-generated method stub
		String[] str = re.split("-");
		weekhead = str[0]+"."+str[1]+"."+str[2]+"-"+str[4]+"."+str[5];
		headView.setText(weekhead);
	}
	Intent intent;
	ServiceConnection synCon;
	/**
	 * 以Binding方式启动的启动服务
	 */
	private void BinderService() {
		intent = new Intent(this, AlertService.class);
		synCon = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName componentName,
					IBinder binder) {
				// 调用bindService方法启动服务时候，如果服务需要与activity交互，
				// 则通过onBind方法返回IBinder并返回当前本地服务
				AppContext.alertService = ((AlertService.LocalBinder) binder)
						.getService();
			}

			@Override
			public void onServiceDisconnected(ComponentName componentName) {
				AppContext.alertService = null;
				// 这里可以提示用户
			}
		};
		bindService(intent, synCon, Context.BIND_AUTO_CREATE);
	}
	/**
	 * 以UnBindService方式停止的启动服务
	 */
	private void UnBindService() {
		intent = new Intent(this, AlertService.class);
		if (synCon != null) {
			unbindService(synCon);
		}
	}


}
