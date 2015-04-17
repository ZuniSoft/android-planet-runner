package com.zunisoft.utility.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class StageGame implements Screen, InputProcessor {
	protected Stage stage;
	protected Stage overlay;
	protected Stage background;
	
	public static int appWidth=0,appHeight=0;
	
	
	private String delayCode;
	private float delayTime=0;
	
	
	
	protected OrthographicCamera camera;
	private Callback callback;
	
	
	public static interface Callback {
		void call(int code);
	}
	
	
	public StageGame() {
		if(appWidth == 0 || appHeight==0) {
			 throw new IllegalArgumentException("StageGame size not defined yet");
		}
				
		Viewport viewport = new ExtendViewport(appWidth	, appHeight);
		
		stage = new Stage(viewport);
		overlay = new Stage(viewport);
		background = new Stage(viewport);
		camera = (OrthographicCamera) stage.getCamera();
		
	}
	public void setCallback(Callback callback) {
		this.callback = callback;
	}
	protected float getStageToOverlayX(float x) {
		float cx = (x - camera.position.x)/camera.zoom;
		float px = camera.viewportWidth/2 + cx;
		return px;
	}
	protected float getStageToOverlayY(float y) {
		float cy = (y - camera.position.y)/camera.zoom;
		float py= camera.viewportHeight/2 + cy;
		return py;
	}
	protected void call(int code) {
		callback.call(code);
	}
	
	protected float getOverlayToStageX(float x) {
		float cx = x - camera.viewportWidth/2;
		float px = (cx*camera.zoom + camera.position.x);
		return px;
	}
	protected float getOverlayToStageY(float y) {
		float cy = y - camera.viewportHeight/2;
		float py = (cy*camera.zoom + camera.position.y);
		return py;
	}
	public static void setAppSize(int w,int h) {
		appWidth = w;
		appHeight = h;
	}
	
	protected void delayCall(String code,float delayTime) {
		delayCode = code;
		this.delayTime = delayTime;
	}
	protected void onDelayCall(String code) {
		
	}
	public float getWidth() {
		return stage.getWidth();
	}
	public float getHeight() {
		return stage.getHeight();
	}
	protected void centerActorXY(Actor actor) {
		centerActorX(actor);
		centerActorY(actor);
	}
	protected void centerActorX(Actor actor) {
		actor.setX((getWidth()-actor.getWidth())/2);
	}
	protected void centerActorX(Actor actor,boolean rounded) {
		if(rounded) {
			actor.setX((int) ((getWidth()-actor.getWidth())/2));
		} else {
			centerActorX(actor);
		}
	}
	protected void centerActorY(Actor actor) {
		actor.setY((getHeight()-actor.getHeight())/2);
	}
	protected void update(float delta) {
		
	}
	protected void render(float delta , float pauseTime) {
		
	}
	protected void runDelay(float delta) {
		
		if(delayTime > 0) {
			delayTime -= delta;
			
			if(delayTime <= 0) {
				delayTime = 0;
				onDelayCall(delayCode);
				
			}
		}
	}
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		//Gdx.gl.glClearColor(255.0f, 255.0f, 255.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);	
		
		
		update(delta);
				
		stage.act(delta);
		overlay.act(delta);
		
		runDelay(delta);
		
		background.draw();
		stage.draw();
		overlay.draw();
		
	}
	public void renderOnPause(float delta) {
		Gdx.gl.glClearColor(255.0f, 255.0f, 255.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);	
		
		overlay.act(delta);
		stage.draw();
		overlay.draw();
	}
	////////////
	public void addChild(Actor actor) {
		stage.addActor(actor);
	}
	public void addChild(Actor actor,float x,float y) {
		addChild(actor);
		actor.setX(x);
		actor.setY(y);
	}
	public void removeChild(Actor actor) {
		actor.remove();
	}
	protected void addOverlayChild(Actor actor) {
		overlay.addActor(actor);
	}
	protected void removeOverlayChild(Actor actor) {
		actor.remove();
	}
	protected void addBackground(Actor actor, boolean centerX, boolean centerY) {
		addBackground(actor,centerX,centerY,true);
	}
	protected void addBackground(Actor actor, boolean centerX, boolean centerY,boolean fillscreen) {
		background.addActor(actor);
		if(fillscreen) {
			fillScreen(actor, centerX, centerY);
		}
	}
	protected void removeBackground(Actor actor) {
		actor.remove();
	}
	
	///////////
	protected void fitSize(Actor actor, float w , float h,boolean centerX,boolean centerY) {
		float scale=1;
		
		if(actor.getWidth() / actor.getHeight() > w/h) {
			scale = h/actor.getHeight();
		} else {
			scale = w / actor.getWidth();
		}
		
		
		
		actor.setWidth(actor.getWidth() * scale);
		actor.setHeight(actor.getHeight() * scale);
		
		
		if(centerX) {
			actor.setX((getWidth()-actor.getWidth())/2);
		}
		if(centerY) {
			actor.setY((getHeight()-actor.getHeight())/2);
		}
	}
	protected void fillScreen(Actor actor , boolean centerX,boolean centerY) {
		fitSize(actor,getWidth(),getHeight(),centerX,centerY);
	}
	protected void fitScreen(Actor actor, boolean centerX,boolean centerY) {
		float scale = getScreenScale();
		
		actor.setWidth(actor.getWidth() * scale);
		actor.setHeight(actor.getHeight() * scale);
		
		
		if(centerX) {
			actor.setX((getWidth()-actor.getWidth())/2);
		}
		if(centerY) {
			actor.setY((getHeight()-actor.getHeight())/2);
		}
	}

	
	private float getScreenScale() {
		if(getWidth()/getHeight() > (float)appWidth/(float)appHeight) {
			return getWidth() / appWidth;
		} else {
			return getHeight() / appHeight;
		}
		
			
	}
	
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
	
	
	//////////////////
	
	@Override
	public boolean keyDown(int keycode) {
		
		
		overlay.keyDown(keycode);
		stage.keyDown(keycode);
		
		return true;
	}
	@Override
	public boolean keyUp(int keycode) {
		overlay.keyUp(keycode);
		stage.keyUp(keycode);
		
		return true;
	}
	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(overlay.touchDown(screenX, screenY, pointer, button)) {
			return true;
		}
		stage.touchDown(screenX, screenY, pointer, button);
		//overlay.touchDown(screenX, screenY, pointer, button);
		return false;
	}
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		stage.touchUp(screenX, screenY, pointer, button);
		overlay.touchUp(screenX, screenY, pointer, button);
		return false;
	}
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		stage.touchDragged(screenX, screenY, pointer);
		overlay.touchDragged(screenX, screenY, pointer);
		return false;
	}
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	protected int mmToPx(float mm) {
		float cm = (float) (mm * 0.1);
		float ppc = Gdx.graphics.getPpcX();
		int px;

		float scale = 1;

		Viewport viewport = overlay.getViewport();
		float screenWidth = viewport.getScreenWidth();
		float screenHeight = viewport.getScreenHeight();
		float gameWidth = StageGame.appWidth;
		float gameHeight = StageGame.appHeight;

		// screen is wider
		if (screenWidth / screenHeight > gameWidth / gameHeight) {
			scale = screenHeight / gameHeight;
		} else {
			scale = screenWidth / gameWidth;
		}

		px = (int) (ppc * cm / scale);
		return px;
	}


}
