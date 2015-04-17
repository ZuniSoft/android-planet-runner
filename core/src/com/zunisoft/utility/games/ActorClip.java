package com.zunisoft.utility.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;


public class ActorClip extends Actor {
	public static boolean paused = false;
	public boolean noPaused=false;
	
	private Clip clip=null;
	private Group bmp;
	
	protected float clipOffsetX=0;
	protected float clipOffsetY=0;
	
	protected float imgOffsetX=0;
	protected float imgOffsetY=0;
		
	
	protected boolean clipOnTop = false;

	
	public ActorClip() {
		
	}
	
	
	public ActorClip(Clip clip) {
		setClip(clip);
	}
	protected void setClip(Clip clip) {
		this.clip = clip;
		
	}
	protected void clearImage() {
		bmp.clearChildren();
	}
	public void setImage(Image img) {
		if(img == null) {
			bmp = null;
			return;
		}
		bmp = new Group();
		bmp.addActor(img);
				
		img.setX(-img.getWidth()/2);
		img.setY(-img.getHeight()/2);
	}
	
	
	
	private Vector2 tmpV = new Vector2();
	@Override
	public void draw (Batch batch, float parentAlpha) {
		
		if(clipOnTop) {
			if(bmp != null) {
				drawImage(batch, parentAlpha);
			}
			if(clip != null) {
				drawClip(batch, parentAlpha);
			}
		} else {
			if(clip != null) {
				drawClip(batch, parentAlpha);
			}
			if(bmp != null) {
				drawImage(batch, parentAlpha);
			}
		}
		
		batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, parentAlpha);
	
	}
	
	@Override
	public void setColor(Color color) {
		if(bmp!=null) bmp.setColor(color);
		if(clip != null)  clip.setColor(color);
		super.setColor(color);
	}


	@Override
	public void setColor(float r, float g, float b, float a) {
		if(bmp!=null) bmp.setColor(r, g, b, a);
		if(clip != null)  clip.setColor(r, g, b, a);
		super.setColor(r, g, b, a);
	}


	private void drawImage(Batch batch, float parentAlpha) {
		
		bmp.setRotation(getRotation());
		bmp.setPosition(getX() + imgOffsetX, getY() + imgOffsetY);
		bmp.setScale(getScaleX(), getScaleY());
		bmp.draw(batch, parentAlpha);
	}
	private void drawClip(Batch batch, float parentAlpha) {
			
		if(getRotation() != 0 && (clipOffsetX !=0 || clipOffsetY != 0)) {
			clip.setRotation(getRotation());
			
			tmpV.set(clipOffsetX*getScaleX() , clipOffsetY);
			tmpV.rotate(getRotation());
			
			clip.setPosition(getX()+tmpV.x, getY() +tmpV.y);
			
			
		} else {
			clip.setPosition(getX()+ clipOffsetX*getScaleX(), getY() + clipOffsetY);
		}
						
		float delta = Gdx.graphics.getDeltaTime();
		if(ActorClip.paused && !noPaused) delta = 0;
		
		Color color = getColor();
		clip.drawClip(batch,delta,parentAlpha * color.a);
	}
	
	
	@Override
	public void setScaleX(float scaleX) {
		super.setScaleX(scaleX);
		if(clip != null) clip.setScale(scaleX, clip.getScaleY());
		if(bmp != null) bmp.setScaleX(scaleX);
		
		
	}
	@Override
	public void setScaleY(float scaleY) {
		super.setScaleY(scaleY);
		if(clip != null) clip.setScale(clip.getScaleX(), scaleY);
		if(bmp != null) bmp.setScaleY(scaleY);
	}
	@Override
	public void setScale(float scale) {
		super.setScale(scale);
		if(clip != null) clip.setScale(scale, scale);
		if(bmp != null) bmp.setScale(scale);
	}
	@Override
	public void setRotation(float degrees) {
		super.setRotation(degrees);
		if(clip != null) clip.setRotation(degrees);
		if(bmp != null) bmp.setRotation(degrees);
	}
	public Clip getClip() {
		return clip;
	}
	
}
