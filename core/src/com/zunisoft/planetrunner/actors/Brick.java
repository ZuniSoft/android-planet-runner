package com.zunisoft.planetrunner.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.zunisoft.utility.games.platformerLib.Entity;
import com.zunisoft.planetrunner.PlanetRunner;

public class Brick extends Entity {

	// Rectangle entity with "brick" image
	public Brick(Rectangle rect) {
		super();
		setSize(rect.width, rect.height);
		Image img = new Image(PlanetRunner.atlas.findRegion("brick"));
		setImage(img);
	}

}
