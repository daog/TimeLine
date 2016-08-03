 package com.timeline.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.timeline.common.DateTimeHelper;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.UIHelper;
import com.timeline.controls.MonthDateView;
import com.timeline.controls.MonthDateView.DateClick;
import com.timeline.adapter.MonthContentListAdapter;
import com.timeline.app.AppContext;
import com.timeline.bean.MeetingInfo;
import com.timeline.bean.MomentBean;
import com.timeline.calendar.CalendarUtils;
import com.timeline.calendar.MomentAdapter;
import com.timeline.interf.FragmentCallBack;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.ui.Main;
import com.timeline.ui.PastMeetingsAc;
import com.timeline.ui.SearchAc;
import com.timeline.webapi.HttpFactory;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


public class MonthFragment extends Fragment implements OnGestureListener{
	
	private ImageView iv_left;
	private ImageView iv_right;
	private TextView tv_month;
	private TextView tv_dateText;
	private MonthDateView monthDateView;
	
	private ListView lv_monthContent;
	
	private GestureDetector gestureDetector = null;
	
	//当前月会议数据列表
	List<MeetingInfo> monthMeetingList = new ArrayList<MeetingInfo>();;
	//选中天的会议列表
	List<MeetingInfo> dayMeetingInfoList;

	//一段时期内会议搜索监听
	private VolleyListenerInterface periodvolleyListener;
	
	FragmentCallBack fragmentCallBack = null;
	
	/**
	 * 绘制事件区域
	 * */
	Handler mMonthdrawHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				//登录好后进行请求
				try {
					Date date = DateTimeHelper.GetDateTimeNow();
					int mCurrYear =DateTimeHelper.getYear(date);
					int mCurrMonth = DateTimeHelper.getMonth(date);
					String first = DateTimeHelper.getFirstDayOfMonth(mCurrYear, mCurrMonth);
					String last = DateTimeHelper.getLastDayOfMonth(mCurrYear, mCurrMonth);
					HttpFactory.getMeetingjoin_list_period(first, last, periodvolleyListener);
				}catch (Exception e) {
					// TODO: handle exception
				}
				break;
			case 1:
				try {
					int mCurrYear = monthDateView.getmSelYear();
					int mCurrMonth = monthDateView.getmSelMonth()+1;
					String first = DateTimeHelper.getFirstDayOfMonth(mCurrYear, mCurrMonth);
					String last = DateTimeHelper.getLastDayOfMonth(mCurrYear, mCurrMonth);
					HttpFactory.getMeetingjoin_list_period(first, last, periodvolleyListener);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			case 2:
				break;
			default:
				break;
			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppContext.getInstance().mMontHandler = mMonthdrawHandler;
		fragmentCallBack = (Main)getActivity();
		//初始化获取一段时间的会议的监听
		periodvolleyListener = new VolleyListenerInterface(getActivity()){
			@Override
			public void onMySuccess(String result) {
				// TODO Auto-generated method stub
				try {
					JSONObject myJsonObject = new JSONObject(result);
					String rest = myJsonObject.getString("re_st");
					if (rest.equals("success")) {
						monthMeetingList.clear();
						MeetingInfo[] meetings
						= JsonToEntityUtils.jsontoMeetingInfo( myJsonObject.getString("re_info"));
						if(meetings != null){
							for(MeetingInfo mi : meetings){
								monthMeetingList.add(mi);
							}
						}
						for (MeetingInfo ele : AppContext.getInstance().getEventmeetingBuffer()) {
							monthMeetingList.add(ele);
						}
						
						monthDateView.setMonthMeetingInfoList(monthMeetingList);
						
						RefreshMonthContentList();
					}else {
						monthMeetingList.clear();
						for (MeetingInfo ele : AppContext.getInstance().getEventmeetingBuffer()) {
							monthMeetingList.add(ele);
						}
						
						monthDateView.setMonthMeetingInfoList(monthMeetingList);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			@Override
			public void onMyError(VolleyError error) {
				// TODO Auto-generated method stub
				monthMeetingList.clear();
				for (MeetingInfo ele : AppContext.getInstance().getEventmeetingBuffer()) {
					monthMeetingList.add(ele);
				}
				
				monthDateView.setMonthMeetingInfoList(monthMeetingList);
			}
			
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = (View) inflater.inflate(R.layout.fragment_month, null);

		gestureDetector = new GestureDetector(this);
		//iv_left = (ImageView) view.findViewById(R.id.iv_left);
		//iv_right = (ImageView) view.findViewById(R.id.iv_right);
		monthDateView = (MonthDateView) view.findViewById(R.id.monthDateView);
		tv_month = (TextView) view.findViewById(R.id.id_monthtext);
		lv_monthContent = (ListView)view.findViewById(R.id.id_contentlist_month);
		
		tv_dateText = (TextView)view.findViewById(R.id.id_dateText);
		
		monthDateView.setTextView(tv_month);
		monthDateView.setMonthMeetingInfoList(monthMeetingList);
		monthDateView.setDateClick(new DateClick() {
			
			@Override
			public void onClickOnDate() {
				initValues();
				RefreshMonthContentList(); 
			}
		});
		monthDateView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return MonthFragment.this.gestureDetector.onTouchEvent(event);
			}
		});
		initValues();
		RefreshMonthContentList(); 
		
		return view;
	}

	private List<Map<String, Object>> getData(List<MeetingInfo> dayMeetingList) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();  
        for (MeetingInfo mi : dayMeetingList) {  
            Map<String, Object> map=new HashMap<String, Object>();  
            //map.put("split", Color.rgb(0, 126, 62));
            if (mi.getAlertbeforetime()!=null) {
            	 map.put("split", getResources().getColor(R.color.red));
			}
            else {
            	map.put("split", getResources().getColor(R.color.green));
			}           
            map.put("startTime", mi.getStart_time());  
            map.put("endTime", mi.getEnd_time());  
            map.put("contentTitle", mi.getSubject()); 
            map.put("meeting", mi); 
            list.add(map);  
        }  
        return list;  
	}

