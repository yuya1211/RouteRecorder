package com.example.routerecorder;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.example.routerecorder.Globals;
import com.example.routerecorder.DbHelper;



public class RouteMapFragment extends Fragment {
	
	
	
	private DbHelper dbhelper = null;
	private String id;
	AddRouteFragment tripData = new AddRouteFragment();
	MyListFragment tripData2 = new MyListFragment();
	private GoogleMap mMap;
	//private Polyline polyline;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);		
		//getActivity().getContentResolver().delete(RouteContentProvider.CONTENT_URI_TRIP,	null, null);
    	//getActivity().getContentResolver().delete(RouteContentProvider.CONTENT_URI_ROUTE,	null, null);
    	
		
		
	}// end of onCreate
	
	
	@Override
	public void onResume(){
		super.onResume();
		
		setUpMapIfNeeded();		
		
	}
	private static View v;
	 @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
         //View v = inflater.inflate(R.layout.activity_route, container, false);
         
         if (v != null) {
             ViewGroup parent = (ViewGroup) v.getParent();
             if (parent != null)
                 parent.removeView(v);
         }
         try {
             v = inflater.inflate(R.layout.activity_route, container, false);
         } catch (InflateException e) {
             /* map is already there, just return view as it is */
         }
         
		return v;
         
	 }
	

	private void openDatabase(){
		dbhelper = new DbHelper(getActivity()); 
	}
	
	
	
	private void setUpMapForSingleRoute(String tripId){
		
		 String[] projectionTrip = {
				 //tripId in the RouteTable
				 RouteTable.COLUMN_TRIP_ID,
				 RouteTable.COLUMN_TRIP_COLOR
		 };
								
			String selectionClauseColor = RouteTable.COLUMN_TRIP_ID + "=?";
			String[] selectionArgsColor = new String[]{ tripId };
			
			Cursor cursorColor = getActivity().getContentResolver().
					 query(RouteContentProvider.CONTENT_URI_TRIP, projectionTrip, 
					 selectionClauseColor, selectionArgsColor, null);
		
			int colorInt = 0;
			
			if( cursorColor != null & cursorColor.moveToFirst()){
				 String TripColorCurrent = cursorColor.getString(
						 cursorColor.getColumnIndexOrThrow(RouteTable.COLUMN_TRIP_COLOR));
				colorInt = Integer.parseInt(TripColorCurrent);
				
			}
						
   				//get points in current trip
            	 String[] projection = {
    					 RouteTable.COLUMN_PLACE,
    					 RouteTable.COLUMN_LAT,
    					 RouteTable.COLUMN_LNG,
    					 RouteTable.COLUMN_TRIP
    			 };		 
            	   	 
            	 String selectionClause = RouteTable.COLUMN_TRIP + "=?";
    			 String[] selectionArgs = new String[]{ tripId };
    			  
    			 Cursor cursor = getActivity().getContentResolver().
    					 query(RouteContentProvider.CONTENT_URI_ROUTE, projection, 
						 selectionClause, selectionArgs, null);
    			 
    			 ArrayList<LatLng> path = new ArrayList<LatLng>();
    			 
    			
		 
    			 if( cursor!=null && cursor.moveToFirst() ){
    				 
    				 double cameraLat = 0.0;
        			 double maxLng = -180.0;
        			 double minLng =  180.0;
        			 int countPlaces = 0;
        			 
    				 //cursor.moveToFirst();
    				 do{
    					 
    					 String PlaceName = cursor.getString(
    							 cursor.getColumnIndexOrThrow(RouteTable.COLUMN_PLACE));
    					 String PlaceLat = cursor.getString(
    							 cursor.getColumnIndexOrThrow(RouteTable.COLUMN_LAT));
    					 String PlaceLng = cursor.getString(
    							 cursor.getColumnIndexOrThrow(RouteTable.COLUMN_LNG));
    					 
    					 Double strLat = Double.parseDouble(PlaceLat);
    					 Double strLng = Double.parseDouble(PlaceLng);
    					 
    					 countPlaces++;
    					 cameraLat += strLat;
    					 if( maxLng < strLng)
    						 maxLng = strLng;
    					 if( minLng > strLng)
    						 minLng = strLng;
    					 
    					// mMap.addMarker(new MarkerOptions().position(
    					//		 new LatLng(strLat, strLng)).title(PlaceName));
    					 
    					 //path.add(new LatLng(strLat, strLng));					 
    					     					 
    				 }while(cursor.moveToNext());
    				 
    				 //mMap.addPolyline((new PolylineOptions())
    				//		 .addAll( path ).width(3).color(colorInt));
    				 cursor.close();
    				 double centerLng = 0.0;
    				 //move the camera to the center of the route
    				 if ( (maxLng - minLng) > 180 )
    					 centerLng = maxLng + ( 360 - (maxLng - minLng) ) / 2;   
    				 else 
    					 centerLng = (maxLng + minLng)/2;
    					 
    				 mMap.moveCamera(CameraUpdateFactory.newLatLng(
    						 new LatLng( cameraLat/countPlaces, centerLng )));
    				 mMap.setMyLocationEnabled(true);
    				 setUpMap();
    				  				 
    			 }//end of if

	}//setUpMapForSingleRoute()
	
	private void setUpMapIfNeeded() {
		
		
		//Log.d("QQ", "here we got the trip id that we selected  " + getTrip());
		//Intent iListView = getActivity().getIntent();
		//String tripSelected = iListView.getStringExtra(ListRoutesActivity.EXTRA_TRIPID);

		String tripSelected  = null;
		if( (tripData.getTrip() != null) &&  (tripData2.getTrip() == null)  )
			tripSelected = tripData.getTrip();
		else if( (tripData.getTrip() == null) &&  (tripData2.getTrip() != null) )
			tripSelected = tripData2.getTrip();
		
			
		Log.d("tripdata" , "tripSelected = " + tripSelected);
//		String tripSelected = receiveData();
		
		openDatabase();
		
		Log.d("QQ", "THE SELECTED TRIP = " + tripSelected );
		
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
        	Log.d("QQ", "WE ARE IN THE mMap = null");
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) ((FragmentActivity) getActivity()).getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
            	mMap.clear();
              	if(checkDataBase() == true){
              		if(tripSelected != null)
              			setUpMapForSingleRoute(tripSelected);
              		else
              			setUpMap();
              		}
            }            
        }
        
        if (mMap != null) {
        	Log.d("QQ", "WE ARE IN THE mMap != null");
        	mMap.clear();
        	if(checkDataBase() == true){		
              		if(tripSelected != null)
              			setUpMapForSingleRoute(tripSelected);
              		else
              			setUpMap();
              		
        		}
        }
    }//end of method setUpMapIfNeeded
	
	private void setUpMap() {	
		 
		 Intent iAddPlace = getActivity().getIntent();
		 double latCenter = iAddPlace.getDoubleExtra(AddPlaceActivity.EXTRA_CENTER_LAT,  0.0 );
		 double lngCenter = iAddPlace.getDoubleExtra(AddPlaceActivity.EXTRA_CENTER_LNG,  0.0);
		 	 
		 String[] projectionTrip = {
				 //tripId in the RouteTable
				 RouteTable.COLUMN_TRIP_ID,
				 RouteTable.COLUMN_TRIP_COLOR
		 };
		
		 Cursor cursorTrip = getActivity().getContentResolver().
				 query(RouteContentProvider.CONTENT_URI_TRIP, projectionTrip, 
						 null, null, null);
		 	
		
		
			 if (cursorTrip != null){
				 cursorTrip.moveToFirst();			
					 	 
				 do{
					 String TripIdCurrent = cursorTrip.getString(
							 cursorTrip.getColumnIndexOrThrow(RouteTable.COLUMN_TRIP_ID));
					 String TripColorCurrent = cursorTrip.getString(
							 cursorTrip.getColumnIndexOrThrow(RouteTable.COLUMN_TRIP_COLOR));
					int colorInt = Integer.parseInt(TripColorCurrent);
					 String[] projection = {
							 RouteTable.COLUMN_PLACE,
							 RouteTable.COLUMN_LAT,
							 RouteTable.COLUMN_LNG,
							 RouteTable.COLUMN_TRIP
					 };		 
					 		
					 String selectionClause = RouteTable.COLUMN_TRIP + "=?";
					 String[] selectionArgs = new String[]{ TripIdCurrent };
					 
					 Cursor cursor = getActivity().getContentResolver().
							 query(RouteContentProvider.CONTENT_URI_ROUTE, projection, 
									 selectionClause, selectionArgs, null);
					
					 ArrayList<LatLng> path = new ArrayList<LatLng>();
			 
					 if (cursor != null ){
						 
						cursor.moveToFirst();
					 do{
						try{
							 
						 String PlaceName = cursor.getString(cursor.getColumnIndexOrThrow(RouteTable.COLUMN_PLACE));
						 String PlaceLat = cursor.getString(cursor.getColumnIndexOrThrow(RouteTable.COLUMN_LAT));
						 String PlaceLng = cursor.getString(cursor.getColumnIndexOrThrow(RouteTable.COLUMN_LNG));
						 Double strLat = Double.parseDouble(PlaceLat);
						 Double strLng = Double.parseDouble(PlaceLng);
							 
						 mMap.addMarker(new MarkerOptions().position(
						 new LatLng(strLat, strLng)).title(PlaceName));
						 path.add( new LatLng(strLat, strLng) ); 
						 }catch(IllegalArgumentException e){
						 }
				        // Move the map so that it is centered on the mutable polyline.
					 }while(cursor.moveToNext());
					 
					 mMap.addPolyline((new PolylineOptions())
							 			.addAll( path ).width(3).color(colorInt));
					 cursor.close();
					 }
			 
					 
				 	}while(cursorTrip.moveToNext());
		 
		 }   //end of if(the cursorTrip != null)
			 
			 
			 //move the camera to the center of the route
			// mMap.moveCamera(CameraUpdateFactory.newLatLng(
			//		 new LatLng( latCenter, lngCenter)));
		 
		 
		 }   //end of showMap
	 
	//actually you don't need this method...
	private boolean checkDataBase(){
		
		 String[] projectionTrip = {
				 //tripId in the RouteTable
				 RouteTable.COLUMN_TRIP_ID
		 };
		
		 Cursor mCursor = getActivity().getContentResolver().
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
		 
		}//end od method cheackdatabase	
	
	
	private String receiveData(){

		String receiveData = null;
		
				BroadcastReceiver receiver = new BroadcastReceiver(){
					@Override
				public void onReceive(Context context , Intent intent){
					String receiveData = intent.getStringExtra("TripId");
					Log.d("QQ", "did we get it?" + receiveData);
					}
				};
	
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.example.routerecorder.DATA_BROADCAST");
		
		getActivity().registerReceiver(receiver, filter);
	
		//FragmentTabs tabHost = new FragmentTabs();
		//return tabHost.getTrip();
		return receiveData;
	}
	
	 @Override
     public void onPause() {
     	super.onPause();
     	tripData.setTrip( null );
     	tripData2.setTrip( null );
		}
	
}