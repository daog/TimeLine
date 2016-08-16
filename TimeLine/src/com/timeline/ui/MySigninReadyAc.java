package com.timeline.ui;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.timeline.app.AppContext;
import com.timeline.bean.JobBase;
import com.timeline.bean.ReturnInfo;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.UIHelper;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.webapi.HttpFactory;
import com.timeline.widget.CircleImageView;

public class MySigninReadyAc extends BaseActivity{

	private CircleImageView headImg;
	private RelativeLayout pastMeetingsRl;
	private RelativeLayout collecsRl;
	
	private ImageView backimg;
	private TextView headText;
	
	private TextView nameText;
	private TextView jobtitleText;
	  @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_my_signin_ready);
	        InitView();
			nameText.setText(AppContext.getUser().getName());
			if (AppContext.getInstance().jonbase!=null) {
				jobtitleText.setText(AppContext.getInstance().jonbase.getJob_title().get(AppContext.getUser().getJob_title()));
			}
			
	  }
	  
	  
	  public void SetClick(View v){
		  UIHelper.showSetting(this);
	  }
	  
	  public void Btn_login(View v){
		  UIHelper.showLogin(this);
	  }
	  public void btn_Advice(View v){
		  UIHelper.showAdvice(this);
	  }
	  
	  public void btn_Myinfo(View v){
		  finish();
		  UIHelper.showMyInfo(this,"","");
	  }
	  public void btn_Collect(View v){
		  UIHelper.showMeetingClassify(MySigninReadyAc.this);
		  //UIHelper.showHistory("collect", MySigninReadyAc.this);
	  }
	  
	  public void close_Click(View v){
		  finish();
	  }
	  private void InitView() {
		  headImg = (CircleImageView)findViewById(R.id.my_head_ima);
		  Bitmap bm = BitmapFactory.decodeFile(AppContext.fileName); 
		  headImg.setImageBitmap(bm);//绑定图片
		  headImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				UIHelper.showMyInfo(MySigninReadyAc.this,"","");
			}
		});
		  collecsRl = (RelativeLayout)findViewById(R.id.ll_tools_collect);
		  pastMeetingsRl = (RelativeLayout)findViewById(R.id.my_info_last);
		  pastMeetingsRl.setOnClickListener(new OnClickListener(){
			  
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UIHelper.showHistory("history", MySigninReadyAc.this);
			}
			  
		  });
		  
			backimg = (ImageView)findViewById(R.id.main_head_logo);
			headText = (TextView)findViewById(R.id.main_head_title);
			headText.setText("个人中心");
			backimg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			nameText =  (TextView)findViewById(R.id.myinfore_name);
			jobtitleText = (TextView)findViewById(R.id.myinfore_jobtitle);
		
			
	}
	  @Override
	  protected void onResume() {
		super.onResume();
	}
}

