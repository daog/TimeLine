package com.timeline.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.timeline.common.UIHelper;
import com.timeline.main.R;

public class ClassifyMeeting extends BaseActivity{

	private ImageButton close;
	  @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_meetclassify);
			//透明状态栏
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	        //透明导航栏
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	        close = (ImageButton)findViewById(R.id.id_classifyMeetingsback);
	        close.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
	        
	  }
	  //1已报名 2待定 3拒绝
	  //报名
	  public void btn_up(View v) {
		UIHelper.showHistory("1", ClassifyMeeting.this);
	  }
	  //待定
	  public void btn_undeside(View v) {
		  UIHelper.showHistory("2", ClassifyMeeting.this);
	  }
	  //拒绝
	  public void btn_refuses(View v) {
		  UIHelper.showHistory("3", ClassifyMeeting.this);
	  }
}
