package com.timeline.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.timeline.adapter.MeetingGuestAdapter.ListItemView;
import com.timeline.bean.guest;
import com.timeline.main.R;

import android.R.integer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class JobDataAdapter extends BaseAdapter{

	private Context context;// 运行上下文
	private Map<String,String> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private int pos = -1;

    List<String> listKey = new ArrayList<String>();
    List<String> listValue = new ArrayList<String>();
	static class ListItemView { // 自定义控件集合，与listitem布局一致
		public TextView name;
		public ImageView image;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public JobDataAdapter(Context context, Map<String,String> data,
			int resource) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
		Maptolist ();
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	/**
	 * ListView Item设置
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 自定义视图
		ListItemView listItemView = null;

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// 获取控件对象

			listItemView.name = (TextView) convertView
					.findViewById(R.id.listitem_name);
			listItemView.image = (ImageView) convertView
					.findViewById(R.id.selectima);
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		// 设置文字和图片

		listItemView.name.setText(listValue.get(position));
		listItemView.name.setTag(listKey.get(position));// 设置隐藏参数(实体类)
		if (position != pos) {
			listItemView.image.setVisibility(View.GONE);
		}else {
			listItemView.image.setVisibility(View.VISIBLE);
		}
		//listItemView.job.setText(gu.getJob());


		return convertView;
	}
	
	public void setSelectPos(int position) {
		pos = position;
	}
	
	private  void Maptolist () {
        Iterator it = listItems.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            listKey.add(key);
            listValue.add(listItems.get(key));
        }
    }


}
