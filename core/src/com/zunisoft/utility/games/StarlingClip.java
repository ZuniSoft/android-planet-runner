package com.zunisoft.utility.games;

import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class StarlingClip extends Clip {
	private Data[] props;
	private int singleId=0;
	private Element element;
	private AtlasRegion region;
		
	
	public StarlingClip(AtlasRegion region, FileHandle xml) {
		this.region = region;
		XmlReader reader = new XmlReader();
		try {
			element = reader.parse(xml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		build();
	
	}
	
	private void build() {
		
		
		int numFrames = element.getChildCount();
		
		frames = new TextureRegion[numFrames];
		props = new Data[numFrames];
		
		int i=0 , x,y,w,h;
		for(i=0;i<numFrames;i++) {
						
			x = (int) ( element.getChild(i).getInt("x"));
			y = (int) (element.getChild(i).getInt("y"));
			w = (int) ( element.getChild(i).getInt("width"));
			h = (int) (element.getChild(i).getInt("height"));
							
			frames[i] = new TextureRegion(region, x, y, w, h);
			
			Data prop = new Data();
			//prop.fw = (int) ( element.getChild(i).getInt("frameWidth",0));
			//(int) (element.getChild(i).getInt("frameHeight",0));
			
			prop.fx = (int) (  element.getChild(i).getInt("frameX",0));
			prop.fy = (int) ( element.getChild(i).getInt("frameY",0));
						
			props[i] = prop;
			
		}
		singleFrame(0);
	}
	
	public void singleFrame(int id) {
		playingFrames = null;
		curFrame = frames[id];
		singleId = id;
	}
	public void drawClip (Batch spriteBatch,float delta) {
		float px,py;
		float rotation = getRotation();
		
		
		if(playingFrames == null) {	
			Data prop = props[singleId];
			
			width = curFrame.getRegionWidth();
			height = curFrame.getRegionHeight();
			
					
			px = getX() - (float)(width)/2;
			py = getY() - (float)(height)/2;
			
			spriteBatch.draw(curFrame, px-prop.fx, py+prop.fy,width/2, height/2, width, height, getScaleX(), getScaleY(), rotation);			
			return;
		}
						
		time += delta;
		int id = (int) (time * fps);
			
		
		
		if(id >= playingFrames.length-1) {
			if(!looping) {
				
				id = playingFrames.length-1;
				
				curFrame = frames[playingFrames[id]];
				Data prop = props[playingFrames[id]];
				
				width = curFrame.getRegionWidth();
				height = curFrame.getRegionHeight();
				
				//px = getX() - (float)(width)/2*getScaleX();
				//py = getY() - (float)(height)/2*getScaleY();
				
				px = getX() - (float)(width)/2;
				py = getY() - (float)(height)/2;
				
				spriteBatch.draw(curFrame, px-prop.fx, py+prop.fy, width/2, height/2, width, height, getScaleX(), getScaleY(), rotation);
				

				if(listeners != null) {
					for(int i=0;i<listeners.size;i++) {
						listeners.get(i).onComplete();
					}
				}
				
				
				playingFrames = null;
				return;
			}
			
			time = time % (playingFrames.length / (float)fps);
			id = (int) (time * fps);
		}
				
		curFrame = frames[playingFrames[id]];
		Data prop = props[playingFrames[id]];
		
		width = curFrame.getRegionWidth();
		height = curFrame.getRegionHeight();
		
		
		
		//px = getX() - (float)(width)/2*getScaleX();
		//py = getY() - (float)(height)/2*getScaleY();
		
		px = getX() - (float)(width)/2;
		py = getY() - (float)(height)/2;
	
		
		if(curFrame != null) spriteBatch.draw(curFrame, px-prop.fx, py+prop.fy, width/2, height/2, width, height, getScaleX(), getScaleY(), rotation);
		
		if(listeners != null) {
			for(int i=0;i<listeners.size;i++) {
				listeners.get(i).onFrame(playingFrames[id]);
			}
		}
		
	}
	
	
	
	////////////////
	private class Data {
		public int fx,fy;
	}



	

}
