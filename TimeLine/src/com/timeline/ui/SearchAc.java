package com.timeline.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.timeline.adapter.PastMeetingAdapter;
import com.timeline.adapter.SearchListAdapter;
import com.timeline.app.AppContext;
import com.timeline.bean.JobBase;
import com.timeline.bean.MeetingSerchBean;
import com.timeline.bean.ReturnInfo;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.UIHelper;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.webapi.HttpFactory;

public class SearchAc extends BaseActivity {
	
	private ListView lv_SearchListView;
	private List<MeetingSerchBean> list = new ArrayList<MeetingSerchBean>();
	private SearchListAdapter adapter;
	private VolleyListenerInterface volleyListener;
	private EditText searchEditText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		 //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        initView();
        getData();       
        searchEditText.addTextChangedListener(textWatcher);
        adapter = new SearchListAdapter(SearchAc.this, list);
        //测试数据 
        lv_SearchListView.setAdapter(adapter);
        lv_SearchListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView meetingView = (TextView)v.findViewById(R.id.id_meetingtitle);
				String mid = (String) meetingView.getTag();
				if (mid != null) {
					UIHelper.showMeetingDetail(SearchAc.this, mid);
				}
			}
		});
        volleyListener = new VolleyListenerInterface(SearchAc.this) {
    		@Override
    		public void onMySuccess(String result) {
    			// TODO Auto-generated method stub
    			try {
    				JSONObject myJsonObject = new JSONObject(result);
    				String rest = myJsonObject.getString("re_st");
    				if (rest.equals("success")) {
    					String sult =  myJsonObject.getString("re_info");
    					MeetingSerchBean[] beans = JsonToEntityUtils.jsontoMeetingsearch(sult);
    					list.clear();
    					for (MeetingSerchBean be:beans ) {
    						list.add(be);
						}
    					adapter.notifyDataSetChanged();

    				}else {
    					ReturnInfo info = JsonToEntityUtils.jsontoReinfo(result);
    					UIHelper.ToastMessage(SearchAc.this,info.getRe_info() );
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
    /**
     * 实时监听用户输入的手机号，输入至最后一位后，计算折扣后的金额
     */
    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
                String text = searchEditText.getText().toString();//用户输入字母
                HttpFactory.Meeting_Search(text, volleyListener);
        }
    };
    
    public void Btn_cancel(View v) {
		finish();
	}
	private void initView(){
		searchEditText = (EditText)findViewById(R.id.id_edSearchContent);
		lv_SearchListView = (ListView)findViewById(R.id.id_searchList);
	}
	
	private void getData() {
		// TODO Auto-generated method stub
//        for (int i = 0; i < 10; i++) {  
//        	MeetingSerchBean bean = new MeetingSerchBean();
//        	bean.setSponsor("美国神经内科学会");
//        	bean.setSubject("2016年第68届美国神经学会年会");
//            list.add(bean);  
//        }  
	}
}
