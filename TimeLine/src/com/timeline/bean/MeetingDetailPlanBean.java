package com.timeline.bean;

import java.util.List;

public class MeetingDetailPlanBean {
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	private String id;
	private String subject;
	private String time;
//	private String detime;
//	private List<String> plans;
//	public String getDetime() {
//		return detime;
//	}
//	public void setDetime(String detime) {
//		this.detime = detime;
//	}
//	public List<String> getPlans() {
//		return plans;
//	}
//	public void setPlans(List<String> plans) {
//		this.plans = plans;
//	}

}
