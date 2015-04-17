package com.zunisoft.planetrunner.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.zunisoft.planetrunner.PlanetRunner;
import com.zunisoft.utility.games.platformerLib.Entity;

public class Water extends Entity {

	// Rectangle entity
	public Water(Rectangle rect) {
		super();
		setSize(rect.width, rect.height);
	}

}
