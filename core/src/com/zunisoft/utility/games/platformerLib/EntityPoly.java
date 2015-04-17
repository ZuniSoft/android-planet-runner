package com.zunisoft.utility.games.platformerLib;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class EntityPoly extends Entity {
	private Polygon polygon;
	
	//private float minX, maxX,minY,maxY;
	private boolean boundCalculated=false;
	private float top,bottom,left,right;
	
	public EntityPoly(float[] vertices) {
		Vector2 centroid = findCentroid(vertices);
		int i=0;
		for(i=0;i<vertices.length;i+=2) {
			//System.out.println(vertices[i]);
			
			vertices[i] -= centroid.x;
			vertices[i+1] -= centroid.y;
			
			//
		}
		
		
		
		polygon = new Polygon(vertices);
		setX(centroid.x);
		setY(centroid.y);
	}
	public EntityPoly(Array<Vector2> vertices) {
		float[] points;
		points = new float[vertices.size * 2];
			
		int i=0;
		for(Vector2 pt : vertices) {
			points[i] = pt.x;
			points[i+1] = pt.y;
			
			i+=2;
		}
		
		Vector2 centroid = findCentroid(points);
		for(i=0;i<points.length;i+=2) {
			points[i] -= centroid.x;
			points[i+1] -= centroid.y;
		}
		polygon = new Polygon(points);
		setX(centroid.x);
		setY(centroid.y);
	}
	
	private Vector2 findCentroid(float[] vertices) { 
		float area=0;
		float x=0,y=0;
				
		for (int i = 0; i < vertices.length; i+=2) { 
			
			float p1x = vertices[i];
			float p1y = vertices[i+1];
			float p2x = vertices[(i + 2) % vertices.length]; 
			float p2y = vertices[(i + 3) % vertices.length]; 
				
					
			area += (p1x * p2y) - (p2x * p1y); 
			x += (p1x + p2x) * (p1x * p2y - p2x * p1y); 
			y += (p1y + p2y) * (p1x * p2y - p2x * p1y); 
		} 
		area = area / 2; 
		return new Vector2(x / (6 * area), y / (6 * area)); 
		
	}
	protected void updateRect() {
		
		if (polygon == null) return;
		polygon.dirty();
		polygon.setPosition(getX(), getY());
		boundCalculated = false;
	}
	private void calcBound() {
		if(boundCalculated) return;

		float[] points = polygon.getTransformedVertices();
		int i;
		float valueX,valueY;
		
		for(i=0;i<points.length;i+=2) {
			if(i==0) {
				left = right = points[i];
				top = bottom = points[i+1];
				continue;
			}
			valueX = points[i];
			valueY = points[i+1];
			
			if(valueX < left) left = valueX;
			if(valueX > right) right = valueX;
			if(valueY > top) top = valueY;
			if(valueY < bottom) bottom = valueY;
		}
				
		
		boundCalculated = true;
	}
	public float getTop() {
		calcBound();
		return top;
	}
	public float getRight() {
		calcBound();
		return right;
	}
	public float getBottom() {
		calcBound();
		return bottom;
	}
	public float getLeft() {
		calcBound();
		return left;
	}
	public Polygon getPolygon() {
		return polygon;
	}
	protected void init() {
		super.init();
		setModel(Entity.POLYGON);
	}
	private boolean isIntersect(Polygon p1, Polygon p2) {
		if(isIntersect2(p1,p2)) return true;
		if(isIntersect2(p2,p1)) return true;
				
		return false;
	}
	private boolean isIntersect2(Polygon p1, Polygon p2) {
		float[] points = p1.getTransformedVertices();
		int i;
		
		for(i=0;i<points.length;i+=2) {
			if(p2.contains(points[i], points[i+1])) {
				return true;
			}
		}
		
		return false;
	}
	public boolean hitTestEntity(Entity item) {
		Rectangle rect;
		EntityPoly poly;
		Polygon p;
							
		if (item.getModel() == Entity.POLYGON) {
			poly = (EntityPoly) item;
			p = poly.getPolygon();
			return isIntersect(p, polygon);
		}
		
		else if (item.getModel() == Entity.BOX) {
			rect = item.getRectangle();
						
			//corner of rect inside poly
			if(polygon.contains(rect.x, rect.y + rect.height)) return true;
			if(polygon.contains(rect.x +  rect.width, rect.y + rect.height)) return true;
			if(polygon.contains(rect.x +  rect.width, rect.y )) return true;
			if(polygon.contains(rect.x , rect.y )) return true;
									
			//corner of poly inside rect
			float[] points = polygon.getTransformedVertices();
			for(int i=0;i<points.length;i+=2) {
				if(rect.contains(points[i], points[i+1])) {
					return true;
				}
			}
			
			return false;
			
		}
		else if (item.getModel() == Entity.CIRCLE) {
			return hitTestCircle(item);
			
		}
		return false;
	}
	private boolean hitTestCircle(Entity item) {
		float dx,dy,dist2;
		
				
		if (item.getBottom() > getTop()) return false;
		if (item.getTop() < getBottom()) return false;
		if (item.getRight() < getLeft()) return false;
		if (item.getLeft() > getRight()) return false;
		
		
		
		float points[];
		points = polygon.getTransformedVertices();
		
		
		int i;
		
		for(i=0;i<points.length;i+=2) {
			dx = points[i] - item.getX();
			dy = points[i+1] - item.getY();
			dist2 = dx * dx + dy * dy;
			
			if (dist2 < item.r * item.r) {
				
				return true;
			}
		}
		
		float dist;
		Line line;
		
		i = 0;
		Vector2 p = new Vector2(item.getX(), item.getY());
				
		for(i=0;i<points.length;i+=2) {
			if (i == points.length - 2) {
				line = new Line(new Vector2(points[i], points[i+1]) , new Vector2(points[0], points[1]));
			} else {
				line = new Line(new Vector2(points[i], points[i+1]) , new Vector2(points[i+2], points[i+3]));
			}
			
			dist = line.distToPoint(p);
			
			if (dist < item.r) {
				return true;
			}
		}
		
		
		
		return false;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (!World.debug && !debug) return;
		
		
		batch.end();
		
		
		ShapeRenderer shapeRenderer  = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(getStage().getCamera().combined);
		
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        
        int i;
		float[] vertices = polygon.getTransformedVertices();
        
		//System.out.println("-----");
		
		for(i=0;i<vertices.length;i+=2) {
			//System.out.println(vertices[i] +" "+ vertices[i+1]);
			
			
			if(i==vertices.length-2) {
				shapeRenderer.line(vertices[i], vertices[i+1], vertices[0], vertices[1]);
				//System.out.println("last" + vertices[i] +" "+ vertices[i+1] + " - "+vertices[0]+" "+ vertices[1]);
			} else {
				shapeRenderer.line(vertices[i], vertices[i+1], vertices[i+2], vertices[i+3]);
				//System.out.println(vertices[i] +" "+ vertices[i+1] + " - "+vertices[i+2]+" "+ vertices[i+3]);
			}

		}
		
		
		//center
		shapeRenderer.circle(getX(), getY(), 1);
        
        
        shapeRenderer.end();
        batch.begin();
	}
	
}
