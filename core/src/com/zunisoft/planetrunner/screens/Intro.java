package com.zunisoft.planetrunner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import com.zunisoft.utility.games.StageGame;

import com.zunisoft.planetrunner.PlanetRunner;

public class Intro extends StageGame {

	public static final int PLAY = 1;
	
	public Intro() {

		// Intro bg
		Image bg = new Image(PlanetRunner.atlas.findRegion("intro_bg"));
		
		// Resize the bg to fill the screen, keep aspect ratio
		fillScreen(bg, true, false);
		addChild(bg);

		// Play button
		ImageButton playBtn = new ImageButton(
				new TextureRegionDrawable(PlanetRunner.atlas.findRegion("play_btn")),
				new TextureRegionDrawable(PlanetRunner.atlas.findRegion("play_btn_down")));
		centerActorX(playBtn);
		playBtn.setY(70);
		addChild(playBtn);
		
		// Btn listener
		playBtn.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				PlanetRunner.media.playSound("click");
				call(PLAY);
			}
			
		});
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.ESCAPE || keycode == Keys.BACK){ // If the back key pressed
			Gdx.app.exit();
			return true;
		}
		return super.keyDown(keycode);
	}

}
