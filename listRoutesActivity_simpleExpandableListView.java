package com.example.routerecorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.widget.SimpleExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class ListRoutesActivity extends Activity{
	
	public final static String EXTRA_TRIPID = "com.example.routerecorder.TRIP_ID_SELECTED";
	
	private ExpandableListView listview;

	@Override
	protected void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.list_route);
		
		if(checkDataBase() == true)
		showListView();
	}
	
	
	public void showListView(){
				
		List<HashMap <String, String >> fillMaps = new ArrayList<HashMap<String, String>>();
		List<List<HashMap<String, String>>> fillChildMaps = new ArrayList<List<HashMap<String, String>>>();
		List<HashMap<String, String>> children;
		HashMap<String, String> map;
		
		String[] from = new String[] {"route_1", "route_2", "route_3"};
		int[] to = new int[] {R.id.route_1, R.id.route_2, R.id.route_3};		
		
		String[] childFrom = new String[] {"place", "delete_btn", "show_btn"};
		int[] childTo = new int[] {R.id.place_name, R.id.delete_trip_btn, R.id.show_trip_btn};
		
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
		 				
		 if(cursor2 != null){
			 cursor2.moveToFirst();
			 do{		 
				 String TripId = cursor2.getString(
						 cursor2.getColumnIndexOrThrow(RouteTable.COLUMN_TRIP_ID));
				 String TripName = cursor2.getString(
						 cursor2.getColumnIndexOrThrow(RouteTable.COLUMN_TRIP_NAME));
				 String TripDesc = cursor2.getString(
						 cursor2.getColumnIndexOrThrow(RouteTable.COLUMN_TRIP_DESC));
				 
				 map = new HashMap<String, String>();
				 map.put("route_1", "  "+ TripId);
				 map.put("route_2", "  "+ TripName);
				 map.put("route_3", "  "+ TripDesc);
				 
				 fillMaps.add(map);
				 
				 Log.d("QQ", "WE GOT HERE 1");
				 //building list child content for each trip
				 StringBuilder placeString = new StringBuilder(); //what we wanna show on the list
				 String selectionClause = RouteTable.COLUMN_TRIP + "=?";
				 String[] selectionArgs = new String[]{ TripId };
				 Log.d("QQ", "TRIP ID = " + TripId);
				 cursorChild = getContentResolver().
						 		query(RouteContentProvider.CONTENT_URI_ROUTE, 
						 				projectionChild, selectionClause, selectionArgs, null);
				 Log.d("QQ", "WE GOT HERE 2");
				 if (cursorChild != null){
					 cursorChild.moveToFirst();								 
					 do{
						 
						 String placeTemp = cursorChild.getString(
								 cursorChild.getColumnIndexOrThrow(RouteTable.COLUMN_PLACE));
						 
						 if (placeTemp != null){
						 placeString.append(placeTemp);
						 if (!cursorChild.isLast())
						 placeString.append("\r\n"); }
						 
						 
					 }while(cursorChild.moveToNext());
					 
					 
				 }
				 
				 map = new HashMap<String, String>();
				 children = new ArrayList<HashMap<String, String>>();
				 map.put("place", placeString.toString());
				 map.put("place", "aloha");
				 children.add(map);
				 fillChildMaps.add(children);
				 
				 
			 }while(cursor2.moveToNext());
			 		 
			 
			 ExpandableListAdapter adapter = new SimpleExpandableListAdapter(
					 					this, fillMaps, R.layout.list_route_collapsed, from, to 
					 						,fillChildMaps, R.layout.list_route_expanded, childFrom, childTo);
			 
			 listview = (ExpandableListView) findViewById(R.id.RouteListView);
			 listview.setItemsCanFocus(true); //???
			 DisplayMetrics metrics = new DisplayMetrics();
		     getWindowManager().getDefaultDisplay().getMetrics(metrics);
			 int width = metrics.widthPixels;
		     int scale = (int) Math.ceil(getResources().getDisplayMetrics().density);
			 listview.setIndicatorBounds(width - scale * 60, width - scale * 15);
			 listview.setAdapter(adapter);
			 
			
			 OnChildClickListener onChildClickListener = new OnChildClickListener()
			 { 
				 @Override
				 public boolean onChildClick(ExpandableListView parent, View v, int position, int childPosition, long id){
				 Log.d("@@", listview.getItemAtPosition(childPosition).toString());
				 String routeItems = listview.getItemAtPosition(position).toString();
				 
				 	 
				 //Log.d("@@", routeItems.toString());
				 //String[] route2Items = routeItems[2].split("=");
				 //Log.d("@@", route2Items.toString());
				 //String tripIdToSend = route2Items[1].substring(0, route2Items[1].length()-1).trim();				 
				 //showOnMap(tripIdToSend);	
				 
				 return true;
				 }
			 };
			 
			 listview.setOnChildClickListener( onChildClickListener);
					 
			 		 
			 
			 cursor2.close();	 
			 
		 }//end of if
		 		
	}//end of showlistview
	
	
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
				 RouteTable.COLUMN_ID
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
