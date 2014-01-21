package com.example.routerecorder;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.example.routerecorder.Globals;
import com.example.routerecorder.DbHelper;



public class RouteActivity extends FragmentActivity {
	
	
	private DbHelper dbhelper = null;
	private String id;
	
	
	private GoogleMap mMap;
	//private Polyline polyline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		//getContentResolver().delete(RouteContentProvider.CONTENT_URI_TRIP,	null, null);
		//getContentResolver().delete(RouteContentProvider.CONTENT_URI_ROUTE,	null, null);
		setContentView(R.layout.activity_route);

	}// end of onCreate
	
	
	@Override
	protected void onResume(){
		super.onResume();
		setUpMapIfNeeded();		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route, menu);
		return true;
	}
	
	private void openDatabase(){
		dbhelper = new DbHelper(this); 
	}
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    int id = item.getItemId();
	    if ( id == R.id.insert){
	    	Intent i = new Intent(this, AddRouteActivity.class);
	    	startActivity(i);
	      return true;}
	    else if( id == R.id.show){
	    	if (checkDataBase() == true)
	    	setUpMap();
	    	return true;}
	    else if( id == R.id.clear){
	    	getContentResolver().delete(RouteContentProvider.CONTENT_URI_TRIP,	null, null);
	    	getContentResolver().delete(RouteContentProvider.CONTENT_URI_ROUTE,	null, null);
			mMap.clear();
	    	return true;}
	    else if( id == R.id.action_show_list){
	    	Intent iListRoutes = new Intent(this, ListRoutesActivity.class);
	    	startActivity(iListRoutes);
	    	return true;
	    }
	    return super.onOptionsItemSelected(item);
	  }
	    
	
    /*private void add(){
    	
    	SQLiteDatabase db = dbhelper.getWritableDatabase();   	
    	ContentValues values = new ContentValues(); 	
    	values.put(DbRouteEntry.COLUMN_NAME_ROUTE_NAME, "Raleigh");   	
    	values.put(DbRouteEntry.COLUMN_NAME_PLACE_POSITION, data.ar1.getTrips().get(0).getPoints().get(0).toString());
    	Log.d("print" , data.ar1.getTrips().get(0).getPoints().get(0).toString());
    	
    	values.put(DbRouteEntry.COLUMN_NAME_TRIP_NUM, "East Coast");   	
    	db.insert(DbRouteEntry.TABLE_NAME, null, values); 	
    	
        }*/
	
	private void setUpMapForSingleRoute(String tripId){
		
		 String[] projectionTrip = {
				 //tripId in the RouteTable
				 RouteTable.COLUMN_TRIP_ID,
				 RouteTable.COLUMN_TRIP_COLOR
		 };
								
			String selectionClauseColor = RouteTable.COLUMN_TRIP_ID + "=?";
			String[] selectionArgsColor = new String[]{ tripId };
			
			Cursor cursorColor = getContentResolver().
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
    			 
    			 Cursor cursor = getContentResolver().
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
    					 
    					 mMap.addMarker(new MarkerOptions().position(
    							 new LatLng(strLat, strLng)).title(PlaceName));
    					 
    					 path.add(new LatLng(strLat, strLng));					 
    					     					 
    				 }while(cursor.moveToNext());
    				 
    				 mMap.addPolyline((new PolylineOptions())
    						 .addAll( path ).width(3).color(colorInt));
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
    				  				 
    			 }//end of if

	}//setUpMapForSingleRoute()
	
	private void setUpMapIfNeeded() {
		
		Intent iListView = this.getIntent();
		String tripSelected = iListView.getStringExtra(ListRoutesActivity.EXTRA_TRIPID);
		
		
		openDatabase();
		
		Log.d("QQ", "THE SELECTED TRIP = " + tripSelected );
		
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
        	Log.d("QQ", "WE ARE IN THE mMap = null");
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
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
		 
		 Intent iAddPlace = this.getIntent();
		 double latCenter = iAddPlace.getDoubleExtra(AddPlaceActivity.EXTRA_CENTER_LAT,  0.0 );
		 double lngCenter = iAddPlace.getDoubleExtra(AddPlaceActivity.EXTRA_CENTER_LNG,  0.0);
		 	 
		 String[] projectionTrip = {
				 //tripId in the RouteTable
				 RouteTable.COLUMN_TRIP_ID,
				 RouteTable.COLUMN_TRIP_COLOR
		 };
		
		 Cursor cursorTrip = getContentResolver().
				 query(RouteContentProvider.CONTENT_URI_TRIP, projectionTrip, 
						 null, null, null);
		 	
		 /* String[] projectionTrip = {
				 //tripId in the RouteTable
				 RouteTable.COLUMN_TRIP
		 };*/
		 
		 //query the rows by column_trip 
		 //to see how many rows are there in the route table
		 
		/* Cursor cursorTrip = getContentResolver().
				 query(RouteContentProvider.CONTENT_URI_ROUTE, projectionTrip, 
						 null, null, null);*/
		 	 
		
			 if (cursorTrip != null){
				 cursorTrip.moveToFirst();			
					 	 
				 do{
					 String TripIdCurrent = cursorTrip.getString(
							 cursorTrip.getColumnIndexOrThrow(RouteTable.COLUMN_TRIP_ID));
					 Log.d("QQ", "ARE WE HERE IN ROUTEACTIVITY? 1");
					 String TripColorCurrent = cursorTrip.getString(
							 cursorTrip.getColumnIndexOrThrow(RouteTable.COLUMN_TRIP_COLOR));
					 Log.d("QQ", "ARE WE HERE IN ROUTEACTIVITY? 2");
					int colorInt = Integer.parseInt(TripColorCurrent);
					 String[] projection = {
							 RouteTable.COLUMN_PLACE,
							 RouteTable.COLUMN_LAT,
							 RouteTable.COLUMN_LNG,
							 RouteTable.COLUMN_TRIP
					 };		 
					 		
					 String selectionClause = RouteTable.COLUMN_TRIP + "=?";
					 String[] selectionArgs = new String[]{ TripIdCurrent };
					 
					 
					 
					 Cursor cursor = getContentResolver().
							 query(RouteContentProvider.CONTENT_URI_ROUTE, projection, 
									 selectionClause, selectionArgs, null);
					 /*Cursor cursor = getContentResolver().
							 query(RouteContentProvider.CONTENT_URI_ROUTE, projection, 
									 null, null, null);*/
					 
					 		
					 ArrayList<LatLng> path = new ArrayList<LatLng>();
			 
					 if (cursor != null){				 
					 cursor.moveToFirst();
					 do{
						 
						 try{
							 
						 Log.d("QQ", cursor.getString(cursor.getColumnIndexOrThrow(RouteTable.COLUMN_PLACE)) );
						 String PlaceName = cursor.getString(cursor.getColumnIndexOrThrow(RouteTable.COLUMN_PLACE));
						 Log.d("QQ", "WE GOT HERE 1");
						 String PlaceLat = cursor.getString(cursor.getColumnIndexOrThrow(RouteTable.COLUMN_LAT));
						 Log.d("QQ", "WE GOT HERE 2");
						 String PlaceLng = cursor.getString(cursor.getColumnIndexOrThrow(RouteTable.COLUMN_LNG));
						 Log.d("QQ", "WE GOT HERE 3");
						 Double strLat = Double.parseDouble(PlaceLat);
						 Log.d("print" , strLat.toString());
						 Double strLng = Double.parseDouble(PlaceLng);
						 Log.d("print" , strLng.toString());
					  		 
						 mMap.addMarker(new MarkerOptions().position(
						 new LatLng(strLat, strLng)).title(PlaceName));
						 path.add( new LatLng(strLat, strLng) ); 
						 }catch(IllegalArgumentException e){
							 Log.d("QQ", "GOT YOU!!!");
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
			 mMap.moveCamera(CameraUpdateFactory.newLatLng(
					 new LatLng( latCenter, lngCenter)));
		 
		 
		 }   //end of showMap
	 
	//actually you don't need this method...
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
		 
		}//end od method cheackdatabase	
	
	
}