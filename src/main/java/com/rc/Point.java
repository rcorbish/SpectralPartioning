package com.rc;


public class Point implements Comparable<Point> {
	final float x , y ;
	
	Point( float x, float y ) {
		this.x = x ; this.y = y ;
	}
	Point( float xy[] ) {
		this( xy[0], xy[1] ) ;
	}
	
	@Override
	public int compareTo(Point o) {
		return o.x==x ? 0 : o.x<x ? 1 : -1 ;
	}

	@Override
	public String toString() {
		return String.valueOf(x) + "," + y ;
	}
}
