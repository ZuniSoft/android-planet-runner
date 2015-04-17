package com.zunisoft.utility.games.platformerLib;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.zunisoft.utility.games.ActorClip;
import com.zunisoft.utility.games.StageGame;

public class World extends StageGame {
	private static final float MAX_DELTA = 0.0833f;
	public static boolean debug=false;
	private Array<Entity> entityList;
	private Lands lands;
	
	private float pauseTime = -1; //sec
	private String afterPause;
	
	protected Vector2 gravity,tmp1=new Vector2(),tmp2=new Vector2();
	private Array<Entity> entToRemove;
	
	protected CameraController camController;
	
	
	public World() {
		
		entityList = new Array<Entity>();
		entToRemove = new Array<Entity>();
		
		camController = new CameraController(camera);
		
		gravity = new Vector2(0, -1000);
		lands = new Lands(this);
		
		resumeWorld();
	}
	
	
	
	protected void doAfterPause(String code) {
		
	}
	public Array<Entity>  getEntityList() {
		return entityList;
	}
	@Override
	public void dispose() {
		super.dispose();
		//tweener.dispose();
		
		for(Entity ent : entityList) {
			ent.dispose();
		}
	}
	public void pauseWorld() {
		pauseWorld(Float.MAX_VALUE,null);
	}
	public void pauseWorld(float time , String afterPause) {
		this.afterPause = afterPause;
		pauseTime = time;
		
		ActorClip.paused = true;
	}
	
	public void resumeWorld() {
		pauseTime = -1;
		
		if (afterPause != null) {
			doAfterPause(afterPause);
		}
		afterPause = null;
		ActorClip.paused = false;
	}
	protected boolean isPaused() {
		return pauseTime > 0;
	}
	
	
	
	
	public boolean isLOS(Entity ent1 , Entity ent2,int step) {
		Vector2 start = ent1.pos;
		Vector2 stop = ent2.pos;
		
		tmp1.set(start);
		
		tmp2.x = stop.x - start.x;
		tmp2.y = stop.y - start.y;
		
		tmp2.nor().scl(step);
		
		
		boolean loop = true;
		
		int maxLoop = 1000;
		while(loop) {
			if(!isHole(tmp1)) {
				
				return false;
			}
			tmp1.x += tmp2.x;
			tmp1.y += tmp2.y;
			
					
			
			if(Vector2.dst2(tmp1.x, tmp1.y, stop.x, stop.y) < step*step*1.2f) {
				loop = false;
				return true;
			}
			if(maxLoop-- < 0) {
				System.err.println("max loop reached");
				return false;
			}
		}
		
		return false;
	}
	public boolean isHole(Vector2 pt) {
		return !lands.isPointInside(pt);
	}
	
	private Vector2 tmpPos = new Vector2();
	
	public int getDistanceToLand(Vector2 pos,int step,int max) {
		int dist = 0;
		tmpPos.set(pos);
		
		while (dist < max) {
			if (lands.isPointInside(tmpPos)) {
				return dist;
			}
			
			tmpPos.y -= step;
			dist += step;
		}
		return max;
	}
	@Override
	protected void update(float delta) {
		
	}
	
	private Vector2 tmpG = new Vector2();
	
	private Rectangle tmpRect = new Rectangle();
	
