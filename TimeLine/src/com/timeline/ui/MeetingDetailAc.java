package com.timeline.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.timeline.adapter.MeetingGuestAdapter;
import com.timeline.adapter.MeetingPlanAdapter;
import com.timeline.app.AppContext;
import com.timeline.bean.JobBase;
import com.timeline.bean.MeetingDescribe;
import com.timeline.bean.MeetingDetailPlanBean;
import com.timeline.bean.MeetingInfo;
import com.timeline.bean.MeetingPlanBean;
import com.timeline.bean.ReturnInfo;
import com.timeline.bean.guest;
import com.timeline.common.DateTimeHelper;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.UIHelper;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.webapi.HttpFactory;
import com.timeline.widget.MyDialog;

import android.R.string;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MeetingDetailAc extends BaseActivity{
	
	private ViewPager mTabPager;
	private ImageView backimg;
	private TextView headText;
	//填充界面
	private View viewMeg, viewPlan, viewGuest;
	private TextView txtTab1, txtTab2,txtTab4;
	private LinearLayout ll_Tab1, ll_Tab2, ll_Tab4;
	private int zero = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int one;// 单个水平动画位移
	private int two;
	private int three;
	
	private String mid;//会议id
	private VolleyListenerInterface volleyListener;
	private ImageView upView;
	private ImageView collectView;
	private ImageView refuseView;
	private TextView uptxtView;
	private TextView collecttxtView;
	private TextView refusetxtView;
	
	//会议参与状态设置
	private VolleyListenerInterface joinStatusvolleyListener;
	private String joinStatus;
	private String collectStatus;
	
	//会议收藏设置
	private VolleyListenerInterface collectvolleyListener;
	//会议通知控件
	private WebView webView;
	
	//会议日程控件
	private ExpandableListView planlistview;
	private MeetingPlanAdapter planAdapter;
	private List<MeetingPlanBean> plans = new ArrayList<MeetingPlanBean>();
	
	//会议嘉宾控件
	private ListView guestlistview;
	private MeetingGuestAdapter guestAdapter;
	private List<guest> guests = new ArrayList<guest>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meeting_detail);
		Bundle bundle = getIntent().getExtras();
		mid = bundle.getString("id");
		initViewPager();
		changeViewPager(currIndex);
		// 初始化各栏数据
		initAllPagerData();
		volleyListener = new VolleyListenerInterface(MeetingDetailAc.this) {
		@Override
		public void onMySuccess(String result) {
			// TODO Auto-generated method stub
			try {
				JSONObject myJsonObject = new JSONObject(result);
				String rest = myJsonObject.getString("re_st");
				if (rest.equals("success")) {
					String infosult =  myJsonObject.getString("re_info");
					JSONObject infoJsonObject = new JSONObject(infosult);
					String meetStr = infoJsonObject.getString("meeting_info");
					String planStr = infoJsonObject.getString("detail");
					JSONObject planJsonObject = new JSONObject(planStr);
					//父列表
					String parent = planJsonObject.getString("parent");
					MeetingPlanBean[] planbeans = JsonToEntityUtils.jsontoMeetingPlanBean(parent);
					//子列表
					
					if (!planJsonObject.isNull("sub") ) {
						String subchild = planJsonObject.getString("sub");
						JSONObject childJsonObject = new JSONObject(subchild);
						for (int i = 0; i < planbeans.length; i++) {
							String subCh = childJsonObject
									.getString(planbeans[i].getId().toString());
							MeetingDetailPlanBean[] childbeans = JsonToEntityUtils
									.jsontoMeetingDetailPlanBean(subCh);
							List<MeetingDetailPlanBean> List = new ArrayList<MeetingDetailPlanBean>();
							for (MeetingDetailPlanBean chPlanBean : childbeans) {
								List.add(chPlanBean);
							}
							planbeans[i].setDetails(List);
						}
					}
					String guestStr = infoJsonObject.getString("honored_guest");
					guest[] gus = JsonToEntityUtils.jsontoguest(guestStr);	
					MeetingDescribe meets = JsonToEntityUtils.jsontoMeetingDes(meetStr);
//					
					String status = meets.getIs_join();
					if (status !=null) {
						if (status.equals("1")) {//已报名
							upView.setImageDrawable(getResources().getDrawable(R.drawable.icon_meeting_upyes));
							uptxtView.setText("已报名");
						}else if(status.equals("2")){//待定
							
						}else if(status.equals("3")){//拒绝
							refuseView.setImageDrawable(getResources().getDrawable(R.drawable.icon_meeeting_refyes));
							refusetxtView.setText("已拒绝");
						}else if(status.equals("4")){//无操作
							
						}
					}	
					collectStatus = meets.getCollect_st();
					if (collectStatus !=null) {
						if (collectStatus.equals("1")) {
							collectView.setImageDrawable(getResources().getDrawable(R.drawable.icon_my_collect));
							collecttxtView.setText("已收藏");
						}
					}
					webView.loadUrl(meets.getServey_url()); 
					guests.clear();
					plans.clear();
					for (guest gu:gus) {
						guests.add(gu);
					}
					for (MeetingPlanBean meetingPlanBean : planbeans) {
						plans.add(meetingPlanBean);
					}
					planAdapter.notifyDataSetChanged();
					guestAdapter.notifyDataSetChanged();
 //					HashMap<String, String> posmap =  toHashMap(posStr);
				}else {
					ReturnInfo info = JsonToEntityUtils.jsontoReinfo(result);
					UIHelper.ToastMessage(MeetingDetailAc.this,info.getRe_info() );
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
	//初始化会议参与监听
	joinStatusvolleyListener= new VolleyListenerInterface(MeetingDetailAc.this){
		@Override
		public void onMySuccess(String result) {
			// TODO Auto-generated method stub
			try {
				JSONObject myJsonObject = new JSONObject(result);
				String rest = myJsonObject.getString("re_st");
				if (rest.equals("success")) {
					if (joinStatus.equals("1")) {
						upView.setImageDrawable(getResources().getDrawable(R.drawable.icon_meeting_upyes));
						uptxtView.setText("已报名");
					}else {
						upView.setImageDrawable(getResources().getDrawable(R.drawable.meeting_up));
						uptxtView.setText("报名");
						refuseView.setImageDrawable(getResources().getDrawable(R.drawable.icon_meeeting_refyes));
						refusetxtView.setText("已拒绝");
					}
				}
				UIHelper.ToastMessage(MeetingDetailAc.this, myJsonObject.getString("re_info"));
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		@Override
		public void onMyError(VolleyError error) {
			// TODO Auto-generated method stub
			
		}
		
	};
	//初始化会议收藏监听
	collectvolleyListener= new VolleyListenerInterface(MeetingDetailAc.this){
		@Override
		public void onMySuccess(String result) {
			// TODO Auto-generated method stub
			try {
				JSONObject myJsonObject = new JSONObject(result);
				String rest = myJsonObject.getString("re_st");
				if (rest.equals("success")) {
					if (collectStatus.equals("1")) {
						collectView.setImageDrawable(getResources().getDrawable(R.drawable.icon_my_collect));
						collecttxtView.setText("已收藏");
					}else {
						collectView.setImageDrawable(getResources().getDrawable(R.drawable.icon_meeting_col));
						collecttxtView.setText("收藏");
					}
				}
				UIHelper.ToastMessage(MeetingDetailAc.this, myJsonObject.getString("re_info"));
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		@Override
		public void onMyError(VolleyError error) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
		HttpFactory.Meeting_Detail(mid, volleyListener);
	}
	
	  public void close_Click(View v){
		  finish();
	  }
	/*
	 * 初始化各栏目数据项
	 */
	private void initAllPagerData() {
		backimg = (ImageView)findViewById(R.id.main_head_logo);
		headText = (TextView)findViewById(R.id.main_head_title);
		headText.setText("会议通知");
		backimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		//会议通知
    	webView = (WebView)viewMeg.findViewById(R.id.webview);
    	//设置WebView属性，能够执行Javascript脚本  
    	webView.getSettings().setJavaScriptEnabled(true);  
   		//设置Web视图  
    	webView.setWebViewClient(new HelloWebViewClient ()); 
		
    	upView = (ImageView)findViewById(R.id.img_join);
    	collectView = (ImageView)findViewById(R.id.img_get);
    	refuseView = (ImageView)findViewById(R.id.img_refuse);
		upView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				MeetingInfo m = meets.
//				Boolean hasConflict = false;
//				for(MeetingInfo m : AppContext.getInstance().getEventmeetingBuffer()){
//					if(m.getAlertbeforetime() == null){
//						continue;
//					}
//					//当前日期（单位：秒）
//					String nowDateStr = DateTimeHelper.getDateNow();
//					Date nowDate = DateTimeHelper.DayStringToDate(nowDateStr);
////									Calendar cal = Calendar.getInstance(); 
////									cal.set(nowDate.getYear(), nowDate.getMonth(), nowDate.getDay(),0,0,0);
//					long nowDateSeconds = nowDate.getTime()/1000L;
//					//当前时间（单位：秒）
//					int nowTimeSeconds = nowDate.getHours() * 24 * 60 * 60 + nowDate.getMinutes() * 60 + nowDate.getSeconds();
//					
//					//个人事件结束日期
//					long personalEventEndDate = Long.parseLong(m.getEnd_date());
//					//个人事件结束时间 
//					int persongalEventEndTime = Integer.parseInt(m.getEnd_time());
//					
//					//会议开始日期
//					long eventStartDate = Long.parseLong(mi.getStart_date());
//					//会议开始时间
//					int eventStartTime = Integer.parseInt(mi.getStart_time());
//					//会议结束日期
//					long eventEndDate = Long.parseLong(mi.getEnd_date());
//					//会议结束时间
//					int eventEndTime = Integer.parseInt(mi.getEnd_time());
//					
//					if(nowDateSeconds <= eventEndDate && personalEventEndDate >= eventStartDate && nowTimeSeconds <= eventEndTime && persongalEventEndTime >= eventStartTime){
//						hasConflict = true;
//						new MyDialog(MeetingDetailAc.this, R.style.MyDialog, "有个人事件与该会议事件重叠，您确定要报名?", "确定", "取消",new MyDialog.DialogClickListener() {
//
//							@Override
//							public void onRightBtnClick(Dialog dialog) {
//								// TODO Auto-generated method stub
//								dialog.dismiss();
//							}
//
//							@Override
//							public void onLeftBtnClick(Dialog dialog) {
//								HttpFactory.Set_Join_Status(mi.getId(), "1", joinStatusvolleyListener);
//								dialog.dismiss();
//							}
//						}).show();
//					}
//				}
//				if(!hasConflict){
//					HttpFactory.Set_Join_Status(mi.getId(), "1", joinStatusvolleyListener);
//				}
//						
//			}
//				
//		});	
				
				new MyDialog(MeetingDetailAc.this, R.style.MyDialog, "您确定要报名?", "确定", "取消",
						new MyDialog.DialogClickListener() {

							@Override
							public void onRightBtnClick(Dialog dialog) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}

							@Override
							public void onLeftBtnClick(Dialog dialog) {
								// TODO Auto-generated method stub
								HttpFactory.Set_Join_Status(mid, "1", joinStatusvolleyListener);
								joinStatus = "1";
								dialog.dismiss();
							}
						}).show();
			}
		});
		
		
		collectView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (collectStatus !=null) {
					// TODO Auto-generated method stub
					if (collectStatus.equals("2")) {
						HttpFactory.SetMeetingCollect(mid, "1",
								collectvolleyListener);
						collectStatus = "1";
					} else {
						HttpFactory.SetMeetingCollect(mid, "2",
								collectvolleyListener);
						collectStatus = "2";
					}
				}else {
					HttpFactory.SetMeetingCollect(mid, "1",
							collectvolleyListener);
					collectStatus = "1";
				}

			}
		});
		
		refuseView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new MyDialog(MeetingDetailAc.this, R.style.MyDialog, "您确定要拒绝?", "确定", "取消",
						new MyDialog.DialogClickListener() {

							@Override
							public void onRightBtnClick(Dialog dialog) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}

							@Override
							public void onLeftBtnClick(Dialog dialog) {
								// TODO Auto-generated method stub
								HttpFactory.Set_Join_Status(mid, "3", joinStatusvolleyListener);
								joinStatus = "3";
								dialog.dismiss();
							}
						}).show();
			}
		});
    	
    	uptxtView = (TextView)findViewById(R.id.meeting_join);
    	collecttxtView = (TextView)findViewById(R.id.meeting_get);
    	refusetxtView = (TextView)findViewById(R.id.meeting_refuse);
		//会议日程
