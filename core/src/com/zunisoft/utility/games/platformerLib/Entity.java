package com.zunisoft.utility.games.platformerLib;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.zunisoft.utility.games.ActorClip;

public class Entity extends ActorClip {

	//model
	public static final String BOX = "box";
	public static final String CIRCLE = "circle";
	public static final String POLYGON = "polygon";
	
	//align
	public static final String CENTER_CENTER = "centercenter";
	public static final String CENTER_BOTTOM = "centerbottom";
			
	public Vector2 a;
	public Vector2 v;
	public Vector2 pos;
	protected float aSpeed;
	protected boolean inAir=true;
	protected Vector2 calculatedV;
	
	//box model
	public float w=32;
	public float h=32;
	
	//circle model
	public float r= 32;
	
	//physic
	public boolean noLandCollision=false;
	public boolean noGravity=false;
	public boolean noCollision=false;
	
	private float onLandTime=0;
	
	public float restitution = 0.5f;
	public float friction = 0.5f;
	public float airFriction = 0.1f;
	public float aFriction = 0.5f;
	
	protected float gravityRatio = 1;
	
	protected float maxSpeedX = 0;
	protected float maxSpeedY = 0;
	protected float maxFallSpeed = 0;
	
	protected float rectRatio = 1;
	protected boolean dragWithLand = false;
			
	protected String align = Entity.CENTER_CENTER;
	protected String model = Entity.BOX;
	
	protected Rectangle rect;
	protected Entity standOn;
	protected boolean debug = false;

	
	
	private Texture debugTex;
	private Vector2 tmpV = new Vector2();
	
	protected boolean skipDraw,ignoreSkipDraw;
	
	public float edgeUpdateLimRatio = 0;
	public float drawEdgeTol = 0;
	public boolean skipUpdate;
	
	//1.0.3
	private World world;
	public Array<Entity> landsRegion = new Array<Entity>();
	public Vector2 lastCheckPos= new Vector2( -99999, -99999);
	private float updateLandRegionDist = 200f;
	
	//1.0.4
	public boolean isSensor = false;
	
	public Entity() {
				
		
		a = new Vector2();
		v = new Vector2();
		pos = new Vector2(0, 0);
		calculatedV = new Vector2();
		init();
					
		setSize(w, h);
	}

	public void setWorld(World world) {
		this.world = world;
	}

	protected void init() { /* sub class to customize this */	}
			 
	public void dispose() {
		
	}

	public boolean isInAir() {
		if(onLandTime > 0) return false;
		return inAir;
	}

	public void setInAir(boolean inAir) {
		this.inAir = inAir;
	}

	public void setStandOn(Entity ent) {
		standOn = ent;
	}

	public Entity getStandOn() {
		return standOn;
	}
	
	
	
	@Override
	public void moveBy (float x, float y) {
		super.moveBy(x, y);
		pos.x += x;
		pos.y += y;
		updateRect();
	}
	@Override
	public void setPosition (float x, float y) {
		super.setPosition(x, y);
		pos.x = x;
		pos.y = y;
		updateRect();
	}
	@Override
	public void setX(float x) {
		super.setX(x);
		pos.x = x;
		updateRect();
	}

	@Override
	public void setY(float y) {
		super.setY(y);
		pos.y = y;
		updateRect();
	}
	
	public void setRadius(float r) {
		this.r = r;
		setModel(Entity.CIRCLE);
	
		super.setWidth(r*2);
		super.setHeight(r*2);
		
		if (World.debug) {
			createDebugOutline();
		}
	}
	
	public void setSize(float w,float  h) {
		super.setSize(w, h);
		
		this.w = w;
		this.h = h;
		
		
		
		if (model == Entity.BOX) {
			rect = new Rectangle(0, 0, w, h);
							
			if (align == Entity.CENTER_CENTER) {
				rect.x = getX() -(w/2);
				rect.y = getY() - (h / 2);
				
			} else if (align == Entity.CENTER_BOTTOM) {
				rect.x = getX()-(w/2);
				rect.y = getY()-h;
			}
		}	
		
		
		if (World.debug) {
			createDebugOutline();
		}
				
	}
	private void createDebugOutline() {
		Pixmap px = null;
		if (model == Entity.BOX) {
			px = new Pixmap((int)w, (int)h, Format.RGBA8888);
			px.setColor(0, 0, 0, 1);
			px.drawRectangle(0, 0, (int)w, (int)h);
			
			
		}
		if (model == Entity.CIRCLE) {
			px = new Pixmap((int)r*2, (int)r*2, Format.RGBA8888);
			px.setColor(0, 0, 0, 1);
			px.drawCircle((int)r, (int)r, (int) r);
		}
		debugTex = new Texture(px);
		px.dispose();
	}
	protected void updateRect() {
		
		
		if (model != Entity.BOX) return;
		
		if (align == Entity.CENTER_CENTER) {
			rect.x = getX()-(w/2);
			rect.y = getY()-(h / 2);
			
		} else if (align == Entity.CENTER_BOTTOM) {
			rect.x = getX()-(w/2);
			rect.y = getY();
		}
		
		
	}
	public void setSkipDraw(boolean skip) {
		skipDraw = skip;
	}
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if(skipUpdate) {
			skipDraw = true;
			return;
		}
		
