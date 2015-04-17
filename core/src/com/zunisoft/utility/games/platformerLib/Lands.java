package com.zunisoft.utility.games.platformerLib;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


public class Lands {
	private float STEP_BACK = 8f;

	private World world;
	private Array<Entity> entityList;
	private Array<Boolean> isFixed;
	private Array<Entity> toRemove;
	private Vector2 vb = new Vector2();
	private Vector2 origPos = new Vector2();
	private Vector2 origPos2 = new Vector2();
	private Vector2 origV = new Vector2();
	
	public Lands(World world) {
		this.world = world;
		entityList = new Array<Entity>();
		isFixed = new Array<Boolean>();
		toRemove = new Array<Entity>();
	}
	public void addEntity(Entity ent, boolean fix) {
		entityList.add(ent);
		world.addChild(ent);
		isFixed.add(fix);
	}
	public void removeEntity(Entity ent) {
		
		world.removeChild(ent);
		toRemove.add(ent);
		
		
	}
	public boolean isPointInside(Vector2 pt) {
		int i = 0;
			
		while(i<entityList.size) {
			if (entityList.get(i).isPointInside(pt)) {
				return true;
			}
			i++;	
		}
		
		
		return false;
	}

	public void update(float delta, Array<Entity> worldEntityList,Rectangle rect) {
		int i;
		int num;
		
		num = toRemove.size;
		
		for(i=0;i<num;i++) {
			int id = entityList.indexOf(toRemove.get(i), true);
			if(id != -1) {
				entityList.removeIndex(id);
				isFixed.removeIndex(id);
			}
		}
		toRemove.clear();
		
		num = entityList.size;
		Entity land;
		
	
		
		for (i = 0; i < num; i++) {
			
			
			if (!isFixed.get(i)) {
				land = entityList.get(i);
				
				if(world.isNeedUpdate(land, rect)) {
					land.update(delta);
									
					//kick out the entity hit the moving land
					kickOut(land, worldEntityList, delta);
				}

			}
		}
	}
	private Vector2 bv = new Vector2();
	
	private void kickOut(Entity land , Array<Entity> worldEntityList,float delta) {
	
		int i;
		Entity ent;
		for(i=0;i<worldEntityList.size;i++) {
			ent = worldEntityList.get(i);
		
			if (!ent.noLandCollision && land.hitTestEntity(ent)) {
									
				
				//test if it really the land hit the ent
				bv.set(land.v).scl(-1).scl(delta);
				land.translate(bv);
				
				
				if (land.hitTestEntity(ent)) { //land did not hit the ent, seems the otherwise, ent hit the land
					
					bv.scl(-1);
					land.translate(bv);
					

					
				} else {

															
					
					
					bv.scl(-1);
					land.translate(bv);				
					ent.translate(bv);
					ent.kickedByLand(land);
				}
				
				//land.translate(land.v.cpy().scl(delta));
				
				
			}
		}
	}
	
	
	
		
	public void checkCollision(Entity ent, float delta) {
		
		ent.setInAir(true);

		Entity land;
		int num = entityList.size;
		int i=0,numCollision=0;
			
		Entity cld0 = null,cld1=null,cld2=null;
		
		Array<Entity> entityListCheck;
				
		if (ent.landsRegion.size == 0) {
			ent.updateRegion();
		}
		
		entityListCheck = entityList;
		num = entityListCheck.size ;
		
		
		for(i=0;i<num;i++) {
			land = entityListCheck.get(i);
			
			if (land.hitTestEntity(ent)) {
				numCollision++;
				if(numCollision==1) {
					cld0 = land;
				} else if(numCollision==2) {
					cld1 = land;
				}  else if(numCollision==3) {
					cld2 = land;
				}
				
				
				if(numCollision == 3) {
					break;
				}
			}
		}
		
		
		
		boolean hitVertically = false;
		
		if(cld0!=null && cld1==null && cld2 == null) { 
			
			
			hitVertically = pushCld(ent, cld0, delta);
			if(!hitVertically) {
				ent.hitWall(cld0);
			}
			ent.hitLand(cld0);
		}
		else if(cld0 != null && cld1 != null && cld2 == null) {
			boolean parallel=false;
			
			
			
			float px = ent.getX();
			float py = ent.getY();
			
			if(px > cld0.getRight() && px > cld1.getRight()) {
				parallel = isParallel(cld0, cld1, 1);
			}
			else if(px < cld0.getLeft() && px < cld1.getLeft()) {
				parallel = isParallel(cld0, cld1, 3);
			}
			else if(py > cld0.getTop() && py > cld1.getTop()) {
				parallel = isParallel(cld0, cld1, 0);
			} 
			else if(py < cld0.getBottom() && py < cld1.getBottom()){
				parallel = isParallel(cld0, cld1, 2);
			}
			
			
			
			if(parallel) {
				
				
				origV.set(ent.v);
				origPos2.set(ent.pos);
				hitVertically = pushCld(ent, cld0, delta);
				
				if(cld1.hitTestEntity(ent)) {
					
					ent.setV(origV);
					ent.setPosition(origPos2.x, origPos2.y);
					hitVertically = pushCld(ent, cld1, delta);
					if(!hitVertically) {
						ent.hitWall(cld1);
					}		
					ent.hitLand(cld1);
				} else {
					if(!hitVertically) {
						ent.hitWall(cld0);
					}
					ent.hitLand(cld0);
				}
				
				//ent.hitLand(cld0);
				//ent.hitLand(cld1);
			} else {
				hitVertically = pushCld(ent, cld0, delta);
				if(!hitVertically) {
					
					ent.hitWall(cld0);
				}
				
				ent.hitLand(cld0);
				
				hitVertically = pushCld(ent, cld1, delta);
				if(!hitVertically) {
					
					ent.hitWall(cld1);
				}
				
				ent.hitLand(cld1);
				
				
			}
			
			
			
			
		} else if (cld0 != null && cld1 != null && cld2 != null){
			
			
			hitVertically = pushCld(ent, cld0, delta);
			if(!hitVertically) {
				ent.hitWall(cld0);
			}
			ent.hitLand(cld0);
			
			if (ent.v.x != 0 || ent.v.y != 0) {
				hitVertically = pushCld(ent, cld1, delta);
				if(!hitVertically) {
					ent.hitWall(cld1);
				}
				ent.hitLand(cld1);
			}
			
			if (ent.v.x != 0 || ent.v.y != 0) {
				hitVertically = pushCld(ent, cld2, delta);
				if(!hitVertically) {
					ent.hitWall(cld2);
				}
				ent.hitLand(cld2);
			}
			
		}
		
		
		/*
		for(i=0;i<num;i++) {
			land = entityList.get(i);
						
			dx = 0;
			dy = 0;
			origPos.set(ent.pos);
			
			if (land.hitTestEntity(ent)) {
						
				
				vb.set(ent.v);
				vb.scl(-1);
				vb.nor().scl(STEP_BACK*delta);
				//vb.nor().scl(0.3f);
				
				while (land.hitTestEntity(ent)) {
					ent.translate(vb);
					dx += vb.x;
					dy += vb.y;
				}
				
									
				//find whether hit on horz or vert
				boolean hitVertically;
				
				//check
				ent.setY(ent.getY() - vb.y);
				
				if (land.hitTestEntity(ent)) {
					hitVertically = true;
				} else {
					hitVertically = false;
				}
						
				
				ent.setY(ent.getY() + vb.y);
				
				if (hitVertically) {
					//restore the horz translation
					ent.setX(ent.getX() - dx); 
					ent.setVY(-ent.v.y * ent.restitution);
					
					if(vb.y > 0) {
						ent.setInAir(false);
						ent.setStandOn(land);
					}
					
				} else {
					
					
					//restore the vert translation
					ent.setY(ent.getY() - dy);
					ent.setVX( -ent.v.x * ent.restitution);
					ent.hitWall(land);
				
				}
				ent.hitLand(land);
				
			}
			
		}
		*/
		
		
	}
	
