package com.howell.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "ecamera_museum.db";
	private static final int DATABASE_VERSION = 1;
	
	public DBHelper(Context context) {
		//CursorFactory设置为null,使用默认值
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//数据库第一次被创建时onCreate会被调用
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS map_item" +
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT" +
				",deviceId VARCHAR " +
				",componentId VARCHAR" +
				",mapId VARCHAR" +
				",x_position REAL" +
				",y_position REAL" +
				",itemType VARCHAR"+
				",angle REAL)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS map" +
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT" +
				",mapId VARCHAR " +
				",mapName VARCHAR" +
				",comment VARCHAR" +
				",mapFormat VARCHAR " +
				",mapDataPath VARCHAR" +
				",MD5Code VARCHAR" +
				",lastModificationTime VARCHAR)");
		
		//isAlarmed 0:false 1:true
		db.execSQL("CREATE TABLE IF NOT EXISTS alarm_list" +
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT" +
				",componentId VARCHAR " +
				",name VARCHAR" +
				",mapId VARCHAR"+
				",eventType VARCHAR" +
				",eventState VARCHAR " +
				",time VARCHAR" +
				",description VARCHAR" +
				",eventId VARCHAR" +
				",imageUrl VARCHAR" +
				",isAlarmed INTEGER)");
	}

	//如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
	}
}
