package com.timeline.bean;

public class MeetingSerchBean {
	private String id;
	private String subject;
	private String sponsor;
	private boolean personal=false ;

	public boolean isPersonal() {
		return personal;
	}
	public void setPersonal(boolean personal) {
		this.personal = personal;
	}
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
	public String getSponsor() {
		return sponsor;
	}
	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

}
