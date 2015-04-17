package com.zunisoft.planetrunner.actors;

import com.zunisoft.utility.MessageEvent;
import com.zunisoft.utility.games.Clip;
import com.zunisoft.utility.games.platformerLib.Entity;

import com.zunisoft.planetrunner.PlanetRunner;

public class BombExp extends Entity {

	// Event to indicate the animation has complete
	public static final int COMPELTED = 1;

	private Clip clip;
	
	public BombExp() {
		noGravity = true;
		noCollision = true;
				
		clip = new Clip(PlanetRunner.atlas.findRegion("bomb_exp") , 19 ,23);
		setClip(clip);
		
		// Listen to the clip
		clip.addListener(new Clip.ClipListener() {
			
			@Override
			public void onFrame(int num) {}
			
			
			// When animation is finished, fire an completed event
			@Override
			public void onComplete() {
				fire(new MessageEvent(COMPELTED));
			}
		});
	}
	
	// Start animation
	public void start() {
		clip.playFrames(0,2, false);
	}

}
