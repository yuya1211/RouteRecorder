package com.yuya.routerecorder;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TripTable {

	public static final String TABLE_TRIP = "trip";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TRIP_NAME = "trip_name";
	public static final String COLUMN_TRIP_DATE = "trip_date";
	public static final String COLUMN_TRIP_DESC = "trip_desc";
	
	private static final String DATABASE_CREATE = "CREATE TABLE "
							+ TABLE_TRIP
							+ " ( "
							+ COLUMN_ID + " INTEGER PRIMARY KEY, "
							+ COLUMN_TRIP_NAME + " TEXT, "
							+ COLUMN_TRIP_DATE + " TEXT, "
							+ COLUMN_TRIP_DESC + " TEXT"
							+ " )";
	
	public static void onCreate(SQLiteDatabase database){
			database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
														  int newVersion){
		
		 Log.w(RouteTable.class.getName(), "Upgrading database from version "
			        + oldVersion + " to " + newVersion
			        + ", which will destroy all old data");
		 
		 database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
		 onCreate(database);
		
	}
	
	

}
