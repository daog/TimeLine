package com.timeline.ui;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.timeline.adapter.JobDataAdapter;
import com.timeline.app.AppContext;
import com.timeline.common.UIHelper;
import com.timeline.main.R;

public class JobListAc extends BaseActivity {
	
	private ListView lisview;
	private JobDataAdapter adapter;
	private String type;
	private String key;
	
	private ImageView backimg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_infolist);	
		Bundle bundle = getIntent().getExtras();
		type = bundle.getString("type");
		InitView();
		if (type.equals("position")) {
			adapter = new JobDataAdapter(this, AppContext.getInstance().jonbase.getPosition(), 
					R.layout.listitem_job);
		}else if (type.equals("job_title")){
			adapter = new JobDataAdapter(this, AppContext.getInstance().jonbase.getJob_title(), 
					R.layout.listitem_job);
		}else {
			Map<String, String> map = new HashMap<String, String>();
			map.put("1", "ÄÐ");
			map.put("2", "Å®");
			adapter = new JobDataAdapter(this, map, 
					R.layout.listitem_job);
		}

		lisview.setAdapter(adapter);
		lisview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				
				for (int i = 0; i < lisview.getChildCount(); i++) {
					LinearLayout layout = (LinearLayout)lisview.getChildAt(i);
					ImageView ima = (ImageView)layout.findViewById(R.id.selectima);
					ima.setVisibility(View.GONE);
				}
				adapter.setSelectPos(position);
				int po = lisview.getFirstVisiblePosition();
				View v = (View) lisview.getChildAt(position - po);
				ImageView ima = (ImageView)v.findViewById(R.id.selectima);
				TextView nameview  = (TextView)v.findViewById(R.id.listitem_name);
				key = (String) nameview.getTag();
				ima.setVisibility(View.VISIBLE);
				
			}
		});
	}
	
	public void Btn_Save(View v) {
		if (key == null) {
			UIHelper.ToastMessage(JobListAc.this, "ÇëÑ¡Ôñ");
			return;
		}
		UIHelper.showMyInfo(JobListAc.this,type,key);
	}
	
	private void InitView() {
		lisview = (ListView)findViewById(R.id.listview);
		backimg = (ImageView)findViewById(R.id.main_head_logo);
		backimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
}
