package com.timeline.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.timeline.app.AppContext;
import com.timeline.app.AppManager;
import com.timeline.bean.ReturnInfo;
import com.timeline.bean.ReturnMsg;
import com.timeline.bean.User;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.UIHelper;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.webapi.HttpFactory;
import com.timeline.widget.MyDialog;

public class SettingAc  extends BaseActivity{

	private Button btnLogoff;
	private ImageButton btnclose;
	private VolleyListenerInterface volleyListener;
	
	private TextView headText;
	  @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_setting_help);
	        InitView();
	        volleyListener = new VolleyListenerInterface(SettingAc.this) {

				@Override
				public void onMySuccess(String result) {
					// TODO Auto-generated method stub
					final ReturnInfo info = JsonToEntityUtils.jsontoReinfo(result);
					if (info.getRe_st().equals("success")) {
						new MyDialog(SettingAc.this, R.style.MyDialog, "您确定要拨打："+info.getRe_info(), "确定", "取消",
								new MyDialog.DialogClickListener() {

									@Override
									public void onRightBtnClick(Dialog dialog) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}

									@Override
									public void onLeftBtnClick(Dialog dialog) {
										// TODO Auto-generated method stub
										Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+info.getRe_info()));
										startActivity(intent);	
										dialog.dismiss();
									}
								}).show();
					}else {
						UIHelper.ToastMessage(SettingAc.this,info.getRe_info() );
					}
					
				}

				@Override
				public void onMyError(VolleyError error) {
					// TODO Auto-generated method stub

				}

			};
	  }
	  
	  private void InitView(){
			headText = (TextView)findViewById(R.id.main_head_title);
			headText.setText("设置与帮助");
		  btnclose = (ImageButton)findViewById(R.id.main_head_logo);
		  btnLogoff = (Button)findViewById(R.id.btn_logoff);
		  if (!AppContext.getInstance().getIslogin()) {
			  btnLogoff.setVisibility(View.GONE);
		  }
		  btnclose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		  btnLogoff.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AppContext.getInstance().logoffPsw();
				AppContext.setUser(null);
				
				AppManager.getAppManager().finishAllActivity();
				UIHelper.showMain(SettingAc.this);
				UIHelper.showLogin(SettingAc.this);
				
			}
		});

	  }
	  
	  public void btn_SetPass(View v) {
		  if (!AppContext.getInstance().getIslogin()) {
			  UIHelper.ToastMessage(this, "请先登录！");
		  }
		  else {
			  UIHelper.showPawSet(this);
		  }
		
	  }
	  
	  public void btn_Call(View v) {
		  HttpFactory.Service_Phone(volleyListener);
	  }
	  
	  public void btn_Grade(View v) {
			
	  }
	  
	  public void btn_Help(View v) {
			UIHelper.showWebview("help", this);
	  }
	  
	  public void btn_Declare(View v) {
		  UIHelper.showWebview("declare", this);
	  }
}
