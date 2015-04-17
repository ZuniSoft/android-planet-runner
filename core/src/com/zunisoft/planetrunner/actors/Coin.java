package com.zunisoft.planetrunner.actors;

import com.zunisoft.utility.MessageEvent;
import com.zunisoft.utility.games.Clip;
import com.zunisoft.utility.games.platformerLib.Entity;
import com.zunisoft.planetrunner.levels.Level;
import com.zunisoft.planetrunner.PlanetRunner;

public class Coin extends Entity {

	public static final int REQUEST_REMOVE= 1;
	
	// Score of coin
	private int score = 10;

	// Track the time when it throwed, when time's up, remove this coin
	private float throwTime;
	
	private Clip clip;
	
	// Whether a reference coin or not a reference coin used to synchronize all coins animation
	private boolean isReference;
	
	public Coin() {
		clip = new Clip(PlanetRunner.atlas.findRegion("coin"), 32, 32);
		setRadius(16);
		clip.setFPS(12);
		setClip(clip);
	}

	public void setAsRefference() {
		// If it's a reference set the animation frames
		clip.playFrames(new int[]{0,1,2,3,4,5,6,7}, true);
		noGravity = true;
		noCollision = true;
		
	}
	
	// Set if coin floating in air
	public void setFloat() {
		noGravity = true;
		setV(0, 0);
	}
	
	// Throw the coin
	public void throwUp() {
		noGravity = false;
		setVY(200);
		setVX(0);
		
		// The throwing time, is sec
		throwTime = 0.6f;
	}

	@Override
	public void update(float delta) {
		
		// Remove it when throwing time has up
		if(throwTime > 0) {
			throwTime -= delta;
			if(throwTime <=0) {
				fire(new MessageEvent(REQUEST_REMOVE));
			}
		}
		
		// If it's a regular coin (not a reference) sync the frame with the reference
		if(!isReference) {
			clip.singleFrame(Level.coin.getClip().getCurFrameId());
		}
		
		super.update(delta);
	}

	public int getScore() {
		return score;
	}

}
