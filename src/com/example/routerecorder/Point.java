package com.example.routerecorder;

public class Point {

	private double x;
	private double y;
	private double Place[];
	
	
	public Point(double Place_x, double Place_y){
		this.x = Place_x;
		this.y = Place_y;
		
	}
	
	public Point(){
				
	}
	
	

	public double[] getPlace(){
		
		Place[1] = this.x;
		Place[2] = this.y;
		
		return Place;
	}

}
