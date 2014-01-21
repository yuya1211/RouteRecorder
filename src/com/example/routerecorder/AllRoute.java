package com.example.routerecorder;

import java.util.ArrayList;

public class AllRoute {

	private ArrayList<Route> trip = new ArrayList<Route>();
	
	
	public int getNumTrips(){
		
		return this.trip.size();
	}//end of method getNumTrips
	
	public ArrayList<Route> getTrips(){
		
		return this.trip;
	}//end of method getTrip
	
	public void addRoute( Route route){
		this.trip.add(route);
	}
	
	
	

}
