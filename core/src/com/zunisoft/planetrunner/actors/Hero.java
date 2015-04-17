package com.zunisoft.planetrunner.actors;

import com.zunisoft.utility.MessageEvent;
import com.zunisoft.utility.games.Clip;

import com.zunisoft.utility.games.platformerLib.Entity;

import com.zunisoft.planetrunner.levels.Level;
import com.zunisoft.planetrunner.Settings;
import com.zunisoft.planetrunner.PlanetRunner;
import com.zunisoft.planetrunner.enemies.Enemy;
import com.zunisoft.planetrunner.enemies.Enemy2;

public class Hero extends Entity {
	
	//Events
	public static final int HERO_DIE = 1;
	
	private float ax = 100;
	
	// The states
	private int state=-1;
	private static final int IDLE=0;
	private static final int WALK=1;
	private static final int JUMP_UP=2;
	private static final int JUMP_DOWN=3;
	private static final int ATTACKED_BY_ENEMY=4;
	private static final int FIRE=5;
	private static final int DIE=6;
	private static final int DODGE=7;
	private static final int SWIM_UP=8;
	private static final int SWIM_DOWN=9;
	
	// Hero animation
	private Clip clip ;
	
	
	// Hero's full health
	private float fullHealth = 3;
	private float health = fullHealth;
	
	// Damage to take if hero attack enemy
	private float damage = 1;
	
	// Time when hero in star power
	private float starTime = 10; //sec
	private float starTimer;
	
	// Star hilite blinking
	private float hiliteAlpha=1;
	private boolean hiliteUp=false;
	
	// Indicate the hero is just attacked
	private float justAttackedTime;

	// Hero has died, hero has completed the mission
	private boolean hasDied,hasCompleted;
	
	
	// State that the fire key has released, to prevent multiple bomb firing at a time
	private boolean fireKeyHasUp;
	
	// Frames
	private int idleFrames[] =new int[]{2,2};
	private int swimFrames[] =new int[]{3,4};
	private int walkFrames[] =new int[]{5,6};
	private int fireFrames[] =new int[]{1,1,1};
	private int fireInAirFrames[] =new int[]{1,1,1};
	
	// If true, the state will be maintained until animation completed
	private boolean waitingOnComplete;
	
	// Reference to the Level class
	private Level level;

	// Hero is not in water
	protected boolean inWater=false;
	
	
	public Hero(Level level) {
		this.level = level;
		
		// Construct the clip, and clip listener
		clip = new Clip(PlanetRunner.atlas.findRegion("hero") , 71, 101);
		setSize(90, 90);
		setClip(clip);
		clip.setFPS(12);
		clip.addListener(new Clip.ClipListener() {
			
			@Override
			public void onFrame(int num) {}
			
			@Override
			public void onComplete() {
				waitingOnComplete = false;
			}
		});

		// No bouncing
		restitution = 0;

		// Walk speed
		maxSpeedX = Settings.WALK_SPEED;
		changeState(IDLE);
	}
	
	// Notified the keys being pressed
	public void onKey(boolean left , boolean right, boolean jump, boolean dodge, boolean fire) {
		if(hasDied || hasCompleted) return;
		
		a.x = 0;
		friction = 0.5f;

		boolean inAir = isInAir();
		boolean inWater = isInWater();

		// Unable to move when the hero is just attacked
		if(justAttackedTime <= 0) {
			
			// Set the acceleration and state based on direction
			if(left) {
				a.x  = -ax;
				friction = 0;
				
				if(!inAir) {
					changeState(WALK);
				} 
			}

			if(right) {
				a.x = ax;
				friction = 0;
				
				if(!inAir) {
					changeState(WALK);
				} 
			}
			
			// Fire a bomb
			if(fire && fireKeyHasUp) {
				fireBomb();
				fireKeyHasUp = false;
			}
		}

		if(!fire) {
			fireKeyHasUp = true;
		}
		
		// State if in the air
		if(inAir) {
			if(v.y < 0)
				changeState(JUMP_DOWN);
			else
				changeState(JUMP_UP);
		} else {
			if(!right && !left) {
				changeState(IDLE);
			}	
		}

		// State if in the water
		if(inWater) {
			if(v.y < 0)
				changeState(SWIM_DOWN);
			else
				changeState(SWIM_UP);
		}

		// Hero jump
		if(jump) {
			
			// Do if hero touches ground only
			if(!inAir && !inWater) {
				jump();
			}

			// Do if hero touches water only
			if(inWater && !inAir) {
				swim();
			}
		}

		// Hero dodge
		if(dodge) {
			changeState(DODGE);
		}

		// Flip the display if moving left
		if(v.x > 0) {
			setScaleX(1);
		} else if(v.x < 0) {
			setScaleX(-1);
		}
		
	}
	
	// Jumping
	private void jump() {
		// Speed to jump
		setVY(Settings.JUMP_SPEED);
		
		// Sound
		PlanetRunner.media.playSound("jump");
	}

	// Swimming
	private void swim() {
		// Speed to swim
		setVY(Settings.SWIM_UP_SPEED);
	}

	// Bombing
	private void fireBomb() {
		if(level.getBombCounts() == 0) return; // Do not have a bomb
		
		// Firing the bomb
		level.heroFireBomb(getScaleX()==1);
		changeState(FIRE);
		
		PlanetRunner.media.playSound("bomb");
	}
	

