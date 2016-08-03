package com.timeline.ui;

import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.timeline.app.AppContext;
import com.timeline.bean.MeetingInfo;
import com.timeline.common.DateTimeHelper;
import com.timeline.common.UIHelper;
import com.timeline.main.R;

public class EventDeAc extends BaseActivity{

	private TextView beginView;
	private TextView endView;
	private TextView repeatvView;
	private TextView addView;
	private TextView alertView;
	private TextView msgView;
	
	private String id;
	private MeetingInfo info;
	//开始时间
	private Date beginDate = new Date(116,4,3,12,0,0);
	//结束时间
	private Date endDate = new Date(116,4,3,17,0,0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_de);
        initView();
		Bundle bundle = getIntent().getExtras();
		id = bundle.getString("id");
		for (int i = 0; i < AppContext.EventmeetingBuffer.size(); i++) {
			if (AppContext.EventmeetingBuffer.get(i).getId().equals(id)) {
				info = AppContext.EventmeetingBuffer.get(i);
			}
		}
	   	Long startDateLong = Long.valueOf(info.getStart_date());
    	Long endDateLong = Long.valueOf(info.getEnd_date());
    	int startTime = Integer.valueOf(info.getStart_time());
    	int endTime = Integer.valueOf(info.getEnd_time());
    	
    	beginDate = new Date((startDateLong + startTime)*1000);
    	endDate = new Date((endDateLong + endTime)*1000);
		beginView.setText(DateTimeHelper.DateToString(beginDate,"MM月dd号 EEEE"));
		endView.setText(DateTimeHelper.DateToString(endDate,"MM月dd号 EEEE"));
		repeatvView.setText(info.getRepeate());
		addView.setText(info.getAddress());
		msgView.setText(info.getDescribe());
    }
    public void btnBack(View v) {
		finish();
	}
    
    public void btnEdit(View v) {
		UIHelper.showNewEvent(EventDeAc.this, info);
	}
    
    private void initView(){
    	beginView = (TextView)findViewById(R.id.event_begintime);
    	endView = (TextView)findViewById(R.id.event_endtime);
    	repeatvView = (TextView)findViewById(R.id.event_repeat_modeval);
    	addView = (TextView)findViewById(R.id.event_addtext);
    	alertView = (TextView)findViewById(R.id.event_alertval);
    	msgView = (TextView)findViewById(R.id.event_remarkval);
    }
}
