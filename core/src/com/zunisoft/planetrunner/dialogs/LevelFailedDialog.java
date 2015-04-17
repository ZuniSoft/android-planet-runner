package com.zunisoft.planetrunner.dialogs;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.zunisoft.utility.MessageEvent;
import com.zunisoft.planetrunner.PlanetRunner;

public class LevelFailedDialog extends Group {

	public static final int ON_CLOSE = 1;

	public LevelFailedDialog() {
		
		// Create bg
		NinePatch patch = new NinePatch(PlanetRunner.atlas.findRegion("dialog_bg"), 30, 30, 30, 30);
		Image bg = new Image(patch);
		bg.setSize(600, 500);
		setSize(bg.getWidth() , bg.getHeight());
		addActor(bg);
		
		// Text title
		Image title = new Image(PlanetRunner.atlas.findRegion("level_failed"));
		addActor(title);
		title.setX((getWidth() - title.getWidth())/2);
		title.setY(getHeight() - title.getHeight() - 100);
		
		// Button
		ImageButton okBtn = new ImageButton(
				new TextureRegionDrawable(PlanetRunner.atlas.findRegion("ok_btn")),
				new TextureRegionDrawable(PlanetRunner.atlas.findRegion("ok_btn_down")));
		
		addActor(okBtn);
		okBtn.setX((getWidth() - okBtn.getWidth())/2);
		okBtn.setY(60);
		
		// Fire event when the button clicked
		okBtn.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				fire(new MessageEvent(ON_CLOSE));
			}
		});
		
	}

}
