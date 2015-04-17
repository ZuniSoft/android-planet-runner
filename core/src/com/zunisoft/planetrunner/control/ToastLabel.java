package com.zunisoft.planetrunner.control;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

import com.zunisoft.utility.MessageEvent;
import com.zunisoft.planetrunner.PlanetRunner;


public class ToastLabel extends Group {

	public static final int REMOVE = 1;
	
	private Label label;
	private LabelStyle style;
	private float time=-1;
	
	public ToastLabel() {
		// Create the label
		style = new LabelStyle();
		style.font = PlanetRunner.font1;
		style.fontColor = new Color(0xfeed8eff);
		
		label = new Label("", style);
		addActor(label);
	}

	public void init(String text , float time) {
		
		// Set text and position
		label.setText(text);
		label.setWidth(style.font.getBounds(text).width);
		label.setX(-label.getWidth()/2);
		
		// Time length the label will appear
		if(time == 0) time = 0.3f;
		this.time = time;
		
	}

	@Override
	public void act(float delta) {
		// Move up
		if(time > 0) {
			time -= delta;
			moveBy(0, delta*100);
			
			// Has completed
			if(time <= 0) {
				time = -1;
				fire(new MessageEvent(REMOVE));
			}
		}
		super.act(delta);
	}

}
