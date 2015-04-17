package com.zunisoft.utility.games.platformerLib;

import com.badlogic.gdx.math.Vector2;

public class Line {

	public Vector2 p1;
	public Vector2 p2;

	public Line(Vector2 p1, Vector2 p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public float gradient() {
		float dx = p2.x - p1.x;
		float dy = p2.y - p1.y;
	
		if(Math.abs(dx)<0.00001) {
			if(dy>0) 	return  9999999;
			else  		return -9999999;
		}
		
		return dy/dx;
	}
	
	
	public float det(Vector2 point) {
		float dx, dy, dx1, dy1;
		 
		dx = p2.x-p1.x;
		dy = p2.y-p1.y;
		dx1 = point.x-p1.x;
		dy1 = point.y-p1.y;
	
		float result = (dx*dy1 - dy*dx1);	
		return result;
	}
	
	
	
	public Vector2 findIntercectionOnSegment(Line line) {
		Vector2 pt = findIntercection(line);
		
		if(pt==null) return null;
		
		if(Math.abs(Math.abs(line.gradient())) > 1) {
			if(pt.y >= line.p1.y && pt.y <= line.p2.y) return pt;
			if(pt.y <= line.p1.y && pt.y >= line.p2.y) return pt;
		} else {
			if(pt.x >= line.p1.x && pt.x <= line.p2.x) return pt;
			if(pt.x <= line.p1.x && pt.x >= line.p2.x) return pt;
		}
		return null;
	}
	
	
	
	public Vector2 findIntercectionOnSegment2(Line line) {
		Vector2 pt = findIntercection(line);
	
		if(pt==null) return null;
		
		if(Math.abs(Math.abs(line.gradient())) > 1) {
			if(pt.y >= line.p1.y && pt.y < line.p2.y) return pt;
			if(pt.y <= line.p1.y && pt.y > line.p2.y) return pt;
		} else {
			if(pt.x >= line.p1.x && pt.x < line.p2.x) return pt;
			if(pt.x <= line.p1.x && pt.x > line.p2.x) return pt;
		}
		return null;
	}
	
	
	public Vector2 findIntercection(Line line) {
		float A1 = this.p2.y - this.p1.y;
		float B1 = this.p1.x - this.p2.x;
		float C1 =  A1*this.p1.x+B1*this.p1.y;
			
		float A2 = line.p2.y - line.p1.y;
		float B2 = line.p1.x - line.p2.x;
		float C2 =  A2*line.p1.x+B2*line.p1.y;
			
		float det  = A1*B2 - A2*B1;
		
		Vector2 point = null;
		
		if(det == 0) {
			//paralel
			
		} else {
			float x  = (B2*C1 - B1*C2)/det;
			float y  = (A1*C2 - A2*C1)/det;
			point = new Vector2(x,y);
		}
		return point;
	}
	
	public boolean equals(Line line) {
		if(p1.equals(line.p1) && p2.equals(line.p2)) return true;
		return false;
	}
	
	
	
	public boolean isPointOnLine(Vector2 point) {
		float dx, dy , dx1 , dy1 ;
		 
		dx = this.p2.x-this.p1.x;
		dy = this.p2.y-this.p1.y;
		dx1 = point.x-this.p1.x;
		dy1 = point.y-this.p1.y;
	
		float det  = (dx*dy1 - dy*dx1);	
		
		float tiny  = 0.00000001f;
		
		if(det > tiny || det < -tiny ) {
			return false;
		} else {
			if( (point.x < this.p1.x-tiny && point.x < this.p2.x-tiny) || 
				(point.x > this.p1.x+tiny && point.x > this.p2.x+tiny) ||
				(point.y < this.p1.y-tiny && point.y < this.p2.y-tiny) ||			
				(point.y > this.p1.y+tiny && point.y > this.p2.y+tiny)			
					
				) {
				return false;	
			}
		}
		return true;
	}
	public boolean isIntersected(Line line) {
		Vector2 pt = findIntercectionOnSegment2(line);
		if(pt==null) return false;
		
		if(Math.abs(Math.abs(gradient())) > 1) {
			if(pt.y >= p1.y && pt.y < p2.y) return true;
			if(pt.y <= p1.y && pt.y > p2.y) return true;
		} else {
			if(pt.x >= p1.x && pt.x < p2.x) return true;
			if(pt.x <= p1.x && pt.x > p2.x) return true;
		}
		
		return false;
	}
	public float distToPoint(Vector2 p) { 
		Vector2 p2 = new Vector2(this.p2.x -p1.x, this.p2.y - p1.y); 
		float something = p2.x * p2.x + p2.y * p2.y; 
		float u = ((p.x - p1.x) * p2.x + (p.y - p1.y) * p2.y) /something;
	
		if (u > 1) u = 1;
		else if (u < 0) u = 0;
		
		float x = p1.x + u * p2.x; 
		float  y = p1.y + u * p2.y;
		float dx = x - p.x; 
		float dy = y - p.y;
		float dist = (float) Math.sqrt(dx*dx + dy*dy);
		return dist; 
	}


}
