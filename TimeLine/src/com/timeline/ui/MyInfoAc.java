package com.timeline.ui;


import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.timeline.app.AppContext;
import com.timeline.bean.JobBase;
import com.timeline.bean.ReturnInfo;
import com.timeline.bean.User;
import com.timeline.common.FileUtils;
import com.timeline.common.ImageUtils;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.PicUtil;
import com.timeline.common.StringUtils;
import com.timeline.common.UIHelper;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.main.R;
import com.timeline.webapi.HttpFactory;
import com.timeline.widget.CircleImageView;

public class MyInfoAc extends BaseActivity {

	private static final int RESULT = 1;
	private CircleImageView headView;
	private String path;
	private TextView nameTextView;
	private TextView sexualView;
	private TextView hospitalView;
	private TextView departmentView;
	private TextView dutyView;
	private TextView jobtitleView;

	private String type;
	private String name;
	private VolleyListenerInterface savevolleyListener;
	private VolleyListenerInterface avatervolleyListener;
	
	private ImageView backimg;
	private TextView headText;
	private Button savaBtn;
	
	private String picStrig;
	private Bitmap bitmap;
	
	private User newUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_info);
		InitView();
		setData();
		
	savevolleyListener = new VolleyListenerInterface(MyInfoAc.this) {
		@Override
		public void onMySuccess(String result) {
			// TODO Auto-generated method stub
			try {
				JSONObject myJsonObject = new JSONObject(result);
				String rest = myJsonObject.getString("re_st");
				if (rest.equals("success")) {
					String callname = nameTextView.getText().toString();
					String gender = sexualView.getTag().toString();
					String position = dutyView.getTag().toString();
					String job_title = jobtitleView.getTag().toString();
					String department = departmentView.getText().toString();
					String hospital = hospitalView.getText().toString();
					AppContext.setUser(newUser);
//					AppContext.getUser().setName(callname);
//					AppContext.getUser().setGender(gender);
//					AppContext.getUser().setPosition(position);
//					AppContext.getUser().setJob_title(job_title);
//					AppContext.getUser().setDepartment(department);
//					AppContext.getUser().setInstitution(hospital);
					String sult =  myJsonObject.getString("re_info");
					JSONObject lastJsonObject = new JSONObject(sult);
					UIHelper.ToastMessage(MyInfoAc.this,lastJsonObject.getString("re_info"));
				}else {
					ReturnInfo info = JsonToEntityUtils.jsontoReinfo(result);
					UIHelper.ToastMessage(MyInfoAc.this,info.getRe_info() );
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
	avatervolleyListener =  new VolleyListenerInterface(MyInfoAc.this) {
		@Override
		public void onMySuccess(String result) {
			// TODO Auto-generated method stub
			try {
				JSONObject myJsonObject = new JSONObject(result);
				String rest = myJsonObject.getString("re_st");
				if (rest.equals("success")) {
					String jobsult =  myJsonObject.getString("re_info");
					ImageUtils.saveImageToSD(AppContext.getInstance(), AppContext.fileName, bitmap,100);

				}else {
					ReturnInfo info = JsonToEntityUtils.jsontoReinfo(result);
					UIHelper.ToastMessage(MyInfoAc.this,info.getRe_info() );
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

	private void setData() {
		  newUser = AppContext.getUser();
		  Bitmap bm = BitmapFactory.decodeFile(AppContext.fileName); 
		  headView.setImageBitmap(bm);//绑定图片
		  nameTextView.setText(AppContext.getUser().getName());
		  if (AppContext.getUser().getGender().equals("1")) {
			  sexualView.setText("男");
		  }else {
			sexualView.setText("女");
		  }

			  departmentView.setText(AppContext.getUser().getDepartment()!=null?AppContext.getUser().getDepartment():"");
			  dutyView.setText(AppContext.getUser().getPosition()!=null?AppContext.getInstance().jonbase.getPosition().get(AppContext.getUser().getPosition()):"" );
		  
		  
		  jobtitleView.setText(AppContext.getUser().getJob_title()!=null?AppContext.getInstance().jonbase.getJob_title().get(AppContext.getUser().getJob_title()):"");
		  hospitalView.setText(AppContext.getUser().getInstitution()!=null?AppContext.getUser().getInstitution():"");
	}
	
	private void InitView() {
		nameTextView = (TextView) findViewById(R.id.myinfo_name_val);
		sexualView = (TextView) findViewById(R.id.myinfo_sexual_val);
		departmentView = (TextView) findViewById(R.id.myinfo_department_val);
		dutyView = (TextView) findViewById(R.id.myinfo_duty_val);
		jobtitleView = (TextView) findViewById(R.id.myinfo_jobtitle_val);
		hospitalView = (TextView) findViewById(R.id.myinfo_hospital_val);
		headView = (CircleImageView) findViewById(R.id.my_head_ima);

		backimg = (ImageView)findViewById(R.id.main_head_logo);
		headText = (TextView)findViewById(R.id.main_head_title);
		headText.setText("个人信息");
		backimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		savaBtn = (Button) findViewById(R.id.btn_save);
		savaBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				if (!newUser.equals(AppContext.getUser())) {
					String callname = newUser.getName();
					String gender = newUser.getGender();
					String position = newUser.getPosition();
					String job_title = newUser.getJob_title();
					String department = newUser.getDepartment();
					String hospital = newUser.getInstitution();
					HttpFactory.Users_Edit(callname, gender, position, job_title, department, hospital, savevolleyListener);
//				}
				if (picStrig != null) {
					HttpFactory.SetAvatar(picStrig, avatervolleyListener);
				}
				finish();
			}
		});
	}

	@Override
	protected void onNewIntent(Intent intent) {

		super.onNewIntent(intent);
		setIntent(intent);// must store the new intent unless getIntent() will
							// return the old one
		Bundle bundle = getIntent().getExtras();
		;
		type = bundle.getString("type");
		name = bundle.getString("name");
		if (type.equals("name")) {
			nameTextView.setText(name);
		} else if (type.equals("sexual")) {
			  if (name.equals("1")) {
				  sexualView.setText("男");
			  }else {
				  sexualView.setText("女");
			  }
			  if (StringUtils.isEmpty(newUser.getGender())|| !newUser.getGender().equals(name)) {
				  newUser.setGender(name);
				  sexualView.setTag(name);
				  sexualView.setTextColor(getResources().getColor(R.color.black));
			  }
		} else if (type.equals("department")) {
			departmentView.setText(name);
			  if (StringUtils.isEmpty(newUser.getDepartment())|| !newUser.getDepartment().equals(name)) {
				  newUser.setDepartment(name);
				  departmentView.setTag(name);
				  departmentView.setTextColor(getResources().getColor(R.color.black));
			  }
		} else if (type.equals("job_title")) {
			jobtitleView.setText(AppContext.getInstance().jonbase.getJob_title().get(name));
			  if (StringUtils.isEmpty(newUser.getJob_title())|| !newUser.getJob_title().equals(name)) {
				  newUser.setJob_title(name);
				  jobtitleView.setTag(name);
				  jobtitleView.setTextColor(getResources().getColor(R.color.black));
			  }
		} else if (type.equals("hospital")) {
			hospitalView.setText(name);
			  if (StringUtils.isEmpty(newUser.getInstitution())|| !newUser.getInstitution().equals(name)) {
				  newUser.setInstitution(name);
				  hospitalView.setTag(name);
				  hospitalView.setTextColor(getResources().getColor(R.color.black));
			  }
		}else if (type.equals("position")) {
			dutyView.setTag(name);
			 dutyView.setText(AppContext.getInstance().jonbase.getPosition().get(name));
			  if (StringUtils.isEmpty(newUser.getPosition())|| !newUser.getPosition().equals(name)) {
				  newUser.setPosition(name);
				  dutyView.setTag(name);
				  dutyView.setTextColor(getResources().getColor(R.color.black));
			  }

		}

	}

	public void btn_HeadSet(View v) {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, RESULT);

	}

	public void btn_NameSet(View v) {
		if (!AppContext.getUser().getStatus().equals("1")) {
			UIHelper.showInfoEdit(this, "name", nameTextView.getText().toString());
		}else {
			UIHelper.ToastMessage(this, "会员名字不能修改!");
		}
		
	}

	public void btn_SexualSet(View v) {
		UIHelper.showJobList(this,"sexual");
	}

	public void btn_OfficeSet(View v) {
		UIHelper.showInfoEdit(this, "department", departmentView.getText()
				.toString());
	}

	public void btn_DutySet(View v) {
		UIHelper.showJobList(this,"position");
	}

	public void btn_JobtitleSet(View v) {
		UIHelper.showJobList(this,"job_title");
	}

	public void btn_HospitalSet(View v) {
		UIHelper.showInfoEdit(this, "hospital", hospitalView.getText()
				.toString());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT && resultCode == RESULT_OK && data != null) {

			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			path = picturePath;
			showPhoto(headView);
		}
	}

	private void showPhoto(ImageView photo) {
		String picturePath =path;// 图片的uri
		if (picturePath.equals(""))
			return;
		// 缩放图片, width, height 按相同比例缩放图片
		BitmapFactory.Options options = new BitmapFactory.Options();
		// options 设为true时，构造出的bitmap没有图片，只有一些长宽等配置信息，但比较快，设为false时，才有图片
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(picturePath, options);
		int scale = (int) (options.outWidth / (float) 300);
		if (scale <= 0)
			scale = 1;
		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(picturePath, options);
		picStrig = PicUtil.bitmaptoString(path);
//		FileUtils.saveFile(p);
//		bitmap = PicUtil.stringtoBitmap(p);
		photo.setImageBitmap(bitmap);
		photo.setMaxHeight(350);
	}
}
