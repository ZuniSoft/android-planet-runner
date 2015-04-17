package com.zunisoft.planetrunner.enemies;

import com.zunisoft.utility.MessageEvent;
import com.zunisoft.utility.games.platformerLib.Entity;

import com.zunisoft.planetrunner.actors.Bomb;
import com.zunisoft.planetrunner.actors.Hero;

public abstract class Enemy extends Entity {

	// Events
	public static final int DIE = 1;

	protected int score = 100;

	// Enemy's health level
	protected float health = 1;
	protected boolean hasDied;
	
	// Damage to take when attack hero
	protected float damage = 1;
	
	public Enemy() {
		restitution = 0;
		edgeUpdateLimRatio = 0.2f;
	}
	
	// Reduce health, die if out of health
	public void attackedByHero(Hero hero,float damageMultiplier) {
		health -= hero.getDamage()*damageMultiplier;
		if(health <=0) die();		
	}

	public void attackHero(Hero hero) {
		// Implemented on subclass
	}

	public boolean isHasDied() {
		return hasDied;
	}

	public int getScore() {
		return score;
	}

	protected void die() {
		hasDied = true;
		noCollision = true;
		
		// Fire event indicate the enemy has die
		fire(new MessageEvent(DIE));
	}
	
	// Hero's bomb hit the enemy
	public void attackedBy(Bomb bomb) {
		// Reduce health
		health -= bomb.getDamage();
		if(health <=0) die();		
	}
	
	// Slide by turtle type enemy
	public void attackedBy(Enemy2 slidingEnemy) {
		health = 0;
		die();
	}
	
	
	public float getDamage() {
		return damage;
	}

	public void flip() {
		// Implemented on subclass
	}

}
