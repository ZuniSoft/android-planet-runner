package com.zunisoft.planetrunner.enemies;

import com.zunisoft.utility.games.Clip;
import com.zunisoft.utility.games.platformerLib.Entity;
import com.zunisoft.planetrunner.actors.Bomb;
import com.zunisoft.planetrunner.actors.Hero;
import com.zunisoft.planetrunner.PlanetRunner;

// THIS is the green fish enemy
public class Enemy5 extends Enemy {

	// States
	protected static final int SWIM = 1;
	private static final int ATTACKED = 2;
	private static final int ATTACK_HERO = 3;
	private static final int HIT_BY_BULLET = 4;
	private static final int DIE = 5;

	protected boolean isMoveRight;
	protected float speed = 90;  //px per sec
	protected int state = -1;

	// Enemy frames
	protected int swimFrames[] = new int[]{0,1};
	protected int attackedFrames[] = new int[]{2,2,2};
	protected int attackHeroFrames[] = new int[]{2,2,2,2};
	protected int hitByBombFrames[] = new int[]{2,2,2,2};
	protected int dieFrame = 2;

	// Enemy is in water
	protected boolean inWater = true;

	private float waitTime; // Wait time after attacked by bomb

	protected boolean waitingOnComplete;

	protected Clip clip;

	public Enemy5() {
		// Animation clip
		clip = new Clip(PlanetRunner.atlas.findRegion("fish_green"), 60, 45);
		setSize(60, 45);
				
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

		noGravity = true;
		score = 300;
	}

	@Override
	public void update(float delta) {
		
		if(!hasDied) {
			if(waitTime <= 0) {
				// Swim (only state for movement)
				if(isInWater()) {
					chState(SWIM);
					
					// Set speed & flip based on direction
					if(isMoveRight) {
						setScaleX(1);
						setVX(speed);
					} else {
						setScaleX(-1);
						setVX(-speed);
					}
					
				}
			}
			// Consume the wait time
			if(waitTime > 0) {
				waitTime -= delta;
			}
		}
		
		super.update(delta);
	}
	
	// Flip the display & direction
	public void flip() {
	
		if(isMoveRight) {
			isMoveRight = false;
			setScaleX(-1);
			
		} else {
			isMoveRight = true;
			setScaleX(1);
		}
	}	
	
	// Flip when hitting wall
	@Override
	public void hitWall(Entity ent) {
		flip();
	}

	@Override
	public void attackedByHero(Hero hero,float damageMultiplier) {
		super.attackedByHero(hero,damageMultiplier);
		
		// Change state or die if lack of health
		if(health > 0) {
			chState(ATTACKED);
		} else {
			die();
		}
	}
	
	@Override
	public void attackedBy(Bomb bomb) {
		super.attackedBy(bomb);
		
		if(health > 0) {
			// Change state
			waitTime = 0.4f;
			chState(HIT_BY_BULLET);
			
			// Change direction based on where the bomb came from
			if(bomb.getX() < getX()) {
				isMoveRight = false;
				setScaleX(-1);
			} else {
				isMoveRight = true;
				setScaleX(1);
			}
		} else {
			// Die
			setVY(200);
			die();
		}
	}
	
	// Getting attacked by sliding enemy
	public void attackedBy(Enemy2 slidingEnemy) {
		super.attackedBy(slidingEnemy);
		
		chState(DIE);
		die();
		setVY(200);
	}
	
	// Attack hero
	@Override
	public void attackHero(Hero hero) {
		chState(ATTACK_HERO);
		
		if(hero.getX() > getX()) {
			isMoveRight = true;
		} else {
			isMoveRight = false;		
		}
	}

	// Die
	@Override
	protected void die() {
		noGravity = false;
		chState(DIE);
		super.die();
	}


	public boolean isInWater() {
		return inWater;
	}

	public void setInWater(boolean inWater) {
		this.inWater = inWater;
	}

	protected void chState(int newstate) {
		chState(newstate,false);
	}
	
	// Change clip state
	protected void chState(int newstate,boolean forced) {
		if(state == newstate) return; //already at the state
		
		// Waiting for prev animation completed
		if(waitingOnComplete)  {
			if(forced) {  // Except forced
				waitingOnComplete = false;
			} else {
				return;
			}
		}
		
		int oldState = state;
		state = newstate;
			
		// Assign the clip frames based on state
		switch (state) {
		case SWIM:
			clip.playFrames(swimFrames, true);
			break;
		case ATTACKED:
			clip.playFrames(attackedFrames, false);
			waitingOnComplete = true;
			break;
		case ATTACK_HERO:
			clip.playFrames(attackHeroFrames, false);
			waitingOnComplete = true;
			break;
		case HIT_BY_BULLET:
			clip.playFrames(hitByBombFrames, false);
			waitingOnComplete = true;
			break;
		case DIE:
			clip.singleFrame(dieFrame);
			break;
		default:
			state = oldState;
			break;
		}
	}

}
