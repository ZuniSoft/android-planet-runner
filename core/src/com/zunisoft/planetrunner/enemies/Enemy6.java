package com.zunisoft.planetrunner.enemies;

import com.zunisoft.utility.games.Clip;
import com.zunisoft.utility.games.platformerLib.Entity;
import com.zunisoft.planetrunner.actors.Bomb;
import com.zunisoft.planetrunner.actors.Hero;
import com.zunisoft.planetrunner.PlanetRunner;

// THIS is the level 3 boss
public class Enemy6 extends Enemy {

	// States
	protected static final int WALK = 1;
	private static final int JUMP = 2;
	private static final int ATTACKED = 3;
	private static final int ATTACK_HERO = 4;
	private static final int HIT_BY_BULLET = 5;
	private static final int DIE = 6;

	protected boolean isMoveRight;
	protected float speed = 100;  //px per sec
	protected int state = -1;

	// Enemy frames
	protected int walkFrames[] = new int[]{0,1,2,3,4,5,6,7,6,5,4,3,2,1};
	protected int attackedFrames[] = new int[]{8,8,8};
	protected int attackHeroFrames[] = new int[]{8,8,8,8};
	protected int hitByBombFrames[] = new int[]{8,8,8,8};
	protected int dieFrame = 8;

	private float waitTime; // Wait time after attacked by bomb

	protected boolean waitingOnComplete;

	protected Clip clip;

	public Enemy6() {
		// Animation clip
		clip = new Clip(PlanetRunner.atlas.findRegion("bain"), 125, 92);
		setSize(80, 80);
				
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

		health = 20;
		score = 475;
	}

	@Override
	public void update(float delta) {
		
		if(!hasDied) {
			if(waitTime <= 0) {
				// Walk
				if(!isInAir()) {
					chState(WALK);
					
					// Set speed & flip based on direction
					if(isMoveRight) {
						setScaleX(1);
						setVX(speed);
					} else {
						setScaleX(-1);
						setVX(-speed);
					}
					
				} else {
					// In air
					chState(JUMP);
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
	
	// Getting attack by the sliding enemy
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
		chState(DIE);
		super.die();
	}

	protected void chState(int newstate) {
		chState(newstate,false);
	}
	
	// Change clip state
	protected void chState(int newstate,boolean forced) {
		if(state == newstate) return; //already at the state
		
		//Waiting for prev animation completed
		if(waitingOnComplete)  {
			if(forced) {  //except forced
				waitingOnComplete = false;
			} else {
				return;
			}
		}
		
		int oldState = state;
		state = newstate;
			
		// Assign the clip frames based on state
		switch (state) {
		case WALK:
			clip.playFrames(walkFrames, true);
			break;
		case JUMP:
			clip.singleFrame(0);
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
