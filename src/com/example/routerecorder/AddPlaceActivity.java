package com.example.routerecorder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddPlaceActivity extends FragmentActivity implements OnClickListener{
	
	private EditText mTripName;
	private EditText mPlaceName;
	private Uri routeUri;
	private GoogleMap placeMap;
	private boolean exitWhenDone = false;
	final Context context = this;
	
	public final static String EXTRA_CENTER_LAT = "com.example.routerecorder.TRIP_CENTER_LAT";
	public final static String EXTRA_CENTER_LNG = "com.example.routerecorder.TRIP_CENTER_LNG";
	
	public Route newRoute = new Route();
	List<HashMap <String, String >> fillMaps = new ArrayList<HashMap<String, String>>();
	
	
	
	@Override
	protected void onCreate(Bundle bundle){
		
		super.onCreate(bundle);
		setContentView(R.layout.add_place);
		
		Intent iPlace = getIntent();
		String TRIP_ID = iPlace.getStringExtra(AddRouteActivity.EXTRA_TRIP_ID);
		String TRIP_NAME = iPlace.getStringExtra(AddRouteActivity.EXTRA_TRIP_NAME);
		newRoute.addTripId(Integer.parseInt(TRIP_ID));
		Log.d("@@",  "id number of the trip passed "+ TRIP_ID);
		Log.d("@@",  "trip name passed "+ TRIP_NAME);
		
		
		mPlaceName = (EditText)findViewById(R.id.place_name);
		
		/*
		ImageButton searchButton = (ImageButton) findViewById(R.id.searchPlaces_button);
		ImageButton addButton = (ImageButton) findViewById(R.id.addPlaces_button);
		ImageButton doneButton = (ImageButton) findViewById(R.id.donePlaces_button);
		*/
		//Log.d("QQ", RouteContentProvider.CONTENT_URI.toString() + " " + values.toString());
/*
		searchButton.setOnClickListener(this);
		addButton.setOnClickListener(this);
		doneButton.setOnClickListener(this);
*/
			
	}//end of onCreate
	
	
	@Override
	public void onClick(View v) {		
		Double placeLat = null;
		Double placeLong = null;
		String placeName = null;
			
		//check for null input
			
		String myPlaceName = mPlaceName.getText().toString();
		if( !myPlaceName.trim().equalsIgnoreCase("")){
				Geocoder geocoder = new Geocoder(this);
				Log.d("QQ" , "WE ARE HERE" + myPlaceName);
				try {
					List<Address> location = geocoder.getFromLocationName( myPlaceName, 1 );
					Log.d("QQ", "WE HERE" + location);
					if(!location.isEmpty()){
					Address locationTemp = location.get(0);
					placeLat  = locationTemp.getLatitude();
					placeLong = locationTemp.getLongitude();
					placeName = locationTemp.getLocality();
					Log.d("QQ", "LOCALOTY = " + placeName);
					if(placeName == null){
						placeName = locationTemp.getSubLocality();
						Log.d("QQ", "SUBLOCALITY = " + placeName);}
					if(placeName == null){
						placeName = locationTemp.getCountryName();
						Log.d("QQ", "COUNTRYNAME = " + placeName);}
							
					placeMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.placeMap))
			                    .getMap();
					}//end of if
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		}//end of if textfield is not empty
		
		Log.d("QQ", "WE HERE" + placeName);
		int id = v.getId();
		if ( id == R.id.searchPlaces_button){
				if(placeName == null){
					Toast toast = Toast.makeText(getBaseContext(), "Can't find location", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
					toast.show();
					return;}
				if(myPlaceName.trim().equalsIgnoreCase("")){
					Toast toast = Toast.makeText(getBaseContext(), "Place name is empty", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
					toast.show();
					return;}
				else{	
				placeMap.clear();
				placeMap.moveCamera( CameraUpdateFactory.newLatLngZoom( 
									new LatLng( placeLat , placeLong), (float) 5.0));
				placeMap.addMarker( new MarkerOptions().
						position(	new LatLng( placeLat , placeLong )).
						title(placeName));	
				placeMap.setIndoorEnabled(true);	
				}
			}//end of if
			
		else if ( id == R.id.addPlaces_button){
			if(myPlaceName.trim().equalsIgnoreCase("")){
				Toast toast = Toast.makeText(getBaseContext(), "Place name is empty", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
			return;}
			else{
				if(placeName == null){
					Toast toast = Toast.makeText(getBaseContext(), "Can't find location", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
					toast.show();
					return;}
			LatLng placePosition = new LatLng( placeLat , placeLong); 
			
						
			newRoute.addPlaceNames( capitalize(myPlaceName) );
			newRoute.addPoint(placePosition);
			mPlaceName.setText("");
			showListView(newRoute);
		
			}		
			}
		else if ( id == 0){
			if( newRoute.getNumPoint() > 0 ){
			exitWhenDone = true;
			Log.d("QQ", "NUMBER OF POINT = " + newRoute.getNumPoint());
			
			
			

			String placeToAdd = "\r\n";
			//ArrayList<String> placesToAdd = new ArrayList<String>();
			for(int i=0; i< newRoute.getNumPoint() ; i++){
				//placesToAdd.add( newRoute.getPlaceNames().get(i));
				placeToAdd = placeToAdd + newRoute.getPlaceNames().get(i) + "\r\n";
			}
			
			AlertDialog.Builder doneDialogBuilder = new AlertDialog.Builder(context);
			doneDialogBuilder.setTitle("Done!");
			doneDialogBuilder.setMessage("You are going to add: " 
										+ placeToAdd + "to this trip")
							 .setCancelable(true)
							 .setPositiveButton("OK", new DialogInterface.OnClickListener() {
								 
								ContentValues values = new ContentValues();
								double centerLat  = 0.0;
								double maxLng = -180.0;
								double minLng =  180.0;
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
					
									
									for(int i = 0 ; i < newRoute.getNumPoint() ; i++ ){
										values.put(RouteTable.COLUMN_PLACE, newRoute.getPlaceNames().get(i));
											Double colLat = newRoute.getPoints().get(i).latitude; 
										values.put(RouteTable.COLUMN_LAT, colLat.toString());
											Double colLng = newRoute.getPoints().get(i).longitude;
										values.put(RouteTable.COLUMN_LNG, colLng.toString());
											Integer colTripId = newRoute.getTripId();
										values.put(RouteTable.COLUMN_TRIP, colTripId.toString());
										
										centerLat += colLat;
										if ( maxLng < colLng )
											maxLng = colLng;
										if ( minLng > colLng)
											minLng = colLng;
										
										getContentResolver().insert(RouteContentProvider.CONTENT_URI_ROUTE, values);				
								}
									
									double centerLng  = 0.0;
									
									// to calculate the center of the Longitude
									if ( (maxLng - minLng) > 180 )
										centerLng = maxLng + ( 360 - (maxLng - minLng))/2;
									else
										centerLng = ( maxLng + minLng)/2;
									
								Intent iMap = new Intent(context, RouteActivity.class);
								iMap.putExtra("finish", true);
								iMap.putExtra(EXTRA_CENTER_LAT, centerLat/newRoute.getNumPoint());
								iMap.putExtra(EXTRA_CENTER_LNG, centerLng );
								iMap.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(iMap);
								finish();
								}// end of dialog onclick
								
							}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							}); //end of Dialog builder
			
					AlertDialog doneDialog = doneDialogBuilder.create();
					doneDialog.show();
				
			}//end of if
		
		}//end of else if
		
	}
		
		public void showListView(Route route){
			
			//String[] from = new String[] {"row_id", "col_item1", "col_item2", "col_item3"};
			//int[] to = new int[] {R.id.item_1, R.id.item_2, R.id.item_3, R.id.item_4};
			
			String[] from = new String[] {"row_id", "col_item1"};
			int[] to = new int[] {R.id.item_1, R.id.item_2};
			
			HashMap<String, String> map = new HashMap<String, String>();		
			
			for(int i =0 ; i < route.getNumPoint() ; i++){
			 map.put("row_id", "" + (i+1) );
			 map.put("col_item1", route.getPlaceNames().get(i));
			 //map.put("col_item2", route.getPoints().get(i).toString());
			 //Integer tripId = route.getTripId();
			 //map.put("col_item3", tripId.toString());
			 
			}
			fillMaps.add(map);
			
			SimpleAdapter adapter = new SimpleAdapter(
										this, fillMaps, R.layout.add_place_grid, from, to);
			ListView listview = (ListView) findViewById(R.id.PlaceListView);
			listview.setAdapter(adapter);
						
		}//end of showListView	
		
		@Override
		protected void onPause(){
			super.onPause();
			if(exitWhenDone != true){			
				Intent iPlace = getIntent();
				String TRIP_ID = iPlace.getStringExtra(AddRouteActivity.EXTRA_TRIP_ID);
				String selectionClauseTrip = RouteTable.COLUMN_TRIP_ID + "=?";
				String[] selectionArgTrip = new String[] { TRIP_ID };
				getContentResolver().
				delete(RouteContentProvider.CONTENT_URI_TRIP, selectionClauseTrip, selectionArgTrip);		
			}
				
				
			
		}
		
		private String capitalize(String line)
		{
		  return Character.toUpperCase(line.charAt(0)) + line.substring(1);
		}
		
		
		
	}
