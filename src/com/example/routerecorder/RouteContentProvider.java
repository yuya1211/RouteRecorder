package com.example.routerecorder;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class RouteContentProvider extends ContentProvider {
	
	private DbHelper database;
	
	private static final int ROUTE = 1;
	private static final int ROUTE_ID = 2;
	
	private static final int TRIP = 3;
	private static final int TRIP_ID = 4;
	
	private static final String AUTHORITY = "com.example.routerecorder.RouteContentProvider";
	private static final String BASE_PATH = "route";
	private static final String BASE_PATH_TRIP = "trip";
	
	public static final Uri CONTENT_URI_ROUTE = 
			Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	public static final Uri CONTENT_URI_TRIP = 
			Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_TRIP); 
	
	//public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "route";
	//public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "route_id";
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, ROUTE);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#" ,ROUTE_ID);
		
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_TRIP, TRIP);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH_TRIP + "/#", TRIP_ID);
	}
	
	
	@Override
	public boolean onCreate() {
		database = new DbHelper( getContext() );
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		//checkCOlumns(projection);		
		Log.d("@@",	"invalid tables");
		
		
		int uriType = sURIMatcher.match(uri);
		Integer uriTYPE = uriType;
		switch(uriType){
		case ROUTE:
			queryBuilder.setTables(RouteTable.TABLE_ROUTE);
			break;
		case ROUTE_ID:
			queryBuilder.setTables(RouteTable.TABLE_ROUTE);
			queryBuilder.appendWhere( RouteTable.COLUMN_ID + "="
										+ uri.getLastPathSegment() );
			break;
		case TRIP:
			queryBuilder.setTables(RouteTable.TABLE_TRIP);
			break;
		case TRIP_ID:
			queryBuilder.setTables(RouteTable.TABLE_TRIP);
			queryBuilder.appendWhere(RouteTable.COLUMN_TRIP_ID + "=" 
										+ uri.getLastPathSegment() );
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}// end of switch
		SQLiteDatabase db = database.getWritableDatabase();
		Log.d("@@",	"invalid tables 4");
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, 
											null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}
	

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase db = database.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType){
		case ROUTE:
			rowsDeleted = db.delete(RouteTable.TABLE_ROUTE, selection, selectionArgs);
			break;
		case ROUTE_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)){
				rowsDeleted = db.delete(RouteTable.TABLE_ROUTE, 
										RouteTable.COLUMN_ID + "=" + id , null);
			}
			else{
				rowsDeleted = db.delete(RouteTable.TABLE_ROUTE, 
										RouteTable.COLUMN_ID + "=" + id + " and " + selection,
										selectionArgs);								
			}
			break;
		case TRIP:
			rowsDeleted = db.delete(RouteTable.TABLE_TRIP, selection, selectionArgs);
			break;
		case TRIP_ID:
			String  id_trip = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)){
				rowsDeleted = db.delete(RouteTable.TABLE_TRIP, 
										RouteTable.COLUMN_TRIP_ID + "=" + id_trip, null);
			}
			else{
				
				rowsDeleted = db.delete(RouteTable.TABLE_TRIP,
										RouteTable.COLUMN_TRIP_ID + "=" + id_trip + " and " + selection,
										selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);			
		}
		
		getContext().getContentResolver().notifyChange(uri, null);	
		return rowsDeleted;
	}

	@Override
	public String getType(Uri arg0) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		Log.d("QQ", "DATABASE HERE 0");
		SQLiteDatabase db = database.getWritableDatabase();	
		Log.d("QQ", "DATABASE HERE 2");
		long id = 0;
		
		switch (uriType){
		case ROUTE:
			Log.d("QQ", "DATABASE HERE 3");
			id = db.insert(RouteTable.TABLE_ROUTE, null, values);
			break;
		case TRIP:
			Log.d("QQ", "DATABASE HERE 4");
			id = db.insert(RouteTable.TABLE_TRIP, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);	}
		
		//id = db.insert(RouteTable.TABLE_ROUTE, null, values);
		
		
		//getContext().getContentResolver().notifyChange(uri, null);	
		return Uri.parse(BASE_PATH + "/" + id);
	}

	

	

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase db = database.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType){
		case ROUTE:
			rowsUpdated = db.update(RouteTable.TABLE_ROUTE, values, selection, selectionArgs);
			break;		
		case ROUTE_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)){
				rowsUpdated = db.update(RouteTable.TABLE_ROUTE, values, 
										RouteTable.COLUMN_ID + "=" + id, null);
										
			}else{
				rowsUpdated = db.update(RouteTable.TABLE_ROUTE, values, 
										RouteTable.COLUMN_ID + "=" + id + " and " + selection, 
										selectionArgs);								
			}
			break;
		case TRIP:
			rowsUpdated = db.update(TripTable.TABLE_TRIP, values, selection, selectionArgs);
			break;
		case TRIP_ID:
			String id_trip = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)){
			rowsUpdated = db.update(TripTable.TABLE_TRIP, values, 
									TripTable.COLUMN_ID + "=" + id_trip, null);
			}else{
				rowsUpdated = db.update(TripTable.TABLE_TRIP, values, 
										TripTable.COLUMN_ID + "=" + id_trip + " and " + selection, 
										selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

}
