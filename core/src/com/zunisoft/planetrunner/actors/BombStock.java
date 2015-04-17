package com.zunisoft.planetrunner.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.zunisoft.utility.games.platformerLib.Entity;
import com.zunisoft.planetrunner.PlanetRunner;

public class BombStock extends Entity {

	// Amount of bombs inside the stock
	private int amount;
	
	public BombStock(int amount) {
		this.amount = amount;
		noGravity = true;
		
		// Get the image
		Image img = new Image(PlanetRunner.atlas.findRegion("bomb_stock"));
		setImage(img);
		setSize(53, 58);
	}
	public int getAmount() {
		return amount;
	}

	// Set if float in air or not
	public void setFloating(boolean floating) {
		noGravity = floating;
	}

}
