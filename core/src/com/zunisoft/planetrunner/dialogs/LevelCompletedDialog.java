package com.zunisoft.planetrunner.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.zunisoft.utility.MessageEvent;
import com.zunisoft.planetrunner.PlanetRunner;

public class LevelCompletedDialog extends Group {

	public static final int ON_CLOSE = 1;
	
	public LevelCompletedDialog(int score) {
		// Create bg
		NinePatch patch = new NinePatch(PlanetRunner.atlas.findRegion("dialog_bg"),30,30,30,30);
		Image bg = new Image(patch);
		bg.setSize(600, 500);
		setSize(bg.getWidth() , bg.getHeight());
		addActor(bg);
		
		// The text
		Image title = new Image(PlanetRunner.atlas.findRegion("level_completed"));
		addActor(title);
		title.setX((getWidth() - title.getWidth())/2);
		title.setY(getHeight() - title.getHeight() - 100);
		
		// Score label
		LabelStyle style = new LabelStyle();
		style.font = PlanetRunner.font1;
		style.fontColor = new Color(0x624601ff);

		Label label = new Label("Score :", style);
		addActor(label);
		label.setPosition((getWidth() - label.getWidth())/2, title.getY() - 140);
		
		LabelStyle style2 = new LabelStyle();
		style2.font = PlanetRunner.font2;
		style2.fontColor = new Color(0x624601ff);
		
		// The score
		Label scoreLabel = new Label(String.valueOf(score) , style2);
		addActor(scoreLabel);
		scoreLabel.setPosition((getWidth() - scoreLabel.getWidth())/2, label.getY() - 50);
		
		// OK button
		ImageButton okBtn = new ImageButton(
				new TextureRegionDrawable(PlanetRunner.atlas.findRegion("ok_btn")),
				new TextureRegionDrawable(PlanetRunner.atlas.findRegion("ok_btn_down")));
		
		addActor(okBtn);
		okBtn.setX((getWidth() - okBtn.getWidth())/2);
		okBtn.setY(60);
		
		// Fire event on button click
		okBtn.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				fire(new MessageEvent(ON_CLOSE));
			}
		});
	}

}
