package com.timeline.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.timeline.calendar.CalendarUtils;
import com.timeline.common.DateTimeHelper;
import com.timeline.common.ZoomOutPageTransformer;
import com.timeline.main.R;
import com.timeline.ui.Main;
import com.timeline.widget.DirectionalViewPager;

import android.os.Bundle;
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
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_week_page, null);
		//initValues();
		initViews(view);
		initData();
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
