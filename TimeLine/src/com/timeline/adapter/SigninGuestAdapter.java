package com.timeline.adapter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.xutils.x;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.timeline.app.AppContext;
import com.timeline.bean.SigninPerson;
import com.timeline.bean.guest;
import com.timeline.common.ImageUtils;
import com.timeline.main.R;
import com.timeline.main.R.color;
import com.timeline.widget.CircleImageView;

public class SigninGuestAdapter extends BaseAdapter {
	private Context context;// 运行上下文
	public List<SigninPerson> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源

	static class ListItemView { // 自定义控件集合，与listitem_signin布局一致
		public TextView name;
		public TextView no;
		public ImageView image;
		public ImageView Noimg;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public SigninGuestAdapter(Context context, List<SigninPerson> data,
			int resource) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
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
	ListItemView listItemView = null;
	/**
	 * ListView Item设置
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 自定义视图
		listItemView = null;

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// 获取控件对象

			listItemView.name = (TextView) convertView
					.findViewById(R.id.item_signin_name);
			listItemView.no = (TextView) convertView
					.findViewById(R.id.item_signin_no);
			listItemView.image = (ImageView)convertView
					.findViewById(R.id.item_signin_ima);
			listItemView.Noimg= (ImageView)convertView
					.findViewById(R.id.sort_ima);
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		// 设置文字和图片
		SigninPerson gu = listItems.get(position);
		if (position ==0) {
			listItemView.no.setTextColor(context.getResources().getColor(R.color.first));
			listItemView.Noimg.setVisibility(View.VISIBLE);
			listItemView.Noimg.setImageDrawable(context.getResources().getDrawable(R.drawable.no1));
		}else if(position ==1){
			listItemView.no.setTextColor(context.getResources().getColor(R.color.second));
			listItemView.Noimg.setVisibility(View.VISIBLE);
			listItemView.Noimg.setImageDrawable(context.getResources().getDrawable(R.drawable.no2));
		}
		else if(position ==2){
			listItemView.no.setTextColor(context.getResources().getColor(R.color.third));
			listItemView.Noimg.setVisibility(View.VISIBLE);
			listItemView.Noimg.setImageDrawable(context.getResources().getDrawable(R.drawable.no3));
		}else {
			listItemView.no.setTextColor(context.getResources().getColor(R.color.black));
			listItemView.Noimg.setVisibility(View.GONE);
		}
		listItemView.name.setText(gu.getName());
		listItemView.name.setTag(gu);// 设置隐藏参数(实体类)
		listItemView.no.setText(String.valueOf(position+1));

		   ImageOptions imageOptions = new ImageOptions.Builder()
           .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
           .setCircular(true)
           .setCrop(true)
          // .setLoadingDrawableId(R.mipmap.ic_launcher)
           //.setFailureDrawableId(R.mipmap.ic_launcher)
           .build();
		   x.image().bind(listItemView.image, "http://event.gooddr.com/"+gu.getAvatar(), imageOptions);


		return convertView;
	}
	
	    /**
	     * 到Url去下載圖片回傳BITMAP回來
	     * @param imgUrl
	     * @return
	     */
	    private Bitmap getBitmapFromUrl(String imgUrl) {
	                URL url;
	                Bitmap bitmap = null;
	                try {
	                        url = new URL("http://event.gooddr.com/api"+imgUrl);
	                        InputStream is = url.openConnection().getInputStream();
	                        BufferedInputStream bis = new BufferedInputStream(is);
	                        bitmap = BitmapFactory.decodeStream(bis);
	                        bis.close();
	                } catch (MalformedURLException e) {
	                        e.printStackTrace();
	                } catch (IOException e) {
	                        e.printStackTrace();
	                }
	                return bitmap;
	        }

}
