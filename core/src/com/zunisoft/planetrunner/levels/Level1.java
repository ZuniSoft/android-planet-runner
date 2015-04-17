package com.zunisoft.planetrunner.levels;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.zunisoft.planetrunner.PlanetRunner;

public class Level1 extends Level {

	public Level1() {
		super(1);
	}

	@Override
	protected void init() {
		levelBg = new Image(PlanetRunner.atlas.findRegion("level1_bg"));
        tmxFile = "tiled/level1.tmx";
	}

}
