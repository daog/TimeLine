package com.timeline.adapter;

import java.util.List;
import java.util.Map;

import com.timeline.adapter.MonthContentListAdapter.ContentListItemControls;
import com.timeline.bean.MeetingSerchBean;
import com.timeline.main.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchListAdapter extends BaseAdapter {

	private List<MeetingSerchBean> data;
	private LayoutInflater layoutInflater;
	private Context context;
	
	public SearchListAdapter(Context context,List<MeetingSerchBean> data){
		this.context=context;
		this.data=data;
		this.layoutInflater=LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		SearchListItemControls searchListItemControls=null;
		if(convertView==null){
			searchListItemControls=new SearchListItemControls();
			//获得组件，实例化组件
			convertView=layoutInflater.inflate(R.layout.listitem_search, null);
			searchListItemControls.searchtitle=(TextView)convertView.findViewById(R.id.id_meetingtitle);
			searchListItemControls.searchContent=(TextView)convertView.findViewById(R.id.id_meetingOrgnizer);
			searchListItemControls.searchImg = (ImageView)convertView.findViewById(R.id.id_meetingImage);
			convertView.setTag(searchListItemControls);
		}else{
			searchListItemControls=(SearchListItemControls)convertView.getTag();
		}
		//绑定数据
		//contentListItemControls.meetingImage.setBackgroundColor((Integer)data.get(position).get("splitColor"));
		searchListItemControls.searchtitle.setText(data.get(position).getSubject().toString());
		searchListItemControls.searchContent.setText("主办方："+data.get(position).getSponsor().toString());
		searchListItemControls.searchtitle.setTag(data.get(position));
		if (data.get(position).isPersonal()) {
			searchListItemControls.searchContent.setText("个人事件");
			searchListItemControls.searchImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_searchred));
		}else {
			searchListItemControls.searchImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_search));
		}
		return convertView;
	}
	
	/**
	 * 组件集合，对应listitem_search.xml中的控件 
	 * @author Administrator
	 */
	public final class SearchListItemControls{
		public TextView searchtitle;
		public TextView searchContent;
		public ImageView searchImg;
	}

}
