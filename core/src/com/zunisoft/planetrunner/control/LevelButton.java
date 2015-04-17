package com.zunisoft.planetrunner.control;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.zunisoft.planetrunner.PlanetRunner;

public class LevelButton extends Group {

	public static final int ON_CLICK = 1;
	
	private boolean locked = false;
	private int id;
	private TextButton button ;
	
	public LevelButton(int id, String name) {
		this.id = id;
		
		TextButtonStyle style = new TextButtonStyle();
		style.font = PlanetRunner.font1;
		style.fontColor = new Color(0x624601ff);
		
		NinePatch patch = new NinePatch(PlanetRunner.atlas.findRegion("level_select_bg"), 14, 14, 14, 14);
		style.up = new NinePatchDrawable(patch);
		
		NinePatch patch2 = new NinePatch(PlanetRunner.atlas.findRegion("level_select_bg_down"), 14, 14, 14, 14);
		style.down = new NinePatchDrawable(patch2);
				
		NinePatch patch3 = new NinePatch(PlanetRunner.atlas.findRegion("level_select_bg_disabled"), 14, 14, 14, 14);
		style.disabled = new NinePatchDrawable(patch3);
		
		button = new TextButton(name, style) ;
		addActor(button);
		
		setSize(button.getWidth(), button.getHeight());
		
		/*
		button.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				fire(new MessageEvent(message));
			}
		});*/
		
		addCaptureListener(new EventListener() {
			@Override
			public boolean handle(Event event) {
				event.setTarget(LevelButton.this);
				return true;
			}
		});
	}
	
	// Lock the icon
	public void lock() {
		locked = true;
		button.setTouchable(Touchable.disabled);
		button.setDisabled(true);
	}
	
	// Unlock the icon
	public void unlock() {
		locked = false;
		button.setTouchable(Touchable.enabled);
		button.setDisabled(false);
	}
	public boolean isLocked() {
		return locked;
	}

	public int getId() {
		return id;
	}

}