	private boolean isParallel(Entity cld0,Entity cld1,int pos) {
		//pos 0 : top , 1: right, 2: bottom , 3: left
		
		
		float diff ;
		float lim = 2;
		
		if(pos == 0) {
			diff = cld0.getTop() - cld1.getTop();
			if((diff >= 0 && diff < lim) || diff <= 0 && diff > -lim) {
				return true;
			}
		} else if(pos == 2) {
			diff = cld0.getBottom() - cld1.getBottom();
			if((diff >= 0 && diff < lim) || diff <= 0 && diff > -lim) {
				return true;
			}
		} else if(pos==3) {
			diff = cld0.getLeft() - cld1.getLeft();
			if((diff >= 0 && diff < lim) || diff <= 0 && diff > -lim) {
				return true;
			}
		} else if(pos == 1) { 
			diff = cld0.getRight() - cld1.getRight();
			if((diff >= 0 && diff < lim) || diff <= 0 && diff > -lim) {
				return true;
			}
		}
		
		return false;
	}
	private boolean pushCld(Entity ent , Entity land,float delta) {
		float dx;
		float dy;
		
		dx = 0;
		dy = 0;
		origPos.set(ent.pos);
		
		vb.set(ent.v);
		vb.scl(-1);
		vb.nor().scl(STEP_BACK*delta);
				
		//TODO: to be optimized
		while (land.hitTestEntity(ent)) {
			ent.translate(vb);
			dx += vb.x;
			dy += vb.y;
		}
		
		
		//find whether hit on horz or vert
		boolean hitVertically;
		
		//check
		ent.setY(ent.getY() - vb.y);
		
		if (land.hitTestEntity(ent)) {
			hitVertically = true;
		} else {
			hitVertically = false;
		}
				
		
		ent.setY(ent.getY() + vb.y);
		
		if (hitVertically) {
			//restore the horz translation
			ent.setX(ent.getX() - dx); 
			ent.setVY(-ent.v.y * ent.restitution);
			
			if(vb.y > 0) {
				//ent.setInAir(false);
				ent.setStandOn(land);
			}
			
		} else {
			//restore the vert translation
			ent.setY(ent.getY() - dy);
			ent.setVX( -ent.v.x * ent.restitution);
			//ent.hitWall(land);
		
		}
		
		return hitVertically;
	}
	public Array<Entity> getList() {
		return entityList;
	}

}
