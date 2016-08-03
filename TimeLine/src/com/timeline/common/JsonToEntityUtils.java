package com.timeline.common;

import java.lang.reflect.Type;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.timeline.bean.JobBase;
import com.timeline.bean.MeetingDescribe;
import com.timeline.bean.MeetingHisBean;
import com.timeline.bean.MeetingInfo;
import com.timeline.bean.MeetingPlanBean;
import com.timeline.bean.MeetingSerchBean;
import com.timeline.bean.ReturnInfo;
import com.timeline.bean.ReturnMsg;
import com.timeline.bean.SigninPerson;
import com.timeline.bean.User;
import com.timeline.bean.guest;

public class JsonToEntityUtils {
	
	public static ReturnInfo jsontoReinfo(String json) {
		Gson gson = new Gson();  
		ReturnInfo infos = gson.fromJson(json, ReturnInfo.class); 
		return infos;
		
	}
	
	public static ReturnMsg jsontoRemsg(String json) {
		Gson gson = new Gson();  
		ReturnMsg info = gson.fromJson(json, ReturnMsg.class); 
		return info;
		
	}
	
	public static User jsontoUser(String json) {
		Gson gson = new Gson();  
		User info = gson.fromJson(json, User.class); 
		return info;
		
	}
	
	public static MeetingInfo[] jsontoMeetingInfo(String json) {
		Gson gson = new Gson();  
		Type listType = new TypeToken<LinkedList<MeetingInfo>>(){}.getType(); 
		LinkedList<MeetingInfo> list = gson.fromJson(json, listType); 
		MeetingInfo[] info = gson.fromJson(json, MeetingInfo[].class); 
		if (info.length == 0) {
			return null;
		}
		return info;
	}
	
	public static MeetingDescribe jsontoMeetingDes(String json) {
		Gson gson = new Gson();  
		MeetingDescribe info = gson.fromJson(json, MeetingDescribe.class); 
		return info;
		
	}
	
	public static JobBase jsontoJobBase(String json) {
		Gson gson = new Gson();  
		JobBase info = gson.fromJson(json, JobBase.class); 
		return info;
		
	}
	
	public static SigninPerson[] jsontoSigninPerson(String json) {
		Gson gson = new Gson();  
		Type listType = new TypeToken<LinkedList<SigninPerson>>(){}.getType(); 
		LinkedList<SigninPerson> list = gson.fromJson(json, listType); 
		SigninPerson[] info = gson.fromJson(json, SigninPerson[].class); 
		if (info.length == 0) {
			return null;
		}
		return info;
	}
	
	public static MeetingHisBean[] jsontoMeetingHises(String json) {
		Gson gson = new Gson();  
		Type listType = new TypeToken<LinkedList<MeetingHisBean>>(){}.getType(); 
		LinkedList<MeetingHisBean> list = gson.fromJson(json, listType); 
		MeetingHisBean[] info = gson.fromJson(json, MeetingHisBean[].class); 
		if (info.length == 0) {
			return null;
		}
		return info;
	}
	
	public static MeetingSerchBean[] jsontoMeetingsearch(String json) {
		Gson gson = new Gson();  
		Type listType = new TypeToken<LinkedList<MeetingSerchBean>>(){}.getType(); 
		LinkedList<MeetingSerchBean> list = gson.fromJson(json, listType); 
		MeetingSerchBean[] info = gson.fromJson(json, MeetingSerchBean[].class); 
		if (info.length == 0) {
			return null;
		}
		return info;
	}
	
	public static guest[] jsontoguest(String json) {
		Gson gson = new Gson();  
		Type listType = new TypeToken<LinkedList<guest>>(){}.getType(); 
		LinkedList<MeetingSerchBean> list = gson.fromJson(json, listType); 
		guest[] info = gson.fromJson(json, guest[].class); 
		if (info.length == 0) {
			return null;
		}
		return info;
	}
	
	public static MeetingPlanBean[] jsontoMeetingPlanBean(String json) {
		Gson gson = new Gson();  
		Type listType = new TypeToken<LinkedList<MeetingPlanBean>>(){}.getType(); 
		LinkedList<MeetingSerchBean> list = gson.fromJson(json, listType); 
		MeetingPlanBean[] info = gson.fromJson(json, MeetingPlanBean[].class); 
		if (info.length == 0) {
			return null;
		}
		return info;
	}
}
