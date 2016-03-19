package com.tuling.robot.helper;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{

	public DBOpenHelper(Context context, String name) {
		super(context, name, null, 1);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table if not exists history(_id integer primary key autoincrement, type integer not null, msg_type integer not null, content text not null, date text not null)");
		db.execSQL("create table if not exists events(_id integer primary key autoincrement, info text not null, register_date text not null, warn_date text not null)");
		
		//db.execSQL("insert into history(type, content, date) values(0, '你多大了?', '2016-03-15 22:00:00')");
		//db.execSQL("insert into history(type, content, date) values(1, '我21', '2016-03-15 22:00:04')");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	};
}
