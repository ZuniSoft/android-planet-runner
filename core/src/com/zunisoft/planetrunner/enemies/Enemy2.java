package com.zunisoft.planetrunner.enemies;

import com.zunisoft.utility.games.Clip;

import com.zunisoft.planetrunner.actors.Hero;
import com.zunisoft.planetrunner.PlanetRunner;

//THIS is the snail enemy
public class Enemy2 extends Enemy1 {

	// Hiding
	private static final int HIDE = 10;

	// States
	private boolean hiding,sliding;
	
	
	public Enemy2() {
		// Animation clip
		clip = new Clip(PlanetRunner.atlas.findRegion("snail"), 60, 42);
        setSize(60,42);

        setClip(clip);
		clip.setFPS(12);

		// Listen on every animation completed
		clip.addListener(new Clip.ClipListener() {
			
			@Override
			public void onFrame(int num) {}
			
			@Override
			public void onComplete() {
				waitingOnComplete = false;
			}
		});
		
		// Frames of this enemy type
		walkFrames = new int[]{1,2};
		attackedFrames  = new int[]{0,0,0,0};
		attackHeroFrames  = new int[]{0,0,0};
		hitByBombFrames = new int[]{0,0,0,0};
		dieFrame = 0;
		
		score = 200;
		speed = 75;
	}
	
	@Override
	public void attackHero(Hero hero) {
		// If the enemy hit hero
				
		if(hiding && !sliding) {
			// It should attack hero but in this case when the slime is hiding, the enemy won't attack
			// hero instead it will start sliding
			setSliding(hero);
		}
	}

	@Override
	public void attackedByHero(Hero hero,float damageMultiplier) {
		if(hero.getDamage() * damageMultiplier > 20) {
			setVY(200);
			die();
			
			return;
		}
		
		
		// If sliding, stop sliding
		if(sliding) {
			stopSliding();
			return;
		}
		
		// If not hiding, hide
		if(!hiding) {
			hide();
			return;
		} else {
			// Slide this
			setSliding(hero);
		}
	}
	private void stopSliding() {
		sliding = false;
		speed = 0;
	}
	
	// Slide right or left, based on hero position
	private void setSliding(Hero hero) {
		sliding = true;
		speed = 400;
		if(getX() > hero.getX()) {
			isMoveRight = true;
			moveBy(4, 0);
		} else {
			isMoveRight = false;
			moveBy(-4, 0);
		}
	}
	
	
	@Override
	public void flip() {
		//System.out.println("flip");
		super.flip();
	}

	private void hide() {
		chState(HIDE);
		hiding = true;
		speed = 0;
	}
	public boolean isHiding() {
		return hiding;
	}
	
	//Change the clip state
	protected void chState(int newstate) {
		if(state == newstate) return;
		if(waitingOnComplete) return;
		if(hiding && !hasDied) return;
		
		super.chState(newstate);
		state = newstate;
		
		switch (state) {
		case HIDE:
			clip.singleFrame(0);
			break;
		default:	
			break;
		}
		
	}

	public boolean isSliding() {
		return sliding;
	}

}
