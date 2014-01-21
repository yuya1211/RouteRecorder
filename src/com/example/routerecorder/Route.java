package com.example.routerecorder;

import java.util.ArrayList;
import com.google.android.gms.maps.model.LatLng;
import android.graphics.*;

public class Route {
	
		private ArrayList<LatLng> Points;
		private ArrayList<String> placeNames;
		private int tripId;
				
		
		
	
	public Route(){
		
		Points = new ArrayList<LatLng>();
		placeNames = new ArrayList<String>();
		
				
	}
	
	
	public int getNumPoint(){	
		return this.Points.size();  	
	}
	
	public ArrayList<LatLng> getPoints(){	
		return this.Points;
	}
	
	public void addPoint( LatLng Place ){	
		this.Points.add(Place);		
	}
	
	public ArrayList<String> getPlaceNames(){
		return this.placeNames;
	}
	
	public void addPlaceNames( String placename ){
		this.placeNames.add(placename);
	}
	
	public void addTripId( int i){
		this.tripId = i;
	}
	
	public int getTripId(){
		return this.tripId;
	}
	
}
