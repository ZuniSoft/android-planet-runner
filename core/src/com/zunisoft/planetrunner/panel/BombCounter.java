package com.zunisoft.planetrunner.panel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

import com.zunisoft.planetrunner.PlanetRunner;

public class BombCounter extends Group {

	private Label label;
	
	public BombCounter() {
		// Bomb image
		Image bomb = new Image(PlanetRunner.atlas.findRegion("bomb_icon"));
		addActor(bomb);
		
		// The label showing how many bombs in stock
		LabelStyle style = new LabelStyle();
		style.font = PlanetRunner.font1;
		style.fontColor = new Color(0xffffffff);
		label = new Label("555", style);
		setSize(label.getRight() + 32, 32);
		bomb.setY((getHeight() - bomb.getHeight()) - 2);
		label.setY((getHeight()-label.getHeight())/2);
		
		addActor(label);
		label.setX(35);
		label.setText("0");
		
	}
	
	// Set the label text
	public void setCount(int count) {
		label.setText(String.valueOf(count));
	}

}
