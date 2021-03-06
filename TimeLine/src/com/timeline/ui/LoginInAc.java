package com.timeline.ui;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.timeline.app.AppContext;
import com.timeline.app.AppManager;
import com.timeline.bean.ReturnInfo;
import com.timeline.bean.User;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.StringUtils;
import com.timeline.common.UIHelper;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.webapi.HttpFactory;

public class LoginInAc extends BaseActivity {

	private EditText accountText;
	private EditText passwordText;
	private Button loginBtn;
	private TextView forgetText;
	private TextView cancelText;
	private TextView registerText;

	private String account;
	private String psw;

	private VolleyListenerInterface volleyListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
		volleyListener = new VolleyListenerInterface(LoginInAc.this) {

			@Override
			public void onMySuccess(String result) {
				// TODO Auto-generated method stub
				try {
					JSONObject myJsonObject = new JSONObject(result);
					String rest = myJsonObject.getString("re_st");
					if (rest.equals("success")) {// 成功
						AppContext.getInstance().remenberPsw(account, psw);
						User us = JsonToEntityUtils.jsontoUser( myJsonObject.getString("re_info"));
						AppContext.setUser(us);
						AppManager.getAppManager().finishAllActivity();
						UIHelper.showMain(LoginInAc.this);
					}else if (rest.equals("verify")) {//当前账号处于待审核状态
						AppContext.getInstance().remenberPsw(account, psw);
						User us = JsonToEntityUtils.jsontoUser( myJsonObject.getString("re_info"));
						AppContext.setUser(us);
						AppManager.getAppManager().finishAllActivity();
						UIHelper.showMain(LoginInAc.this);
					}else if (rest.equals("consummate")) {//请完善个人信息
						AppContext.getInstance().remenberPsw(account, psw);
						User us = JsonToEntityUtils.jsontoUser( myJsonObject.getString("re_info"));
						AppContext.setUser(us);
						AppManager.getAppManager().finishAllActivity();
						UIHelper.showMain(LoginInAc.this);
						UIHelper.showMyInfo(LoginInAc.this, "", "");
					}
					else {
						ReturnInfo info = JsonToEntityUtils.jsontoReinfo(result);
						UIHelper.ToastMessage(LoginInAc.this,info.getRe_info() );
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

	}

	public void Btn_Register(View v) {
		UIHelper.showRegister1(this,"1");//［1:注册 2:找回密码］
	}

	public void Btn_Cancel(View v) {
		this.finish();
	}
	
	public void Btn_forget(View v) {
		UIHelper.showRegister1(this,"2");//［1:注册 2:找回密码］
	}

	private void initView() {
		accountText = (EditText) findViewById(R.id.login_account);
		passwordText = (EditText) findViewById(R.id.login_password);
		loginBtn = (Button) findViewById(R.id.login_btn_login);
		forgetText = (TextView) findViewById(R.id.login_forget);
		cancelText = (TextView) findViewById(R.id.login_head_cancel);
		registerText = (TextView) findViewById(R.id.login_head_cancel);

		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				account = accountText.getText().toString();
				psw = passwordText.getText().toString();
				if (!StringUtils.isEmpty(account)&&!StringUtils.isEmail(psw)) {
					HttpFactory.Login(account, psw, volleyListener);
				}else {
					UIHelper.ToastMessage(LoginInAc.this, "请输入账号密码");
				}
				
				
			}
		});
	}
}
