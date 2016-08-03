package com.timeline.sqlite;


import java.util.List;

import com.timeline.bean.MeetingInfo;
import com.timeline.sqlite.DaoMaster.DevOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class InfoHelper {
	private SQLiteDatabase DJdb;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	
	private MeetingInfoDao meetingInfodao;
	
	public synchronized DaoSession InitDAOSession(Context context){
		String DataBaseName =Environment.getExternalStorageDirectory().getAbsolutePath()+"/TIMELINE/"+"Common.sdf";
		
		try {
			DevOpenHelper helper = new DevOpenHelper(context,
					DataBaseName, null);
			DJdb = helper.getWritableDatabase();
			daoMaster = new DaoMaster(DJdb);
			daoSession = daoMaster.newSession();
			return daoSession;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void insertInfo(Context context,MeetingInfo entity) {
		try {
			daoSession = InitDAOSession(context);
			meetingInfodao  = daoSession.getMeetingInfoDao();
			meetingInfodao.insert(entity);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public void updateInfo(Context context,MeetingInfo entity) {
		try {
			daoSession = InitDAOSession(context);
			meetingInfodao  = daoSession.getMeetingInfoDao();
			meetingInfodao.update(entity);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void deleteInfo(Context context,MeetingInfo entity) {
		try {
			daoSession = InitDAOSession(context);
			meetingInfodao  = daoSession.getMeetingInfoDao();
			meetingInfodao.delete(entity);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public List<MeetingInfo> loadInfo(Context context) {
		try {
			daoSession = InitDAOSession(context);
			meetingInfodao  = daoSession.getMeetingInfoDao();
			return meetingInfodao.loadAll();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
}
