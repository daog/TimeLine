package com.timeline.controls;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.timeline.app.AppContext;
import com.timeline.bean.MeetingInfo;
import com.timeline.common.DateTimeHelper;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.LunarCalendar;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.webapi.HttpFactory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MonthDateView extends View {
	private static final int NUM_COLUMNS = 7;
	private static final int NUM_ROWS = 6;
	private Paint mPaint;
	private int mDayColor = Color.parseColor("#000000");
	private int mSelectDayColor = Color.parseColor("#ffffff");
	private int mSelectBGColor = Color.parseColor("#3b5fc3");
	private int mCurrentColor = Color.parseColor("#ff0000");
	private int mCurrYear,mCurrMonth,mCurrDay;
	private int mSelYear,mSelMonth,mSelDay;
	private int mColumnSize,mRowSize;
	private DisplayMetrics mDisplayMetrics;
	private int mDaySize = 18;
	private TextView tv_month;
	private int weekRow;
	private int [][] daysString;
	private int mCircleRadius = 6;
	private DateClick dateClick;
	private int mCircleColor = R.color.blue;;
	private int widthMeasure, heightMeasure;
	
	//private List<DayMeetingNumAndState> dayMeetingNumAndStateList;
	private List<MeetingInfo> monthMeetingInfoList;
	
	private LunarCalendar lc;
	private Canvas mcanvas;
	public MonthDateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		lc = new LunarCalendar();
		mDisplayMetrics = getResources().getDisplayMetrics();
		Calendar calendar = Calendar.getInstance();
		mPaint = new Paint();
		mCurrYear = calendar.get(Calendar.YEAR);
		mCurrMonth = calendar.get(Calendar.MONTH);
		mCurrDay = calendar.get(Calendar.DATE);
		setSelectYearMonth(mCurrYear,mCurrMonth,mCurrDay);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		widthMeasure = widthMeasureSpec; 
		heightMeasure = heightMeasureSpec;
		super.onMeasure(widthMeasure, heightMeasure);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		initSize();
		mcanvas = canvas;
		daysString = new int[6][7];
		mPaint.setTextSize(mDaySize*mDisplayMetrics.scaledDensity/2);
		String dayString;
		int mMonthDays = DateUtils.getMonthDays(mSelYear, mSelMonth);
		int weekNumber = DateUtils.getFirstDayWeek(mSelYear, mSelMonth);
		//int showWeekNum = (weekNumber + 6)%7 == 0 ? 7 : (weekNumber + 6)%7;


		for(int day = 0;day < mMonthDays;day++){
			String lunarDayStr = getLunarDay(mSelYear, mSelMonth + 1, day+1);
//			dayString = (day + 1)+"";// + "\n" + getLunarDay(day+1, mSelMonth + 1, mSelYear + 1);
//			int column = (day+showWeekNum - 1) % 7;
//			int row = (day+ showWeekNum - 1) / 7;
			dayString = (day + 1) + "";
			int column = (day+weekNumber - 1) % 7;
			int row = (day+weekNumber - 1) / 7;
			if(column == 6 || column == 0)
			{
				mPaint.setColor(Color.GRAY);
				canvas.drawLine( 0, mRowSize * (row), mColumnSize* 7,mRowSize * (row) , mPaint);
			}
			
			daysString[row][column]=(day + 1);
			int startX = (int) (mColumnSize * column + (mColumnSize - mPaint.measureText(dayString))/2);
			int startY = (int) (mRowSize * row + mRowSize/2 - (mPaint.descent() - mPaint.ascent())*0.5);
			//int startY = (int) (mRowSize * row + mRowSize/10 );
			int lunarStartX = (int) (mColumnSize * column + (mColumnSize - mPaint.measureText(lunarDayStr))/2);
			//int lunarStartY = (int) (mRowSize * row + mRowSize/10 + mPaint.measureText(dayString));
			int lunarStartY = (int) (mRowSize * row + mRowSize/2 - (mPaint.ascent() + mPaint.descent())*0.7);
			if(dayString.equals(mSelDay+"")){
				float startCircBgX = mColumnSize * column + mColumnSize/2;
				float startCircBgY = (float)(mRowSize * row + mRowSize/2*0.9);
				
				//绘制选中的日期的背景色
				mPaint.setColor(mSelectBGColor);
				float radius = (float)((mPaint.measureText("22")/2 + mPaint.measureText("廿九"))/2*1.1);

				canvas.drawCircle(startCircBgX+5, startCircBgY, radius+3, mPaint);
				
				weekRow = row + 1;
			}

			setmCircleRadius(mRowSize/20);
			
			String dateStr = mSelYear + "-" + String.format("%02d", mSelMonth + 1) + "-" + String.format("%02d", day +1);
			drawCircle(row,column,dateStr,canvas);
			if(dayString.equals(mSelDay+"")){
				mPaint.setColor(mSelectDayColor);
			}else if(dayString.equals(mCurrDay+"") && mCurrDay != mSelDay && mCurrMonth == mSelMonth){
				mPaint.setColor(mCurrentColor);
			}else{
				mPaint.setColor(mDayColor);
			}
			mPaint.setTextSize(45);
			if (dayString.length()==1) {
				canvas.drawText(dayString, startX, startY+10, mPaint);
			}else {
				canvas.drawText(dayString, startX-5, startY+10, mPaint);
			}
			
			mPaint.setTextSize(26);
			canvas.drawText(lunarDayStr, lunarStartX+5, lunarStartY+15, mPaint);
			if(tv_month != null){
				tv_month.setText( getmSelMonthText(mSelYear, mSelMonth));
			}
//			if(tv_week != null){
//				tv_week.setText("第" + weekRow  +"周");
//			}
		}
	}
	private CharSequence getmSelMonthText(int mSelectedYear, int mSelectedMonth) {
		switch (mSelectedMonth) {
		case 0:
			return mSelectedYear + "年一月" ;
		case 1:
			return mSelectedYear + "年二月" ;
		case 2:
			return mSelectedYear + "年三月" ;

		case 3:
			return mSelectedYear + "年四月" ;

		case 4:
			return mSelectedYear + "年五月" ;

		case 5:
			return mSelectedYear + "年六月" ;

		case 6:
			return mSelectedYear + "年七月" ;

		case 7:
			return mSelectedYear + "年八月" ;

		case 8:
			return mSelectedYear + "年九月" ;

		case 9:
			return mSelectedYear + "年十月" ;

		case 10:
			return mSelectedYear + "年十一月" ;

		case 11:
			return mSelectedYear + "年十二月" ;
		}
		return null;
	}

	private void drawCircle(int row,int column,String dateStr,Canvas canvas){
		if(monthMeetingInfoList != null && monthMeetingInfoList.size() > 0){
			List<MeetingInfo> drawDayMeetingInfos = new ArrayList<MeetingInfo>();
			for(MeetingInfo mi : monthMeetingInfoList){
				if (mi.getEndDate()!=null&&mi.getStartDate()!=null) {
					if (DateTimeHelper.isInDate(dateStr, mi.getStartDate(),
							mi.getEndDate())) {
						drawDayMeetingInfos.add(mi);
					}
				}
			}
			
			int i = 1;
			for (MeetingInfo mi : drawDayMeetingInfos) {
				if (mi.getAlertbeforetime()!=null) {
					mPaint.setColor(getResources().getColor(R.color.week_red));
				} else {
					mPaint.setColor(getResources().getColor(R.color.tasking));
				}
				
				float circleX = (float) (mColumnSize * column +	i * mColumnSize/(drawDayMeetingInfos.size()+1));
				float circley = (float) (mRowSize * row + mRowSize*0.88);
				canvas.drawCircle(circleX, circley, mCircleRadius, mPaint);
				i++;
			}
		}
		
	}
	@Override
	public boolean performClick() {
		return super.performClick();
	}

	private int downX = 0,downY = 0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int eventCode=  event.getAction();
		switch(eventCode){
		case MotionEvent.ACTION_DOWN:
			downX = (int) event.getX();
			downY = (int) event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			int upX = (int) event.getX();
			int upY = (int) event.getY();
			if(Math.abs(upX-downX) < 10 && Math.abs(upY - downY) < 10){
				performClick();
				doClickAction((upX + downX)/2,(upY + downY)/2);
			}
			break;
		}
		return true;
	}

	/**
	 * 初始化行高和列宽
	 */
	private void initSize(){
		mColumnSize = getWidth() / NUM_COLUMNS;
		mRowSize = getHeight() / NUM_ROWS;
	}
	
	/**
	 * 获取当前月的天数
	 * @param year
	 * @param month
	 */
	private void setSelectYearMonth(int year,int month,int day){
		mSelYear = year;
		mSelMonth = month;
		mSelDay = day;
	}
	/**
	 * 执行点击事件
	 * @param x
	 * @param y
	 */
	private void doClickAction(int x,int y){
		int row = y / mRowSize;
		int column = x / mColumnSize;
		setSelectYearMonth(mSelYear,mSelMonth,daysString[row][column]);
		invalidate();

		if(dateClick != null){
			dateClick.onClickOnDate();
		}
	}

	/**
	 * 上一个月 点击事件
	 */
	public void onLeftClick(){
		int year = mSelYear;
		int month = mSelMonth;
		int day = mSelDay;
		if(month == 0){
			year = mSelYear-1;
			month = 11;
		}else if(DateUtils.getMonthDays(year, month) == day){
			month = month-1;
			day = DateUtils.getMonthDays(year, month);
		}else{
			month = month-1;
		}
		setSelectYearMonth(year,month,day);
		invalidate();
	}
	
	/**
	 * 下一个月 点击事件
	 */
	@SuppressLint("NewApi")
	public void onRightClick(){
		int year = mSelYear;
		int month = mSelMonth;
		int day = mSelDay;
		if(month == 11){
			year = mSelYear+1;
			month = 0;
		}else if(DateUtils.getMonthDays(year, month) == day){
			month = month + 1;
			day = DateUtils.getMonthDays(year, month);
		}else{
			month = month + 1;
		}
		setSelectYearMonth(year,month,day);
		invalidate();
	}
	
	/**
	 * 获取当前选中的年份
	 * @return
	 */
	public int getmSelYear() {
		return mSelYear;
	}
	/**
	 * 获取当前选中的月份
	 * @return
	 */
	public int getmSelMonth() {
		return mSelMonth;
	}
	/**
	 * 获取当前选中的天
	 * @param mSelDay
	 */
	public int getmSelDay() {
		return this.mSelDay;
	}
	/**
	 * 设置日的字体颜色
	 * @param mDayColor
	 */
	public void setmDayColor(int mDayColor) {
		this.mDayColor = mDayColor;
	}
	
	/**
	 * 设置当前选中日的字体颜色
	 * @param mSelectDayColor
	 */
	public void setmSelectDayColor(int mSelectDayColor) {
		this.mSelectDayColor = mSelectDayColor;
	}

	/**
	 * 设置当前选中日的背景颜色
	 * @param mSelectBGColor
	 */
	public void setmSelectBGColor(int mSelectBGColor) {
		this.mSelectBGColor = mSelectBGColor;
	}
	/**
	 * 设置当前颜色
	 * @param mCurrentColor
	 */
	public void setmCurrentColor(int mCurrentColor) {
		this.mCurrentColor = mCurrentColor;
	}

	/**
	 *
	 * @param mDaySize
	 */
	public void setmDaySize(int mDaySize) {
		this.mDaySize = mDaySize;
	}
	/**
	 * 设置日期内容
	 * @param tv_month
	 */
	public void setTextView(TextView tv_month){
		this.tv_month = tv_month;
		invalidate();
	}

	/**
	 * 设置当日事项（即日下边的小点）
	 * @param daysHasThingList
	 */
	public void setMonthMeetingInfoList(List<MeetingInfo> monthMeetingInfoList) {
		this.monthMeetingInfoList = monthMeetingInfoList;
		invalidate();
	}

	/***
	 * 璁设置事项圆点半径
	 * @param mCircleRadius
	 */
	public void setmCircleRadius(int mCircleRadius) {
		this.mCircleRadius = mCircleRadius;
	}
	
	/**
	 * 设置事项圆点颜色
	 * @param mCircleColor
	 */
	public void setmCircleColor(int mCircleColor) {
		this.mCircleColor = mCircleColor;
	}
	
	/**
	 * 日期点击事件
	 * @author
	 *
	 */
	public interface DateClick{
		public void onClickOnDate();
	}

	/**
	 *  设置日期点击事件
	 * @param dateClick
	 */
	public void setDateClick(DateClick dateClick) {
		this.dateClick = dateClick;
	}
	
	/**
	 * 跳转到当前日期
	 */
	public void setTodayToView(){
		setSelectYearMonth(mCurrYear,mCurrMonth,mCurrDay);
		invalidate();
	}
	
	/**
	 * 根据日期的年月日返回阴历日期
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public String getLunarDay(int year, int month, int day) {
		String lunarDay = lc.getLunarDate(year, month, day, true);
		// {由于在取得阳历对应的阴历日期时，如果阳历日期对应的阴历日期为"初一"，就被设置成了月份(如:四月，五月。。。等)},所以在此就要判断得到的阴历日期是否为月份，如果是月份就设置为"初一"
		if (lunarDay.substring(1, 2).equals("月")) {
			lunarDay = "初一";
		}
		return lunarDay;
	}
	
	//每天的会议数和状态
	public class DayMeetingsAndState{
		//当前月的第几天
		public int day;
		//本天的会议数量
		public int meetingNum;
		
	}
	
	public class MeetingAcceptedState{
		//是否报名该会议
		public boolean isAccepted;
	}
}
