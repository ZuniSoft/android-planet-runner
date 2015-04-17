package com.zunisoft.planetrunner.media;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

import com.zunisoft.planetrunner.PlanetRunner;


// This class handle musics and sounds
public class Media {

	private AssetManager manager;
	
	// The state
	private boolean isSoundMuted,isMusicMuted;
	
	// Currently played music
	private Array<Music> playingMusics = new Array<Music>();

	public Media(AssetManager manager) {
		this.manager = manager;
	}
	
	// Check if the persistent data has changed
	public void updateState() {
		isSoundMuted = PlanetRunner.data.isSoundMuted();
		isMusicMuted = PlanetRunner.data.isMusicMuted();
		
		//mute all played music
		for(Music music : playingMusics) {
			if(isMusicMuted)
				music.setVolume(0);
			else
				music.setVolume(1);
		}
	}

	// Play music based on the name
	public void playMusic(String name) {
		Music music=getMusic(name);
		
		if(music !=null) {
			if(music.isPlaying()) return;
			
			music.setLooping(true);
			music.play();
			playingMusics.add(music);
			
			updateState();
		}
	}

	public void muteAllMusic() {
		if(isMusicMuted) return; // Already mute
		
		for(Music music : playingMusics) {
			music.setVolume(0);
		}
	}
	public void unMuteAllMusic() {
		if(isMusicMuted) return; // In mute state, no need to unmute
		
		for(Music music : playingMusics) {
			music.setVolume(1);
		}
	}
	
	// Stop all music
	public void stopAllMusic() {
		for(Music music : playingMusics) {
			music.stop();
		}
		playingMusics.clear();
	}
	
	// Stop music by name
	public void stopMusic(String name) {
		Music music=getMusic(name);
		
		if(music !=null) {
			music.stop();
			playingMusics.removeValue(music, true);
		}
	}

	// This method map the music name with the respective music file
	private Music getMusic(String name) {
		Music music=null;
		
		if(name.equals("intro")) {
			music = manager.get("sounds/music/intro.wav", Music.class);
		}
		else if(name.equals("level")) {
			music = manager.get("sounds/music/level.wav", Music.class);
		}
		else if(name.equals("star")) {
			music = manager.get("sounds/music/star.ogg", Music.class);
		}
		return music;
	}
	
	// Play sound by it's name
	public void playSound(String name) {
		if(isSoundMuted) return;
		
		if(name.equals("jump")) {
			manager.get("sounds/jump.mp3", Sound.class).play();
		}
		else if(name.equals("click")) {
			manager.get("sounds/click.mp3", Sound.class).play();
		}
		else if(name.equals("coin")) {
			manager.get("sounds/coin.ogg", Sound.class).play();
		}
		else if(name.equals("hit")) {
			manager.get("sounds/hit.ogg", Sound.class).play();
		}
		else if(name.equals("hit2")) {
			manager.get("sounds/hit2.ogg", Sound.class).play();
		}
		else if(name.equals("bomb_pack")) {
			manager.get("sounds/bomb_pack.ogg", Sound.class).play();
		}
		else if(name.equals("bomb")) {
			manager.get("sounds/bomb.mp3", Sound.class).play();
		}
		else if(name.equals("flag")) {
			manager.get("sounds/flag.mp3", Sound.class).play();
		}
		else if(name.equals("level_completed")) {
			manager.get("sounds/level_completed.mp3", Sound.class).play();
		}
		else if(name.equals("throw_fireball")) {
			manager.get("sounds/throw_fireball.mp3", Sound.class).play();
		}
	}

}
