package com.timeline.bean;

import java.io.Serializable;
import java.util.Date;

import com.timeline.common.DateTimeHelper;
import com.timeline.common.StringUtils;

import android.R.integer;

public class MeetingInfo implements Serializable ,Comparable {
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
    
    private int daystate = 0;//1ֻ��һ�� 2�ڿ�ʼ���� 3�ڽ������� 4���м�
    



	public int getDaystate() {
		return daystate;
	}

	public void setDaystate(int daystate) {
		this.daystate = daystate;
	}



	private String IncludeDayStr;//���龭�������ڣ�������鿪ʼ������2016-07-24������������2016-07��30�������2016-07-24��2016-07-30֮���ÿһ�춼�����Ǹ����Ե�ֵ��

	private long timelong;//ʱ�䳤��
    
    
    public String getSponsor_name() {
		return sponsor_name;
	}

	public void setSponsor_name(String sponsor_name) {
		this.sponsor_name = sponsor_name;
	}

	public long getTimelong() {
		long start=Long.valueOf(start_time);
		long end=Long.valueOf(end_time);
		return (end-start);
	}

	public void setTimelong(long timelong) {
		this.timelong = timelong;
	}

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
	 * ��ȡ��ʼ����ʱ��
	 * @return��ʼ����
	 */
	public Date getStartDateTime(){
		if(StringUtils.isEmpty(start_date)||StringUtils.isEmpty(start_time)){
			return null;
		}
			Long dateLong = (Long.valueOf(start_date)+Long.valueOf(start_time))*1000;
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

	/**
	 * ��ȡ��������ʱ��
	 * @return��ʼ����
	 */
	public Date getEndDateTime(){
		if(StringUtils.isEmpty(end_date)||StringUtils.isEmpty(end_time)){
			return null;
		}
			Long dateLong = (Long.valueOf(end_date)+Long.valueOf(end_time))*1000;
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
    private String Edit_TX2;//���������¼���ID
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
    @Override  
    public int compareTo(Object obj) {  
        if (obj instanceof MeetingInfo) {  
        	MeetingInfo stu = (MeetingInfo) obj;  
            return (int) (stu.getTimelong()-getTimelong() );  
        }  
        return 0;  
    } 
//	@Override
//	public int compareTo(Object another) {
//		// TODO Auto-generated method stub
//		MeetingInfo in = (MeetingInfo)another;
//		//return getTimelong()>in.getTimelong()?0:(getTimelong()==in.getTimelong()?-1:0);
//		return getTimelong()>in.getTimelong()?1:(getTimelong()==in.getTimelong()?0:-1);
//	}
}
