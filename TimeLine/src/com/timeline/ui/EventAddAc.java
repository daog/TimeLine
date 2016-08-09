package com.timeline.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.timeline.app.AppContext;
import com.timeline.bean.MeetingInfo;
import com.timeline.common.DateTimeHelper;
import com.timeline.common.StringUtils;
import com.timeline.common.UIHelper;
import com.timeline.main.R;
import com.timeline.slidedatetimepicker.SlideDateTimeListener;
import com.timeline.slidedatetimepicker.SlideDateTimePicker;
import com.timeline.sqlite.InfoHelper;

public class EventAddAc extends BaseActivity{

	private SimpleDateFormat currentFormatter = new SimpleDateFormat("MM月dd号 EEEE HH:mm");
	private SimpleDateFormat dFormatter = new SimpleDateFormat("MM月dd号 EEEE");
	private SimpleDateFormat tFormatter = new SimpleDateFormat("MM月dd号 EEEE HH:mm");
	private SimpleDateFormat dformat=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat tformat=new SimpleDateFormat("HH:mm:ss");
	
	private TextView headView;
	private TextView beginText;
	private TextView endText;
	
	private TextView repeatText;
	private RadioOnClick OnClick = new RadioOnClick(1);
	private AlertTimeRadioOnClick alertTimeRadioOnClick = new AlertTimeRadioOnClick(0);
	private ListView areaListView;
	
	private EditText titleEd;
	private EditText addEd;
	private EditText remarkEd;
	
	//全天选择按钮
	private ToggleButton wholeDayToggleButton;
	//开始时间
	private Date beginDate ;
	//结束时间
	private Date endDate ;
	//提前xx分钟提醒
	private TextView alertValTv;
	
	private ImageView close;
	private MeetingInfo info = new MeetingInfo();
	private Date alertbetime;
	
	//提醒时间选项
	private String[] alertTimes = new String[]{"不提前","提前5分钟", 
			"提前15分钟", "提前30分钟", "提前1小时","提前1天" };
	
	//重复选项
	private String[] repites = new String[]{"不重复","每天", 
			"每周", "每月", "每年" };
	//编辑的活动
	private MeetingInfo editMeetingInfo = null;
	
	InfoHelper infohelper = new InfoHelper();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        editMeetingInfo = (MeetingInfo)bundle.getSerializable("meetingInfo");
        
        initView();
        List<MeetingInfo> info = infohelper.loadInfo(EventAddAc.this);
        
