package com.example.routerecorder;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddRouteActivity extends Activity implements OnClickListener{
	
	public final static String EXTRA_TRIP_ID = "com.example.routerecorder.TRIP_ID";
	public final static String EXTRA_TRIP_NAME = "com.example.routerecorder.TRIP_NAME";
	public static int colorIndex = 0;

	private EditText mTripName;
	//private DatePicker mTripStartDate;
	//private DatePicker mTripEndDate;
	private EditText mTripDesc;
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.add_route);
		
		mTripName = (EditText) findViewById(R.id.trip_name_2);
		//mTripStartDate = (DatePicker) findViewById(R.id.trip_start_date);
		//mTripEndDate = (DatePicker) findViewById(R.id.trip_end_date);
		mTripDesc = (EditText) findViewById(R.id.trip_desc);
		Button addButton = (Button) findViewById(R.id.addTrip_button);
		
		
		addButton.setOnClickListener(this);
		
		
	}
	

	
	@Override
	public void onClick(View v) {
		
		String TripId2 = null;
		
		String myTripName = mTripName.getText().toString();
		String myTripDesc = mTripDesc.getText().toString();
		

		
		if(myTripName.trim().equalsIgnoreCase("")){
			Toast toast = Toast.makeText(getBaseContext(), "Trip Name is Empty", 
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,0);
			toast.show();
			return;
		}
		
		if(myTripDesc.trim().equalsIgnoreCase("")){
			Toast toast = Toast.makeText(getBaseContext(), "Trip Description is Empty", 
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
			
			return;
		}
		
		int id = v.getId();
		if ( id == R.id.addTrip_button){
			//generate a color for this trip		
			//int R = (int)(Math.random( )*256);
			//int G = (int)(Math.random( )*256);
			//how many rows are there in the trip table?
			String[] projectionColor = {
					RouteTable.COLUMN_TRIP_ID
			};
			Cursor mCursorColor = getContentResolver().query(
					RouteContentProvider.CONTENT_URI_TRIP, projectionColor, null, null, null);
			int numRows = 0;
			if(mCursorColor != null & mCursorColor.moveToFirst()){
				mCursorColor.moveToLast();
				String numRowsStr = mCursorColor.getString(
						mCursorColor.getColumnIndexOrThrow(RouteTable.COLUMN_TRIP_ID));
				numRows = Integer.parseInt(numRowsStr);
			}
			
			ArrayList<Integer> colorList = new ArrayList<Integer>();
			colorList.add( -7829368);//grey
			colorList.add( -16776961);//blue
			colorList.add( -16711681);//cyan
			colorList.add( -12303292);//dark grey			
			colorList.add( -16711936);//green
			colorList.add( -65281);//mangma
			colorList.add( -65536);//red
			colorList.add(  -1);//white
			colorList.add( -256);//yellow
			
			Integer routeColor = colorList.get( numRows%10 );
			Log.d("QQ" , "IN ADD ROUTE 0");
			
			ContentValues values = new ContentValues();
			values.put(RouteTable.COLUMN_TRIP_NAME, myTripName);
			values.put(RouteTable.COLUMN_TRIP_DESC, myTripDesc);
			Log.d("QQ" , "IN ADD ROUTE 1");
			values.put(RouteTable.COLUMN_TRIP_COLOR, routeColor.toString());
			
			Log.d("QQ" , "IN ADD ROUTE 2");			
			getContentResolver().insert(RouteContentProvider.CONTENT_URI_TRIP, values);	
			
			Log.d("QQ" , "IN ADD ROUTE 3");
			//get trip _id value from TABLE_TRIP
			String[] projection = {
					RouteTable.COLUMN_TRIP_ID
			};
			
			Cursor mCursor = getContentResolver().query(
					RouteContentProvider.CONTENT_URI_TRIP, projection, null, null, null);
			if(mCursor != null){
				mCursor.moveToLast();
				TripId2 = mCursor.getString(
						mCursor.getColumnIndexOrThrow(RouteTable.COLUMN_TRIP_ID));	
			}
			
			Log.d("@@",  "id number of the trip "+ TripId2);
			
			//send tripId to another activity(AddPlaceActivity.class)
			Intent i = new Intent(this, AddPlaceActivity.class);		
			i.putExtra(EXTRA_TRIP_ID, 	TripId2);
			i.putExtra(EXTRA_TRIP_NAME, myTripName);
			startActivity(i);
	
		}//end of switch
			
		
	
	}


}
