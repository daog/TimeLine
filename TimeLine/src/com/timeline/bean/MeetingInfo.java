package com.timeline.bean;

import java.io.Serializable;
import java.util.Date;

import com.timeline.common.DateTimeHelper;
import com.timeline.common.StringUtils;

import android.R.integer;

public class MeetingInfo implements Serializable {
	private String id;
    private String subject;
    private String describe;
    private String address;
    private String start_time; //���鿪ʼʱ�䣨��ÿ��0��0��0�뿪ʼ���㣬��λ�롣��
    private String end_time; //�������ʱ�䣨��ÿ��0��0��0�뿪ʼ���㣬��λ�롣��
    private String servey_url;//���黥����ҳ����
    private String servey_st; //���黥����ҳ��������״̬  1 ����  2 �ر�
	private String start_date;
    private String end_date;
    private String sponsor_name;
    private String join_st;  //�������״̬ 1�ѱ��� 2���� 3�ܾ� 4�޲���
    



	private String IncludeDayStr;//���龭�������ڣ�������鿪ʼ������2016-07-24������������2016-07��30�������2016-07-24��2016-07-30֮���ÿһ�춼�����Ǹ����Ե�ֵ��

    
    
    public MeetingInfo() {
    }

    public MeetingInfo(String id) {
        this.id = id;
    }

    public MeetingInfo(String id, String subject, String describe, String address, String start_time, String end_time, String servey_url, String servey_st, String start_date, String end_date, String repeate, String alertbeforetime, String Edit_TX1, String Edit_TX2, String Edit_TX3) {
        this.id = id;
        this.subject = subject;
        this.describe = describe;
        this.address = address;
        this.start_time = start_time;
        this.end_time = end_time;
        this.servey_url = servey_url;
        this.servey_st = servey_st;
        this.start_date = start_date;
        this.end_date = end_date;
        this.repeate = repeate;
        this.alertbeforetime = alertbeforetime;
    }
    
    public String getSponsor() {
		return sponsor_name;
	}
	public void setSponsor(String sponsor) {
		this.sponsor_name = sponsor;
	}
    public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
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
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getServey_url() {
		return servey_url;
	}
	public void setServey_url(String servey_url) {
		this.servey_url = servey_url;
	}
	public String getServey_st() {
		return servey_st;
	}
	public void setServey_st(String servey_st) {
		this.servey_st = servey_st;
	}

	

	private String repeate;//�ظ�����
	private String alertbeforetime;//��ǰ����ʱ��
	public String getRepeate() {
		return repeate;
	}
	public void setRepeate(String repeate) {
		this.repeate = repeate;
	}
	public String getAlertbeforetime() {
		return alertbeforetime;
	}
	public void setAlertbeforetime(String alertbeforetime) {
		this.alertbeforetime = alertbeforetime;
	}

	
	private int timeno =0; 
    public int getTimeno() {
		return timeno;
	}
	public void setTimeno(int timeno) {
		this.timeno = timeno;
	}
	
	private String lastAlertTime;
	
    public String getLastAlertTime() {
		return lastAlertTime;
	}

	public void setLastAlertTime(String lastAlertTime) {
		this.lastAlertTime = lastAlertTime;
	}

	/**
	 * ��ȡ��ʼ���ڸ�ʽ���ַ�����yyyy-MM-dd��
	 * @return��ʼ���ڸ�ʽ���ַ���
	 */
	public String getStartDateStr(String strFormat){
		if(StringUtils.isEmpty(start_date)){
			return "";
		}
		if(StringUtils.isEmpty(strFormat)){
			strFormat = "yyyy-MM-dd";
		}
		
		Long dateLong = Long.valueOf(start_date)*1000;
		Date date = new Date(dateLong);
		return DateTimeHelper.DateToString(date,strFormat);
	}
	
	/**
	 * ��ȡ��ʼ����
	 * @return��ʼ����
	 */
	public Date getStartDate(){
		if(StringUtils.isEmpty(start_date)){
			return null;
		}
			Long dateLong = Long.valueOf(start_date)*1000;
			Date date = new Date(dateLong);
			return date;

	}
	
	/**
	 * ��ȡ�������ڸ�ʽ���ַ�����yyyy-MM-dd��
	 * @return�������ڸ�ʽ���ַ���
	 */
	public String getEndDateStr(String strFormat){
		if(StringUtils.isEmpty(end_date)){
			return "";
		}
		if(StringUtils.isEmpty(strFormat)){
			strFormat = "yyyy-MM-dd";
		}
		Long dateLong = Long.valueOf(end_date)*1000;
		Date date = new Date(dateLong);
		return DateTimeHelper.DateToString(date,strFormat);
	}
	
	/**
	 * ��ȡ��������
	 * @return��������
	 */
	public Date getEndDate() {
		if (StringUtils.isEmpty(end_date)) {
			return null;
		}
		Long dateLong = Long.valueOf(end_date)*1000;
		Date date = new Date(dateLong);
		return date;
	}

	public String getIncludeDayStr() {
		return IncludeDayStr;
	}

	public void setIncludeDayStr(String includeDayStr) {
		IncludeDayStr = includeDayStr;
	}

    public String getJoin_st() {
		return join_st;
	}

	public void setJoin_st(String join_st) {
		this.join_st = join_st;
	}


	private String Edit_TX1;
    private String Edit_TX2;
    private String Edit_TX3;
    public String getEdit_TX1() {
        return Edit_TX1;
    }

    public void setEdit_TX1(String Edit_TX1) {
        this.Edit_TX1 = Edit_TX1;
    }

    public String getEdit_TX2() {
        return Edit_TX2;
    }

    public void setEdit_TX2(String Edit_TX2) {
        this.Edit_TX2 = Edit_TX2;
    }

    public String getEdit_TX3() {
        return Edit_TX3;
    }

    public void setEdit_TX3(String Edit_TX3) {
        this.Edit_TX3 = Edit_TX3;
    }
}
