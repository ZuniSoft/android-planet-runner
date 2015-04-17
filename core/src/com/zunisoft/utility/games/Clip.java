package com.zunisoft.utility.games;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Clip extends Sprite {
	
	public int width,height;
	
	public static final int ALIGN_CENTER = 1;
	public static final int ALIGN_LEFT_BOTTOM = 2;
	public int align = ALIGN_CENTER;
	
	
	public static float imgScale = 1;
	
	protected int fps= 30;
	protected float time=0;
	protected TextureRegion[] frames;
	protected int[] playingFrames;
	protected boolean looping;
	protected TextureRegion curFrame;
	
	
	private int curId;
	protected Array<ClipListener> listeners;
	
	public static interface ClipListener {
		public void onComplete();
		public void onFrame(int num);
	}	
	
	
	public Clip() {
		
	}
	public Clip(TextureRegion region,int gridWidth,int gridHeight) {
		width = (int) (gridWidth*imgScale);
		height = (int) (gridHeight*imgScale);
		
		int numCols = region.getRegionWidth()/width;
		int numRows = region.getRegionHeight()/height;
		int numFrames = numCols*numRows;
						
		frames = new TextureRegion[numFrames];
		int i=0,px,py;
		for(i=0;i<numFrames;i++) {
			px = (i%numCols)*gridWidth;
			py = i/numCols * gridHeight;
			frames[i] = new TextureRegion(region, px, py, gridWidth, gridHeight);
		}
		singleFrame(0);
	}
	public TextureRegion getTextureRegion(int frame) {
		return frames[frame];
	}
	public void singleFrame(int id) {
		
		playingFrames = null;
		curFrame = frames[id];
		curId = id;
				
		if(listeners != null) {
			for(int i=0;i<listeners.size;i++) {
				listeners.get(i).onFrame(id);
			}
		}
	}
	
	public void playFrames(int from , int to,boolean loop) {
		int[] playingFrames = new int[to-from+1];
		int i=0;
		
		while(from<=to) {
			playingFrames[i] = from;
			from++;
			i++;
		}
		playFrames(playingFrames,loop);
	}
	
	public void playFrames(int[] frames,boolean loop) {
		time = 0;
		playingFrames = frames;
		looping = loop;
	}	
	public void addListener(ClipListener listener) {
		if(listeners == null) listeners = new Array<ClipListener>();
		listeners.add(listener);
	}
	public void removeListener(ClipListener listener) {
		if(listeners == null) return;
		listeners.removeValue(listener, true);
	}
	public void setFPS(int fps) {
		this.fps = fps;
	}

	public void drawClip (Batch spriteBatch,float delta, float parentAlpha) {
	
		float px,py;
		spriteBatch.setColor(1, 1, 1, parentAlpha);
		
		if(align == ALIGN_LEFT_BOTTOM) {
			px = getX() ;
			py = getY() ;
		} else {
			px = getX() - (float)(width)/2;
			py = getY() - (float)(height)/2;
		}
			
		if(playingFrames == null) {	
			
			spriteBatch.draw(curFrame, px, py, width/2 , height/2, width, height, getScaleX(), getScaleY(), getRotation());
			//spriteBatch.end();
			//spriteBatch.begin();
			return;
		}
						
		time += delta;
		int id = (int) (time * fps);
			
				
		if(id >= playingFrames.length-1) {
			if(!looping) {
				
				curFrame = frames[playingFrames[playingFrames.length-1]];
				playingFrames = null;
				spriteBatch.draw(curFrame, px, py, width/2 , height/2, width, height, getScaleX(), getScaleY(), getRotation());
				
				if(listeners != null) {
					for(int i=0;i<listeners.size;i++) {
						listeners.get(i).onComplete();
					}
				}
				
				return;
			}
			
			time = time % (playingFrames.length / (float)fps);
			id = (int) (time * fps);
		}
		
		
		curFrame = frames[playingFrames[id]];
		if(curFrame != null) spriteBatch.draw(curFrame,px, py,width/2 , height/2, width, height, getScaleX(), getScaleY(), getRotation());
		
		
		if(listeners != null) {
			for(int i=0;i<listeners.size;i++) {
				if(playingFrames[id] != curId) listeners.get(i).onFrame(playingFrames[id]);
			}
		}
		
		// was -- > curId = id;
		curId = playingFrames[id];
		
	}
	public int getCurFrameId() {
		return curId;
	}

	public void dispose() {
		listeners.clear();
	}
	
	
}
