package com.timeline.webapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes.Name;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Environment;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.timeline.app.AppConfig;
import com.timeline.app.AppContext;
import com.timeline.bean.ReturnInfo;
import com.timeline.common.ImageUtils;
import com.timeline.common.JsonToEntityUtils;
import com.timeline.common.StringUtils;
import com.timeline.common.UIHelper;
import com.timeline.interf.VolleyListenerInterface;
import com.timeline.ui.Main;

public class HttpFactory {

	private static void cheackUser() {
		if (AppContext.getUser()==null ) {
			return;
		}
		if (!AppContext.getUser().getStatus().equals("1")) {
			UIHelper.ToastMessage(Main.instance, "账号信息未完善或正在审核");
			return;
		}
	}
	/**
	 * 获取验证码htp
	 * @param number
	 * @param type
	 */
	public static void getSend_SMS(final Context context,final String number,final String type, VolleyListenerInterface volleyListenerInterface) {
		String url = "http://event.gooddr.com/api/user/send_sms";
		StringRequest request = new StringRequest(Method.POST, url,
				volleyListenerInterface.responseListener(), 
				volleyListenerInterface.errorListener()) {  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("mobile",number);  
			    map.put("type", type);  
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);	
	}
	
	/**
	 * 注册http
	 * @param psw
	 * @param verify
	 * @param volleyListenerInterface
	 */
	public static void Register(final String psw,final String verify, VolleyListenerInterface volleyListenerInterface){
		String url = "http://event.gooddr.com/api/user/register";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("mobile",AppContext.phoneNum);  
			    map.put("password", psw);  
			    map.put("uuid", AppContext.GetIMEI()); 
			    map.put("verify_code", verify); 
			    map.put("device_type", "3"); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	
	/**
	 * 修改密码http
	 * @param psw
	 * @param verify
	 * @param volleyListenerInterface
	 */
	public static void ResetPassword(final String psw,final String verify, VolleyListenerInterface volleyListenerInterface){
		String url = "http://event.gooddr.com/api/user/reset_password";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("mobile",AppContext.getInstance().getAccount());  
			    map.put("password", psw);  
			    map.put("verify_code", verify); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	/**
	 * 登陆http
	 * @param psw
	 * @param verify
	 * @param volleyListenerInterface
	 */
	public static void Login(final String account ,final String psw ,VolleyListenerInterface volleyListenerInterface){
		String url = "http://event.gooddr.com/api/user/login";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("mobile",account);  
			    map.put("password", psw);  
			    map.put("uuid", AppContext.GetIMEI()); 
			    //map.put("uuid", "123456"); 
			    map.put("device_type", "3"); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	/**
	 * 意见反馈http
	 * @param uid
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void FeedBack(final String content,VolleyListenerInterface volleyListenerInterface){
		String url = "http://event.gooddr.com/api/user/feedback";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("user_id",AppContext.getUser().getId());  
			    map.put("login_token", AppContext.getUser().getLogin_token());  
			    map.put("content",content); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	/**
	 * 获取客服电话http
	 * @param volleyListenerInterface
	 */
	public static void Service_Phone(VolleyListenerInterface volleyListenerInterface){
		String url = "http://event.gooddr.com/api/user/servicephone";
		StringRequest request = new StringRequest(Method.GET, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener());  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	/**
	 * 获取指定日期会员参与的会议列表http
	 * @param uid
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void getMeetingjoin_list(final String date,VolleyListenerInterface volleyListenerInterface){
		cheackUser();
		String url = "http://event.gooddr.com/api/meeting/my_join_meeting_list";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("user_id",AppContext.getUser().getId());  
			    map.put("login_token", AppContext.getUser().getLogin_token());  
			    map.put("the_day",date); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	/**
	 * 获取会议简介http
	 * @param uid
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void getMeetingDescribe(final String id,VolleyListenerInterface volleyListenerInterface){
		cheackUser();
		String url = "http://event.gooddr.com/api/meeting/meeting_describe";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("user_id",AppContext.getUser().getId());  
			    map.put("login_token", AppContext.getUser().getLogin_token());  
			    map.put("meeting_id",id); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	
	/**
	 * 获取会议签到人列表http
	 * @param uid
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void getMeetingSignPerson(final String id,VolleyListenerInterface volleyListenerInterface){
		String url = "http://event.gooddr.com/api/meeting/sign_in_list";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("user_id",AppContext.getUser().getId());  
			    map.put("login_token", AppContext.getUser().getLogin_token());  
			    map.put("meeting_id",id); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	
	/**
	 *会议签到http
	 * @param uid
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void MeetingSignin(final String id,VolleyListenerInterface volleyListenerInterface){
		String url = "http://event.gooddr.com/api/meeting/sign_in";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("user_id",AppContext.getUser().getId());  
			    map.put("login_token", AppContext.getUser().getLogin_token());  
			    map.put("meeting_id",id); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	/**
	 *自己收藏的会议列表http
	 * @param uid
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void MeetingCollect(VolleyListenerInterface volleyListenerInterface){
		cheackUser();
		String url = "http://event.gooddr.com/api/meeting/meeting_collect_list";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("user_id",AppContext.getUser().getId());  
			    map.put("login_token",AppContext.getUser().getLogin_token()); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	/**
	 *收藏此次会议列http
	 * @param id
	 * @param status 状态类别 // 1收藏 2移除收藏
	 * @param volleyListenerInterface
	 */
	public static void SetMeetingCollect(final String id,final String status,VolleyListenerInterface volleyListenerInterface){
		cheackUser();
		String url = "http://event.gooddr.com/api/meeting/meeting_collect";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("user_id",AppContext.getUser().getId());  
			    map.put("login_token",AppContext.getUser().getLogin_token()); 
			    map.put("meeting_id",id); 
			    map.put("status",status); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	
	/**
	 *自己参加过的会议历史http
	 * @param uid
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void MeetingHistory(final String page,VolleyListenerInterface volleyListenerInterface){
		cheackUser();
		String url = "http://event.gooddr.com/api/meeting/meeting_history";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("user_id",AppContext.getUser().getId());  
			    map.put("login_token",AppContext.getUser().getLogin_token()); 
			    map.put("page",page); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	
	/**
	 *当前正在进行的会议http
	 * @param uid
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void MeetingNowMyJoin(VolleyListenerInterface volleyListenerInterface){
		cheackUser();
		String url = "http://event.gooddr.com/api/meeting/now_my_join_meeting_list";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("user_id",AppContext.getUser().getId());  
			    map.put("login_token",AppContext.getUser().getLogin_token()); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	/**
	 *职位信息http
	 * @param uid
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void BASE_Dictionary(VolleyListenerInterface volleyListenerInterface){
		String url = "http://event.gooddr.com/api/user/dictionary";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("user_id",AppContext.getUser().getId());  
			    map.put("login_token",AppContext.getUser().getLogin_token()); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	/**
	 *会议详情http
	 * @param uid
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void Meeting_Detail(final String meetingid,VolleyListenerInterface volleyListenerInterface){
		String url = "http://event.gooddr.com/api/meeting/meeting_detail";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("user_id",AppContext.getUser().getId());  
			    map.put("login_token",AppContext.getUser().getLogin_token()); 
			    map.put("meeting_id",meetingid); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	
	/**
	 *会议搜索http
	 * @param uid
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void Meeting_Search(final String keyword,VolleyListenerInterface volleyListenerInterface){
		cheackUser();
		String url = "http://event.gooddr.com/api/meeting/search";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("user_id",AppContext.getUser().getId());  
			    map.put("login_token",AppContext.getUser().getLogin_token()); 
			    map.put("key_word",keyword); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	
	/**
	 *会员信息编辑http
	 * @param uid
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void Users_Edit(final String Name,final String gender,final String position,final String job_title,
			final String department,final String hospital,VolleyListenerInterface volleyListenerInterface){
		String url = "http://event.gooddr.com/api/user/edit";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("user_id",AppContext.getUser().getId());  
			    map.put("name",Name); 
			    map.put("gender",gender); 
			    map.put("position",position); 
			    map.put("job_title",job_title);
			    map.put("department",department); 
			    map.put("hospital",hospital); 
			    map.put("login_token",AppContext.getUser().getLogin_token()); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	/**
	 *设置头像http
	 * @param uid
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void SetAvatar(final String pic,VolleyListenerInterface volleyListenerInterface){
		String url = "http://event.gooddr.com/api/user/avatar";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("user_id",AppContext.getUser().getId());  
			    map.put("login_token",AppContext.getUser().getLogin_token()); 
			    map.put("type","1");
			    map.put("user_avatar",pic); 
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	/**
	 *获取声明http
	 * @param uid
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void GetAnnounce(VolleyListenerInterface volleyListenerInterface){
		String url = "http://event.gooddr.com/api/user/announce";
		StringRequest request = new StringRequest(Method.GET, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener());  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	/**
	 *获取帮助http
	 * @param uid
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void GetHelp(VolleyListenerInterface volleyListenerInterface){
		String url = "http://event.gooddr.com/api/user/help";
		StringRequest request = new StringRequest(Method.GET, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener());  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	/**
	 *获取头像http
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void getAvatar(String url){
		ImageRequest imageRequest = new ImageRequest(  
				url,  
		        new Response.Listener<Bitmap>() {  
		            @Override  
		            public void onResponse(Bitmap response) {  
		                  try {
							ImageUtils.saveImageToSD(AppContext.getInstance(), AppContext.fileName, response,100);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            }  
		        }, 0, 0, Config.RGB_565, new Response.ErrorListener() {  
		            @Override  
		            public void onErrorResponse(VolleyError error) {  
		            
		            }  
		        });  

		imageRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(imageRequest);
	}
	
	/**
	 * 获取指定的一段日期会员参与的会议列表http
	 * @param uid
	 * @param content
	 * @param volleyListenerInterface
	 */
	public static void getMeetingjoin_list_period(final String startDate, final String endDate, VolleyListenerInterface volleyListenerInterface){
		String url = "http://event.gooddr.com/api/meeting/get_meeting_list_by_period";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("user_id",AppContext.getUser().getId());  
			    map.put("login_token", AppContext.getUser().getLogin_token());  
			    map.put("start_day",startDate); 
			    map.put("end_day",endDate);
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
	
	public static void Set_Join_Status(final String id, final String status, VolleyListenerInterface volleyListenerInterface){
		String url = "http://event.gooddr.com/api/meeting/set_meeting_join_status";
		StringRequest request = new StringRequest(Method.POST, url,volleyListenerInterface.responseListener(), 
			volleyListenerInterface.errorListener())
			{  
			  @Override  
			  protected Map<String, String> getParams() throws AuthFailureError {  
			    Map<String, String> map = new HashMap<String, String>();  
			    map.put("user_id",AppContext.getUser().getId());  
			    map.put("login_token", AppContext.getUser().getLogin_token());  
			    map.put("meeting_id",id); 
			    map.put("status",status);
			    return map;  
			  }  
			};  

		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppContext.getInstance().mQueue.add(request);
	}
}
