package com.example.routerecorder;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.routerecorder.Globals;
import com.example.routerecorder.DbHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


    public class AddRouteFragment extends Fragment {
    	
    	//public static String TripId2;
    	
    	private static String tripSelected;	
    	
    	public String getTrip(){
    		return tripSelected;
    	}
    	
    	public void setTrip(String trip){
    		this.tripSelected = trip;
    	}
    	
    	
        int mNum;
        private EditText mTripName;
    	//private DatePicker mTripStartDate;
    	//private DatePicker mTripEndDate;
    	private EditText mTripDesc;
    	private DbHelper dbhelper = null;
    	private ListView listview;
    	private String myTripName;
    	private String myTripDesc;
    	private TextView tripNameView;
    	private TextView tripDescView;
    	


    	private EditText mPlaceName;
    	private Uri routeUri;
    	private GoogleMap placeMap;
    	private boolean exitWhenDone = false;
    	
    	public final static String EXTRA_CENTER_LAT = "com.example.routerecorder.TRIP_CENTER_LAT";
    	public final static String EXTRA_CENTER_LNG = "com.example.routerecorder.TRIP_CENTER_LNG";
    	
    	public Route newRoute = new Route();;
    	List<HashMap <String, String >> fillMaps = new ArrayList<HashMap<String, String>>();

    	String placeName = null;
		Double placeLat = null;
		Double placeLong = null;
        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
            
        	
        
        	
        
        }
        
        




		/**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         * @return 
         * 
         */
        /*public FragmentActivity getActivity2(){
        	Log.d("QQ", FragmentTabs.instance.toString());
        	return FragmentTabs.instance;
        }*/
        
        
        
        private static View v;
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            //View v = inflater.inflate(R.layout.add_route, container, false);
        	//newRoute = new Route();
        	
        	
        	if(myTripName==null || myTripDesc==null)
        	textEntryTrip();
        	
        
            
        	if (v != null) {
                ViewGroup parent = (ViewGroup) v.getParent();
                if (parent != null)
                    parent.removeView(v);
            }
            try {
                v = inflater.inflate(R.layout.add_route, container, false);
            } catch (InflateException e) {
                /* map is already there, just return view as it is */
            }
            
            
            /*******text field in this fragment*******/ 
            	
    		String myPlaceName = ( (EditText)v.findViewById(R.id.place_name) ).toString();
            /*******text field in this fragment*******/ 
	
    		//watch for button clicks
            Button button = (Button)v.findViewById(R.id.addTrip_button);
            button.setOnClickListener(new OnClickListener() {
            	public void onClick(View v){   
            		
            		//myTripName = mTripName.getText().toString();
            		//myTripDesc = mTripDesc.getText().toString();
            		
            		if(myTripName == null ){
            			Toast toast = Toast.makeText(getActivity().getBaseContext(), "Trip Name is Empty", 
            					Toast.LENGTH_SHORT);
            			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,0);
            			toast.show();
            			return;
            		}
            		
            		if(myTripDesc == null){
            		//if(myTripDesc.trim().equalsIgnoreCase("")){
            			Toast toast = Toast.makeText(getActivity().getBaseContext(), "Trip Description is Empty", 
            					Toast.LENGTH_SHORT);
            			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
            			toast.show();
            			return;
            		}
            		
            		
            		
            		if(newRoute.isEmpty() ){
            			Toast toast = Toast.makeText(getActivity().getBaseContext(), "You haven't put places in this trip",
            					Toast.LENGTH_SHORT);
            			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
            			toast.show();
            			return;
            		}
            		
            		
            		
            		String placeToAdd = "\r\n";
        			//ArrayList<String> placesToAdd = new ArrayList<String>();
        			for(int i=0; i< newRoute.getNumPoint() ; i++){
        				//placesToAdd.add( newRoute.getPlaceNames().get(i));
        				placeToAdd = placeToAdd + newRoute.getPlaceNames().get(i) + "\r\n";
        			}
            		
            		AlertDialog.Builder doneDialogBuilder = new AlertDialog.Builder(getActivity());
            		doneDialogBuilder.setTitle("Done!");
            		doneDialogBuilder.setMessage("You are going to add: " + placeToAdd + "to this trip")        
            						.setCancelable(false)
            						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            							
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											//Log.d("QQ", "got the trip in addroutefragment" + getTrip() );
																		
            		
							            		//EditText myTripName = (EditText)getActivity().findViewById(R.id.trip_name_2);
							            		//EditText myTripDesc = (EditText)getActivity().findViewById(R.id.trip_desc);
											tripNameView = (TextView) getActivity().findViewById(R.id.trip_name_show);
						                	tripDescView = (TextView) getActivity().findViewById(R.id.trip_desc_show);
						                	
						                	tripNameView.setText("New Trip Name");
						            		tripDescView.setText("New Trip Description");
						                	
							            		
							            		
							            		Log.d("QQ" , "IN ADD ROUTE 0");
							            	
							            			String[] projectionColor = {
							            					RouteTable.COLUMN_TRIP_ID
							            			};
							            			Cursor mCursorColor = getActivity().getContentResolver().query(
							            					RouteContentProvider.CONTENT_URI_TRIP, projectionColor, null, null, null);
							            			int numRows = 0;
							            			
							            			Log.d("QQ" , "IN ADD ROUTE 1");
							            			if(mCursorColor != null & mCursorColor.moveToFirst()){
							            				mCursorColor.moveToLast();
							            				String numRowsStr = mCursorColor.getString(
							            						mCursorColor.getColumnIndexOrThrow(RouteTable.COLUMN_TRIP_ID));
							            				numRows = Integer.parseInt(numRowsStr);
							            			}
							            			  			
							            			Log.d("QQ" , "IN ADD ROUTE 2");	
							            			ArrayList<Integer> colorList = new ArrayList<Integer>();
							            			
							
							            			Log.d("QQ" , "IN ADD ROUTE 3");
							            			/*colorList.add( -7829368);//grey
							            			colorList.add( -16776961);//blue
							            			colorList.add( -16711681);//cyan
							            			colorList.add( -12303292);//dark grey			
							            			colorList.add( -16711936);//green
							            			colorList.add( -65281);//mangma
							            			colorList.add( -65536);//red
							            			colorList.add(  -1);//white
							            			colorList.add( -256);//yellow*/
							            			
							            			//colorList.add(-16762355);
							            			colorList.add(-15446505);
							            			//colorList.add(-16768706);
							            			colorList.add(-15446505);
							            			//colorList.add(-13365934);
							            			colorList.add(-12052644);
							            			colorList.add(-9437184);
							            			colorList.add(-5741056);
							            			
							            			Integer routeColor = colorList.get( numRows%5 );
							            			
							
							            			/******put trip information to the database*******/
							            			ContentValues valuesTrip = new ContentValues();
							            			valuesTrip.put(RouteTable.COLUMN_TRIP_NAME, myTripName.toString());
							            			valuesTrip.put(RouteTable.COLUMN_TRIP_DESC, myTripDesc.toString());
							            			valuesTrip.put(RouteTable.COLUMN_TRIP_COLOR, routeColor.toString()); 
							            			myTripName = null;
							            			myTripDesc = null;
							            			getActivity().getContentResolver().insert(RouteContentProvider.CONTENT_URI_TRIP, valuesTrip);	
							            			/******put trip information to the database*******/
							            			
							            			
							            			/******get trip _id value from TABLE_TRIP******/
							            			String[] projection = {
							            					RouteTable.COLUMN_TRIP_ID
							            			};        			
							            			Cursor mCursor = getActivity().getContentResolver().query(
							            					RouteContentProvider.CONTENT_URI_TRIP, projection, null, null, null);
							            			if(mCursor != null){
							            				mCursor.moveToLast();
							            				setTrip( mCursor.getString(
							            						mCursor.getColumnIndexOrThrow(RouteTable.COLUMN_TRIP_ID)) );	
							            				
							            			}
							            			Log.d("@@",  "id number of the trip "+ getTrip());
							            			/******get trip _id value from TABLE_TRIP******/
							            			
							            	
							            			/******put route information to the database******/
							            			ContentValues valuesRoute = new ContentValues();
							            			double centerLat  = 0.0;
													double maxLng = -180.0;
													double minLng =  180.0;
							            			for(int i = 0 ; i < newRoute.getNumPoint() ; i++ ){
														valuesRoute.put(RouteTable.COLUMN_PLACE, newRoute.getPlaceNames().get(i));
															Double colLat = newRoute.getPoints().get(i).latitude; 
														valuesRoute.put(RouteTable.COLUMN_LAT, colLat.toString());
															Double colLng = newRoute.getPoints().get(i).longitude;
														valuesRoute.put(RouteTable.COLUMN_LNG, colLng.toString());
														valuesRoute.put(RouteTable.COLUMN_TRIP, getTrip() );
														
														centerLat += colLat;
														if ( maxLng < colLng )
															maxLng = colLng;
														if ( minLng > colLng)
															minLng = colLng;
														
														getActivity().getContentResolver().insert(RouteContentProvider.CONTENT_URI_ROUTE, valuesRoute);				
							            			}//end of for loop
							            			/******put route information to the database******/
							            			
							            			
							            			/******Clean up the information that already been added to database*****/
							            			mTripName.setText("");
							            			mTripDesc.setText("");
							            			newRoute = null;
							            			newRoute = new Route();
							            			//listview.setAdapter(null);
							
							            			listview = (ListView) getActivity().findViewById(R.id.PlaceListView);
							            			fillMaps.clear();
							            			//listview.removeAllViewsInLayout();
							            			//listview.
							            			//listview = null;
							            			ViewGroup.LayoutParams params = listview.getLayoutParams();
							                		params.height = 0;
							                		listview.setLayoutParams(params);
							                		listview.requestLayout();
							                		placeMap.clear();
							        				placeMap.moveCamera( CameraUpdateFactory.zoomTo( 0 ));
							
							            			/******Clean up the information that already been added to database*****/
							            			
							        				
							        				/******switch to Map view******/
							        	        	/*Fragment newFragment = new RouteMapFragment();
							        	        	FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
							        	        	transaction.add(R.id.fragment_container, newFragment);
							        	        	transaction.addToBackStack(null);    
							        	        	// Commit the transaction    
							        	        	transaction.commit();  */
							        				

													
							        				
							        	        	//TabHost tabHost =  (TabHost) getActivity().getParent().findViewById(R.id.fragment_container);
							        	            //  tabHost.setCurrentTab(1);
							        				getActivity().getActionBar().setSelectedNavigationItem(0);
							        				/******switch to Map view******/
        				
							        				mCursor.close();
														}
										}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
											
											@Override
										public void onClick(DialogInterface dialog, int which) {
												// TODO Auto-generated method stub
												dialog.cancel();
											}
										}); //end of DialogListner 
            		
            		AlertDialog doneDialog = doneDialogBuilder.create();
					doneDialog.show();
				            						       			
            	}
            }); //end of AddTrip ButtonListner
            
            ImageButton searchButton = (ImageButton) v.findViewById(R.id.searchPlaces_button);
            searchButton.setOnClickListener(new OnClickListener(){
            	public void onClick(View v){
            		
            		
            		

            		EditText myPlace =  (EditText) getActivity().findViewById(R.id.place_name);
            		String myPlaceName = myPlace.getText().toString();
            		Log.d("QQ", myPlaceName);
            		
            		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
          			      Context.INPUT_METHOD_SERVICE);
          			imm.hideSoftInputFromWindow(myPlace.getWindowToken(), 0);
            		
            		if( !myPlaceName.trim().equalsIgnoreCase("")){
        				Geocoder geocoder = new Geocoder( getActivity() );
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
        					
        					 placeMap = ((SupportMapFragment) ((FragmentActivity) getActivity()).getSupportFragmentManager().findFragmentById(R.id.placeMap))
        			                    .getMap();
        					}//end of if
        				} catch (IOException e) {
        					// TODO Auto-generated catch block
        					Toast toast = Toast.makeText(getActivity().getBaseContext(), "Make sure you have internet service or you may" +
        							" have to reboot for this location finder", Toast.LENGTH_LONG);
        					toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
        					toast.show();
        					e.printStackTrace();
        				}
        		
        		}//end of if textfield is not empty
            		
            		if(placeName == null){
    					Toast toast = Toast.makeText(getActivity().getBaseContext(), "Can't find location", Toast.LENGTH_SHORT);
    					toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
    					toast.show();
    					return;}
    				if(myPlaceName.trim().equalsIgnoreCase("")){
    					Toast toast = Toast.makeText(getActivity().getBaseContext(), "Place name is empty", Toast.LENGTH_SHORT);
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
            	}
            }
            );
            
            Button addButton = (Button) v.findViewById(R.id.addPlaces_button);
            addButton.setOnClickListener( new OnClickListener(){
            	public void onClick(View v){
            		
            		EditText myPlace =  (EditText) getActivity().findViewById(R.id.place_name);
            		String myPlaceName = myPlace.getText().toString();
            		Log.d("QQ", myPlaceName);
            		
            		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
            			      Context.INPUT_METHOD_SERVICE);
            			imm.hideSoftInputFromWindow(myPlace.getWindowToken(), 0);
            		
            		if( !myPlaceName.trim().equalsIgnoreCase("")){
        				Geocoder geocoder = new Geocoder(getActivity());
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
        							
        					
        					 placeMap = ((SupportMapFragment) ((FragmentActivity) getActivity()).getSupportFragmentManager().findFragmentById(R.id.placeMap))
        			                    .getMap();
        					}//end of if
        				} catch (IOException e) {
        					// TODO Auto-generated catch block
        					Toast toast = Toast.makeText(getActivity().getBaseContext(), "Make sure you have internet service or you may" +
        							" have to reboot for this location finder", Toast.LENGTH_LONG);
        					toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
        					toast.show();
        					e.printStackTrace();
        				}
        		
        		}//end of if textfield is not empty

            		
            		if(myPlaceName.trim().equalsIgnoreCase("")){
        				Toast toast = Toast.makeText(getActivity().getBaseContext(), "Place name is empty", Toast.LENGTH_SHORT);
        				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
        				toast.show();
        			return;}
        			else{
        				if(placeName == null){
        					Toast toast = Toast.makeText(getActivity().getBaseContext(), "Can't find location", Toast.LENGTH_LONG);
        					toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
        					toast.show();
        					return;}
        			LatLng placePosition = new LatLng( placeLat , placeLong); 
        				
        			newRoute.addPlaceNames( capitalize( myPlaceName ) );
        			newRoute.addPoint(placePosition);

        			myPlace.setText("");

        			//showListView(newRoute);
        		
        			String[] from = new String[] {"row_id", "col_item1"};
        			int[] to = new int[] {R.id.item_1, R.id.item_2};
        			
        			HashMap<String, String> map = new HashMap<String, String>();	
        			
        			
        			
        			for(int i =0 ; i < newRoute.getNumPoint() ; i++){
        			 map.put("row_id", "" + (i+1) );
        			 map.put("col_item1", newRoute.getPlaceNames().get(i)); 
        			}
        			
        			fillMaps.add(map);
        			
        			SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, 
        										R.layout.add_place_grid, from, to);
        			listview = (ListView) getActivity().findViewById(R.id.PlaceListView);
        			listview.setAdapter(adapter);
        			setListViewHeightBasedOnChildren(listview);
        			
        			
        						
        			}
            	}
            });
            
            
            
           // ImageButton doneButton = (ImageButton) v.findViewById(R.id.donePlaces_button);
            
            return v;
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
			
			SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, 
										R.layout.add_place_grid, from, to);
			ListView listview = (ListView) getActivity().findViewById(R.id.PlaceListView);
			listview.setAdapter(adapter);
			
			setListViewHeightBasedOnChildren(listview);
						
		}//end of showListView
        
        private String capitalize(String line)
		{
		  return Character.toUpperCase(line.charAt(0)) + line.substring(1);
		}
        
        public static void setListViewHeightBasedOnChildren(ListView listView) {
    		ListAdapter listAdapter = listView.getAdapter();
    		if (listAdapter == null) {
    			// pre-condition
    			return;
    		}
     
    		int totalHeight = 0;
    		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
    		for (int i = 0; i < listAdapter.getCount(); i++) {
    			View listItem = listAdapter.getView(i, null, listView);
    			listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
    			totalHeight += listItem.getMeasuredHeight();
    		}
     
    		ViewGroup.LayoutParams params = listView.getLayoutParams();
    		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
    		listView.setLayoutParams(params);
    		listView.requestLayout();
     
    	}
        
        
        
        private void textEntryTrip(){
        	

        	Log.d("QQ" , "not yet got the trip text?");
        	

        	Log.d("QQ" , "got the trip text?");
        	
            LayoutInflater factory = LayoutInflater.from( getActivity() );
            final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
           
            
            AlertDialog.Builder newTripDialogBuilder = new AlertDialog.Builder( getActivity() );
    		newTripDialogBuilder.setTitle("New Trip");
    		newTripDialogBuilder.setView(textEntryView)
    						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		                    public void onClick(DialogInterface dialog, int whichButton) {
    		                    	
    		                    	
    		                        
    		                        /* User clicked OK so do some stuff */
    		                    	mTripName = (EditText) textEntryView.findViewById(R.id.trip_name_edit);
    		                		//mTripStartDate = (DatePicker) findViewById(R.id.trip_start_date);
    		                		//mTripEndDate = (DatePicker) findViewById(R.id.trip_end_date);
    		                		mTripDesc = (EditText) textEntryView.findViewById(R.id.trip_desc_edit);   	
    		                		
    		                                  	
    		                    	myTripName = mTripName.getText().toString();
				            		myTripDesc = mTripDesc.getText().toString();
				            		
				            		tripNameView = (TextView) getActivity().findViewById(R.id.trip_name_show);
				                	tripDescView = (TextView) getActivity().findViewById(R.id.trip_desc_show);
				                	
				                	if( !myTripName.isEmpty() && !myTripDesc.isEmpty()){
				            		tripNameView.setText(myTripName);
				            		tripDescView.setText(myTripDesc);
				                	}
				                	
				            		if(myTripName.isEmpty() || myTripDesc.isEmpty()){
				            		//if(myTripName.trim().equalsIgnoreCase("") || myTripDesc.trim().equalsIgnoreCase("")){
    		                			
    		                			getActivity().getActionBar().setSelectedNavigationItem(0);
    		                			myTripName = null;
    		                			myTripDesc = null;
    		                		}
    		                		
    		         
    		                    }
    		                })
    						.setNegativeButton( 
    									"Cancel", new DialogInterface.OnClickListener() {
    				                    public void onClick(DialogInterface dialog, int whichButton) {

    				                        /* User clicked cancel so do some stuff */
    				                    	getActivity().getActionBar().setSelectedNavigationItem(0);
					        				
    				                    }
    				                })
    				                .setCancelable(false)
    						.create()
    						.show();
    		
    		
    			
        	
        }
        
       


    }
    
