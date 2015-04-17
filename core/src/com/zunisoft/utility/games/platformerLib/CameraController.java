package com.zunisoft.utility.games.platformerLib;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CameraController {
	public OrthographicCamera camera;
	
	private Entity followedEnt;
	private float followOffsetX=0;
	private float followOffsetY=0;
	
	private Vector2 v;
	private float maxSpeed;
	private float freeRadius;
	private Rectangle boundary;
	private float defaultZoom = 1;
	private boolean fittingScreen = false;
	private float autoZoomRad = 100;
	private boolean autoZoom = false;
	private float speedScale = 6;
	
	public CameraController(OrthographicCamera camera) {
		this.camera = camera;
				
		v = new Vector2(1,1);
		maxSpeed = 1000;
		freeRadius = 20;
	}
	public float getTop() {
		return camera.position.y + camera.viewportHeight*camera.zoom/2;
	}
	public float getLeft() {
		return camera.position.x - camera.viewportWidth*camera.zoom/2;
	}
	public float getRight() {
		return camera.position.x + camera.viewportWidth*camera.zoom/2;
	}
	public float getBottom() {
		return camera.position.y - camera.viewportHeight*camera.zoom/2;
	}
	public float getWidth() {
		return camera.viewportWidth*camera.zoom;
	}
	public float getHeight() {
		return camera.viewportHeight*camera.zoom;
	}
	
	public void moveBy(float x,float y) {
		camera.position.x += x;
		camera.position.y += y;
		
		checkBoundary();
	}
	public void setTop(float top) {
		camera.position.y = top - camera.viewportHeight*camera.zoom/2;
	}
	public void setLeft(float left) {
		camera.position.x = left + camera.viewportWidth*camera.zoom/2;
	}
	public void setBottom(float bottom) {
		camera.position.y = bottom + camera.viewportHeight*camera.zoom/2;
	}
	public boolean isEntityOnScreen(Entity ent,float ox,float oy) {
		if(ent.getLeft() > camera.position.x + camera.zoom*camera.viewportWidth/2 + ox) {
			return false;
		}
		if(ent.getRight() < camera.position.x - camera.zoom*camera.viewportWidth/2 -ox) {
			return false;
		}
		if(ent.getBottom() > camera.position.y + camera.zoom*camera.viewportHeight/2 + oy) {
			return false;
		}
		if(ent.getTop() < camera.position.y - camera.zoom*camera.viewportHeight/2 - oy) {
			return false;
		}
		return true;
	}
	public float getZoom() {
		return camera.zoom;
	}
	public void setDefaultZoom(float zoom) {
		camera.zoom = zoom;
	}
	public void setAutozoom(boolean auto) {
		autoZoom = auto;
	}
	public void fitScreen(float w,float h) {
		fittingScreen = true;
		
		float zoom = 1;
		
		if(w/h > camera.viewportWidth/camera.viewportHeight) {
			zoom = h/camera.viewportHeight;
		} else {
			zoom = w/camera.viewportWidth;
		}
				
		camera.zoom = zoom;
		checkBoundary();
	}
	public void normalScreen() {
		fittingScreen = false;
	}
	public void setBoundary(Rectangle boundary) {
		this.boundary = boundary;
	}
	public void followObject(Entity followedEnt,float offsetX,float offsetY) {
		this.followedEnt = followedEnt;
		followOffsetX = offsetX;
		followOffsetY = offsetY;
	}
	public void followObject(Entity followedEnt) {
		followObject(followedEnt,0,0);
	}
	public void update(float delta) {
		
		
		if(followedEnt == null) return;
		if(fittingScreen) return;
		
		float camX = camera.position.x;
		float camY = camera.position.y;
		float objX = followedEnt.getX() + followOffsetX;
		float objY = followedEnt.getY() + followOffsetY;
		
		
		
		float dist2 = (World.pointDist2(camX, camY, objX,objY));
		float diff2 = dist2 - freeRadius*freeRadius;
		
		
		
		if(diff2 < 0) {
			return;
		}
		
		
		
		float speed = speedScale * diff2 / (freeRadius*freeRadius) * delta;
		
		
		
		v.x = objX - camX;
		v.y = objY - camY;
				
		if(speed > maxSpeed) speed = maxSpeed;
		if(speed > 0 && speed < 0.1) return;
		if(speed <0 && speed >-0.1) return;
				
		v.nor().scl(speed);
		
		
		
		camera.position.x += v.x;
		camera.position.y += v.y;
		
		
		
		if(autoZoom) {
			if(camera.zoom > defaultZoom) {
				camera.zoom -= 0.5* camera.zoom * delta;
				
				if(camera.zoom < defaultZoom) {
					camera.zoom = defaultZoom;
				}
			}
			
			checkAutoZoom(delta);
		}
		checkBoundary();
		
		//camera.position.x = (int) camera.position.x;
		//camera.position.y = (int) camera.position.y;
	}
	private void checkAutoZoom(float delta) {
		float camX = camera.position.x;
		float camY = camera.position.y;
		float objX = followedEnt.getX();
		float objY = followedEnt.getY();
		
		float dist = World.pointDist2(camX, camY, objX,objY);
		
		//System.out.println(dist < autoZoomRad * autoZoomRad);
		
		if (dist < autoZoomRad * autoZoomRad) {
			if (camera.zoom > defaultZoom) {
				camera.zoom -= 0.2 * camera.zoom * delta;
				
				if (camera.zoom <= defaultZoom) {
					camera.zoom = defaultZoom;
				}
			} 
			return;
		}
		
		
		float ratio = 1;
		ratio = 0.4f * dist / (autoZoomRad*camera.zoom * autoZoomRad*camera.zoom);
		if (ratio > 5) ratio = 5;
					
		
		if (dist  > autoZoomRad * autoZoomRad * camera.zoom *camera.zoom ) {
			camera.zoom += ratio  * delta;
			if (camera.zoom > 100) camera.zoom = 100;
		} 
		if (dist  < 0.7*0.7*autoZoomRad * autoZoomRad * camera.zoom *camera.zoom ) {
			camera.zoom -= ratio   * delta;
		} 
	}
	public boolean checkBoundary() {
		if(boundary == null) return false;
		
		float left,right,top,bottom;
		
		left = camera.position.x - camera.zoom*camera.viewportWidth/2;
		right = camera.position.x + camera.zoom*camera.viewportWidth/2;
		top = camera.position.y + camera.zoom*camera.viewportHeight/2;
		bottom = camera.position.y - camera.zoom*camera.viewportHeight/2;
		
		
		
		
		boolean clamp = false;
		if(boundary.x + left < 0) {
			camera.position.x = camera.zoom*camera.viewportWidth/2 ;
			clamp = true;
		}
		
		if(camera.zoom*camera.viewportWidth < boundary.width) {
			if(boundary.x + boundary.width - right < 0) {
				camera.position.x = boundary.x + boundary.width - camera.zoom*camera.viewportWidth/2 ;
				clamp = true;
			}
		}
		
		if(boundary.y + bottom < 0) {
			camera.position.y = camera.zoom*camera.viewportHeight/2 ;
			clamp = true;
		}
		
		
		if(camera.zoom*camera.viewportHeight < boundary.height) {
			if(boundary.y + boundary.height - top < 0) {
				camera.position.y = boundary.y + boundary.height - camera.zoom*camera.viewportHeight/2 ;
				clamp = true;
			}
		}
		return clamp;
	}
	public void lookAt(float x,float y) {
		camera.position.x =x;
		camera.position.y =y;
		
		checkBoundary();
	}
	public void lookAt(Entity ent) {
		lookAt(ent.getX() , ent.getY());
	}
	public void setMaxSpeed(float speed) {
		maxSpeed = speed;
	}
	public void setSpeedScale(float scale) {
		speedScale = scale;
	}
	public Vector2 getPosition() {
		return new Vector2(camera.position.x, camera.position.y);
	}
	
	
	
}
