package com.zunisoft.planetrunner.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.zunisoft.utility.MessageEvent;
import com.zunisoft.utility.games.platformerLib.Entity;

import com.zunisoft.planetrunner.PlanetRunner;

public class Bomb extends Entity {

	// Events
	public static final int REMOVE = 1;
	public static final int REMOVE_NO_EXP = 2;
	
	private float speed;

	// Damage to take if hit an enemy
	private float damage = 5;
	
	public Bomb() {
		// Set the image
		Image img = new Image(PlanetRunner.atlas.findRegion("bomb"));
		setImage(img);
		setRadius(7);
		
		edgeUpdateLimRatio = 0.1f;
		
		// Full bounce if hit ground
		restitution = 1; 
		airFriction = 0;
		friction = 0;
	}
	
	// Launch by assigning speed based on direction
	public void launch(boolean toRight) {
		setV(0,0);
		if(toRight) {
			speed = 450;
		} else {
			speed = -450;
		}
		setVX(speed);
	}

	@Override
	public void hitWall(Entity ent) {
		// Hit a wall, fire an event to remove this bomb
		fire(new MessageEvent(REMOVE));
	}
	
	// Get the damage of this bomb
	public float getDamage() {
		return damage;
	}
	
	// Remove the bomb silently, because not in screen anymore
	@Override
	public void onSkipUpdate() {
		fire(new MessageEvent(REMOVE_NO_EXP));
	}

}
