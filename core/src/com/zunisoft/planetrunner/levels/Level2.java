package com.zunisoft.planetrunner.levels;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.zunisoft.planetrunner.PlanetRunner;

public class Level2 extends Level {

	public Level2() {
		super(2);
		
	}
	@Override
	protected void init() {
		levelBg = new Image(PlanetRunner.atlas.findRegion("level2_bg"));
		tmxFile = "tiled/level2.tmx";
	}
}
