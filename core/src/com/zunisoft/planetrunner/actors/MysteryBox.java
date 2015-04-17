package com.zunisoft.planetrunner.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.zunisoft.utility.games.platformerLib.Entity;
import com.zunisoft.planetrunner.levels.Level;
import com.zunisoft.planetrunner.PlanetRunner;

public class MysteryBox extends Entity {

	public static final int EXPIRED = 1;
	
	// The contents, it should only one of these :
	private int numCoin, numBombs;

	private Level level;
	
	private Image img,imgHit,imgInactive;
	private float hitTime;
	private boolean hasExpired;
	
	
	public MysteryBox(Level level,Rectangle rect) {
		this.level = level;
		setSize(rect.width, rect.height);
		
		// 3 image states of the box
		img = new Image(PlanetRunner.atlas.findRegion("mystery_box"));
		imgHit = new Image(PlanetRunner.atlas.findRegion("mystery_box_hit"));
		imgInactive = new Image(PlanetRunner.atlas.findRegion("mystery_box_inactive"));
		
		
		setImage(img);
		
		// Floating
		noGravity = true;
	}
	
	// Set the num of coins
	public void setCoin(int num) {
		numCoin = num;
	}

	// Set the num of bombs
	public void setBomb(int numBombs) {
		this.numBombs = numBombs;
	}
	
	// When hero hits the box
	public void hit() {
		if(hasExpired) return;
		PlanetRunner.media.playSound("coin");
		
		// If coins inside
		if(numCoin > 0) {
			level.toastCoin(this);
			hitTime = 0.1f;
			setImage(imgHit);
			numCoin--;
			
			if(numCoin==0) {
				setExpired();
			}
			return;
		}
		
		// If bombs inside
		if(numBombs>0) {
			
			// Put bomb stock above the box
			BombStock stock = new BombStock(numBombs);
			stock.setX(getX() + (getWidth() - stock.getWidth())/2);
			stock.setY(getTop() + stock.getHeight()/2);
			stock.setFloating(false);
			level.addEntity(stock);
			
			// Little bounce
			stock.setVY(70);
			setExpired();
			
			return;
		}
	}
	
	// Box has expired, turn into a metal block
	private void setExpired() {
		setImage(imgInactive);
		hasExpired = true;
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		// Prevent multiple hits at a time, check the min elapsed time between hits
		if(hitTime > 0) {
			hitTime -= delta;
			if(hitTime <=0) {
				setImage(img);
				if(hasExpired) {
					setImage(imgInactive);
					
				}
			}
		}
	}

}
