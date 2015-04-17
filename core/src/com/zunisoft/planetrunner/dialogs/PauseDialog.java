package com.zunisoft.planetrunner.dialogs;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import com.zunisoft.utility.MessageEvent;
import com.zunisoft.utility.MessageListener;
import com.zunisoft.planetrunner.PlanetRunner;
import com.zunisoft.planetrunner.control.ToggleButton;

public class PauseDialog extends Group {

	// 2 mode of the dialog, quit game or resume game
	public static final int ON_RESUME = 1;
	public static final int ON_QUIT = 2;
	
	// Music & sound mute btn
	private ToggleButton musicBtn,soundBtn;

	public PauseDialog() {
		// The bg
		NinePatch patch = new NinePatch(PlanetRunner.atlas.findRegion("dialog_bg"),30,30,30,30);
		Image bg = new Image(patch);
		bg.setSize(600, 500);
		setSize(bg.getWidth() , bg.getHeight());
		addActor(bg);
		
		// Title
		Image title = new Image(PlanetRunner.atlas.findRegion("paused"));
		addActor(title);
		title.setX((getWidth() - title.getWidth())/2);
		title.setY(getHeight() - title.getHeight() - 36);
		
		// Resume btn
		ImageButton resumeBtn = new ImageButton(
				new TextureRegionDrawable(PlanetRunner.atlas.findRegion("resume_btn")),
				new TextureRegionDrawable(PlanetRunner.atlas.findRegion("resume_btn_down")));
		
		addActor(resumeBtn);
		resumeBtn.setX(getWidth() - resumeBtn.getWidth()-40);
		resumeBtn.setY(46);
		
		// Fire the 'resume' event
		resumeBtn.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				fire(new MessageEvent(ON_RESUME));
			}
		});
		
		// Exit btn
		ImageButton exitBtn = new ImageButton(
				new TextureRegionDrawable(PlanetRunner.atlas.findRegion("quit_btn")),
				new TextureRegionDrawable(PlanetRunner.atlas.findRegion("quit_btn_down")));
		
		addActor(exitBtn);
		exitBtn.setX(40);
		exitBtn.setY(46);
		
		// Fire 'quit' event
		exitBtn.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				fire(new MessageEvent(ON_QUIT));
			}
		});
		
		
		// The toggle button of music mute
		musicBtn = new ToggleButton(new Image(PlanetRunner.atlas.findRegion("music_on")), new Image(PlanetRunner.atlas.findRegion("music_off")));
		addActor(musicBtn);
		musicBtn.setY(300);
		musicBtn.setX(getWidth()/2 - musicBtn.getWidth()/2);
		musicBtn.setOn(!PlanetRunner.data.isMusicMuted());
		
		// Listen for the action
		musicBtn.addListener(new MessageListener(){
			@Override
			protected void receivedMessage(int message, Actor actor) {
				// Not muted
				if(message == ToggleButton.ON) {
					// Save the persistent state
					PlanetRunner.data.setMusicMute(false);
					// Update the media class
					PlanetRunner.media.updateState();
				}
				// Muted
				else if(message == ToggleButton.OFF) {
					PlanetRunner.data.setMusicMute(true);
					PlanetRunner.media.updateState();
				}
			}
		});
		
		// Same above, but for sound
		soundBtn = new ToggleButton(new Image(PlanetRunner.atlas.findRegion("sound_on")), new Image(PlanetRunner.atlas.findRegion("sound_off")));
		addActor(soundBtn);
		soundBtn.setY(musicBtn.getY() - 80);
		soundBtn.setX(getWidth()/2 - soundBtn.getWidth()/2);
		soundBtn.setOn(!PlanetRunner.data.isSoundMuted());
		soundBtn.addListener(new MessageListener(){
			@Override
			protected void receivedMessage(int message, Actor actor) {
				if(message == ToggleButton.ON) {
					PlanetRunner.data.setSoundMute(false);
					PlanetRunner.media.updateState();
				}
				else if(message == ToggleButton.OFF) {
					PlanetRunner.data.setSoundMute(true);
					PlanetRunner.media.updateState();
				}
			}
		});
	}

}
