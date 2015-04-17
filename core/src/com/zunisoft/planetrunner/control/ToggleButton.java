package com.zunisoft.planetrunner.control;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.zunisoft.utility.MessageEvent;

public class ToggleButton extends Group {

	// Button state
	public static final int ON = 1;
	public static final int OFF = 2;
	
	// Button img based on states
	private Image on,off;
	
	// On state
	private Boolean onState;
	
	public ToggleButton(Image on, Image off) {
		this.on = on;
		this.off = off;
				
		setSize(on.getWidth(), on.getHeight());
		
		// Call toggle() on click
		addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				toggle();
			}
		});
		setOn(true);
	}
	
	// Toggle the state, and fire the event
	private void toggle() {
		setOn(!onState);
		
		if(onState)
			fire(new MessageEvent(ON));
		else
			fire(new MessageEvent(OFF));
	}
	
	// Set on or off and change the display
	public void setOn(boolean onState) {
		this.onState = onState;
		
		if(onState) {
			addActor(on);
			off.remove();
		} else {
			addActor(off);
			on.remove();
		}
	}

}
