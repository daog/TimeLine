package com.timeline.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.timeline.bean.ReturnInfo;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.UIHelper;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.webapi.HttpFactory;
import com.timeline.widget.MyDialog;

public class WebviewAc extends BaseActivity{
	
	private WebView webView;
	private String url;
	private ImageView backimg;
	private TextView headText;
	private String type;
	private VolleyListenerInterface volleyListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction);
        Bundle bundle = getIntent().getExtras();;
        type = bundle.getString("type"); 
        initView();
    }
    
    private void initView(){
        volleyListener = new VolleyListenerInterface(WebviewAc.this) {

			@Override
			public void onMySuccess(String result) {
				// TODO Auto-generated method stub
				final ReturnInfo info = JsonToEntityUtils.jsontoReinfo(result);
				if (info.getRe_st().equals("success")) {
					String customHtml = info.getRe_info();
					webView.loadDataWithBaseURL(null, customHtml, "text/html","UTF-8", null);

				}		
			}

			@Override
			public void onMyError(VolleyError error) {
				// TODO Auto-generated method stub

			}

		};
    	webView = (WebView)findViewById(R.id.webview);
    	 //设置WebView属性，能够执行Javascript脚本  
    	webView.getSettings().setJavaScriptEnabled(true);  
    	//设置Web视图  
        webView.setWebViewClient(new HelloWebViewClient ());  
		backimg = (ImageView)findViewById(R.id.main_head_logo);
		headText = (TextView)findViewById(R.id.main_head_title);
		if (type.equals("help")) {
			headText.setText("帮助");
			HttpFactory.GetHelp(volleyListener);
		}
		else {
			headText.setText("声明");
			HttpFactory.GetAnnounce(volleyListener);
		}
		
		backimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});


    }
    
	  public void close_Click(View v){
		  finish();
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
