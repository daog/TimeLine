package com.timeline.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.timeline.app.AppContext;
import com.timeline.common.UIHelper;
import com.timeline.main.R;
import com.timeline.widget.CircleImageView;

public class MySigninAc extends BaseActivity{

	private CircleImageView headImg;
	private ImageView close;
	
	  @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_my_signin);
	        InitView();
	  }
	  
	  
	  public void SetClick(View v){
		  UIHelper.showSetting(this);
	  }
	  
	  public void Btn_login(View v){
		  UIHelper.showLogin(this);
	  }
	  
	  public void btn_Collect(View v){
		  
	  }
	  
	  public void btn_Advice(View v){
		  UIHelper.showAdvice(this);
	  }
	  
	  private void InitView() {
		  headImg = (CircleImageView)findViewById(R.id.my_head_ima);
		  close = (ImageView)findViewById(R.id.my_head_back);
		  close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	  @Override
	  protected void onResume() {
		super.onResume();
	}
}
