package com.zunisoft.planetrunner.panel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

import com.zunisoft.planetrunner.PlanetRunner;

public class ScoreCounter extends Group {

	private Label label;
	
	public ScoreCounter() {
		// The label
		LabelStyle style = new LabelStyle();
		style.font = PlanetRunner.font1;
		style.fontColor = new Color(0xffffffff);
		label = new Label("", style);
		label.setWidth(260);
		setScore(0);
		
		addActor(label);
		setSize(label.getWidth(), label.getHeight());
	}
	
	// Set text
	public void setScore(int score) {
		label.setText("Score : "+score);
	}
}
