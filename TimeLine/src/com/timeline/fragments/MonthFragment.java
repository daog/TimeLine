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
import com.timeline.common.DensityUtil;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.StringUtils;
import com.timeline.common.UIHelper;
import com.timeline.controls.MonthDateView;
import com.timeline.controls.MonthDateView.DateClick;
import com.timeline.adapter.MonthContentListAdapter;
import com.timeline.app.AppContext;
import com.timeline.bean.MeetingInfo;
import com.timeline.bean.MomentBean;
import com.timeline.calendar.CalendarUtils;
import com.timeline.calendar.MomentAdapter;
import com.timeline.fragments.DayFragment.PopupWindowBtnClickListener;
import com.timeline.fragments.DayFragment.poponDismissListener;
import com.timeline.interf.FragmentCallBack;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.sqlite.InfoHelper;
import com.timeline.ui.Main;
import com.timeline.ui.PastMeetingsAc;
import com.timeline.ui.SearchAc;
import com.timeline.webapi.HttpFactory;
import com.timeline.widget.MyDialog;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
	private LinearLayout llLayout;
	private int datewhith,dateheight;
	
	private ListView lv_monthContent;
	
	private GestureDetector gestureDetector = null;
	
	//当前月会议数据列表
	List<MeetingInfo> monthMeetingList = new ArrayList<MeetingInfo>();;
	//选中天的会议列表
	List<MeetingInfo> dayMeetingInfoList;

	//一段时期内会议搜索监听
	private VolleyListenerInterface periodvolleyListener;
	
	FragmentCallBack fragmentCallBack = null;
	
	
	//会议参与状态设置
	private VolleyListenerInterface hoinStatusvolleyListener;
	//弹出窗口
	PopupWindow popupWindow;
	
	Handler mMonthRefreshDateHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				//刷新日期后进行请求
				try {
					int year = AppContext.CurrentSelectedDate.getYear() + 1900;
					int month = AppContext.CurrentSelectedDate.getMonth();
					int day = AppContext.CurrentSelectedDate.getDate();
					monthDateView.setSelectedDate(year, month, day);
//					initValues();
//					RefreshMonthContentList(); 
				}catch (Exception e) {
					// TODO: handle exception
				}
				break;
			case 1:
				break;
			case 2:
				break;
			default:
				break;
			}
		}
	};
	
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
		AppContext.getInstance().mMonthRefreshDateHandler = mMonthRefreshDateHandler;
		fragmentCallBack = (Main)getActivity();
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
		llLayout = (LinearLayout)view.findViewById(R.id.ll_DateView);
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
		monthDateView.setSelectedDate(AppContext.CurrentSelectedDate.getYear()+1900, AppContext.CurrentSelectedDate.getMonth(), AppContext.CurrentSelectedDate.getDate());
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
            	 map.put("split", getResources().getColor(R.color.week_red));
			}
            else {
            	map.put("split", getResources().getColor(R.color.tasking));
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
				showPopupWindow(info);

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
	
	@SuppressLint("NewApi") 
	private void showPopupWindow(MeetingInfo info) {

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
		
		MeetingInfo meetingInfo = info;
		if(meetingInfo == null){
			return;
		}

		if (meetingInfo.getAlertbeforetime() == null) {
			//隐藏个人操作按钮
			lledit.setVisibility(View.GONE);
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
		if (meetingInfo.getStart_date()!=null&&meetingInfo.getStart_time()!=null&&
				meetingInfo.getEnd_date()!=null&&meetingInfo.getEnd_time()!=null) {
			dateTimeTv.setText(DateTimeHelper.Str2Date(meetingInfo.getStart_date()) + " " +
					DateTimeHelper.int2Time(Integer.valueOf(meetingInfo.getStart_time())) + "至" 
							+DateTimeHelper.Str2Date(meetingInfo.getEnd_date())+" "+
							DateTimeHelper.int2Time(Integer.valueOf(meetingInfo.getEnd_time())));
		}

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
							mMonthdrawHandler.sendEmptyMessage(1);
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
}