	@Override
	public void hitLand(Entity land) {
		super.hitLand(land);

		// If hitting land from below the indication is vertical velocity is zero and hero is in a
		// lower position comparing to the object
		
		if(v.y == 0) {
			if(getTop() - 2 < land.getBottom()) {
				// Check if hit a brick or a mystery box
				
				if(land instanceof Brick) {
					// Hit a brick
					level.heroHitBrick((Brick) land);
				}
				else if(land instanceof MysteryBox) {
					// Hit a mystery box
					level.heroHitMystery((MysteryBox) land);
				}
			}

			if(getBottom() - 2 < land.getTop()) {
				if(land instanceof Water) {
					// Hit water
					level.heroHitWater((Water) land);
				} else {
					this.setInWater(false);
				}
			}
		}
		
		if(justAttackedTime > 0) {
			justAttackedTime = 0.01f;
		}
	}

	public boolean isInWater() {
		return inWater;
	}

	public void setInWater(boolean inWater) {
		this.inWater = inWater;
	}

	// Is still in attacked time, hero can't be attacked by enemy twice
	public boolean isImmune() {
		return justAttackedTime > 0;
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		if(hasCompleted) {
			if(!isInAir()) {
				a.x = ax;
				changeState(WALK);
			}
		}
		
		// Star hilite animation (alpha value), based on elapsed time
		if(starTimer > 0) {
			starTimer -= delta;
			
			if(hiliteUp) {
				hiliteAlpha += delta*5;
				if(hiliteAlpha > 1) {
					hiliteAlpha = 1;
					hiliteUp = false;
				}
			} else {
				hiliteAlpha -= delta*5;
				
				if(hiliteAlpha < 0)  {
					hiliteAlpha = 0;
					hiliteUp = true;
				}
			}

			if(starTimer <= 0) {
				setImage(null);
				
				PlanetRunner.media.playMusic("level");
				PlanetRunner.media.stopMusic("star");
			}
		}

		if(justAttackedTime > 0) {
			justAttackedTime -= delta;
			airFriction = 0;
			
			if(justAttackedTime <= 0) {
				airFriction = 0.1f;
			}
		}
	}

	private void changeState(int newState) {
		changeState(newState, false);
	}	
	
	// Changing the state
	private void changeState(int newState,boolean force) {
		if(state == newState) return; // Already in that state
		if(justAttackedTime > 0) return; // Still attacked
		
		
		if(waitingOnComplete && !force) {  // Waitng to complete pref animation
			return;
		} else {
			waitingOnComplete = false;  // It was forced to change
		}

		if(hasDied) return;
		
		state = newState;

		// Change the clip animation based on state
		switch (state) {
		case IDLE:
			clip.playFrames(idleFrames, true);
			break;
		case SWIM_UP:
			clip.playFrames(swimFrames, true);
			break;
		case SWIM_DOWN:
			clip.playFrames(swimFrames, true);
			break;
		case WALK:
			clip.playFrames(walkFrames, true);
			break;
		case JUMP_UP:
			clip.singleFrame(1);
			break;
		case JUMP_DOWN:
			clip.singleFrame(6);
			break;
		case DODGE:
			clip.singleFrame(7);
			break;
		case ATTACKED_BY_ENEMY:
			clip.singleFrame(0);
			break;		
		case DIE:
			clip.singleFrame(0);
			break;
		case FIRE:
			if(isInAir()) {
				clip.playFrames(fireInAirFrames, false);
			} else {
				clip.playFrames(fireFrames, false);
			}
			waitingOnComplete = true;
			break;
		default:
			break;
		}
	}

	public float getDamage() {
		return damage;
	}
	
	// Hero step on enemy, bounce up
	public void stepEnemy(Enemy enemy) {
		setY(enemy.getTop() + getHeight()/2);
		setVY(300); //bounce up
	}
	
	// Hero attacked by enemy
	public void attackedBy(Enemy enemy) {
		// Check if enemy type is Enemy2 (turtle enemy)
		// if he is hiding, do nothing with hero
		if(enemy instanceof Enemy2) {
			Enemy2 enemy2 = (Enemy2) enemy;
			
			if(enemy2.isSliding()) {
				
			}
			else if(enemy2.isHiding()) {
				return;
			}
		}
		
		// Reduce health
		health -= enemy.getDamage();
		
		if(health <=0) {
			// Out of health, hero will die
			if(v.x > 0) {
				setScaleX(1);
			} else if(v.x < 0) {
				setScaleX(-1);
			}
			
			die();
		} else {
			// Still has health
			changeState(ATTACKED_BY_ENEMY,true);
			justAttackedTime = 2;  // Within 2 sec they can't attack hero again
			if(getX() > enemy.getX()) {
				setVX(260);
			} else {
				setVX(-260);
			}
		}
		
		// Move up
		setVY(200);
	}

	public boolean isDied() {
		return hasDied;
	}
	
	// Hero died,
	private void die() {
		
		a.x = 0;
		airFriction = 0.1f;
		
		// Change state
		changeState(DIE,true);
		hasDied = true;
		noCollision = true;
		
		// Notify the Level by firing an event
		fire(new MessageEvent(HERO_DIE));
	}
	
	// Falling off a cliff
	public void fall() {
		if(hasDied) return;
		
		die();
		setVY(0);
	}

	public float getHealthRatio() {
		return health/fullHealth;
	}
	public void gameCompleted() {
		hasCompleted=true;		
	}
	public void justBeatBoss() {
		a.x = 0;
	}

}