		if(!ignoreSkipDraw && getStage()!=null) {
			if(!getStage().getCamera().frustum.boundsInFrustum(getX(), getY(), 0, (w+drawEdgeTol)/2, (h+drawEdgeTol)/2, 0)) {
				skipDraw = true;
			} else {
				skipDraw = false;
			}
		}
		
		if(!ignoreSkipDraw && skipDraw) return;
		
		super.draw(batch, parentAlpha);
				
		if (World.debug) {
			Color color = getColor();
			batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
			
			if(model == Entity.BOX) batch.draw(debugTex, getX() - w/2, getY()-h/2);
			else if(model == Entity.CIRCLE)  batch.draw(debugTex, getX() - r, getY()-r);
 		}
	}
	
	public Boolean isPointInside(Vector2 pt) {
		if (model == Entity.BOX) {
			return rect.contains(pt);
		}
		if (model == Entity.CIRCLE) {
			float dist2 = World.pointDist2(pos, pt);
			return dist2 < r * r;
		}
		return false;
	}
	public void applyGravity(Vector2 a) {
		tmpV.set(a).scl(gravityRatio);
		v.add(tmpV);
	}
	public void applyA(Vector2 a) {
		v.add(a);
	}
	public void translate(Vector2 t) {
		setX(getX() + t.x);
		setY(getY() + t.y);
	}
	public void updateRegion() {
		world.getLandsRegion(this, updateLandRegionDist,landsRegion);
		lastCheckPos.set(pos);
	}
	public void update(float delta) {
		if(world != null && !noLandCollision) {
			if (getX() - lastCheckPos.x > updateLandRegionDist) {
				updateRegion();
			}
			else if (getX() - lastCheckPos.x < -updateLandRegionDist) {
				updateRegion();
			}
			else if (getY() - lastCheckPos.y < -updateLandRegionDist) {
				updateRegion();
			}
			else if (getY() - lastCheckPos.y > updateLandRegionDist) {
				updateRegion();
			}
		}
		
		tmpV.set(a);;
		v.add(tmpV);
		
		
		
		if (!inAir) {
			v.x *= (1 - friction);
		} else {
			v.x *= (1- airFriction);
			
			if(onLandTime > 0) {
				onLandTime-= delta;
			}
		}
		
		tmpV.set(v).scl(delta);
		calculatedV.set(tmpV);
		
		
		if(v.y < 0) {
			if (maxFallSpeed < 0) {
				if (calculatedV.y < 0 && calculatedV.y < maxFallSpeed * delta) {
					calculatedV.y = maxFallSpeed * delta;
					v.y = maxFallSpeed;
				}
			}
		}
		
		if(maxSpeedY != 0) {
			if (calculatedV.y > 0) {
				if (calculatedV.y > maxSpeedY * delta) {
					calculatedV.y = maxSpeedY * delta;
					v.y = maxSpeedY;
				}
				
			} else if(calculatedV.y < 0) {
				if (calculatedV.y < -maxSpeedY * delta) {
					calculatedV.y = -maxSpeedY * delta;
					v.y = -maxSpeedY;
				}
			}
		}
		if (maxSpeedX != 0) {
			if (calculatedV.x > 0) {
				if (calculatedV.x > maxSpeedX * delta) {
					calculatedV.x = maxSpeedX * delta;
					v.x = maxSpeedX;
				}
				
			} else if(calculatedV.x < 0) {
				if (calculatedV.x < -maxSpeedX * delta) {
					calculatedV.x = -maxSpeedX * delta;
					v.x = -maxSpeedX;
				}
			}
		}
		
		

		if (standOn!=null && dragWithLand && !isInAir()) {
			tmpV.set(standOn.v).scl(delta);
			calculatedV.add(tmpV);
		}
		
		pos.add(calculatedV);
		
		setX(pos.x);
		setY(pos.y);
		
		
	
		
		rotateBy(aSpeed * delta);
		aSpeed *= (1 - aFriction);
		
		updateRect();
	}
	public Vector2 getPos() {
		return pos;
	}
	public float getTop() {
		if (model == Entity.BOX) {
			return rect.y + rect.height;
		}
		else if (model == Entity.CIRCLE) {
			return getY() + r;
		}
		return super.getTop();
	}
	public float getRight() {
		if (model == Entity.BOX) {
			return rect.x + rect.width;
		}
		else if (model == Entity.CIRCLE) {
			return getX() + r;
		}
		return super.getRight();
	}
	public float getBottom() {
		if (model == Entity.BOX) {
			return rect.y;
		}
		else if (model == Entity.CIRCLE) {
			return getY() - r;
		}
		return getY();
	}
	public float getLeft() {
		if (model == Entity.BOX) {
			return rect.x;
		}
		else if (model == Entity.CIRCLE) {
			return getX() - r;
		}
		return getX();
	}
	public Rectangle getRectangle() {
		return rect;
	}
	public void setAlign(String align) {
		// ????
		this.align = align;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String  getModel() {
		return model;
	}
	public void setVRad(float v, float rad) {
		this.v.x = (float) (v * Math.cos(rad));
		this.v.y = (float) (v * Math.sin(rad));
		
		
	}
	public void setVDeg(float v, float deg) {
		this.v.x = (float) (v * Math.cos(deg*3.1416/180));
		this.v.y = (float) (v * Math.sin(deg*3.1416/180));
	}
	public void setASpeed(float deg) {
		aSpeed = deg;
	}
	
	public void setV(float vx , float vy) {
		setVX(vx);
		setVY(vy);
	}
	public void setVX(float vx) {
		v.x = vx;
	}
	public void setVY(float vy) {
		v.y = vy;
		
		if(vy > 0) {
			onLandTime = 0;
		}
	}
	public void setVYMin(float vy) {
		if (vy < 0 && v.y < 0) {
			if (vy > v.y) {
				return;
			}
		} else if (vy > 0 && v.y > 0) {
			if (vy < v.y) {
				return;
			}
		}
		v.y = vy;
	}
	public void setV(Vector2 v) {
		setVX(v.x);
		setVY(v.y);
	}
	public void setV2Min(Vector2 v) {
		if (v.len2() < this.v.len2()) {
			return;
		}
		setVX(v.x);
		setVY(v.y);
	}
	public void addV(Vector2 v) {
		setVX(this.v.x + v.x);
		setVY(this.v.y + v.y);
	}
	public void  addVX(float vx) {
		this.v.x += vx;
	}
	public Rectangle getCalculatedRect() {
		/*
		Rectangle cRect;
		if(rectRatio < 1) {
			cRect = new Rectangle(rect);
			cRect.width *= rectRatio;
			cRect.height *= rectRatio;
			
			cRect.x += (rect.width - cRect.width)/2;
			cRect.y += (rect.height - cRect.height)/2;
			
			return cRect;
		}*/
		return rect;
	}
	public boolean hitTestEntity(Entity item) {
		Rectangle rect1;
		Rectangle rect2;
					
		if (model == Entity.BOX && item.getModel() == Entity.BOX) {
			rect1 = getCalculatedRect();
			rect2 = item.getCalculatedRect();
							
			if (rect1.overlaps(rect2)) return true;
			else return false;
		}
		if (model == Entity.CIRCLE && item.getModel() == Entity.CIRCLE) {
			
			if ((r + item.r)*(r + item.r) > World.pointDist2(getX(), getY(), item.getX(), item.getY())) {
				return true;
			} else {
				return false;
			}
		}
		if (model == Entity.BOX && item.getModel() == Entity.CIRCLE) {
			return hitTestBoxCircle(this,item);
		}
		if (model == Entity.CIRCLE && item.getModel() == Entity.BOX) {
			return hitTestBoxCircle(item,this);
		}
		if (item.getModel() == Entity.POLYGON) {
			return item.hitTestEntity(this);
		}
		return false;
	}
	public void hitLand(Entity land) {
		
		if(land.getTop() < getBottom() + 5) {
						
			float dist ;
						
			if(getX() > land.getX()) {
				dist = getLeft() - land.getRight();
			} else {
				dist =  land.getLeft() - getRight();
			}
			
			//System.out.println(dist);
			
			if(dist > 0 && dist < 2) {
				return;
			}
			if(dist < 0 && dist > -2) {
				
				return;
			}
			
			onLandTime = 0.25f;
			
		}
	}
	public void hitWall(Entity ent) {
		
	}
	public void kickedByLand(Entity land) {
		
	}
	
	private boolean hitTestBoxCircle(Entity box,Entity circ) {
		float cx = circ.getX();
		float cy = circ.getY();
		Rectangle rect = box.getCalculatedRect();
		
		
		if (circ.getX() < rect.x) {
			cx = rect.x;
		} else if (circ.getX() > rect.x + rect.width) {
			cx = rect.x + rect.width;
		}
		
		if (circ.getY() > rect.y + rect.height) {
			cy = rect.y + rect.height;
		} else if (circ.getY() < rect.y) {
			cy = rect.y;
		}
		
				
		if (World.pointDist2(cx, cy, circ.getX(), circ.getY()) < circ.r * circ.r) {
			return true;
		}
		return false;
	}
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		setSize(width, h);
	}
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		setSize(w, height);
	}

	public void onSkipUpdate() {
		
	}
	

}
