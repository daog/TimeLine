package com.timeline.bean;

import java.util.List;
import java.util.Map;

public class JobBase {

	public Map<String, String> getPosition() {
		return position;
	}
	public void setPosition(Map<String, String> position) {
		this.position = position;
	}
	public Map<String, String> getJob_title() {
		return job_title;
	}
	public void setJob_title(Map<String, String> job_title) {
		this.job_title = job_title;
	}
	private Map<String, String> position;// 职务
	private Map<String, String> job_title;// 职称
	

}
