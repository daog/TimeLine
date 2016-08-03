package com.timeline.adapter;

import java.util.List;
import java.util.Map;

import com.timeline.bean.MeetingInfo;
import com.timeline.common.DateTimeHelper;
import com.timeline.main.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MonthContentListAdapter extends BaseAdapter {
	
		private List<Map<String, Object>> data;
		private LayoutInflater layoutInflater;
		private Context context;
		public MonthContentListAdapter(Context context,List<Map<String, Object>> data){
			this.context=context;
			this.data=data;
			this.layoutInflater=LayoutInflater.from(context);
		}
		/**
		 * 获取istitem_monthcontent.xml.xml
		 * @author Administrator
		 */
		public final class ContentListItemControls{
			public LinearLayout split;
			public TextView startTime;
			public TextView endTime;
			public TextView contentTitle;
		}
		@Override
		public int getCount() {
			return data.size();
		}
		/**
		 * 数据
		 */
		@Override
		public Object getItem(int position) {
			return data.get(position);
		}
		/**
		 * 位置
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ContentListItemControls contentListItemControls=null;
			if(convertView==null){
				contentListItemControls=new ContentListItemControls();
				//获取控件
				convertView=layoutInflater.inflate(R.layout.listitem_monthcontent, null);
				contentListItemControls.startTime=(TextView)convertView.findViewById(R.id.id_startTime);
				contentListItemControls.endTime=(TextView)convertView.findViewById(R.id.id_endTime);
				contentListItemControls.split=(LinearLayout)convertView.findViewById(R.id.id_split);
				contentListItemControls.contentTitle=(TextView)convertView.findViewById(R.id.id_contentTitle);
				convertView.setTag(contentListItemControls);
			}else{
				contentListItemControls=(ContentListItemControls)convertView.getTag();
			}
			//填充数据
			contentListItemControls.split.setBackgroundColor((Integer)data.get(position).get("split")); 
			int start = Integer.valueOf((String) data.get(position).get("startTime"));
			int end = Integer.valueOf((String) data.get(position).get("endTime"));
			contentListItemControls.startTime.setText(DateTimeHelper.int2Time(start));
			contentListItemControls.endTime.setText(DateTimeHelper.int2Time(end));
			contentListItemControls.contentTitle.setText((String)data.get(position).get("contentTitle"));
			MeetingInfo info = (MeetingInfo) data.get(position).get("meeting");
			contentListItemControls.contentTitle.setTag(info);
			return convertView;
		}

	}

