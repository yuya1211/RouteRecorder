package com.example.routerecorder;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper{
	
	private final static String DATABASE_NAME = "route.db";
	private final static int DATABASE_VERSION = 7;
							
	
	public DbHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase database){
		RouteTable.onCreate(database);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		
		RouteTable.onUpgrade(database, oldVersion, newVersion);
		
	}



}
