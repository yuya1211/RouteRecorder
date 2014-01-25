package com.yuya.routerecorder;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class Globals {

	public static AllRoute ar1 = new AllRoute();
	public static Route r1 = new Route();
	public static Route r2 = new Route();
	
	
	private static final LatLng Raleigh = new LatLng(35.7719, -78.6389);
	private static final LatLng Florence = new LatLng(34.1953, -79.7628);
	private static final LatLng Charleston = new LatLng(32.7764, -79.9311);
	
	 private static final LatLng RDU = new LatLng(35.7719, -78.6389);
	 private static final LatLng DFW = new LatLng(32.8969, -97.0381);
	 private static final LatLng SFO = new LatLng(37.6190, -122.3749);
	 
	 
	 
	 
	
	Globals(){
		
		
		
		
		r1.addPoint(Raleigh);
		r1.addPoint(Florence);
		r1.addPoint(Charleston);
		
		r2.addPoint(RDU);
		r2.addPoint(DFW);
		r2.addPoint(SFO);		
		
		ar1.addRoute(r1);
		ar1.addRoute(r2);
		
		
	}
	
	
	
	
	
	

	

}//end of class Globals
