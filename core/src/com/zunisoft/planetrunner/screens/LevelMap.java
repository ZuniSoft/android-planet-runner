package com.zunisoft.planetrunner.screens;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.zunisoft.utility.games.StageGame;

import com.zunisoft.planetrunner.control.LevelButton;
import com.zunisoft.planetrunner.PlanetRunner;

public class LevelMap extends StageGame {

	public static final int ON_ICON_SELECTED = 1;
	public static final int ON_BACK = 2;
	
	public int selectedLevelId;
			
	public LevelMap() {
		
		// Background
		NinePatch patch = new NinePatch(PlanetRunner.atlas.findRegion("level_bg"), 2, 2, 2, 2);
		Image bg = new Image(patch);
		fillScreen(bg, true, false);
		addChild(bg);

		// Total score
		int totalScore=0;
				
		int curLevelProgress = 1+PlanetRunner.data.getLevelProgress();

		// World 1
		LevelButton level1 = new LevelButton(1, "World-1");
		addChild(level1);
		centerActorX(level1);
		level1.setY(getHeight() - level1.getHeight() - 150);
		level1.addListener(levelButtonListener);
		totalScore += PlanetRunner.data.getScore(1);

		// World 2
		LevelButton level2 = new LevelButton(2, "World-2");
		addChild(level2);
		centerActorX(level2);
		level2.setY(level1.getY() - level2.getHeight() - 20);
		level2.addListener(levelButtonListener);

		if(level2.getId() > curLevelProgress) {
			level2.lock();
		}

		// World 3
		LevelButton level3 = new LevelButton(3, "World-3");
		addChild(level3);
		centerActorX(level3);
		level3.setY(level2.getY() - level3.getHeight() - 20);
		level3.addListener(levelButtonListener);

		if(level3.getId() > curLevelProgress) {
			level3.lock();
		}

		totalScore += PlanetRunner.data.getScore(2);

		// Displaying total score
		LabelStyle style = new LabelStyle();
		style.font = PlanetRunner.font1;
		style.fontColor = new Color(0x116ab5ff);
		Label label = new Label("Score : "+totalScore, style);
		addChild(label);
		label.setY(10);
		centerActorX(label);
	}
	
	// If icon clicked
	private ClickListener levelButtonListener = new ClickListener(){

		@Override
		public void clicked(InputEvent event, float x, float y) {
			LevelButton icon = (LevelButton)event.getTarget();
			
			if(icon.isLocked()) return;

			// Note the selected id
			selectedLevelId = icon.getId();
			
			// Notify main program
			call(ON_ICON_SELECTED);
			
			PlanetRunner.media.playSound("click");
		}
		
	};

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.ESCAPE || keycode == Keys.BACK){  // If back key is pressed
			call(ON_BACK);
			PlanetRunner.media.playSound("click");
			
			return true;
		}
		return super.keyDown(keycode);
	}

}