	private void initValues() {
		// 获得Service实例
		if(monthDateView.getmSelDay() == 0){
			tv_dateText.setText("");
			return;
		}
		tv_dateText.setText(monthDateView.getmSelMonth()+1+"月"+monthDateView.getmSelDay()+"日");
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}
	
	private void setOnlistener(){
		iv_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				monthDateView.onLeftClick();
				initValues();
				RefreshMonthContentList();
			}
		});
		
		iv_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				monthDateView.onRightClick();
				initValues();
				RefreshMonthContentList();
			}
		});
	}
	
	private void RefreshMonthContentList() {
		String dateStr = monthDateView.getmSelYear() + "-" + String.format("%02d", monthDateView.getmSelMonth() + 1) + "-" + String.format("%02d", monthDateView.getmSelDay());
		dayMeetingInfoList = new ArrayList<MeetingInfo>();
		for(MeetingInfo mi : monthMeetingList){
			if(DateTimeHelper.isInDate(dateStr, DateTimeHelper.Str2Date(mi.getStart_date()), DateTimeHelper.Str2Date(mi.getEnd_date()))){
			//if(mi.getStart_date().equals(dateStr)){
				dayMeetingInfoList.add(mi);
			}
		}
		
		List<Map<String, Object>> contentList=getData(dayMeetingInfoList);  
		lv_monthContent.setAdapter(new MonthContentListAdapter(getActivity(),contentList)); 
		lv_monthContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				TextView contentTitle = (TextView) v.findViewById(R.id.id_contentTitle);
				MeetingInfo info = (MeetingInfo) contentTitle.getTag();
				if (info == null) {
					return;
				}
				if (info.getAlertbeforetime() == null) {
					UIHelper.showMeetingDetail(getActivity(), info.getId());
				}else {
					UIHelper.showEventDe(getActivity(),info.getId());
				}
			}
		});
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		int gvFlag = 0;
		if (e1.getX() - e2.getX() > 80) {
			// 向左滑
			monthDateView.onRightClick();
			mMonthdrawHandler.sendEmptyMessage(1);
			initValues();
			RefreshMonthContentList();
			fragmentCallBack.callbackFun2(monthDateView.getmSelMonth()+1);
			return true;
		} else if (e1.getX() - e2.getX() < -80) {
			monthDateView.onLeftClick();
			initValues();
			mMonthdrawHandler.sendEmptyMessage(1);
			RefreshMonthContentList();
			fragmentCallBack.callbackFun2(monthDateView.getmSelMonth()+1);
			return true;
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
