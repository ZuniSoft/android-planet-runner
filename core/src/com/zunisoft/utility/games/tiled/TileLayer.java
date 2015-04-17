package com.zunisoft.utility.games.tiled;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class TileLayer extends Image {
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private int layers[];
	private MapLayer layer;
	
	
	public static boolean hasLayerName(TiledMap map , String name) {
		int i;
		int num = map.getLayers().getCount();
		
		
		for(i=0;i<num;i++) {
			if(map.getLayers().get(i).getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	public TileLayer(OrthographicCamera camera, TiledMap map , String name, Batch batch) {
		int layerId=-1;
		int i;
		int num = map.getLayers().getCount();
		
		for(i=0;i<num;i++) {
			if(map.getLayers().get(i).getName().equals(name)) {
				layerId = i;
				layer = map.getLayers().get(i);
				break;
			}
		}
		int layers[] = {layerId};
		create(camera,map,layers,batch);
		
	}
	
	public TileLayer(OrthographicCamera camera, TiledMap map , int layerId, Batch batch) {
		int layers[] = {layerId};
		layer = map.getLayers().get(layerId);
		create(camera,map,layers,batch);
	}

	private void create(OrthographicCamera camera, TiledMap map , int layers[], Batch batch) {
		this.camera = camera;
		this.layers = layers;
		float unitScale = 1 / 1f;
		renderer = new OrthogonalTiledMapRenderer(map, unitScale,batch);
		
	}
	
	public MapObjects getObjects() {
		if(layer == null) return null;
		MapObjects objects = layer.getObjects();
		
		if(objects.getCount() == 0) return null;
		return objects;
	}
	
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		
		renderer.setView(camera);
		renderer.render(layers);
		
		
		batch.begin();
		
	}

	
}
