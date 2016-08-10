package com.timeline.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.timeline.bean.Answer;
import com.timeline.bean.Page;
import com.timeline.bean.Quesition;
import com.timeline.main.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InteractionAc extends BaseActivity{
	
	private WebView webView;
	private String url;
	private ImageView backimg;
	private TextView headText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction);
        url = "http://ucb.gooddr.com/vote/survey?openid=o8MN8wTZzmG64VTfdhgmmJb_fRDc1&q=8d8fcea1e4baf1f9334b4719cca292d6&act=tony";
        initView();
        
    }
	  public void close_Click(View v){
		  finish();
	  }
    private void initView(){
    	webView = (WebView)findViewById(R.id.webview);
    	 //设置WebView属性，能够执行Javascript脚本  
    	webView.getSettings().setJavaScriptEnabled(true);  
    	webView.loadUrl(url);  
    	//设置Web视图  
        webView.setWebViewClient(new HelloWebViewClient ());  
		backimg = (ImageView)findViewById(R.id.main_head_logo);
		headText = (TextView)findViewById(R.id.main_head_title);
		headText.setText("问卷调查");
		backimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});


    }
    //Web视图  
        private class HelloWebViewClient extends WebViewClient {  
            @Override 
            public boolean shouldOverrideUrlLoading(WebView view, String url) {  
               view.loadUrl(url);  
               return true;  
            }  
       }

}
