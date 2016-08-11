package com.timeline.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.timeline.adapter.SigninGuestAdapter;
import com.timeline.app.AppContext;
import com.timeline.bean.MeetingInfo;
import com.timeline.bean.ReturnInfo;
import com.timeline.bean.ReturnMsg;
import com.timeline.bean.SigninPerson;
import com.timeline.bean.guest;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.StringUtils;
import com.timeline.common.UIHelper;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.webapi.HttpFactory;
import com.timeline.widget.CircleImageView;

public class GuestSigninAc extends BaseActivity{
	
	private ImageView signView;
	private ImageView readysignView;
	private RelativeLayout rlmy;
	private TextView siginnotxt;
	private CircleImageView myhead;
	
	private ListView guestsView;
	private SigninGuestAdapter guestAdapter;
	private VolleyListenerInterface meetingSignPervolleyListener;//当前会议签到人搜索监听
	private String meetid;
	private List<SigninPerson> PersonsList;
	
	private VolleyListenerInterface SignvolleyListener;//当前会议自己签到监听
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
		 //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		Bundle bundle = getIntent().getExtras();
		meetid = bundle.getString("meetingid");
        InitView();
        InitData();  
        HttpFactory.getMeetingSignPerson(meetid, meetingSignPervolleyListener);
        }
  
  private void InitView() {
	  guestsView = (ListView)findViewById(R.id.signin_listview);
	  signView = (ImageView)findViewById(R.id.signin_head_ima);
	  readysignView = (ImageView)findViewById(R.id.signin_head_ima1);
	  rlmy = (RelativeLayout)findViewById(R.id.re_tabpeople_job);
	  siginnotxt = (TextView)findViewById(R.id.item_signin_no);
	  myhead = (CircleImageView)findViewById(R.id.item_signin_ima);
	  Bitmap bm = BitmapFactory.decodeFile(AppContext.fileName); 
	  myhead.setImageBitmap(bm);//绑定图片
	  signView.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
//			Intent in = new Intent(GuestSigninAc.this, Test.class);
//			startActivity(in);
			HttpFactory.MeetingSignin(meetid, SignvolleyListener);
		}
	});
			  
}
	  public void btn_close(View v)
	  {
		  finish();
	  }
  private void InitData() {
	  PersonsList = new ArrayList<SigninPerson>();
	  guestAdapter = new SigninGuestAdapter(GuestSigninAc.this, PersonsList, R.layout.listitem_signin);
	  guestsView.setAdapter(guestAdapter);

	  meetingSignPervolleyListener = new VolleyListenerInterface(GuestSigninAc.this){
		@Override
		public void onMySuccess(String result) {
			// TODO Auto-generated method stub
			try {
				JSONObject myJsonObject = new JSONObject(result);
				String rest = myJsonObject.getString("re_st");
				if (rest.equals("success")) {
					String info = myJsonObject.getString("re_info");
					JSONObject listJsonObject = new JSONObject(info);
					SigninPerson[] persons
					= JsonToEntityUtils.jsontoSigninPerson( listJsonObject.getString("list"));
					PersonsList = Arrays.asList(persons);
					guestAdapter.listItems = PersonsList;
					guestAdapter.notifyDataSetChanged();
					String userNoString =listJsonObject.getString("user_sign_in");
					if (!StringUtils.isEmpty(userNoString)) {
						readysignView.setVisibility(View.VISIBLE);
						signView.setVisibility(View.GONE);
						signView.setImageDrawable(getResources().getDrawable(R.drawable.icon_signin_ready));
						rlmy.setVisibility(View.VISIBLE);
					}
				}
				else {
					UIHelper.ToastMessage(GuestSigninAc.this, myJsonObject.getString("re_info"));	
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		@Override
		public void onMyError(VolleyError error) {
			// TODO Auto-generated method stub
			
		}		
	};
	
	
	SignvolleyListener = new VolleyListenerInterface(GuestSigninAc.this){
		@Override
		public void onMySuccess(String result) {
			// TODO Auto-generated method stub
			try {
				ReturnInfo info = JsonToEntityUtils.jsontoReinfo(result);
				if (info.getRe_st().equals("success")) {
					readysignView.setVisibility(View.VISIBLE);
					signView.setVisibility(View.GONE);
					signView.setImageDrawable(getResources().getDrawable(R.drawable.icon_signin_ready));
					HttpFactory.getMeetingSignPerson(meetid, meetingSignPervolleyListener);
				}
				UIHelper.ToastMessage(GuestSigninAc.this,info.getRe_info().toString());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		@Override
		public void onMyError(VolleyError error) {
			// TODO Auto-generated method stub
			
		}		
	};
  }
}
