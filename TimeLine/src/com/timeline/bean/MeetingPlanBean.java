package com.timeline.bean;

import java.util.ArrayList;
import java.util.List;

public class MeetingPlanBean {
	private String id;
	private String subject;
	private String time;
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

	private List<MeetingDetailPlanBean> details = new ArrayList<MeetingDetailPlanBean>();

	public String getTime() {
		return time;
	}

	public void setTime(String timeString) {
		this.time = timeString;
	}

	public List<MeetingDetailPlanBean> getDetails() {
		return details;
	}

	public void setDetails(List<MeetingDetailPlanBean> details) {
		this.details = details;
	}

}
