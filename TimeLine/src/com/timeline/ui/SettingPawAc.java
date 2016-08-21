package com.timeline.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.timeline.app.AppContext;
import com.timeline.bean.ReturnInfo;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.StringUtils;
import com.timeline.common.UIHelper;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.webapi.HttpFactory;

public class SettingPawAc extends BaseActivity {
	private ImageButton backBtn;
	private TextView headTxt;
	private EditText yanzhengTxt;
	private EditText passwordTxt;
	private EditText agpasswordTxt;
	private Button okBtn;
	
	private String psw;
	private String pswag;
	
	private VolleyListenerInterface volleyListener;
	private VolleyListenerInterface smsvolleyListener;
	
	private ImageView backimg;
	private TextView headText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settingpaw);
		InitView();
		volleyListener = new VolleyListenerInterface(SettingPawAc.this) {
				
			@Override
			public void onMySuccess(String result) {
				// TODO Auto-generated method stub
				Log.e("", result);
				ReturnInfo info = JsonToEntityUtils.jsontoReinfo(result);
				if (info.getRe_st().equals("success")) {
					AppContext.getInstance().logoffPsw();
					UIHelper.showLogin(SettingPawAc.this);
					UIHelper.ToastMessage(SettingPawAc.this,info.getRe_info().toString());
					SettingPawAc.this.finish();
				}
				else {
					UIHelper.ToastMessage(SettingPawAc.this,info.getRe_info().toString());
				}
			}
			@Override
			public void onMyError(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		};
		
        smsvolleyListener = new VolleyListenerInterface(SettingPawAc.this){			
		@Override
		public void onMySuccess(String result) {
			// TODO Auto-generated method stub
			Log.e("", result);
			ReturnInfo info = JsonToEntityUtils.jsontoReinfo(result);
			UIHelper.ToastMessage(SettingPawAc.this,info.getRe_info().toString());

			
		}
		@Override
		public void onMyError(VolleyError error) {
			// TODO Auto-generated method stub
			
		}
	};
	}
	  public void close_Click(View v){
		  finish();
	  }
	public void Btn_GetVer(View v) {
		HttpFactory.getSend_SMS(SettingPawAc.this, AppContext.getInstance().getAccount(), "2", smsvolleyListener);
	}
	private void InitView() {
		   backBtn =(ImageButton)findViewById(R.id.main_head_logo);
		   headTxt = (TextView)findViewById(R.id.main_head_title);
		   yanzhengTxt = (EditText)findViewById(R.id.send_number);
		   passwordTxt = (EditText)findViewById(R.id.login_password);
		   agpasswordTxt = (EditText)findViewById(R.id.login_password_again);
		   okBtn = (Button)findViewById(R.id.btn_ok);
		   okBtn.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				psw = passwordTxt.getText().toString();
				pswag = agpasswordTxt.getText().toString();
				String verify= yanzhengTxt.getText().toString();
				if (!StringUtils.isEmpty(psw)&&psw.equals(pswag)) {
					HttpFactory.ResetPassword(psw, verify, volleyListener);
				}
				else {
					UIHelper.ToastMessage(SettingPawAc.this, "«ÎºÏ≤È√‹¬Î «∑ÒÕ≥“ª");
				}
			}
		});
			backimg = (ImageView)findViewById(R.id.main_head_logo);
			headText = (TextView)findViewById(R.id.main_head_title);
			headText.setText("÷ÿ÷√√‹¬Î");
			backimg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					finish();
				}
			});
	}
}