//		List<String> strings = new ArrayList<String>();
//		strings.add("大会签到");
//		strings.add("大会签到");
//		strings.add("大会签到");
//		for (int i = 0; i < 5; i++) {
//			MeetingPlanBean plan = new MeetingPlanBean();
//			plan.setTime("2015-11-27 上午 （12:00-13：00）");
//			for (int j = 0; j < 2; j++) {
//				MeetingDetailPlanBean dePlan = new MeetingDetailPlanBean();
//				dePlan.setDetime("07:00-07:35");
//				dePlan.setPlans(strings);
//				plan.getDetails().add(dePlan);
//			}
//			plans.add(plan);
//		}
		planlistview = (ExpandableListView)viewPlan.findViewById(R.id.meeing_plan_listview);
		planAdapter = new MeetingPlanAdapter(this, plans, R.layout.listitem_faomeetplan, R.layout.item_meeting_plan);
		planlistview.setAdapter(planAdapter);
		planlistview.setGroupIndicator(null);
		//会议嘉宾
//		for (int i = 0; i < 10; i++) {
//			guest gu = new guest();
//			gu.setHonored_guest("蒋一峰");
//			gu.setHonored_guest_describe("中国医疗器械行业副会长");
//			guests.add(gu);
//		}
		guestlistview =(ListView)viewGuest.findViewById(R.id.meeting_tab_listview);
		guestAdapter = new MeetingGuestAdapter(MeetingDetailAc.this,guests,R.layout.listitem_meeting_people);
		guestlistview.setAdapter(guestAdapter);
	}
	
	
	  public void up_Click(View v){
		  upView.performClick();
	  }
	
	  public void col_Click(View v){
		  collectView.performClick();
	  }
	  
	  public void refuse_Click(View v){
		  refuseView.performClick();
	  }
	   //Web视图  
    private class HelloWebViewClient extends WebViewClient {  
        @Override 
        public boolean shouldOverrideUrlLoading(WebView view, String url) {  
           view.loadUrl(url);  
           return true;  
        }  
   }
	
	/**
	 * 初始化ViewPager
	 */
	private void initViewPager() {
		mTabPager = (ViewPager) findViewById(R.id.Meeting_Tabpager);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());

		txtTab1 = (TextView) findViewById(R.id.meeting_mes);
		txtTab2 = (TextView) findViewById(R.id.meeting_shc);
		// txtTab3 = (TextView) findViewById(R.id.temptask_tab_recorder);
		txtTab4 = (TextView) findViewById(R.id.meeting_peo);

		txtTab1.setOnClickListener(new MyOnClickListener(0));
		txtTab2.setOnClickListener(new MyOnClickListener(1));
		// txtTab3.setOnClickListener(new MyOnClickListener(2));
		txtTab4.setOnClickListener(new MyOnClickListener(2));

		ll_Tab1 = (LinearLayout) findViewById(R.id.meeting_tab_mes);
		ll_Tab2 = (LinearLayout) findViewById(R.id.meeting_tab_shc);
		// ll_Tab3 = (LinearLayout) findViewById(R.id.ll_temptask_tab_recorder);
		ll_Tab4 = (LinearLayout) findViewById(R.id.meeting_tab_peo);

		ll_Tab1.setOnClickListener(new MyOnClickListener(0));
		ll_Tab2.setOnClickListener(new MyOnClickListener(1));
		// ll_Tab3.setOnClickListener(new MyOnClickListener(2));
		ll_Tab4.setOnClickListener(new MyOnClickListener(2));

		Display currDisplay = getWindowManager().getDefaultDisplay();// 获取屏幕当前分辨率
		int displayWidth = currDisplay.getWidth();
		int displayHeight = currDisplay.getHeight();
		one = displayWidth / 4; // 设置水平动画平移大小
		two = one * 2;
		three = one * 3;
		// Log.i("info", "获取的屏幕分辨率为" + one + two + three + "X" + displayHeight);

		// InitImageView();//使用动画
		// 将要分页显示的View装入数组中
		LayoutInflater mLi = LayoutInflater.from(this);
		viewMeg = mLi.inflate(R.layout.meeting_tab_meg, null);
		viewPlan = mLi.inflate(R.layout.meeting_tab_she, null);
		viewGuest = mLi.inflate(R.layout.meeting_tab_people, null);

		// 每个页面的view数据
		final ArrayList<View> views = new ArrayList<View>();
		views.add(viewMeg);
		views.add(viewPlan);
		views.add(viewGuest);
		// 填充ViewPager的数据适配器
		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			// @Override
			// public CharSequence getPageTitle(int position) {
			// return titles.get(position);
			// }

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		};
		mTabPager.setAdapter(mPagerAdapter);
		mTabPager.setCurrentItem(0);//
	}
	/*
	 * 页卡切换监听(原作者:D.Winter)
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			changeViewPager(arg0);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
	/**
	 * ViewPager图标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mTabPager.setCurrentItem(index);
		}
	};
	/**
	 * 切换Page
	 */
	private void changeViewPager(int arg0) {

		switch (arg0) {
		case 0:// 描述
			ll_Tab1.setEnabled(true);
			ll_Tab2.setEnabled(false);
			ll_Tab4.setEnabled(false);

			txtTab1.setTextColor(Color.RED);
			txtTab2.setTextColor(Color.BLACK);
			txtTab4.setTextColor(Color.BLACK);
			break;
		case 1:// 拍照
			ll_Tab1.setEnabled(false);
			ll_Tab2.setEnabled(true);
			ll_Tab4.setEnabled(false);

			txtTab1.setTextColor(Color.BLACK);
			txtTab2.setTextColor(Color.RED);
			// txtTab3.setTextColor(Color.BLACK);
			txtTab4.setTextColor(Color.BLACK);

			break;

		 
		case 2:// 历史
			ll_Tab1.setEnabled(false);
			ll_Tab2.setEnabled(false);
			ll_Tab4.setEnabled(true);

			txtTab1.setTextColor(Color.BLACK);
			txtTab2.setTextColor(Color.BLACK);
			txtTab4.setTextColor(Color.RED);
			break;
		}
		currIndex = arg0;


	}

}