        if(editMeetingInfo!= null){
            SetData(editMeetingInfo);
        }
        else{
        	Date nowDate = DateTimeHelper.GetDateTimeNow();

        	endText.setText(currentFormatter.format(nowDate));
        	beginText.setText(currentFormatter.format(nowDate));
        }
    }
    
    //初始化界面参数
    private void SetData(MeetingInfo mi) {
    	titleEd.setText(mi.getSubject());
    	Long startDateLong = Long.valueOf(mi.getStart_date());
    	Long endDateLong = Long.valueOf(mi.getEnd_date());
    	int startTime = Integer.valueOf(mi.getStart_time());
    	int endTime = Integer.valueOf(mi.getEnd_time());
    	
    	beginDate = new Date((startDateLong + startTime)*1000);
    	endDate = new Date((endDateLong + endTime)*1000);
    	alertbetime = beginDate;
    	
    	String startTimeStr = DateTimeHelper.int2TimeHHmmss(Integer.valueOf(mi.getStart_time()));
    	String endTimeStr = DateTimeHelper.int2TimeHHmmss(Integer.valueOf(mi.getEnd_time()));
    	if(startTimeStr.equals("00:00:00") && endTimeStr.equals("23:59:59")){
    		wholeDayToggleButton.setChecked(true);
    		wholeDayTbClick(wholeDayToggleButton);
    	}
    	else{
    		wholeDayToggleButton.setChecked(false);
    		wholeDayTbClick(wholeDayToggleButton);
    	}
    	
    	if(!StringUtils.isEmpty(mi.getRepeate())){
    		repeatText.setText(mi.getRepeate());
    	}
    	
    	addEd.setText(mi.getAddress());
    	remarkEd.setText(mi.getDescribe());
		
	}

	private void initView(){
    	beginText =(TextView)findViewById(R.id.event_begintime);
    	endText =(TextView)findViewById(R.id.event_endtime);
    	repeatText = (TextView)findViewById(R.id.event_repeat_modeval);
    	titleEd = (EditText)findViewById(R.id.event_title);
    	addEd = (EditText)findViewById(R.id.event_addtext);
    	remarkEd = (EditText)findViewById(R.id.event_remarkval);
    	close = (ImageView)findViewById(R.id.event_head_cancel);
    	alertValTv = (TextView)findViewById(R.id.event_alertval);
    	headView = (TextView)findViewById(R.id.event_head_title);
    	headView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
    	alertValTv.setOnClickListener(new AlertValClickListener());
    	close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
    	
    	wholeDayToggleButton = (ToggleButton)findViewById(R.id.event_wholeDayTb);
    	wholeDayToggleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ToggleButton wholeDayTb = (ToggleButton)v;
				wholeDayTbClick(wholeDayTb);
			}
		});
    }
	
	//提前xx分钟提醒
	private class AlertValClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			 AlertDialog ad =new AlertDialog.Builder(EventAddAc.this).setTitle("选择提醒时间")
	    			    .setSingleChoiceItems(alertTimes,alertTimeRadioOnClick.getIndex(), alertTimeRadioOnClick).create();
	    			    areaListView=ad.getListView();
	    			    ad.show();
		}
		
	}
	
	//全天按钮点击事件
	private void wholeDayTbClick(ToggleButton wholeDayTb) {
		if(wholeDayTb.isChecked()){
			currentFormatter = dFormatter;
			if(beginDate != null){
				beginDate.setHours(0);
				beginDate.setMinutes(0);
				beginDate.setSeconds(0);
				
				beginText.setText(DateTimeHelper.DateToString(beginDate,"MM月dd号 EEEE"));
			}
			
			if(endDate != null){
				endDate.setHours(23);
				endDate.setMinutes(59);
				endDate.setSeconds(59);
				
				endText.setText(DateTimeHelper.DateToString(endDate,"MM月dd号 EEEE"));
			}
		}
		else{
			currentFormatter = tFormatter;
			if(beginDate != null){
				beginText.setText(DateTimeHelper.DateToString(beginDate,"MM月dd号 EEEE HH:mm"));
			}
			
			if(endDate != null){
				endText.setText(DateTimeHelper.DateToString(endDate,"MM月dd号 EEEE HH:mm"));
			}
			
		}
		
	}
    
    public void Begintime_Click(View v){
        new SlideDateTimePicker.Builder(getSupportFragmentManager())
        .setListener(listener)
        .setInitialDate(new Date())
        .setIs24HourTime(true)
        //.setTheme(SlideDateTimePicker.HOLO_DARK)
        //.setIndicatorColor(Color.parseColor("#990000"))
        .build()
        .show();
    }
    public void Endtime_Click(View v){
        new SlideDateTimePicker.Builder(getSupportFragmentManager())
        .setListener(enlistener)
        .setInitialDate(new Date())
        .setIs24HourTime(true)
        //.setTheme(SlideDateTimePicker.HOLO_DARK)
        //.setIndicatorColor(Color.parseColor("#990000"))
        .build()
        .show();
    }
    
    public void btn_Alert(View v){
    }
    public void btnBack(View v){
	}

	//保存
    public void btnSave(View v){
    	if (endDate == null ||beginDate==null) {
			UIHelper.ToastMessage(EventAddAc.this, "请设置开始结束时间");
			return;
		}
    	if(endDate.before(beginDate)){
    		UIHelper.ToastMessage(EventAddAc.this, "结束时间不能早于开始时间！");
    		return;
    	}
    	
    	if(StringUtils.isEmpty(titleEd.getText().toString().trim())){
			UIHelper.ToastMessage(EventAddAc.this, "活动标题不能为空！");
    		return;
		}
    	
    	if(editMeetingInfo == null){
    		UUID uuid = UUID.randomUUID();
        	info.setSubject(titleEd.getText().toString());
        	info.setAddress(addEd.getText().toString());
        	info.setDescribe(remarkEd.getText().toString());
        	info.setId(uuid.toString());
        	
        	//设置提前提醒分钟字段
        	info.setEdit_TX1(alertValTv.getText().toString());
			int alertBerforeMinutes = getAlertBeforeMinutes(alertValTv
					.getText().toString());
			alertbetime = DateTimeHelper.AddMinutes(alertbetime,
					alertBerforeMinutes);
			info.setAlertbeforetime(DateTimeHelper
					.DateToString(alertbetime));
			
        	infohelper.insertInfo(EventAddAc.this, info);
        	UIHelper.ToastMessage(EventAddAc.this, "保存成功！");
        	finish();
    	}
    	else{
        	editMeetingInfo.setSubject(titleEd.getText().toString());
        	editMeetingInfo.setAddress(addEd.getText().toString());
        	editMeetingInfo.setDescribe(remarkEd.getText().toString());
        	
        	String startSt = DateTimeHelper.DateToString(beginDate,"yyyy-MM-dd");
        	String startStr = String.valueOf(DateTimeHelper.DayStringToDate(startSt).getTime()/1000);
        	editMeetingInfo.setStart_date(startStr);
        	
        	String endSt = DateTimeHelper.DateToString(endDate,"yyyy-MM-dd");
        	String endStr = String.valueOf(DateTimeHelper.DayStringToDate(endSt).getTime()/1000);
        	editMeetingInfo.setEnd_date(endStr);
        	
        	String beginTime = tformat.format(beginDate);
        	String[] beginTimes = beginTime.split(":");
        	int IbeginTime = Integer.valueOf(beginTimes[0])*3600+Integer.valueOf(beginTimes[1])*60+Integer.valueOf(beginTimes[2]);
        	editMeetingInfo.setStart_time(String.valueOf(IbeginTime));
        	
        	String endTime = tformat.format(endDate);
        	String[] endTimes = endTime.split(":");
        	int IendTime = Integer.valueOf(endTimes[0])*3600+Integer.valueOf(endTimes[1])*60+Integer.valueOf(endTimes[2]);
        	editMeetingInfo.setEnd_time(String.valueOf(IendTime));
        	
        	String repeatStr = repeatText.getText().toString();
        	if(repeatStr.equals("不重复")){
        		editMeetingInfo.setRepeate("");
        	}
        	else{
        		editMeetingInfo.setRepeate(repeatStr);
        	}
        	
        	//设置提前提醒分钟字段
        	editMeetingInfo.setEdit_TX1(alertValTv.getText().toString());
			int alertBerforeMinutes = getAlertBeforeMinutes(alertValTv
					.getText().toString());
			alertbetime = DateTimeHelper.AddMinutes(alertbetime,
					alertBerforeMinutes);
			editMeetingInfo.setAlertbeforetime(DateTimeHelper
					.DateToString(alertbetime));
			
			infohelper.updateInfo(EventAddAc.this, editMeetingInfo);
        	UIHelper.ToastMessage(EventAddAc.this, "保存成功！");
    	}
    	AppContext.getInstance().getEventmeetingBuffer();
    	finish();
    }
    
    //获取提前提醒的分钟数
    private int getAlertBeforeMinutes(String alertTime){
    	if(alertTime.equals("不提前")){
    		return 0;
    	}
    	else if(alertTime.equals("提前5分钟")){
    		return -5;
    	}
    	else if(alertTime.equals("提前15分钟")){
    		return -15;
    	}
    	else if(alertTime.equals("提前30分钟")){
    		return -30;
    	}
    	else if(alertTime.equals("提前1小时")){
    		return -60;
    	}
    	else if(alertTime.equals("提前1天")){
    		return -24*60;
    	}
    	return 0;
    }
    
    public void close_Click(View v){
    	finish();
   }
    
    public void save_Click(View v){
    	btnSave(v);
   }
    
    public void Repeat_Click(View v){
    	 AlertDialog ad =new AlertDialog.Builder(EventAddAc.this).setTitle("选择重复项")
    			    .setSingleChoiceItems(repites,OnClick.getIndex(),OnClick).create();
    			    areaListView=ad.getListView();
    			    ad.show();
    }
    
    class RadioOnClick implements DialogInterface.OnClickListener{
    	   private int index;
    	 
    	   public RadioOnClick(int index){
    	    this.index = index;
    	   }
    	   public void setIndex(int index){
    	    this.index=index;
    	    repeatText.setText(repites[index]);
    	   }
    	   public int getIndex(){
    	    return index;
    	   }
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
		     setIndex(which);
		     info.setRepeate(repites[index]);
		     Toast.makeText(EventAddAc.this, "您已经选择了 " +  ":" + repites[index], Toast.LENGTH_SHORT).show();
		     dialog.dismiss();
		}
    }
    
    class AlertTimeRadioOnClick implements DialogInterface.OnClickListener{
 	   private int index;
 	 
 	   public AlertTimeRadioOnClick(int index){
 	    this.index = index;
 	   }
 	   public void setIndex(int index){
 	    this.index=index;
 	    alertValTv.setText(alertTimes[index]);
 	   }
 	   public int getIndex(){
 	    return index;
 	   }
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
		     setIndex(which);
		     Toast.makeText(EventAddAc.this, "您已经选择了 " +  ":" + alertTimes[index], Toast.LENGTH_SHORT).show();
		     dialog.dismiss();
		}
 }
    
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
        	beginDate = date;
        	Date nowDate = DateTimeHelper.GetDateTimeNow();
        	if(beginDate.before(nowDate)){
        		beginDate = null;
        		UIHelper.ToastMessage(EventAddAc.this, "开始时间不能早于当前时间！");
        		return;
        	}
        	beginText.setText(currentFormatter.format(date));
        	String startSt = DateTimeHelper.DateToString(date,"yyyy-MM-dd");
        	String startStr = String.valueOf(DateTimeHelper.DayStringToDate(startSt).getTime()/1000);
        	info.setStart_date(startStr);
        	String time = tformat.format(date);
        	String[] times = time.split(":");
        	int Itime = Integer.valueOf(times[0])*3600+Integer.valueOf(times[1])*60+Integer.valueOf(times[2]);
        	info.setStart_time(String.valueOf(Itime));
        	alertbetime = date; 
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {

        }
    };
    private SlideDateTimeListener enlistener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
        	endDate = date;
        	Date nowDate = DateTimeHelper.GetDateTimeNow();
        	if(endDate.before(nowDate)){
        		UIHelper.ToastMessage(EventAddAc.this, "开始时间不能早于当前时间！");
        		endDate =null;
        		return;
        	}
        	
        	endText.setText(currentFormatter.format(date));
        	String endSt = DateTimeHelper.DateToString(date,"yyyy-MM-dd");
        	String endStr = String.valueOf(DateTimeHelper.DayStringToDate(endSt).getTime()/1000);
        	info.setEnd_date(endStr);
        	String time = tformat.format(date);
        	String[] times = time.split(":");
        	int Itime = Integer.valueOf(times[0])*3600+Integer.valueOf(times[1])*60+Integer.valueOf(times[2]);
        	info.setEnd_time(String.valueOf(Itime));
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {

        }
    };
}
