package com.example.routerecorder;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class ListRoutesActivity extends Activity{
	

	
	public final static String EXTRA_TRIPID = "com.example.routerecorder.TRIP_ID_SELECTED";

	private ExpandableListView listview;

	@Override
	protected void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.list_route);
		
		if(checkDataBase() == true){
		showListView();}
	}
	
	
	public void showListView(){
			
		List< List < String >> tripGroup = new  ArrayList< List< String >>();
		List<List< String >> placeChild = new ArrayList<List <String >>();
		
		
		 String[] projection2 = {
				 RouteTable.COLUMN_TRIP_ID,
				 RouteTable.COLUMN_TRIP_NAME,
				 RouteTable.COLUMN_TRIP_DESC
		 };		 
		 
		 String[] projectionChild = {
				 RouteTable.COLUMN_PLACE,
				 RouteTable.COLUMN_TRIP_ID
		 };
		 		 
		 Cursor cursor2 = getContentResolver().
				 query(RouteContentProvider.CONTENT_URI_TRIP, projection2, null, null, null);
		 Cursor cursorChild; 
		 
		 if(cursor2 != null && checkDataBase() ){
			 cursor2.moveToFirst();
			 do{		 
				 
				 Log.d("QQ", "FINAL CHECK IN CURSOR2");
				 String tripId = cursor2.getString(
						 		cursor2.getColumnIndexOrThrow(RouteTable.COLUMN_TRIP_ID));
				 String tripName = cursor2.getString(
						 		cursor2.getColumnIndexOrThrow(RouteTable.COLUMN_TRIP_NAME));
				 String tripDesc = cursor2.getString(
						 		cursor2.getColumnIndexOrThrow(RouteTable.COLUMN_TRIP_DESC));
				 
				 ArrayList<String> tripAttr = new ArrayList<String>();
				 tripAttr.add(tripId);
				 tripAttr.add(tripName);
				 tripAttr.add(tripDesc); 
				 
				 
				 tripGroup.add( tripAttr );
				 
				 Log.d("QQ", "WE GOT HERE 1");
				 
				 //building list child content for each trip
				// StringBuilder placeString = new StringBuilder(); //what we want to show on the list
				 String selectionClause = RouteTable.COLUMN_TRIP + "=?";
				 String[] selectionArgs = new String[]{ tripId };
				 Log.d("QQ", "TRIP ID = " + tripId);
				 cursorChild = getContentResolver().
						 		query(RouteContentProvider.CONTENT_URI_ROUTE, 
						 				projectionChild, selectionClause, selectionArgs, null);
				 
				 List<String> placeNames = new ArrayList< String >();
				 if (cursorChild != null){
					 cursorChild.moveToFirst();
					
					 do{
						 String placeTemp = cursorChild.getString(
								 			cursorChild.getColumnIndexOrThrow(RouteTable.COLUMN_PLACE));
						 placeNames.add( placeTemp);
						  
					 }while(cursorChild.moveToNext());
					 				 
				 }
				 
				 placeChild.add(placeNames);
				 			 
			 }while(cursor2.moveToNext());
			 
		 }//end of if
			
		 MyListFragment fragment = new MyListFragment();
			 ExpandableListAdapter adapter = new routeExpandableListAdapter(fragment,
					 					this, tripGroup, placeChild);
			 
			 listview = (ExpandableListView) findViewById(R.id.RouteListView);
			 listview.setItemsCanFocus(true); //???
			 DisplayMetrics metrics = new DisplayMetrics();
		     getWindowManager().getDefaultDisplay().getMetrics(metrics);
			 int width = metrics.widthPixels;
		     int scale = (int) Math.ceil(getResources().getDisplayMetrics().density);
			 listview.setIndicatorBounds(width - scale * 60, width - scale * 15);
			 listview.setAdapter(adapter);
			 
			 			 
			 cursor2.close();	 
			 
		
		 		
	}//end of showlistview
	
	public void expandGroup( final int groupPosition){
		
		if( listview.isGroupExpanded(groupPosition))
			listview.collapseGroup(groupPosition);
		else
			listview.expandGroup(groupPosition);
	}
	
	public void deleteItem( String tripId){
		
		String selectionClauseRoute = RouteTable.COLUMN_TRIP + "=?";
		String[] selectionArgRoute = new String[] {tripId};
		
		String selectionClauseTrip = RouteTable.COLUMN_TRIP_ID + "=?";
		String[] selectionArgTrip = new String[] {tripId};
		
		getContentResolver().
				delete(RouteContentProvider.CONTENT_URI_ROUTE, selectionClauseRoute, selectionArgRoute);
		getContentResolver().
				delete(RouteContentProvider.CONTENT_URI_TRIP, selectionClauseTrip, selectionArgTrip);
		
		Log.d("QQ", "ARE WE IN THE DELETE METHOD? 1");
		showListView();
		Log.d("QQ", "ARE WE IN THE DELETE METHOD? 2");
	}
	
	public void showOnMap(String tripId){
		Intent intent = new Intent(this, RouteActivity.class);
		intent.putExtra(EXTRA_TRIPID, tripId);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
		
	}
	
	private boolean checkDataBase(){
		
		 String[] projectionTrip = {
				 //tripId in the RouteTable
				 RouteTable.COLUMN_TRIP_ID
		 };
		
		 Cursor mCursor = getContentResolver().
				 query(RouteContentProvider.CONTENT_URI_TRIP, projectionTrip, 
						 null, null, null);

		 Boolean rowExists;

		 if (mCursor.moveToFirst())
		    // DO SOMETHING WITH CURSOR
		   rowExists = true;
		 else
		    // I AM EMPTY
		    rowExists = false;
		 return rowExists;
		 
		}//end of method cheackdatabase	
 

	
	
	
	


}
