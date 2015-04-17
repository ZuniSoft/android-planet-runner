package com.zunisoft.planetrunner.levels;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.zunisoft.planetrunner.PlanetRunner;

public class Level3 extends Level {

	public Level3() {
		super(3);
		
	}
	@Override
	protected void init() {
		levelBg = new Image(PlanetRunner.atlas.findRegion("level2_bg"));
		tmxFile = "tiled/level3.tmx";
	}
}
