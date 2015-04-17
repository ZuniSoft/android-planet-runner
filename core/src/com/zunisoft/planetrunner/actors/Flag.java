package com.zunisoft.planetrunner.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.zunisoft.utility.MessageEvent;
import com.zunisoft.utility.games.platformerLib.Entity;
import com.zunisoft.planetrunner.PlanetRunner;

public class Flag extends Entity {

	public static final int RAISED = 1;
	
	// Height of flag pole
	private float height = 300;

	// Container of pole and flag
	private Group group;

	// Flag
	private Image flag;
	
	// The raising time
	private float downTime;
	private boolean raised;
	
	public Flag() {
		group = new Group();
		noGravity = true;
		noLandCollision = true;
		
		// Create pole and set position
		NinePatch patch = new NinePatch(PlanetRunner.atlas.findRegion("pole"),7,7,4,4);
		Image pole = new Image(patch);
		pole.setHeight(height);
		
		setSize(pole.getWidth(), height);
		pole.setX(-getWidth()/2);
		pole.setY(-getHeight()/2);
		
		// Create flag and positioning
		flag = new Image(PlanetRunner.atlas.findRegion("flag"));
		group.addActor(flag);
		group.addActor(pole);
		
		flag.setY(pole.getY() + pole.getHeight() - flag.getHeight());
		flag.setX(pole.getX() + 10);
		
		// Accessories of the pole
		Image top = new Image(PlanetRunner.atlas.findRegion("pole_top"));
		group.addActor(top);
		top.setX(pole.getX() + (pole.getWidth() - top.getWidth())/2);
		top.setY(pole.getTop()-2);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		
		super.draw(batch, parentAlpha);
		
		// Draw the container (pole and flag inside it)
		if(!skipDraw) {
			group.setPosition(getX(), getY());
			group.draw(batch, parentAlpha);
		}
		
	}

	@Override
	public void update(float delta) {
		// When the raising is finished, fire an event
		if(downTime > 0) {
			downTime -= delta;
			if(downTime <=0 ) {
				fire(new MessageEvent(RAISED));
			}
		}
		super.update(delta);
	}

	@Override
	public void act(float delta) {
		group.act(delta);
		super.act(delta);
	}

	// Raise the flag
	public void down() {
		raised = true;
		//flag.addAction(Actions.moveBy(0, height-flag.getHeight(), 0.7f));
		flag.addAction(Actions.moveBy(0, -(height-flag.getHeight()), 0.7f));
		downTime = 2f;
	}
	public boolean hasRaised() {
		return raised;
	}

}
