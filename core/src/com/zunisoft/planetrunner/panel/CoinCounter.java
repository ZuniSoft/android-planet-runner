package com.zunisoft.planetrunner.panel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

import com.zunisoft.planetrunner.PlanetRunner;

public class CoinCounter extends Group {

	private Label label;
	
	public CoinCounter() {
		// Coin image
		Image coin = new Image(PlanetRunner.atlas.findRegion("coin_icon"));
		addActor(coin);
		
		// Label for coin number
		LabelStyle style = new LabelStyle();
		style.font = PlanetRunner.font1;
		style.fontColor = new Color(0xffffffff);
		label = new Label("555", style);
		setSize(label.getRight() + 30 , coin.getHeight());
		
		addActor(label);
		label.setX(36);
		label.setY(-5);
		label.setText("0");
	}
	
	// Set the text
	public void setCount(int count) {
		label.setText(String.valueOf(count));
	}

}
