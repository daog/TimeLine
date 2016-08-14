package com.timeline.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.timeline.app.AppContext;
import com.timeline.calendar.CalendarUtils;
import com.timeline.common.DateTimeHelper;
import com.timeline.common.ZoomOutPageTransformer;
import com.timeline.interf.FragmentCallBack;
import com.timeline.main.R;
import com.timeline.ui.Main;
import com.timeline.widget.DirectionalViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WeekPageFragment extends Fragment {

	private DirectionalViewPager mWeeksViewPager;
	
	//当前选择的一周，每天的日期字符串列表
	private static List<String> currentWeekDateStrs = new ArrayList<String>();
	
	private int lastitem;
	
	private int mposition = 0;
	//日期联动Handler
	Handler mWeekRefreshDateHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				//刷新日期后进行请求
				try {
					Date selectedDateTime = AppContext.CurrentSelectedDate;
					Date selectedDate = DateTimeHelper.DayStringToDate(DateTimeHelper.DateToString(selectedDateTime, "yyyy-MM-dd"));
					
					Date currentDateTime = DateTimeHelper.GetDateTimeNow();
					Date currentDate = DateTimeHelper.StringToDate(DateTimeHelper.DateToString(currentDateTime, "yyyy-MM-dd"));
					
					Calendar calendar = Calendar.getInstance();
			        int currentDayForWeek =  calendar.get(Calendar.DAY_OF_WEEK);
			        
			        calendar.setTime(selectedDate);
			        int selectedDayForWeek =  calendar.get(Calendar.DAY_OF_WEEK);
			        mposition = selectedDayForWeek;
			        
			        int daysDiff = (int)((selectedDate.getTime() - currentDate.getTime())/(24*60*60*1000));
					if(daysDiff > 0 ){
						daysDiff++;
					}
//			        if(daysDiff < 0){
//			        	daysDiff--;
//			        }
					int daysDiffWholeWeek =  daysDiff - selectedDayForWeek + currentDayForWeek ;
					int weeksDiff = daysDiffWholeWeek/7;
					
					
//					ScreenSlidePagerAdapter screenSlidePagerAdapter = new ScreenSlidePagerAdapter(
//							getActivity().getSupportFragmentManager());
					
					lastitem = 500 + weeksDiff;
//					mWeeksViewPager.removeAllViews();
//					mWeeksViewPager.setAdapter(screenSlidePagerAdapter);
					mWeeksViewPager.setCurrentItem(lastitem);
					
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
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
	
	FragmentCallBack fragmentCallBack = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_week_page, null);
		//initValues();
		fragmentCallBack = (Main)getActivity();
		initViews(view);
		initData();
		AppContext.getInstance().mWeekRefreshDateHandler = mWeekRefreshDateHandler;
		return view;
	}
	private void initViews(View view) {
		// TODO Auto-generated method stub
		mWeeksViewPager = (DirectionalViewPager) view
				.findViewById(R.id.weeks_page);
		mWeeksViewPager.setOrientation(DirectionalViewPager.VERTICAL);
		final ScreenSlidePagerAdapter screenSlidePagerAdapter = new ScreenSlidePagerAdapter(
				getActivity().getSupportFragmentManager());
		
		mWeeksViewPager.setAdapter(screenSlidePagerAdapter);
		//mWeeksViewPager.setOffscreenPageLimit(0);//“3”代表的是：加载的页数

		lastitem = CalendarUtils.CURRENT_PAGENO;
		mWeeksViewPager.setCurrentItem(CalendarUtils.CURRENT_PAGENO);

		mWeeksViewPager
		.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				List<String> DateStrs =	CalendarUtils.getInstance().getSelectedWeek(position, currentWeekDateStrs);
				fragmentCallBack.callbackFun3(DateStrs.get(0)+"-"+DateStrs.get(6));
				
				String[] firstDayStrs = DateStrs.get(mposition-1).split("-");
				AppContext.CurrentSelectedDate.setYear(Integer.parseInt(firstDayStrs[0]) - 1900);
				AppContext.CurrentSelectedDate.setMonth(Integer.parseInt(firstDayStrs[1]) - 1);
				AppContext.CurrentSelectedDate.setDate(Integer.parseInt(firstDayStrs[2]));
			}

			@Override
			public void onPageScrolled(int position,
					float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	private void initData() {
		// TODO Auto-generated method stub
		currentWeekDateStrs = getCurrentWeekDateStr();
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
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return WeekFragment.create(position);
		}

		@Override
		public int getCount() {
			return 1000;
		}
	     @Override  
	            public void destroyItem(ViewGroup container, int position, Object object) {  
	                 System.out.println( "position Destory" + position);  
	                 super.destroyItem(container, position, object);  
	             }  

	}
}
