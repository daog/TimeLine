package com.timeline.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.R.drawable;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.timeline.adapter.MonthContentListAdapter;
import com.timeline.adapter.PastMeetingAdapter;
import com.timeline.app.AppContext;
import com.timeline.bean.MeetingHisBean;
import com.timeline.bean.ReturnInfo;
import com.timeline.bean.User;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.UIHelper;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.webapi.HttpFactory;

/**
 * 往期会议
 * @author lxb
 *
 */
public class PastMeetingsAc extends BaseActivity {

	private ListView lv_pastMeetings;
	private PastMeetingAdapter adapter;
	private List<Map<String, Object>> pastMeetingsList;
	private VolleyListenerInterface volleyListener;
	
	private String type;
	private ImageView backimg;
	private TextView headText;
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pastmeetings);
		Bundle bundle = getIntent().getExtras();
		type = bundle.getString("type");
		
		//透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        lv_pastMeetings = (ListView)findViewById(R.id.id_pastMeetingsLst);
        
        //测试数据
        pastMeetingsList=new ArrayList<Map<String,Object>>();  
        adapter = new PastMeetingAdapter(this,pastMeetingsList);
        lv_pastMeetings.setAdapter(adapter);
        volleyListener = new VolleyListenerInterface(PastMeetingsAc.this) {

			@Override
			public void onMySuccess(String result) {
				// TODO Auto-generated method stub
				try {
					JSONObject myJsonObject = new JSONObject(result);
					String rest = myJsonObject.getString("re_st");
					if (rest.equals("success")) {
						MeetingHisBean[] hismeetings = JsonToEntityUtils.jsontoMeetingHises(myJsonObject.getString("re_info"));
						getData(hismeetings);
					}else {
						ReturnInfo info = JsonToEntityUtils.jsontoReinfo(result);
						UIHelper.ToastMessage(PastMeetingsAc.this,info.getRe_info() );
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
		headText  = (TextView)findViewById(R.id.id_pastMeetingshead);
		if (type.equals("history")) {
			HttpFactory.MeetingHistory("10", volleyListener);
		}else {
			headText.setText("我的收藏");
			HttpFactory.MeetingCollect(volleyListener);
		}
		
		lv_pastMeetings.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				TextView meetingView = (TextView)v.findViewById(R.id.id_meetingTitle);
				String mid = (String) meetingView.getTag();
				if (mid != null) {
					UIHelper.showMeetingDetail(PastMeetingsAc.this, mid);
				}
			}
		});
		backimg = (ImageView)findViewById(R.id.id_pastMeetingsback);
		backimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	private void getData(MeetingHisBean[] hismeetings) {
		// TODO Auto-generated method stub
        for (MeetingHisBean bean :hismeetings) {  
            Map<String, Object> map=new HashMap<String, Object>();  
            map.put("meetingImage", null);  
            map.put("meetingOrgnizer", bean.getSponsor());  
            map.put("meetingTitle",bean.getSubject());  
            map.put("meetingDate",bean.getStart_date()); 
            map.put("meetingId",bean.getMeeting_id()); 
            pastMeetingsList.add(map);  
        }  
        adapter.notifyDataSetChanged();
	}
	
}
