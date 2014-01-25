package com.yuya.routerecorder;

import java.util.ArrayList;
import java.util.List;

import com.yuya.routerecorder.R;

import android.app.AlertDialog;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class MyListFragment extends Fragment{
	

	
	public final static String EXTRA_TRIPID = "com.example.routerecorder.TRIP_ID_SELECTED";
	private static String tripSelected;
	private ExpandableListView listview;
	private View v;
	private Integer listCount = 0;

	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		
		
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_route, container, false);
		
		if(checkDataBase() == true){
		showListView( v );}
	return v;
	}
	
	public void showListView(View v){
			
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
		 		 

		
		 Cursor cursor2 = getActivity().getContentResolver().
				 query(RouteContentProvider.CONTENT_URI_TRIP, projection2, null, null, null);
		 Cursor cursorChild; 

		 
		 if(cursor2 != null && checkDataBase() ){
			 cursor2.moveToFirst();
			 do{		 
				 
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

				 listCount ++;
				 				 
				 
				 //building list child content for each trip
				// StringBuilder placeString = new StringBuilder(); //what we want to show on the list
				 String selectionClause = RouteTable.COLUMN_TRIP + "=?";
				 String[] selectionArgs = new String[]{ tripId };
				 cursorChild = getActivity().getContentResolver().
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
		
			 ExpandableListAdapter adapter = new routeExpandableListAdapter(this,
					 getActivity(), tripGroup, placeChild);

			 
			 listview = (ExpandableListView) v.findViewById(R.id.RouteListView);
			 listview.setItemsCanFocus(true); //???
			 DisplayMetrics metrics = new DisplayMetrics();
			 getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
		
		//if(getActivity().getContentResolver() == null)
		getActivity().getContentResolver().
				delete(RouteContentProvider.CONTENT_URI_ROUTE, selectionClauseRoute, selectionArgRoute);
		//Log.d("QQ", "what the hell......." + tripId);
		getActivity().getContentResolver().
				delete(RouteContentProvider.CONTENT_URI_TRIP, selectionClauseTrip, selectionArgTrip);
		
		showListView( v );
		
	}
	
	public void showOnMap(String tripId){
		/*Intent intent = new Intent(getActivity(), RouteActivity.class);
		intent.putExtra(EXTRA_TRIPID, tripId);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		getActivity().finish();*/
		//FragmentTabs tabHost = new FragmentTabs();
		//tabHost.setTrip(tripId);
		//Intent dataIntent  = new Intent(getActivity(), RouteMapFragment.class);
		//dataIntent.setAction("com.example.routerecorder.DATA_BROADCAST");
		//dataIntent.putExtra("TripId", tripId);
		//getActivity().sendBroadcast(dataIntent);	
		//getActivity().getActionBar().setSelectedNavigationItem(0);
		
		setTrip(tripId);
		getActivity().getActionBar().setSelectedNavigationItem(0);

		
	}
	
	private boolean checkDataBase(){
		
		 String[] projectionTrip = {
				 //tripId in the RouteTable
				 RouteTable.COLUMN_TRIP_ID
		 };
		
		 Cursor mCursor = getActivity().getContentResolver().
				 query(RouteContentProvider.CONTENT_URI_TRIP, projectionTrip, 
						 null, null, null);

		 Boolean rowExists;

		 if (mCursor.moveToFirst()){
		    // DO SOMETHING WITH CURSOR
		   rowExists = true;}
		 else{
		    // I AM EMPTY
		    rowExists = false;}
		 return rowExists;
		 
		}//end of method cheackdatabase	
 

	public String getTrip(){
		return tripSelected;
	}
	
	public void setTrip(String trip){
		this.tripSelected = trip;
		}
	
	
	


}