	public boolean isNeedUpdate(Entity ent , Rectangle rect) {
		float d;
		
		d = ent.getLeft() - (rect.x+rect.width);
		if(d > rect.width*ent.edgeUpdateLimRatio) {
			return false;
		}
		
		d = rect.x - ent.getRight();
		if(d > rect.width*ent.edgeUpdateLimRatio) {
			return false;
		}
		
		d = ent.getBottom() - (rect.y + rect.height);
		if(d > rect.height*ent.edgeUpdateLimRatio) {
			return false;
		}
		
		d = rect.y - ent.getTop();
		if(d > rect.height*ent.edgeUpdateLimRatio) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public void render(float delta) {
		if (delta > MAX_DELTA) delta = MAX_DELTA;
		
		tmpRect.width = camController.getWidth();
		tmpRect.height = camController.getHeight();
		tmpRect.x = camController.getLeft();
		tmpRect.y = camController.getBottom();
		
		removeEntity2();
				
				
		if (pauseTime > 0) {
			pauseTime -= delta;
			
			if (pauseTime <= 0) {
				resumeWorld();
			}
			
			//overlay not affected with game pause..
			renderOnPause(delta);
			runDelay(delta);
			
			return;
		}
		
		camController.update(delta);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);	
		
		update(delta);
		
		
		stage.act(delta);
		overlay.act(delta);
		
		runDelay(delta);
		
		Entity ent,ent2;
		int i;
		int j;
		int num = entityList.size;
		
		
		lands.update(delta,entityList,tmpRect);
				
		for (i=0;i<num;i++) {
			ent = entityList.get(i);
			
			
			if(!ent.isVisible()) continue;
			if(!isNeedUpdate(ent , tmpRect)) {
				ent.skipUpdate = true;
				ent.onSkipUpdate();
				continue;
			}
			
			
			ent.skipUpdate = false;
			
			
			
			//apply gravity
			if (!ent.noGravity) {
				tmpG.set(gravity);
				tmpG.scl(delta);
				ent.applyGravity(tmpG);
			}
		
			
			ent.update(delta);
			
			//collision with other entity
			if(!ent.noCollision) {
				//check collision with "lands"
			
				
				if (ent.isSensor) {
					checkSensor(ent);
				} else {
					if (!ent.noLandCollision) {
						lands.checkCollision(ent,delta);
					}
				}
				
				for (j = i + 1; j < num; j++) {	
					ent2 = entityList.get(j);
					if(ent2.noCollision) continue;
					if(ent2.skipUpdate) continue;
					if(!ent2.isVisible()) continue;
					
					
					if (ent.hitTestEntity(ent2)) {
						onCollide(ent, ent2,delta);
					} 
				}
			}
		}
		
		
		
		float cLeft = camController.getLeft();
		float cBottom = camController.getBottom();
		float cZoom = camera.zoom;
		
		camera.zoom = 1;
		camController.setLeft(0);
		camController.setBottom(0);
		
			background.draw();
			
		camera.zoom = cZoom;
		camController.setLeft(cLeft);
		camController.setBottom(cBottom);
		
			stage.draw();
		
		
		
		camera.zoom = 1;
		camController.setLeft(0);
		camController.setBottom(0);
		
			overlay.draw();
		
		camera.zoom = cZoom;
		camController.setLeft(cLeft);
		camController.setBottom(cBottom);
		
	
	}
	private void checkSensor(Entity ent) {
		
		Array<Entity> entityListCheck;
		
		if (ent.landsRegion.size > 0) {
			entityListCheck = ent.landsRegion;
		} else {
			entityListCheck = lands.getList();
		}
		
		int num = entityListCheck.size;
		Entity land;
		
		int i;
		for(i=0;i<num;i++) {
			land = entityListCheck.get(i);				
			if (land.hitTestEntity(ent)) {
				onCollide(ent, land,0);
			}
		}
			
		
	}
	@Override
	public void renderOnPause(float delta) {
		Gdx.gl.glClearColor(255.0f, 255.0f, 255.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);	
		
		overlay.act(delta);
		
		float cLeft = camController.getLeft();
		float cBottom = camController.getBottom();
		float cZoom = camera.zoom;
		
		camera.zoom = 1;
		camController.setLeft(0);
		camController.setBottom(0);
		
			background.draw();
			
		camera.zoom = cZoom;
		camController.setLeft(cLeft);
		camController.setBottom(cBottom);
		
			stage.draw();
		
		
		
		camera.zoom = 1;
		camController.setLeft(0);
		camController.setBottom(0);
		
			overlay.draw();
		
		camera.zoom = cZoom;
		camController.setLeft(cLeft);
		camController.setBottom(cBottom);
	}

	

	protected void onCollide(Entity entA, Entity entB,float delta) {
		
	}
	public void addEntity(Entity ent) {
		//TODO : check if already added
	
		entityList.add(ent);
		ent.setWorld(this);
		addChild(ent);
	}
	public void removeEntity(Entity ent) {
		entToRemove.add(ent);
		if(ent.hasParent()) removeChild(ent);
	}
	private void removeEntity2() { 
		int i=0;
		int num = entToRemove.size;
		
		for(i=0;i<num;i++) {
			entityList.removeValue(entToRemove.get(i), true);
		}
		
		entToRemove.clear();
	}
	
	
	public void addLand(Entity ent,boolean fix) {
		lands.addEntity(ent, fix);
	}
	public void removeLand(Entity ent) {
		lands.removeEntity(ent);
	}
	
	//utility
	public static float pointDist2(Vector2 pt1,Vector2 pt2) {
		float dx = pt2.x - pt1.x;
		float dy = pt2.y - pt1.y;
		
		return dx*dx + dy*dy;
	}
	public static float pointDist2(float x1,float y1 , float x2 , float y2) {
		float dx = x2 - x1;
		float dy = y2 - y1;

		return dx*dx + dy*dy;
	}



	public void getLandsRegion(Entity ent, float dist,Array<Entity> landsRegion) {
		
		landsRegion.clear();
		Array<Entity> list = lands.getList();
		int num = list.size;
		int i;
		Entity land;
		
		for (i = 0; i < num; i++) {
			land = list.get(i);
			
			if (land.getLeft() - ent.getX() > dist + 1) continue;
			else if (ent.getX() - land.getRight() > dist + 1) continue;
			else if (ent.getY()  - land.getBottom() > dist + 1) continue;
			else if (land.getTop() - ent.getY() > dist + 1) continue;
			
			landsRegion.add(land);
		}
		
	}
}
